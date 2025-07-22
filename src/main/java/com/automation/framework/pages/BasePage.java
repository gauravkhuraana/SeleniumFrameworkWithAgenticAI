package com.automation.framework.pages;

import com.automation.framework.driver.WebDriverManager;
import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Base Page class implementing common page operations
 * All page objects should extend this class
 * 
 * @author Automation Framework
 * @version 1.0
 */
public abstract class BasePage {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WaitUtils waitUtils;
    protected final Actions actions;
    protected final JavascriptExecutor jsExecutor;
    protected final ConfigurationManager config;
    
    /**
     * Constructor to initialize BasePage
     */
    public BasePage() {
        this.driver = WebDriverManager.getDriver();
        this.waitUtils = new WaitUtils(driver);
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
        this.config = ConfigurationManager.getInstance();
        PageFactory.initElements(driver, this);
        logger.debug("BasePage initialized for: {}", getClass().getSimpleName());
    }
    
    /**
     * Navigate to specific URL
     * @param url URL to navigate to
     */
    public void navigateTo(String url) {
        logger.info("Navigating to URL: {}", url);
        driver.navigate().to(url);
        waitForPageToLoad();
    }
    
    /**
     * Get current page URL
     * @return Current URL
     */
    public String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.debug("Current URL: {}", url);
        return url;
    }
    
    /**
     * Get current page title
     * @return Page title
     */
    public String getTitle() {
        String title = driver.getTitle();
        logger.debug("Page title: {}", title);
        return title;
    }
    
    /**
     * Refresh current page
     */
    public void refreshPage() {
        logger.info("Refreshing page");
        driver.navigate().refresh();
        waitForPageToLoad();
    }
    
    /**
     * Navigate back
     */
    public void navigateBack() {
        logger.info("Navigating back");
        driver.navigate().back();
        waitForPageToLoad();
    }
    
    /**
     * Navigate forward
     */
    public void navigateForward() {
        logger.info("Navigating forward");
        driver.navigate().forward();
        waitForPageToLoad();
    }
    
    /**
     * Wait for page to load completely
     */
    public void waitForPageToLoad() {
        waitUtils.waitForPageToLoad();
    }
    
    /**
     * Find element with wait
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement findElement(By locator) {
        return waitUtils.waitForElementToBeVisible(locator);
    }
    
    /**
     * Find elements with wait
     * @param locator Element locator
     * @return List of WebElements
     */
    protected List<WebElement> findElements(By locator) {
        return waitUtils.waitForElementsToBeVisible(locator);
    }
    
    /**
     * Click element with wait
     * @param locator Element locator
     */
    protected void click(By locator) {
        WebElement element = waitUtils.waitForElementToBeClickable(locator);
        logger.debug("Clicking element: {}", locator);
        element.click();
    }
    
    /**
     * Click element with JavaScript
     * @param locator Element locator
     */
    protected void clickWithJS(By locator) {
        WebElement element = findElement(locator);
        logger.debug("Clicking element with JavaScript: {}", locator);
        jsExecutor.executeScript("arguments[0].click();", element);
    }
    
    /**
     * Type text into element
     * @param locator Element locator
     * @param text Text to type
     */
    protected void type(By locator, String text) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        logger.debug("Typing text '{}' into element: {}", text, locator);
        element.clear();
        element.sendKeys(text);
    }
    
    /**
     * Get text from element
     * @param locator Element locator
     * @return Element text
     */
    protected String getText(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        String text = element.getText();
        logger.debug("Got text '{}' from element: {}", text, locator);
        return text;
    }
    
    /**
     * Get attribute value from element
     * @param locator Element locator
     * @param attribute Attribute name
     * @return Attribute value
     */
    protected String getAttribute(By locator, String attribute) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        String value = element.getAttribute(attribute);
        logger.debug("Got attribute '{}' value '{}' from element: {}", attribute, value, locator);
        return value;
    }
    
    /**
     * Check if element is displayed
     * @param locator Element locator
     * @return true if displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            boolean displayed = findElement(locator).isDisplayed();
            logger.debug("Element {} is displayed: {}", locator, displayed);
            return displayed;
        } catch (Exception e) {
            logger.debug("Element {} is not displayed: {}", locator, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * @param locator Element locator
     * @return true if enabled, false otherwise
     */
    protected boolean isEnabled(By locator) {
        try {
            boolean enabled = findElement(locator).isEnabled();
            logger.debug("Element {} is enabled: {}", locator, enabled);
            return enabled;
        } catch (Exception e) {
            logger.debug("Element {} is not enabled: {}", locator, e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if element is selected
     * @param locator Element locator
     * @return true if selected, false otherwise
     */
    protected boolean isSelected(By locator) {
        try {
            boolean selected = findElement(locator).isSelected();
            logger.debug("Element {} is selected: {}", locator, selected);
            return selected;
        } catch (Exception e) {
            logger.debug("Element {} is not selected: {}", locator, e.getMessage());
            return false;
        }
    }
    
    /**
     * Select dropdown option by visible text
     * @param locator Dropdown locator
     * @param text Visible text to select
     */
    protected void selectByText(By locator, String text) {
        WebElement dropdown = waitUtils.waitForElementToBeVisible(locator);
        Select select = new Select(dropdown);
        logger.debug("Selecting option '{}' from dropdown: {}", text, locator);
        select.selectByVisibleText(text);
    }
    
    /**
     * Select dropdown option by value
     * @param locator Dropdown locator
     * @param value Value to select
     */
    protected void selectByValue(By locator, String value) {
        WebElement dropdown = waitUtils.waitForElementToBeVisible(locator);
        Select select = new Select(dropdown);
        logger.debug("Selecting option with value '{}' from dropdown: {}", value, locator);
        select.selectByValue(value);
    }
    
    /**
     * Select dropdown option by index
     * @param locator Dropdown locator
     * @param index Index to select
     */
    protected void selectByIndex(By locator, int index) {
        WebElement dropdown = waitUtils.waitForElementToBeVisible(locator);
        Select select = new Select(dropdown);
        logger.debug("Selecting option at index '{}' from dropdown: {}", index, locator);
        select.selectByIndex(index);
    }
    
    /**
     * Hover over element
     * @param locator Element locator
     */
    protected void hover(By locator) {
        WebElement element = waitUtils.waitForElementToBeVisible(locator);
        logger.debug("Hovering over element: {}", locator);
        actions.moveToElement(element).perform();
    }
    
    /**
     * Double click element
     * @param locator Element locator
     */
    protected void doubleClick(By locator) {
        WebElement element = waitUtils.waitForElementToBeClickable(locator);
        logger.debug("Double clicking element: {}", locator);
        actions.doubleClick(element).perform();
    }
    
    /**
     * Right click element
     * @param locator Element locator
     */
    protected void rightClick(By locator) {
        WebElement element = waitUtils.waitForElementToBeClickable(locator);
        logger.debug("Right clicking element: {}", locator);
        actions.contextClick(element).perform();
    }
    
    /**
     * Drag and drop element
     * @param sourceLocator Source element locator
     * @param targetLocator Target element locator
     */
    protected void dragAndDrop(By sourceLocator, By targetLocator) {
        WebElement source = waitUtils.waitForElementToBeVisible(sourceLocator);
        WebElement target = waitUtils.waitForElementToBeVisible(targetLocator);
        logger.debug("Dragging element {} to {}", sourceLocator, targetLocator);
        actions.dragAndDrop(source, target).perform();
    }
    
    /**
     * Scroll to element
     * @param locator Element locator
     */
    protected void scrollToElement(By locator) {
        WebElement element = findElement(locator);
        logger.debug("Scrolling to element: {}", locator);
        jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }
    
    /**
     * Scroll to top of page
     */
    protected void scrollToTop() {
        logger.debug("Scrolling to top of page");
        jsExecutor.executeScript("window.scrollTo(0, 0);");
    }
    
    /**
     * Scroll to bottom of page
     */
    protected void scrollToBottom() {
        logger.debug("Scrolling to bottom of page");
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
    
    /**
     * Execute JavaScript
     * @param script JavaScript to execute
     * @param args Arguments for the script
     * @return Script result
     */
    protected Object executeJS(String script, Object... args) {
        logger.debug("Executing JavaScript: {}", script);
        return jsExecutor.executeScript(script, args);
    }
    
    /**
     * Switch to window by title
     * @param title Window title
     */
    protected void switchToWindowByTitle(String title) {
        logger.debug("Switching to window with title: {}", title);
        Set<String> windows = driver.getWindowHandles();
        for (String window : windows) {
            driver.switchTo().window(window);
            if (driver.getTitle().contains(title)) {
                logger.debug("Switched to window: {}", title);
                return;
            }
        }
        logger.warn("Window with title '{}' not found", title);
    }
    
    /**
     * Switch to window by index
     * @param index Window index
     */
    protected void switchToWindow(int index) {
        logger.debug("Switching to window at index: {}", index);
        Set<String> windows = driver.getWindowHandles();
        String[] windowArray = windows.toArray(new String[0]);
        if (index < windowArray.length) {
            driver.switchTo().window(windowArray[index]);
            logger.debug("Switched to window at index: {}", index);
        } else {
            logger.warn("Window at index '{}' not found", index);
        }
    }
    
    /**
     * Close current window and switch to main window
     */
    protected void closeCurrentWindow() {
        logger.debug("Closing current window");
        driver.close();
        switchToWindow(0);
    }
    
    /**
     * Abstract method to verify page is loaded
     * Must be implemented by each page class
     * @return true if page is loaded, false otherwise
     */
    public abstract boolean isPageLoaded();
    
    /**
     * Abstract method to get page URL pattern
     * Must be implemented by each page class
     * @return URL pattern for the page
     */
    public abstract String getPageUrlPattern();
}
