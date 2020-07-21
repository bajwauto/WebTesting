package expedia;

import org.testng.ITestContext;
import static log.Log.*;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Events extends Base implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestStart(result);
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		info(result.getMethod().getMethodName() + " PASSED with parameters " + result.getParameters()[0]);
	}

	@Override
	public void onTestFailure(ITestResult result) {
		// TODO Auto-generated method stub
		Base.currentVPSSPath = Base.currentVPSSPath.replaceAll("SS\\[XXX\\]", "ERROR");
		System.out.println("Saving screenshot at path "+Base.currentVPSSPath);
		captureScreenshot(false);
		error(result.getMethod().getMethodName() + " FAILED with parameters " + result.getParameters()[0]);
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestSkipped(result);
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedButWithinSuccessPercentage(result);
	}

	@Override
	public void onTestFailedWithTimeout(ITestResult result) {
		// TODO Auto-generated method stub
		ITestListener.super.onTestFailedWithTimeout(result);
	}

	@Override
	public void onStart(ITestContext context) {
		// TODO Auto-generated method stub
		ITestListener.super.onStart(context);
	}

	@Override
	public void onFinish(ITestContext context) {
		// TODO Auto-generated method stub
		ITestListener.super.onFinish(context);
	}

}
