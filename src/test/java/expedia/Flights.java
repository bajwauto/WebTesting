package expedia;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.Utility;

public class Flights extends Base {

	@Test
	public void oneWayFlight_checkResultsDefaultDate() {
		String travelDate = "8/12/20";
		clickTab("Flights");
		clickTab("One-way");
		selectCity("source", "Chennai");
		selectCity("destination", "Delhi");
		selectDate("departure", travelDate, "d/M/yy");
		browser.click("Search");
		WebElement defaultDate = browser.findElement(By.cssSelector("th.depart-date.selected"));
		String actualDefaultDate = browser.getText(defaultDate);
		String expectedDefaultDate = Utility.changeDateFormat(travelDate, "d/M/yy", "EEE, d MMM");
		System.out.println(actualDefaultDate + "\r\n" + expectedDefaultDate);
		Assert.assertEquals(actualDefaultDate, expectedDefaultDate, "The results page shows flights for the date "+actualDefaultDate);
		browser.captureScreenshotOfHighlightedElement("E:\\Gursharan\\test.jpg", defaultDate);
	}

}
