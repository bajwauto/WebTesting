package expedia;

import org.testng.ITestContext;
import static log.Log.*;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class Events extends Base implements ITestListener {

	@Override
	public void onTestStart(ITestResult result) {
		// TODO Auto-generated method stub
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>STARTING TEST<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
		if (result.getParameters().length > 0)
			info("STARTING TEST EXECUTION FOR THE TEST \"" + result.getMethod().getMethodName() + "\" WITH TEST DATA - "
					+ result.getParameters()[0]);
		else
			info("STARTING TEST EXECUTION FOR THE TEST \"" + result.getMethod().getMethodName() + "\"");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		// TODO Auto-generated method stub
		if (result.getParameters().length > 0)
			info("TEST CASE \"" + result.getMethod().getMethodName() + "\" PASSED WITH PARAMETERS "
					+ result.getParameters()[0]);
		else
			info("TEST CASE \"" + result.getMethod().getMethodName() + "\" PASSED");
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>ENDING TEST<<<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
	}

	@Override
	public void onTestFailure(ITestResult result) {
		// TODO Auto-generated method stub
		Base.currentVPSSPath = Base.currentVPSSPath.replaceAll("SS\\[XXX\\]", "ERROR");
		captureScreenshot(false);
		if (result.getParameters().length > 0)
			error(result.getMethod().getMethodName() + " FAILED WITH PARAMETERS " + result.getParameters()[0]);
		else
			error("TEST CASE \"" + result.getMethod().getMethodName() + "\" FAILED");
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>ENDING TEST<<<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
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
