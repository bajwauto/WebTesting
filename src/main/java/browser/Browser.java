package browser;

import static log.Log.info;
import static log.Log.warn;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;
import properties.Property;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.coordinates.WebDriverCoordsProvider;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import utils.Utility;

public class Browser {
	private WebDriver driver;
	private Actions actions;
	private JavascriptExecutor jse;

	public Browser(String browser) {
		info("Launching the \"" + browser + "\" Browser");
		switch (browser.trim().toLowerCase()) {
		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			break;
		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			break;
		case "chrome":
		default:
			ChromeOptions options = new ChromeOptions();
			options.addArguments("--disable-notifications");
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver(options);
		}
		actions = new Actions(driver);
		jse = (JavascriptExecutor) driver;
	}

	/**
	 * This method is used to maximize the browser window opened by the WebDriver
	 */
	public void maximize() {
		info("Maximizing browser window");
		driver.manage().window().maximize();
	}

	/**
	 * This method is used to set the implicit wait timeout. Specifies the amount of
	 * time the driver should wait when searching for an element if it is not
	 * immediately present.
	 * 
	 * @param time - the amount of time to wait
	 * @param unit - the unit of time
	 */
	public void setImplicitTimeout(long time, TimeUnit unit) {
		driver.manage().timeouts().implicitlyWait(time, unit);
	}

	/**
	 * This method is used to delete all the cookies of the current domain
	 */
	public void deleteAllCookies() {
		info("Deleting all the cookies");
		driver.manage().deleteAllCookies();
	}

	/**
	 * This method is used to navigate to the provided URL
	 * 
	 * @param url - url of the website to navigate to
	 */
	public void goTo(String url) {
		info("Navigating to URL - " + url);
		driver.get(url);
	}

	/**
	 * This method is used to get the window handle for the currently active window
	 * 
	 * @return String containing the window handle for the currently active window
	 */
	public String getWindowHandle() {
		return driver.getWindowHandle();
	}

	/**
	 * This method is used to get the window handles for all the windows opened by
	 * the WebDriver
	 * 
	 * @return a Set containing the window handles of all the windows opened by the
	 *         WebDriver
	 */
	public Iterator<String> getWindowHandles() {
		return driver.getWindowHandles().iterator();
	}

	/**
	 * This method is used to switch to an alert
	 * 
	 * @return - A handle to the dialog
	 */
	private Alert switchToAlert() {
		info("Switching to the Alert");
		Alert alert;
		try {
			alert = driver.switchTo().alert();
		} catch (Exception e) {
			alert = null;
			warn("Could not switch to the Alert");
			e.printStackTrace();
		}
		return alert;
	}

	/**
	 * This method is used to switch to a frame
	 * 
	 * @param frameElement - reference to the frame
	 */
	public void switchToFrame(WebElement frameElement) {
		info("Switching to the frame - " + frameElement.toString());
		driver.switchTo().frame(frameElement);
	}

	/**
	 * This method is used to switch to a new tab/window opened by the webDriver
	 * 
	 * @param windowHandle - window handle of the window to switch to
	 */
	public void switchToWindow(String windowHandle) {
		info("Switching to the window with window handle - " + windowHandle);
		driver.switchTo().window(windowHandle);
	}

	/**
	 * This method is used to get the text from the alert
	 * 
	 * @return - the text present in the alert
	 */
	public String getAlertText() {
		String alertText;
		Alert alert = switchToAlert();
		if (alert == null)
			alertText = null;
		else
			alertText = alert.getText();
		info("Alert text fetched - " + alertText);
		return alertText;
	}

	/**
	 * This method is used to accept or dismiss an alert
	 * 
	 * @param action - action to perform on the alert. Acceptable values: accept,
	 *               dismiss
	 */
	public void manageAlert(String action) {
		Alert alert = switchToAlert();
		info("Performing action \"" + action + "\" on the alert");
		if (alert == null)
			System.err.println("Cannot perform action on null object(alert)");
		else {
			switch (action.trim().toLowerCase()) {
			case "accept":
				alert.accept();
				break;
			case "dismiss":
				alert.dismiss();
				break;
			}
		}
	}

	/**
	 * This method is used to get the title of the currently active window
	 * 
	 * @return - title of the currently active window
	 */
	public String getTitle() {
		String pageTitle = driver.getTitle();
		info("Page title fetched - " + pageTitle);
		return pageTitle;
	}

	/**
	 * This method is used to get the identification property of an object stored in
	 * the Object Repository
	 * 
	 * @param objectName - name of the object as stored in the Object Repository
	 * @return - reference to the object locator
	 * @throws Exception
	 */
	public By getStoredObjectProperty(String objectName) throws Exception {
		By by = null;
		try {
			String orPath = Utility.getAbsoluteProjectPaths("or");
			String propertyValuePair = Property.read(orPath, objectName);
			String propertyName = propertyValuePair.split("\\s*:\\s*")[0];
			String propertyValue = propertyValuePair.split("\\s*:\\s*")[1];
			switch (propertyName.toLowerCase()) {
			case "id":
				by = By.id(propertyValue);
				break;
			case "xpath":
				by = By.xpath(propertyValue);
				break;
			case "cssselector":
				by = By.cssSelector(propertyValue);
				break;
			case "name":
				by = By.name(propertyValue);
				break;
			case "classname":
				by = By.className(propertyValue);
				break;
			case "tagname":
				by = By.tagName(propertyValue);
				break;
			case "linktext":
				by = By.linkText(propertyValue);
				break;
			case "partiallinktext":
				by = By.partialLinkText(propertyValue);
				break;
			}
		} catch (Exception e) {
			String errorMessage = "The object \"" + objectName + "\" could not be found in the OR";
			warn(errorMessage);
			throw new Exception(errorMessage);
		}
		return by;
	}

	/**
	 * This method is used to find an object on the screen
	 * 
	 * @param by - reference to the Object locator
	 * @return - reference to the webElement, if found; else, null
	 */
	public WebElement findElement(By by) {
		WebElement element;
		if (by == null) {
			element = null;
			warn("Could not find element having null locator!!!");
		} else {
			WebDriverWait wait = new WebDriverWait(driver, 20);
			try {
				element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			} catch (Exception e) {
				element = null;
				e.printStackTrace();
			}
		}
		return element;
	}

	/**
	 * This method is used to get all the objects present on a screen identified by
	 * the specified locator
	 * 
	 * @param by - reference to the Object locator
	 * @return - List of all the objects present on the screen identified by the
	 *         specified locator
	 */
	public List<WebElement> findElements(By by) {
		List<WebElement> elements = new ArrayList<WebElement>();
		if (by == null) {
			elements = null;
			warn("Could not find element having null locator!!!");
		} else {
			WebDriverWait wait = new WebDriverWait(driver, 20);
			try {
				elements = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
			} catch (Exception e) {
				elements = null;
				e.printStackTrace();
			}
		}
		return elements;
	}

	/**
	 * This method is used to find the child objects of a parent object on the basis
	 * of provided locator
	 * 
	 * @param parentElement - reference to the parent object(loaded) on the screen
	 * @param by            - reference to the Child object locator
	 * @return - list of all the child objects found by using the locator
	 */
	public List<WebElement> findElements(WebElement parentElement, By by) {
		List<WebElement> children = new ArrayList<WebElement>();
		if (parentElement == null) {
			children = null;
			warn("Cannot find child elements of a null object");
		} else if (by == null) {
			children = null;
			warn("Cannot find child elements with null object locator");
		} else {
			children = parentElement.findElements(by);
		}
		return children;
	}

	/**
	 * This method is used to find the object stored in the Object repository on the
	 * screen
	 * 
	 * @param objectName - name of the object as mentioned in the OR
	 * @return - reference to the WebElement, if found; else, null
	 */
	public WebElement findElement(String objectName) throws Exception {
		By by = getStoredObjectProperty(objectName);
		return findElement(by);
	}

	/**
	 * This method is used to write a text in a WebElement after clearing its
	 * existing contents
	 * 
	 * @param element - reference to the Object on screen
	 * @param text    - text to be written to the WebElement
	 * @throws Exception
	 */
	public void sendKeys(WebElement element, String text) throws Exception {
		if (element != null) {
			info("Sending text \"" + text + "\" to the object - " + element.toString());
			element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			element.sendKeys(text);
		} else {
			warn("Cannot perform sendKeys operation on a null object!!!");
			throw new Exception("Cannot perform sendKeys operation on a null object!!!");
		}
	}

	/**
	 * This method is used to write a text in a WebElement
	 * 
	 * @param by   - reference to the Object Locator
	 * @param text - text to be written to the WebElement
	 * @throws Exception
	 */
	public void sendKeys(By by, String text) throws Exception {
		WebElement element = findElement(by);
		sendKeys(element, text);
	}

	/**
	 * This method is used to write a text in a WebElement
	 * 
	 * @param objectName - name of the object as stored in the OR
	 * @param text       - text to be written to the WebElement
	 * @throws Exception
	 */
	public void sendKeys(String objectName, String text) throws Exception {
		By by = getStoredObjectProperty(objectName);
		sendKeys(by, text);
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param element - reference to the Object
	 */
	public void click(WebElement element) throws Exception {
		if (element == null) {
			warn("Cannot click on a null object!!!");
			throw new Exception("Cannot click on a null object!!!");
		} else {
			info("Clicking on the object - " + element.toString());
			element.click();
		}
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param by - reference to the Object locator
	 * @throws Exception
	 */
	public void click(By by) throws Exception {
		WebElement element = findElement(by);
		click(element);
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param objectName- name of the object as stored in the OR
	 * @throws Exception
	 */
	public void click(String objectName) throws Exception {
		By by = getStoredObjectProperty(objectName);
		click(by);
	}

	/**
	 * This method is used to move the mouse to the middle of an Object
	 * 
	 * @param element - reference to the Object
	 * @throws Exception
	 */
	public void hoverMouse(WebElement element) throws Exception {
		if (element == null) {
			warn("Cannot move the mouse to a null object!!!");
			throw new Exception("Cannot move the mouse to a null object!!!");
		} else {
			info("Hovering the mouse pointer on top of the object - " + element.toString());
			actions.moveToElement(element).build().perform();
		}
	}

	/**
	 * This method is used to move the mouse to the middle of an Object
	 * 
	 * @param by - reference to the Object locator
	 * @throws Exception
	 */
	public void hoverMouse(By by) throws Exception {
		WebElement element = findElement(by);
		hoverMouse(element);
	}

	/**
	 * This method is used to perform the double click operation on an object
	 * 
	 * @param element - reference to the Object
	 * @throws Exception
	 */
	public void doubleClick(WebElement element) throws Exception {
		if (element == null) {
			warn("Cannot perform double-click on a null object!!!");
			throw new Exception("Cannot perform double-click on a null object!!!");
		} else {
			info("Double-clicking on the object - " + element.toString());
			actions.doubleClick(element).build().perform();
		}
	}

	/**
	 * This method is used to drag an object and drop it to a point which is xOffset
	 * pixels and yOffset pixels away from the current point along the x-axis and
	 * y-axis respectively
	 * 
	 * @param element - reference to the WebElement
	 * @param xOffset - pixels to move along the x-axis
	 * @param yOffset - pixels to move along the y-axis
	 */
	public void dragAndDrop(WebElement element, int xOffset, int yOffset) {
		info("Dragging and Dropping the object \"" + element.toString() + "\" to a point which is (" + xOffset + ","
				+ yOffset + ") pixels relative to the current position");
		actions.dragAndDropBy(element, xOffset, yOffset).build().perform();
	}

	/**
	 * This method is used to drag a source element and drop it on top of a target
	 * element
	 * 
	 * @param sourceElement - object to be dragged
	 * @param targetElement - object on which the source object is to be dropped
	 */
	public void dragAndDrop(WebElement sourceElement, WebElement targetElement) {
		info("Dragging the object \"" + sourceElement.toString() + "\" and dropping it on top of the object \""
				+ targetElement.toString() + "\"");
		actions.dragAndDrop(sourceElement, targetElement).build().perform();
//		actions.clickAndHold(sourceElement).moveToElement(targetElement).build().perform();
	}

	/**
	 * This method is used to scroll down to the bottom of the page
	 */
	public void scrollToPageBottom() {
		info("Scrolling to the page bottom in one-go");
		jse.executeScript("window.scrollTo(0,document.body.scrollHeight)");
	}

	/**
	 * This method is used to scroll to the top of the page
	 */
	public void scrollToPageTop() {
		info("Scrolling to the page top in one-go");
		jse.executeScript("window.scrollTo(0,0)");
	}

	/**
	 * This method is used to scroll to the bottom of a page by taking small steps
	 * 
	 * @param stepSize - the pixel size of each step
	 */
	public void scrollToPageBottom(long stepSize) {
		info("Scrolling to the page bottom by taking small steps(of size " + stepSize + " pixels)");
		Double lastScrollPosition, currentScrollPosition;
		currentScrollPosition = ((Number) jse.executeScript("return window.pageYOffset")).doubleValue();
		lastScrollPosition = currentScrollPosition - 1;
		while (currentScrollPosition > lastScrollPosition) {
			lastScrollPosition = currentScrollPosition;
			jse.executeScript("window.scrollBy(0," + stepSize + ")");
			currentScrollPosition = ((Number) jse.executeScript("return window.pageYOffset")).doubleValue();
		}
	}

	/**
	 * This method is used to bring an element into view by scrolling
	 * 
	 * @param element - reference to the object to be brought into view
	 */
	public void scrollIntoView(WebElement element) {
		info("Scrolling the page to bring the object \"" + element.toString() + "\" in the visible area");
		jse.executeScript("arguments[0].scrollIntoView(true)", element);
	}

	/**
	 * This method is used to get the visible (i.e. not hidden by CSS) text of this
	 * element, including sub-elements.
	 * 
	 * @param element - reference to the Object
	 * @return - the visible text of the Object
	 * @throws Exception
	 */
	public String getText(WebElement element) throws Exception {
		String text = "";
		if (element == null) {
			warn("Cannot fetch text of a null object!!!");
			throw new Exception("Cannot fetch text of a null object!!!");
		} else {
			text = element.getText();
			info("The text \"" + text + "\" was fetched from the object " + element.toString());
		}
		return text;
	}

	/**
	 * This method is used to get the value of an attribute/property of an object
	 * 
	 * @param element   - reference to the WebElement
	 * @param attribute - name of the attribute or property whose value is required
	 * @return - The attribute/property's current value or null if the value is not
	 *         set.
	 * @throws Exception
	 */
	public String getAttribute(WebElement element, String attribute) throws Exception {
		String attributeValue = "";
		if (element == null) {
			warn("Cannot fetch attribute values of a null object");
			throw new Exception("Cannot fetch attribute values of a null object");
		} else {
			attributeValue = element.getAttribute(attribute);
			info("The value of the attribute(\"" + attribute + "\") is \"" + attributeValue + "\" for the object "
					+ element.toString());
		}
		return attributeValue;
	}

	/**
	 * This method is used to select an option from a Drop-Down list
	 * 
	 * @param element        - reference to the DropDown object
	 * @param optionToSelect - value/visible-text to be selected
	 * @throws Exception
	 */
	public void selectFromDropDown(WebElement element, String optionToSelect) throws Exception {
		if (element == null) {
			warn("Cannot perform select operation on a null object!!!");
			throw new Exception("Cannot perform select operation on a null object!!!");
		} else {
			info("Selecting the option " + optionToSelect + " from the drop-down list - " + element.toString());
			Select dropDown = new Select(element);
			try {
				dropDown.selectByVisibleText(optionToSelect);
			} catch (NoSuchElementException e1) {
				try {
					dropDown.selectByValue(optionToSelect);
				} catch (NoSuchElementException e2) {
					warn("The option \"" + optionToSelect + "\"could not be selected from the dropdown");
					throw new Exception("The option \"" + optionToSelect + "\"could not be selected from the dropdown");
				}
			}
		}
	}

	/**
	 * This method is used to capture the screenshot and save it to the provided
	 * location
	 * 
	 * @param filePath - path to the file where the screenshot is to be saved
	 */
	public void captureScreenshot(String filePath) {
		info("Capturing screenshot of the visible area and saving at path - " + filePath);
		File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			File dest = new File(filePath);
			Utility.createDirectory(dest.getParent());
			FileUtils.copyFile(file, dest);
		} catch (IOException e) {
			warn("Could not capture & save the visible area screenshot at path - " + filePath);
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to capture screenshot of a webElement
	 * 
	 * @param filePath - path to the file where the screenshot is to be saved
	 * @param element  - reference to the Object whose screenshot is to be captured
	 */
	public void captureScreenshot(String filePath, WebElement element) {
		info("Capturing screenshot of the object \"" + element.toString() + "\" and saving at path - " + filePath);
		Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider())
				.shootingStrategy(ShootingStrategies.viewportPasting(500)).takeScreenshot(driver, element);
		try {
			File dest = new File(filePath);
			Utility.createDirectory(dest.getParent());
			ImageIO.write(screenshot.getImage(), "jpg", dest);
		} catch (IOException e) {
			warn("Could not capture & save the screenshot of the object \"" + element.toString() + "\" at path - "
					+ filePath);
		}
	}

	/**
	 * This method is used to highlight an object on screen and take screenshot of
	 * the visible area
	 * 
	 * @param filePath - path to the file where the screenshot is to be saved
	 * @param element  - object to be highlighted/boxed in the screenshot
	 */
	public void captureScreenshotWithHighlightedElement(String filePath, WebElement element) {
		info("Highlighting the object - " + element.toString());
		jse.executeScript("arguments[0].setAttribute('style','border:2px solid red;')", element);
		captureScreenshot(filePath);
		jse.executeScript("arguments[0].style.border='2px white;'", element);
	}

	/**
	 * This method is used to capture the screenshot of the entire webpage by
	 * scrolling down every 1 second, if needed
	 * 
	 * @param filePath - path to the file where the screenshot is to be saved
	 */
	public void capturePageScreenshot(String filePath) {
		info("Capturing screenshot of the entire webpage and saving at path - " + filePath);
		Screenshot screenshot = new AShot().coordsProvider(new WebDriverCoordsProvider())
				.shootingStrategy(ShootingStrategies.viewportPasting(1000)).takeScreenshot(driver);
		try {
			File dest = new File(filePath);
			Utility.createDirectory(dest.getParent());
			ImageIO.write(screenshot.getImage(), "jpg", dest);
		} catch (IOException e) {
			warn("Could not capture & save the screenshot of the entire webpage at path - " + filePath);
			e.printStackTrace();
		}
	}

	/**
	 * This method is used to close the currently active window which is opened by
	 * the WebDriver
	 */
	public void close() {
		info("Closing the active window opened by the WebDriver");
		driver.close();
	}

	/**
	 * This method is used to quit the browser by closing all the windows opened by
	 * the WebDriver
	 */
	public void quit() {
		info("Closing all the windows/tabs opened by the WebDriver");
		driver.quit();
	}
}
