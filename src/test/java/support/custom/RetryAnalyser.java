package support.custom;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import static log.Log.info;

public class RetryAnalyser implements IRetryAnalyzer {
	private static int counter = 0;

	@Override
	public boolean retry(ITestResult result) {
		Retry annotation = result.getMethod().getConstructorOrMethod().getMethod().getAnnotation(Retry.class);
		if (annotation != null && annotation.count() > counter) {
			counter++;
			info("THE TEST \"" + result.getName() + "\" HAS " + getStatus(result.getStatus())
					+ ". RETRYING TEST EXECUTION(ATTEMPT )" + counter);
			return true;
		} else {
			counter = 0;
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
