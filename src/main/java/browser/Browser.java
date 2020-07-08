package browser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
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
import utils.Utility;

public class Browser {
	private WebDriver driver;
	private Actions actions;
	private JavascriptExecutor jse;

	public Browser(String browser) {
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
			actions = new Actions(driver);
			jse = (JavascriptExecutor) driver;
		}
	}

	/**
	 * This method is used to maximize the browser window opened by the WebDriver
	 */
	public void maximize() {
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
	 * This method is used to navigate to the provided URL
	 * 
	 * @param url - url of the website to navigate to
	 */
	public void goTo(String url) {
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
		Alert alert;
		try {
			alert = driver.switchTo().alert();
		} catch (Exception e) {
			alert = null;
			e.printStackTrace();
		}
		return alert;
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
		return driver.getTitle();
	}

	/**
	 * This method is used to get the identification property of an object stored in
	 * the Object Repository
	 * 
	 * @param objectName - name of the object as stored in the Object Repository
	 * @return - reference to the object locator
	 */
	public By getStoredObjectProperty(String objectName) {
		By by = null;
		String orPath = Utility.getAbsoluteProjectPaths("or");
		try {
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
			System.err.println("The object \"" + objectName + "\" could not be found in the OR");
			e.printStackTrace();
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
			System.err.println("Could not find element having null locator!!!");
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
			System.err.println("Could not find element having null locator!!!");
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
			System.err.println("Cannot find child elements of a null object");
		} else if (by == null) {
			children = null;
			System.err.println("Cannot find child elements with null object locator");
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
	public WebElement findElement(String objectName) {
		By by = getStoredObjectProperty(objectName);
		return findElement(by);
	}

	/**
	 * This method is used to write a text in a WebElement after clearing its
	 * existing contents
	 * 
	 * @param element - reference to the Object on screen
	 * @param text    - text to be written to the WebElement
	 */
	public void sendKeys(WebElement element, String text) {
		if (element != null) {
			element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
			element.sendKeys(text);
		} else
			System.err.println("Cannot perform sendKeys operation on a null object!!!");
	}

	/**
	 * This method is used to write a text in a WebElement
	 * 
	 * @param by   - reference to the Object Locator
	 * @param text - text to be written to the WebElement
	 */
	public void sendKeys(By by, String text) {
		WebElement element = findElement(by);
		sendKeys(element, text);
	}

	/**
	 * This method is used to write a text in a WebElement
	 * 
	 * @param objectName - name of the object as stored in the OR
	 * @param text       - text to be written to the WebElement
	 */
	public void sendKeys(String objectName, String text) {
		By by = getStoredObjectProperty(objectName);
		sendKeys(by, text);
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param element - reference to the Object
	 */
	public void click(WebElement element) {
		if (element == null)
			System.err.println("Cannot click on a null object!!!");
		else
			element.click();
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param by - reference to the Object locator
	 */
	public void click(By by) {
		WebElement element = findElement(by);
		click(element);
	}

	/**
	 * This method is used to perform left click operation on an Object
	 * 
	 * @param objectName- name of the object as stored in the OR
	 */
	public void click(String objectName) {
		By by = getStoredObjectProperty(objectName);
		click(by);
	}

	/**
	 * This method is used to move the mouse to the middle of an Object
	 * 
	 * @param element - reference to the Object
	 */
	public void hoverMouse(WebElement element) {
		if (element == null)
			System.err.println("Cannot move the mouse to a null object!!!");
		else
			actions.moveToElement(element).build().perform();
	}

	/**
	 * This method is used to perform the double click operation on an object
	 * 
	 * @param element - reference to the Object
	 */
	public void doubleClick(WebElement element) {
		if (element == null)
			System.err.println("Cannot perform double-click on a null object!!!");
		else
			actions.doubleClick(element).build().perform();
	}

	/**
	 * This method is used to scroll down to the bottom of the page
	 */
	public void scrollToPageBottom() {
		jse.executeScript("window.scrollTo(0,document.body.scrollHeight)");
	}

	/**
	 * This method is used to scroll to the bottom of a page by taking small steps
	 * 
	 * @param stepSize - the pixel size of each step
	 */
	public void scrollToPageBottom(long stepSize) {
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
	 * This method is used to move the mouse to the middle of an Object
	 * 
	 * @param by - reference to the Object locator
	 */
	public void hoverMouse(By by) {
		WebElement element = findElement(by);
		hoverMouse(element);
	}

	/**
	 * This method is used to get the visible (i.e. not hidden by CSS) text of this
	 * element, including sub-elements.
	 * 
	 * @param element - reference to the Object
	 * @return - the visible text of the Object
	 */
	public String getText(WebElement element) {
		String text = "";
		if (element == null)
			System.err.println("Cannot fetch text of a null object!!!");
		else
			text = element.getText();
		return text;
	}

	/**
	 * This method is used to get the value of an attribute/property of an object
	 * 
	 * @param element   - reference to the WebElement
	 * @param attribute - name of the attribute or property whose value is required
	 * @return - The attribute/property's current value or null if the value is not
	 *         set.
	 */
	public String getAttribute(WebElement element, String attribute) {
		String attributeValue = "";
		if (element == null)
			System.err.println("Cannot fetch attribute values of a null object");
		else
			attributeValue = element.getAttribute(attribute);
		return attributeValue;
	}

	/**
	 * This method is used to select an option from a Drop-Down list
	 * 
	 * @param element        - reference to the DropDown object
	 * @param optionToSelect - value/visible-text to be selected
	 */
	public void selectFromDropDown(WebElement element, String optionToSelect) {
		if (element == null)
			System.err.println("Cannot perform select operation on a null object!!!");
		else {
			Select dropDown = new Select(element);
			try {
				dropDown.selectByVisibleText(optionToSelect);
			} catch (NoSuchElementException e1) {
				try {
					dropDown.selectByValue(optionToSelect);
				} catch (NoSuchElementException e2) {
					System.err.println("The option \"" + optionToSelect + "\"could not be selected from the dropdown");
				}
			}
		}
	}

	/**
	 * This method is used to switch to a new tab/window opened by the webDriver
	 * 
	 * @param windowHandle
	 */
	public void switchToWindow(String windowHandle) {
		driver.switchTo().window(windowHandle);
	}

	/**
	 * This method is used to close the currently active window which is opened by
	 * the WebDriver
	 */
	public void close() {
		driver.close();
	}

	/**
	 * This method is used to quit the browser by closing all the windows opened by
	 * the WebDriver
	 */
	public void quit() {
		driver.quit();
	}
}
