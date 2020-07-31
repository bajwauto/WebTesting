package expedia;

import java.util.Map;

import org.testng.annotations.Test;

import support.custom.Retry;

public class Flights extends Base {

	@Retry
	@Test(dataProviderClass = Data.class, dataProvider = "excel", priority = 1, groups = {
			"regression" }, enabled = true)
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
		searchFlights();
		validateDefaultDate(departureDate);
	}

	@Retry
	@Test(dataProviderClass = Data.class, dataProvider = "excel", priority = 3, groups = {
			"regression" }, enabled = true)
	public void returnFlight_searchDetailsValidation(Map<String, Object> data) {
		String leavingFrom = (String) data.get("Leaving from");
		String gointTo = (String) data.get("Going to");
		String departureDate = (String) data.get("Departure Date");
		String returnDate = (String) data.get("Return Date");
		String travelClass = (String) data.get("Travel Class");
		int adults = ((Double) data.get("Adults")).intValue();
		int children = ((Double) data.get("Children")).intValue();
		int infants = ((Double) data.get("Infants")).intValue();

		clickTab("Flights");
		clickTab("Return");
		selectCity("source", leavingFrom);
		selectCity("destination", gointTo);
		selectDate("departure", departureDate, "d/M/yy");
		selectDate("return", returnDate, "d/M/yy");
		selectTravelClass(travelClass);
		selectTravellers(adults, children, infants);
		searchFlights();
		validateReturnFlightDetails(data);
	}

	@Retry
	@Test(dataProviderClass = Data.class, dataProvider = "excel", priority = 2, groups = { "regression" })
	public void returnFlight_sortingValidation(Map<String, Object> data) {
		String leavingFrom = (String) data.get("Leaving from");
		String gointTo = (String) data.get("Going to");
		String departureDate = (String) data.get("Departure Date");
		String returnDate = (String) data.get("Return Date");
		String travelClass = (String) data.get("Travel Class");
		int adults = ((Double) data.get("Adults")).intValue();
		int children = ((Double) data.get("Children")).intValue();
		int infants = ((Double) data.get("Infants")).intValue();
		String sortBy = (String) data.get("sort by");

		clickTab("Flights");
		clickTab("Return");
		selectCity("source", leavingFrom);
		selectCity("destination", gointTo);
		selectDate("departure", departureDate, "d/M/yy");
		selectDate("return", returnDate, "d/M/yy");
		selectTravelClass(travelClass);
		selectTravellers(adults, children, infants);
		searchFlights();
		validateSorting(sortBy);
	}
}
