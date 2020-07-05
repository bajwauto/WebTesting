package examples;

import org.openqa.selenium.By;

import browser.Browser;

public class Example {
	public static void main(String[] args) throws InterruptedException {
		Browser b = new Browser("chrome");
		b.maximize();
		b.goTo("http://www.way2automation.com/");
		b.hoverMouse(By.xpath("//a[text()='Resources']"));
		b.click(By.xpath("//a[text()='Practice site 1']"));
		b.sendKeys(By.cssSelector("[name='name']"), "Lala Hardayal");
		b.sendKeys(By.cssSelector("[name='phone']"), "9988776655");
		b.sendKeys(By.cssSelector("[name='email']"), "hardayal@lala.com");
		b.selectFromDropDown(b.findElement(By.cssSelector("[name='country']")), "Nepal");
		b.sendKeys(By.cssSelector("[name='city']"), "Kathmandu");
		b.sendKeys(By.cssSelector("div.fancybox-overlay input[name='username']"), "goldenMan");
		b.sendKeys(By.cssSelector("div.fancybox-overlay input[name='password']"), "testing123");
		b.click(By.cssSelector("div.fancybox-overlay input.button"));
		Thread.sleep(3000);
		b.quit();
	}
}
