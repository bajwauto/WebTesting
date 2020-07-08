package examples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import browser.Browser;
import utils.Utility;

public class Example {
	public static void main(String[] args) throws InterruptedException {
		Browser b = new Browser("chrome");
		b.maximize();
		String source = "Dehradun", destination = "Mumbai", travelDate = "10/8/2020", travelClass = "Economy";
		int adults = 2, children = 2, infants = 1;
		b.goTo("https://www.goibibo.com/");
		b.sendKeys(By.cssSelector("input#gosuggest_inputSrc"), source);
		b.click(By.xpath("//li//span[contains(text(),'" + source + "')]"));
		b.sendKeys(By.cssSelector("input#gosuggest_inputDest"), destination);
		b.click(By.xpath("//li//span[contains(text(),'" + destination + "')]"));
		travelDate = Utility.changeDateFormat(travelDate, "d/M/yyyy", "d-MMMM yyyy");
		String travelDay = travelDate.split("-")[0];
		String travelMonthYear = travelDate.split("-")[1];
		boolean check;
		while (!(check = b.getText(b.findElement(By.cssSelector("div.DayPicker-Caption")))
				.equalsIgnoreCase(travelMonthYear))) {
			b.click(By.cssSelector("div.DayPicker-NavBar span"));
		}
		b.click(By.xpath("//div[@class='DayPicker-Body']//div[text()='" + travelDay + "']"));
		b.click(By.cssSelector("div.fl.col-sm-5"));
		WebElement adultBox = b.findElement(By.cssSelector("input#adultPaxBox"));
		b.doubleClick(adultBox);
		b.sendKeys(adultBox, "" + adults);
		WebElement childrenBox = b.findElement(By.cssSelector("input#childPaxBox"));
		b.doubleClick(childrenBox);
		b.sendKeys(childrenBox, "" + children);
		WebElement infantBox = b.findElement(By.cssSelector("input#infantPaxBox"));
		b.doubleClick(infantBox);
		b.sendKeys(infantBox, "" + infants);
		b.selectFromDropDown(b.findElement(By.id("gi_class")), travelClass);
		b.click(By.id("gi_search_btn"));
		b.findElement(By.cssSelector("div[data-cy^='flightItem_'"));
		b.scrollToPageBottom(20);
		System.out.println(b.findElements(By.cssSelector("div[data-cy^='flightItem_'")).size());
		b.quit();
	}
}
