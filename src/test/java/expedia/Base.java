package expedia;

import static log.Log.getLogger;
import static log.Log.info;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.asserts.SoftAssert;

import browser.Browser;
import utils.Utility;
import xml.Xml;

public class Base {
	protected static Browser browser;
	protected static String vpScreenshotBasePath = Utility.getAbsoluteProjectPaths("screenshots") + File.separator
			+ Utility.formatDate(new Date(), "dd-MMM-yy'" + File.separator + "'hh.mm.ss a");
	protected static String currentVPSSPath;
	protected static int sscounter = 0;
	protected static int icounter = 1;
	protected static boolean captureScreenshots = false;
	protected static String lastTestMethod = "";
	protected static String configPath = Utility.getAbsoluteProjectPaths("configuration");
	private static String escapeProperty = "org.uncommons.reportng.escape-output";
	protected static String reportsPath = vpScreenshotBasePath.replaceAll("screenshots", "reports");
	public static boolean enableExtentReport;

	@BeforeSuite(alwaysRun = true)
	@Parameters({ "browser", "captureScreenshots", "enableExtentReport" })
	public void suiteSetup(@Optional("chrome") String browserName, @Optional("true") String captureScreenshots,
			@Optional("false") String enableExtentReporting) throws IOException {
		System.setProperty("emailUser",
				Utility.decode(Xml.read(configPath, "//executionLogsEmail/username/text()").get(0)));
		System.setProperty("emailPass",
				Utility.decode(Xml.read(configPath, "//executionLogsEmail/password/text()").get(0)));
		System.setProperty(escapeProperty, "false");

		enableExtentReport = Boolean.parseBoolean(enableExtentReporting);
		if (enableExtentReport)
			Utility.createDirectory(reportsPath);

		getLogger("Expedia");
		info("Suite execution started");
		browser = new Browser(browserName);
		browser.maximize();
		Base.captureScreenshots = Boolean.parseBoolean(captureScreenshots);
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
			Assert.fail("Could not select " + date + " as the " + type + " date", e);
		}
	}

	public void selectTravelClass(String travelClassName) {
		try {
			browser.click("travelClass");
			browser.click(By.xpath("//a[@class='uitk-list-item' and contains(text(),'" + travelClassName + "')]"));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Could not select \"" + travelClassName + "\" as the travel class");
		}

	}

	public void selectTravellers(int adults, int children, int infants) {
		try {
			if (adults <= 6 && children <= 6 && infants <= 6) {
				browser.click("travellers");
				WebElement adultTravellers = browser.findElement(By.id("adult-input-0"));
				for (int i = Integer.parseInt(browser.getAttribute(adultTravellers, "value")); i > 1; i--) {
					browser.click(By.xpath("//input[@id='adult-input-0']/preceding-sibling::button"));
				}
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
			e.printStackTrace();
			Assert.fail("Could not select travellers correctly");
		}
	}

	public void searchFlights() {
		try {
			captureScreenshot(false);
			browser.click("Search");
			browser.findElement(By.cssSelector("button[data-test-id='select-button']"));
		} catch (Exception e) {
			e.printStackTrace();
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
			e.printStackTrace();
			Assert.fail("Could not validate the default date");
		}
	}

	public void validateReturnFlightDetails(Map<String, Object> data) {
		SoftAssert azzert = new SoftAssert();
		try {
			browser.click(By.id("flights-advanced-options-toggle"));
			String actualSource = browser.getAttribute(browser.findElement(By.cssSelector("input#departure-airport-1")),
					"value");
			String expectedSource = (String) data.get("Leaving from");

			String actualDestination = browser
					.getAttribute(browser.findElement(By.cssSelector("input#arrival-airport-1")), "value");
			String expectedDestination = (String) data.get("Going to");

			WebElement actualDepartureDate = browser.findElement(By.cssSelector("input#departure-date-1"));
			String actualDepartureDateValue = browser.getAttribute(actualDepartureDate, "value");
			String expectedDepartureDateValue = Utility.changeDateFormat((String) data.get("Departure Date"), "d/M/yy",
					"dd/MM/yyyy");

			WebElement actualReturnDate = browser.findElement(By.cssSelector("input#return-date-1"));
			String actualReturnDateValue = browser.getAttribute(actualReturnDate, "value");
			String expectedReturnDateValue = Utility.changeDateFormat((String) data.get("Return Date"), "d/M/yy",
					"dd/MM/yyyy");

			String actualClass = browser
					.getSelectedOptionFromDropdown(browser.findElement(By.cssSelector("select#seating-class")));
			String expectedClass = (String) data.get("Travel Class");

			info("Actual Source City: " + actualSource + "\nExpected Source City: " + expectedSource);
			azzert.assertTrue(actualSource.contains(expectedSource),
					"Actual Source City: " + actualSource + "\nExpected Source City: " + expectedSource);
			info("Actual Destination City: " + actualDestination + "\nExpected Destination City: "
					+ expectedDestination);
			azzert.assertTrue(actualDestination.contains(expectedDestination), "Actual Destination City: "
					+ actualDestination + "\nExpected Destination City: " + expectedDestination);
			info("Actual Departure Date: " + actualDepartureDateValue + "\nExpected Departure Date: "
					+ expectedDepartureDateValue);
			azzert.assertTrue(actualDepartureDateValue.equalsIgnoreCase(expectedDepartureDateValue),
					"Actual Departure Date: " + actualDepartureDateValue + "\nExpected Departure Date: "
							+ expectedDepartureDateValue);
			info("Actual Return Date: " + actualReturnDateValue + "\nExpected Return Date: " + expectedReturnDateValue);
			azzert.assertTrue(actualReturnDateValue.equalsIgnoreCase(expectedReturnDateValue), "Actual Return Date: "
					+ actualReturnDateValue + "\nExpected Return Date: " + expectedReturnDateValue);
			info("Actual Tavel Class: " + actualClass + "\nExpected Travel Class: " + expectedClass);
			azzert.assertTrue(actualClass.contains(expectedClass),
					"Actual Class: " + actualClass + "\nExpected Travel Class: " + expectedClass);

			azzert.assertAll();
			captureScreenshot(browser.findElement(By.id("wizardSearch")));

		} catch (Exception e) {
			Assert.fail("Could not validate the Flight Search Details");
		}
	}

	public void validateSorting(String sortBy) {
		try {
			SoftAssert azzert = new SoftAssert();
			browser.selectFromDropDown(browser.findElement("sortFlights"), sortBy);
			switch (sortBy) {
			case "Price (Highest)":
				List<Double> actualAOPrices = new ArrayList<Double>();
				List<Double> expectedAOPrices = new ArrayList<Double>();
				browser.findElement(By.cssSelector("ul.segmented-list.results-list.price-sort"));
				List<WebElement> flightAOPriceElements = browser
						.findElements(By.cssSelector("span[data-test-id='listing-price-dollars']"));
				for (WebElement flightPriceElement : flightAOPriceElements) {
					Double currentPrice = Double
							.parseDouble(browser.getText(flightPriceElement).replaceAll("Rs|,", ""));
					actualAOPrices.add(currentPrice);
					expectedAOPrices.add(currentPrice);
				}
				Collections.sort(expectedAOPrices, Collections.reverseOrder());
				info("Actual Sorted Prices: " + actualAOPrices + "\nExpected Sorted prices: " + expectedAOPrices);
				azzert.assertEquals(actualAOPrices, expectedAOPrices,
						"The flight details are not correctly sorted by " + sortBy);
				break;
			case "Price (Lowest)":
				List<Double> actualDOPrices = new ArrayList<Double>();
				List<Double> expectedDOPrices = new ArrayList<Double>();
				browser.findElement(By.cssSelector("ul.segmented-list.results-list.price-sort"));
				List<WebElement> flightDOPriceElements = browser
						.findElements(By.cssSelector("span[data-test-id='listing-price-dollars']"));
				for (WebElement flightPriceElement : flightDOPriceElements) {
					Double currentPrice = Double
							.parseDouble(browser.getText(flightPriceElement).replaceAll("Rs|,", ""));
					actualDOPrices.add(currentPrice);
					expectedDOPrices.add(currentPrice);
				}
				Collections.sort(expectedDOPrices);
				info("Actual Sorted Prices: " + actualDOPrices + "\nExpected Sorted prices: " + expectedDOPrices);
				azzert.assertEquals(actualDOPrices, expectedDOPrices,
						"The flight details are not correctly sorted by " + sortBy);
				break;
			case "Duration (Shortest)":
				List<Integer> actualAODurations = new ArrayList<Integer>();
				List<Integer> expectedAODurations = new ArrayList<Integer>();
				browser.findElement(By.cssSelector("ul.segmented-list.results-list.duration-sort"));
				List<WebElement> flightAODurationElements = browser
						.findElements(By.cssSelector("span[data-test-id='duration']"));
				for (WebElement flightDurationElement : flightAODurationElements) {
					String currentDuration = browser.getText(flightDurationElement).trim();
					List<String> mAndH = Utility.getMatchesNGroups(currentDuration, "(\\d+)h\\s*(\\d+)m").get(0);
					int currentDurationMinutes = Integer.parseInt(mAndH.get(1)) * 60 + Integer.parseInt(mAndH.get(2));
					actualAODurations.add(currentDurationMinutes);
					expectedAODurations.add(currentDurationMinutes);
				}
				Collections.sort(expectedAODurations);
				info("Actual Sorted Flight Durations(minutes): " + actualAODurations
						+ "\nExpected Sorted Flight Durations(minutes): " + expectedAODurations);
				azzert.assertEquals(actualAODurations, expectedAODurations,
						"The flight details are not correctly sorted by " + sortBy);
				break;
			case "Duration (Longest)":
				List<Integer> actualDODurations = new ArrayList<Integer>();
				List<Integer> expectedDODurations = new ArrayList<Integer>();
				browser.findElement(By.cssSelector("ul.segmented-list.results-list.duration-sort"));
				List<WebElement> flightDODurationElements = browser
						.findElements(By.cssSelector("span[data-test-id='duration']"));
				for (WebElement flightDurationElement : flightDODurationElements) {
					String currentDuration = browser.getText(flightDurationElement).trim();
					List<String> mAndH = Utility.getMatchesNGroups(currentDuration, "(\\d+)h\\s*(\\d+)m").get(0);
					int currentDurationMinutes = Integer.parseInt(mAndH.get(1)) * 60 + Integer.parseInt(mAndH.get(2));
					actualDODurations.add(currentDurationMinutes);
					expectedDODurations.add(currentDurationMinutes);
				}
				Collections.sort(expectedDODurations, Collections.reverseOrder());
				info("Actual Sorted Flight Durations(minutes): " + actualDODurations
						+ "\nExpected Sorted Flight Durations(minutes): " + expectedDODurations);
				azzert.assertEquals(actualDODurations, expectedDODurations,
						"The flight details are not correctly sorted by " + sortBy);
				break;
			}
			azzert.assertAll();
			captureScreenshot(true);
		} catch (Exception e) {
			Assert.fail("Sorting Validation Failed");
		}
	}

	public void openFeedbackPage() {
		try {
			browser.click("signIn");
			browser.click("Feedback");
			WebElement websiteFeedback = browser.findElement(By.xpath("//a[contains(text(),'Website Feedback')]/.."));
			browser.click(websiteFeedback);
		} catch (Exception e) {
			Assert.fail("Unable to open the feedback page");
		}
	}

	public void provideFeedback(Map<String, Object> data) {
		String rating = (String) data.get("Rating");
		String topic = (String) data.get("Topic");
		String description = (String) data.get("Description");
		String email = (String) data.get("Email");
		String ratingClass = "";

		try {
			WebElement feedbackTopic = browser.findElement("FeedbackTopic");
			WebElement ratingButton = browser.findElement(By.cssSelector("a[title='" + rating + "']"));
			browser.click(ratingButton);
			ratingClass = browser.getAttribute(ratingButton, "class").replaceAll(" ", ".");
			ratingClass = Utility.getMatchesNGroups(ratingClass, "^\\w+\\.\\w+").get(0).get(0);
			browser.selectFromDropDown(feedbackTopic, topic);
			browser.sendKeys("FeedbackBox", description);
			if (!email.trim().equalsIgnoreCase(""))
				browser.sendKeys("FeedbackEmail", email);
			browser.click("SendFeedback");
			browser.findElement(By.cssSelector("div." + ratingClass));
			captureScreenshot(browser.findElement(By.cssSelector("div." + ratingClass)));
		} catch (Exception e) {
			Assert.fail("Could not provide appropriate feedback");
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
