package expedia;

import java.util.Map;
import org.testng.annotations.Test;

public class Flights extends Base {

	@Test(dataProviderClass = Data.class, dataProvider = "excel", priority = 1, groups = { "regression" })
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
		searchFlights();
		validateDefaultDate(departureDate);
	}

}
