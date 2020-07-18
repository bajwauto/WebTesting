package expedia;

import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import browser.Browser;
import utils.Utility;

public class Base {
	protected Browser browser;

	@BeforeSuite(alwaysRun = true)
	@Parameters("browser")
	public void suiteSetup(@Optional("chrome") String browserName) {
		browser = new Browser(browserName);
		browser.maximize();
	}

	@BeforeMethod(alwaysRun = true)
	@Parameters("url")
	public void testSetup(@Optional("https://www.expedia.co.in/") String baseURL) {
		browser.goTo(baseURL);
	}

	@AfterSuite(alwaysRun = true)
	public void suiteTeardown() {
		browser.quit();
	}

	public void clickTab(String tabName) {
		browser.click(tabName);
	}

	public void selectCity(String type, String place) {
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
	}

	public void selectDate(String type, String date, String format) {
		String desiredDate = Utility.changeDateFormat(date, format, "d-MMMM yyyy");
		String day = desiredDate.split("-")[0];
		String monthYear = desiredDate.split("-")[1];
		if (type.trim().equalsIgnoreCase("departure"))
			browser.click("departureDateButton");
		else if (type.trim().equalsIgnoreCase("return"))
			browser.click("returnDateButton");
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
		while (!browser.getText(browser.findElement(By.cssSelector("div.uitk-new-date-picker-month:first-child h2")))
				.equalsIgnoreCase(monthYear)) {
			browser.click(By
					.cssSelector("button.uitk-button.uitk-button-small.uitk-flex-item.uitk-button-paging:last-child"));
		}
		browser.click(By.xpath("//div[@class='uitk-new-date-picker-month'][1]//button[@data-day='" + day + "']"));
		browser.click(By.cssSelector("button[data-stid='apply-date-picker'] > span"));
	}
}
