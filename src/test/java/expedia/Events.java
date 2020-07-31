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
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class Events implements ITestListener {
	private static ExtentSparkReporter htmlReporter;
	private static ExtentReports extentReports;
	private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<ExtentTest>();

	@Override
	public void onStart(ITestContext context) {
		if (Base.enableExtentReport) {
			String testName = context.getName();
			String extentReportPath = Base.reportsPath + File.separator + testName + ".html";
			htmlReporter = new ExtentSparkReporter(extentReportPath);
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
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		if (extentReports != null) {
			ExtentTest localExtentTest = extentReports.createTest(testMethodName);
			extentTest.set(localExtentTest);
		} else
			extentTest.set(null);

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
		if (extentTest.get() != null) {
			Markup markup = MarkupHelper.createLabel(logMessage, ExtentColor.GREEN);
			extentTest.get().pass(markup);
		}
	}

	@Override
	public void onTestFailure(ITestResult result) {
		String testMethodName = result.getMethod().getMethodName();
		String logMessage;
		Base.currentVPSSPath.set(Base.currentVPSSPath.get().replaceAll("SS\\[XXX\\]", "ERROR"));
		Base.captureScreenshot(false);
		if (result.getParameters().length > 0)
			logMessage = testMethodName + " FAILED WITH PARAMETERS " + result.getParameters()[0];
		else
			logMessage = "TEST CASE \"" + testMethodName + "\" FAILED";
		error(logMessage);
		info("|******************************************************************************************************************|");
		info("|!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>>ENDING TEST<<<<<<<<<<<<<<<<!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!|");
		info("|******************************************************************************************************************|");
		if (extentTest.get() != null) {
			Markup markup = MarkupHelper.createLabel(logMessage, ExtentColor.GREEN);
			extentTest.get().fail(markup);
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
		if (extentTest.get() != null) {
			Markup markup = MarkupHelper.createLabel(logMessage, ExtentColor.GREEN);
			extentTest.get().warning(markup);
		}
	}

	@Override
	public void onFinish(ITestContext context) {
		if (extentReports != null)
			extentReports.flush();
	}
}
