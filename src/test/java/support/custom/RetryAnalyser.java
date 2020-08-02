package support.custom;

import static log.Log.info;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

import expedia.Base;

public class RetryAnalyser implements IRetryAnalyzer {
	//Making it thread-local for the case when the tests are executed in parallel from the DataProvider itself
	private static ThreadLocal<Integer> counter = new ThreadLocal<Integer>() {
		protected Integer initialValue() {
			return 0;
		}
	};

	@Override
	public boolean retry(ITestResult result) {
		Retry annotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Retry.class);
		if (annotation != null && annotation.count() > counter.get()) {
			counter.set(counter.get() + 1);
			info("THE TEST \"" + result.getName() + "\" HAS " + getStatus(result.getStatus())
					+ ". RETRYING TEST EXECUTION(ATTEMPT " + counter.get() + ")");
			Base.retrying.set(true);
			return true;
		} else {
			counter.set(0);
			;
			return false;
		}
	}

	public String getStatus(int status) {
		if (status == 1)
			return "PASSED";
		else if (status == 3)
			return "BEEN SKIPPED";
		else
			return "FAILED";
	}
}
