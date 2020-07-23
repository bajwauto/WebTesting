package expedia;

import static log.Log.getLogger;
import static log.Log.info;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import browser.Browser;
import utils.Utility;
import xml.Xml;

public class Base {
	protected static Browser browser;
	protected static String vpScreenshotBasePath = Utility.getAbsoluteProjectPaths("screenshots") + File.separator
			+ Utility.formatDate(new Date(), "dd-MMM-yy'" + File.separator + "'hh.mm a");
	protected static String currentVPSSPath;
	protected static int sscounter = 0;
	protected static int icounter = 1;
	protected static boolean captureScreenshots = false;
	protected static String lastTestMethod = "";
	protected static String configPath = Utility.getAbsoluteProjectPaths("configuration");

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "browser", "captureScreenshots" })
	public void suiteSetup(@Optional("chrome") String browserName, @Optional("true") String captureScreenshots)
			throws IOException {
		System.setProperty("emailUser",
				Utility.decode(Xml.read(configPath, "//executionLogsEmail/username/text()").get(0)));
		System.setProperty("emailPass",
				Utility.decode(Xml.read(configPath, "//executionLogsEmail/password/text()").get(0)));
		getLogger("Expedia");
		info("Suite execution started");
		browser = new Browser(browserName);
		browser.maximize();
		this.captureScreenshots = Boolean.parseBoolean(captureScreenshots);
	}

	@BeforeMethod(alwaysRun = true)
	@Parameters("url")
	public void testSetup(String baseURL, Method method) {
		String currentTestMethod = method.getName();
		sscounter = 0;
		if (currentTestMethod.equalsIgnoreCase(lastTestMethod))
			icounter++;
		else
			icounter = 1;

		currentVPSSPath = vpScreenshotBasePath + File.separator + currentTestMethod + File.separator + "Dataset"
				+ icounter + "_SS[XXX].jpg";
		lastTestMethod = currentTestMethod;
		browser.deleteAllCookies();
		browser.goTo(baseURL);
	}

	@AfterSuite(alwaysRun = true)
	public void suiteTeardown() {
		browser.quit();
		info("Suite execution completed");
	}

	public void clickTab(String tabName) {
		try {
			browser.click(tabName);
		} catch (Exception e) {
			Assert.fail("Could not click the Tab - " + tabName, e);
		}
	}

	public void selectCity(String type, String place) {
		try {
			WebElement cityListButton;
			switch (type.trim().toLowerCase()) {
			case "source":
			case "departure":
				cityListButton = browser
						.findElement(By.cssSelector("[data-stid='location-field-leg1-origin-menu-trigger']"));
				browser.click(cityListButton);
				browser.sendKeys(By.id("location-field-leg1-origin"), place);
				browser.click(By.xpath("//strong[contains(text(),'" + place + "')]"));
				break;
			case "destination":
			case "arrival":
				cityListButton = browser
						.findElement(By.cssSelector("[data-stid='location-field-leg1-destination-menu-trigger']"));
				browser.click(cityListButton);
				browser.sendKeys(By.id("location-field-leg1-destination"), place);
				browser.click(By.xpath("//strong[contains(text(),'" + place + "')]"));
				break;
			default:
				cityListButton = null;
			}
		} catch (Exception e) {
			Assert.fail("Could not select " + place + " as the " + type + " city", e);
		}
	}

	public void selectDate(String type, String date, String format) {
		try {
			String desiredDate = Utility.changeDateFormat(date, format, "d-MMMM yyyy");
			String day = desiredDate.split("-")[0];
			String monthYear = desiredDate.split("-")[1];
			String buttonName = "departureDateButton";
			if (type.trim().equalsIgnoreCase("departure"))
				buttonName = "departureDateButton";
			else if (type.trim().equalsIgnoreCase("return"))
				buttonName = "returnDateButton";
			browser.click(buttonName);
			WebElement calendar = browser.findElement(By.cssSelector("div.uitk-new-date-picker"));
			browser.scrollIntoView(calendar);

			// Bring Calendar to current month
			WebElement button;
			try {
				while ((button = browser.findElement(By.cssSelector(
						"button.uitk-button.uitk-button-small.uitk-flex-item.uitk-button-paging:first-child"))) != null) {
					button.click();
				}
			} catch (ElementClickInterceptedException e) {
				// ignore
			}

			// Move to the desired month-year
			while (!browser
					.getText(browser.findElement(By.cssSelector("div.uitk-new-date-picker-month:first-child h2")))
					.equalsIgnoreCase(monthYear)) {
				browser.click(By.cssSelector(
						"button.uitk-button.uitk-button-small.uitk-flex-item.uitk-button-paging:last-child"));
			}
			browser.click(By.xpath("//div[@class='uitk-new-date-picker-month'][1]//button[@data-day='" + day + "']"));
			browser.click(By.cssSelector("button[data-stid='apply-date-picker'] > span"));
			browser.scrollToPageTop();
		} catch (Exception e) {
			Assert.fail("Could not select " + date + " as the " + type + " date", e);
		}
	}

	public void selectTravelClass(String travelClassName) {
		try {
			browser.click("travelClass");
			browser.click(By.xpath("//a[@class='uitk-list-item' and contains(text(),'" + travelClassName + "')]"));
		} catch (Exception e) {
			Assert.fail("Could not select \"" + travelClassName + "\" as the travel class");
		}

	}

	public void selectTravellers(int adults, int children, int infants) {
		try {
			if (adults <= 6 && children <= 6 && infants <= 6) {
				browser.click("travellers");
				WebElement adultTravellers = browser.findElement(By.id("adult-input-0"));
				while (Integer.parseInt(browser.getAttribute(adultTravellers, "value")) != adults)
					browser.click(By.xpath("//input[@id='adult-input-0']/following-sibling::button"));
				WebElement childTravellers = browser.findElement(By.id("child-input-0"));
				while (Integer.parseInt(browser.getAttribute(childTravellers, "value")) != children)
					browser.click(By.xpath("//input[@id='child-input-0']/following-sibling::button"));
				WebElement infantTravellers = browser.findElement(By.id("infant-input-0"));
				while (Integer.parseInt(browser.getAttribute(infantTravellers, "value")) != infants)
					browser.click(By.xpath("//input[@id='infant-input-0']/following-sibling::button"));
				browser.click("Done");
				if (adults + children + infants > 6) {
					browser.click("travellers");
					String actualErrorMessage = browser.getText(browser.findElement("travellersErrorSummary"));
					String expectedErrorMessage = "We are only able to book between 1 and 6 travellers. Please adjust the number of travellers for your search.";
					Assert.assertEquals(actualErrorMessage, expectedErrorMessage,
							"Invalid/No error message shown when no. of travellers exceed the mark of 6");
					browser.click("Done");
				}
			}
		} catch (Exception e) {
			Assert.fail("Could not select travellers correctly");
		}
	}

	public void searchFlights() {
		try {
			browser.click("Search");
		} catch (Exception e) {
			Assert.fail("Could not click on the Serch Button");
		}
	}

	public void validateDefaultDate(String departureDate) {
		try {
			WebElement defaultDate = browser.findElement(By.cssSelector("th.depart-date.selected"));
			String actualDefaultDate = browser.getText(defaultDate);
			String expectedDefaultDate = Utility.changeDateFormat(departureDate, "d/M/yy", "EEE, d MMM");
			Assert.assertEquals(actualDefaultDate, expectedDefaultDate,
					"The results page shows flights for the date " + actualDefaultDate);
			captureScreenshot(defaultDate);
		} catch (Exception e) {
			Assert.fail("Could not validate the default date");
		}

	}

	protected void captureScreenshot(boolean fullPageScreenshot) {
		if (captureScreenshots) {
			sscounter++;
			if (fullPageScreenshot)
				browser.capturePageScreenshot(currentVPSSPath.replaceAll("\\[XXX\\]", sscounter + ""));
			else
				browser.captureScreenshot(currentVPSSPath.replaceAll("\\[XXX\\]", sscounter + ""));
		}
	}

	protected void captureScreenshot(WebElement element) {
		if (captureScreenshots) {
			sscounter++;
			browser.captureScreenshotWithHighlightedElement(currentVPSSPath.replaceAll("\\[XXX\\]", sscounter + ""),
					element);
		}

	}
}
