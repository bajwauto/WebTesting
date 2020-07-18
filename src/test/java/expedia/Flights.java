package expedia;

import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.Test;

import utils.Utility;

public class Flights extends Base {

	@Test(dataProviderClass = Data.class, dataProvider = "excel",priority = 1, groups = {"regression"})
	public void oneWayFlight_checkEconomyClassResultsDefaultDate(Map<String, Object> data) {
		String leavingFrom = (String) data.get("Leaving from");
		String gointTo = (String) data.get("Going to");
		String departureDate = (String) data.get("Departure Date");
		String travelClass = (String) data.get("Travel Class");
		int adults = ((Double) data.get("Adults")).intValue();
		int children = ((Double) data.get("Children")).intValue();
		int infants = ((Double) data.get("Infants")).intValue();
		
		clickTab("Flights");
		clickTab("One-way");
		selectCity("source", leavingFrom);
		selectCity("destination", gointTo);
		selectDate("departure", departureDate, "d/M/yy");
		selectTravelClass(travelClass);
		selectTravellers(adults, children, infants);
		captureScreenshot(false);
		browser.click("Search");
		WebElement defaultDate = browser.findElement(By.cssSelector("th.depart-date.selected"));
		String actualDefaultDate = browser.getText(defaultDate);
		String expectedDefaultDate = Utility.changeDateFormat(departureDate, "d/M/yy", "EEE, d MMM");
		Assert.assertEquals(actualDefaultDate, expectedDefaultDate,
				"The results page shows flights for the date " + actualDefaultDate);
		captureScreenshot(defaultDate);
	}

}
