package examples;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import browser.Browser;

public class Example {
	public static void main(String[] args) throws InterruptedException {
		Browser b = new Browser("chrome");
		b.maximize();
		b.goTo("https://money.rediff.com/gainers/bse/daily/groupa?src=gain_lose");
		List<WebElement> parents = b.findElements(By.cssSelector("table.dataTable tr"));
		for(WebElement parent:parents) {
			List<WebElement>children = b.findElements(parent, By.cssSelector("td"));
			for(WebElement child:children)
				System.out.print(b.getText(child) + "\t");
			System.out.print("\n");
		}
		Thread.sleep(2000);
		b.quit();
	}
}
