package examples;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import browser.Browser;

public class Example {
	public static Browser b;

	public static void main(String[] args) throws InterruptedException {
		b = new Browser("chrome");
		b.maximize();
		b.goTo("https://jqueryui.com/slider/#colorpicker");
		String currentWindow = b.getWindowHandle();
		WebElement iFrame = b.findElement(By.className("demo-frame"));
		b.scrollIntoView(iFrame);
		b.switchToFrame(iFrame);
		selectColor("green", 0);
		selectColor("red", 50);
		selectColor("blue", 250);
		b.switchToWindow(currentWindow);
		b.click(By.xpath("//a[text()='Droppable']"));
		iFrame = b.findElement(By.className("demo-frame"));
		b.scrollIntoView(iFrame);
		b.switchToFrame(iFrame);
		b.dragAndDrop(b.findElement(By.id("draggable")), b.findElement(By.id("droppable")));
		Thread.sleep(5000);
		b.quit();
	}

	public static void selectColor(String color, int value) {
		WebElement sliderParent = b.findElement(By.cssSelector("div#" + color));
		WebElement sliderRange = b.findElement(By.cssSelector("div#" + color + " div.ui-slider-range"));
		WebElement sliderHandle = b.findElement(By.cssSelector("div#" + color + " span.ui-slider-handle"));
		int maxWidth = sliderParent.getRect().getWidth();
		int currentWidth = sliderRange.getRect().getWidth();
		int slideAmount = (int) (value * maxWidth / 255);
		System.out.println(maxWidth + "--" + slideAmount);
		b.dragAndDrop(sliderHandle, -1 * currentWidth, 0);
		b.dragAndDrop(sliderHandle, slideAmount, 0);
	}
}
