package expedia;

import static log.Log.error;
import static log.Log.info;
import static log.Log.warn;

import java.io.File;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Events extends Base implements ITestListener {
	private static ExtentHtmlReporter htmlReporter;
	private static ExtentReports extentReports;
	private static ExtentTest extentTest;
	private static Markup markup;

	@Override
	public void onTestStart(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		if (extentReports != null)
			extentTest = extentReports.createTest(testMethodName);
		else
			extentTest = null;
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>STARTING TEST<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
		if (result.getParameters().length > 0)
			info("STARTING TEST EXECUTION FOR THE TEST \"" + testMethodName + "\" WITH TEST DATA - "
					+ result.getParameters()[0]);
		else
			info("STARTING TEST EXECUTION FOR THE TEST \"" + testMethodName + "\"");
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		String logMessage;
		if (result.getParameters().length > 0)
			logMessage = "TEST CASE \"" + testMethodName + "\" PASSED WITH PARAMETERS " + result.getParameters()[0];
		else
			logMessage = "TEST CASE \"" + testMethodName + "\" PASSED";
		info(logMessage);
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>ENDING TEST<<<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
		if (extentTest != null) {
			markup = MarkupHelper.createLabel(logMessage, ExtentColor.GREEN);
			extentTest.pass(markup);
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		String logMessage;
		Base.currentVPSSPath = Base.currentVPSSPath.replaceAll("SS\\[XXX\\]", "ERROR");
		captureScreenshot(false);
		if (result.getParameters().length > 0)
			logMessage = testMethodName + " FAILED WITH PARAMETERS " + result.getParameters()[0];
		else
			logMessage = "TEST CASE \"" + testMethodName + "\" FAILED";
		error(logMessage);
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>ENDING TEST<<<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
		if (extentTest != null) {
			markup = MarkupHelper.createLabel(logMessage, ExtentColor.RED);
			extentTest.fail(markup);
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		String logMessage;
		if (result.getParameters().length > 0)
			logMessage = "THE TEST CASE \"" + testMethodName + "\" HAS SKIPPED EXECUTION WITH PARAMETERS "
					+ result.getParameters()[0];
		else
			logMessage = "THE TEST CASE \"" + testMethodName + "\" HAS SKIPPED EXECUTION";
		warn(logMessage);
		if (extentTest != null) {
			markup = MarkupHelper.createLabel(logMessage, ExtentColor.YELLOW);
			extentTest.warning(markup);
		}
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
		String testName = context.getName();
		if (Base.enableExtentReport) {
			String extentReportPath = Base.reportsPath + File.separator + testName + ".html";
			htmlReporter = new ExtentHtmlReporter(extentReportPath);
			htmlReporter.config().setEncoding("UTF-8");
			htmlReporter.config().setTheme(Theme.DARK);
			htmlReporter.config().setDocumentTitle("Expedia Test Automation");
			htmlReporter.config().setReportName(testName);
			extentReports = new ExtentReports();
			extentReports.attachReporter(htmlReporter);
			extentReports.setSystemInfo("Application", "Expedia");
			extentReports.setSystemInfo("URL", "https://www.expedia.co.in/");
			extentReports.setSystemInfo("Automation Tester", "Bajwauto");
			extentReports.setSystemInfo("Organisation", "Bajwa Auto works Co.");
		} else {
			htmlReporter = null;
			extentReports = null;
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		if (extentReports != null)
			extentReports.flush();
	}
}
