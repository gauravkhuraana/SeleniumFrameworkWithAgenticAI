package com.automation.framework.utils;

import com.automation.framework.config.ConfigurationManager;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Utility class for handling various wait operations in Selenium
 * Provides methods for explicit waits, fluent waits, and custom wait conditions
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class WaitUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(WaitUtils.class);
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final ConfigurationManager config;
    
    /**
     * Constructor to initialize WaitUtils
     * @param driver WebDriver instance
     */
    public WaitUtils(WebDriver driver) {
        this.driver = driver;
        this.config = ConfigurationManager.getInstance();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        logger.debug("WaitUtils initialized with explicit wait timeout: {} seconds", config.getExplicitWait());
    }
    
    /**
     * Wait for element to be visible
     * @param locator Element locator
     * @return WebElement when visible
     */
    public WebElement waitForElementToBeVisible(By locator) {
        logger.debug("Waiting for element to be visible: {}", locator);
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element is now visible: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be visible: {}", locator, e);
            throw new TimeoutException("Element not visible within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be visible with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout in seconds
     * @return WebElement when visible
     */
    public WebElement waitForElementToBeVisible(By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be visible with custom timeout {}: {}", timeoutInSeconds, locator);
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        try {
            WebElement element = customWait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            logger.debug("Element is now visible: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be visible: {}", locator, e);
            throw new TimeoutException("Element not visible within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for elements to be visible
     * @param locator Element locator
     * @return List of WebElements when visible
     */
    public List<WebElement> waitForElementsToBeVisible(By locator) {
        logger.debug("Waiting for elements to be visible: {}", locator);
        try {
            List<WebElement> elements = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
            logger.debug("Elements are now visible. Count: {}, Locator: {}", elements.size(), locator);
            return elements;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for elements to be visible: {}", locator, e);
            throw new TimeoutException("Elements not visible within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be clickable
     * @param locator Element locator
     * @return WebElement when clickable
     */
    public WebElement waitForElementToBeClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element is now clickable: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be clickable: {}", locator, e);
            throw new TimeoutException("Element not clickable within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be clickable with custom timeout
     * @param locator Element locator
     * @param timeoutInSeconds Custom timeout in seconds
     * @return WebElement when clickable
     */
    public WebElement waitForElementToBeClickable(By locator, int timeoutInSeconds) {
        logger.debug("Waiting for element to be clickable with custom timeout {}: {}", timeoutInSeconds, locator);
        WebDriverWait customWait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds));
        try {
            WebElement element = customWait.until(ExpectedConditions.elementToBeClickable(locator));
            logger.debug("Element is now clickable: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be clickable: {}", locator, e);
            throw new TimeoutException("Element not clickable within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be present in DOM
     * @param locator Element locator
     * @return WebElement when present
     */
    public WebElement waitForElementToBePresent(By locator) {
        logger.debug("Waiting for element to be present: {}", locator);
        try {
            WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
            logger.debug("Element is now present: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be present: {}", locator, e);
            throw new TimeoutException("Element not present within timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for element to disappear/become invisible
     * @param locator Element locator
     * @return true when element is no longer visible
     */
    public boolean waitForElementToDisappear(By locator) {
        logger.debug("Waiting for element to disappear: {}", locator);
        try {
            boolean disappeared = wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
            logger.debug("Element has disappeared: {}", locator);
            return disappeared;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to disappear: {}", locator, e);
            throw new TimeoutException("Element still visible after timeout: " + locator, e);
        }
    }
    
    /**
     * Wait for text to be present in element
     * @param locator Element locator
     * @param text Text to wait for
     * @return true when text is present
     */
    public boolean waitForTextToBePresent(By locator, String text) {
        logger.debug("Waiting for text '{}' to be present in element: {}", text, locator);
        try {
            boolean textPresent = wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
            logger.debug("Text '{}' is now present in element: {}", text, locator);
            return textPresent;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for text '{}' in element: {}", text, locator, e);
            throw new TimeoutException("Text not present within timeout: " + text, e);
        }
    }
    
    /**
     * Wait for element attribute to contain specific value
     * @param locator Element locator
     * @param attribute Attribute name
     * @param value Value to wait for
     * @return true when attribute contains value
     */
    public boolean waitForAttributeToContain(By locator, String attribute, String value) {
        logger.debug("Waiting for attribute '{}' to contain '{}' in element: {}", attribute, value, locator);
        try {
            boolean attributeContains = wait.until(ExpectedConditions.attributeContains(locator, attribute, value));
            logger.debug("Attribute '{}' now contains '{}' in element: {}", attribute, value, locator);
            return attributeContains;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for attribute '{}' to contain '{}' in element: {}", attribute, value, locator, e);
            throw new TimeoutException("Attribute value not found within timeout: " + value, e);
        }
    }
    
    /**
     * Wait for page title to contain specific text
     * @param title Title text to wait for
     * @return true when title contains text
     */
    public boolean waitForTitleToContain(String title) {
        logger.debug("Waiting for page title to contain: {}", title);
        try {
            boolean titleContains = wait.until(ExpectedConditions.titleContains(title));
            logger.debug("Page title now contains: {}", title);
            return titleContains;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for title to contain: {}", title, e);
            throw new TimeoutException("Title not found within timeout: " + title, e);
        }
    }
    
    /**
     * Wait for URL to contain specific text
     * @param urlPart URL part to wait for
     * @return true when URL contains text
     */
    public boolean waitForUrlToContain(String urlPart) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        try {
            boolean urlContains = wait.until(ExpectedConditions.urlContains(urlPart));
            logger.debug("URL now contains: {}", urlPart);
            return urlContains;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for URL to contain: {}", urlPart, e);
            throw new TimeoutException("URL part not found within timeout: " + urlPart, e);
        }
    }
    
    /**
     * Wait for alert to be present
     * @return Alert when present
     */
    public Alert waitForAlert() {
        logger.debug("Waiting for alert to be present");
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            logger.debug("Alert is now present");
            return alert;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for alert", e);
            throw new TimeoutException("Alert not present within timeout", e);
        }
    }
    
    /**
     * Wait for page to load completely using JavaScript
     */
    public void waitForPageToLoad() {
        logger.debug("Waiting for page to load completely");
        try {
            wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                return js.executeScript("return document.readyState").equals("complete");
            });
            logger.debug("Page has loaded completely");
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for page to load", e);
            throw new TimeoutException("Page did not load within timeout", e);
        }
    }
    
    /**
     * Wait for jQuery to complete (if jQuery is present)
     */
    public void waitForJQueryToComplete() {
        logger.debug("Waiting for jQuery to complete");
        try {
            wait.until(driver -> {
                JavascriptExecutor js = (JavascriptExecutor) driver;
                try {
                    return (Boolean) js.executeScript("return jQuery.active == 0");
                } catch (WebDriverException e) {
                    // jQuery not present, assume complete
                    return true;
                }
            });
            logger.debug("jQuery operations completed");
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for jQuery to complete", e);
            throw new TimeoutException("jQuery operations did not complete within timeout", e);
        }
    }
    
    /**
     * Wait for element to be enabled
     * @param locator Element locator
     * @return WebElement when enabled
     */
    public WebElement waitForElementToBeEnabled(By locator) {
        logger.debug("Waiting for element to be enabled: {}", locator);
        try {
            WebElement element = wait.until(driver -> {
                WebElement el = driver.findElement(locator);
                return el.isEnabled() ? el : null;
            });
            logger.debug("Element is now enabled: {}", locator);
            return element;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for element to be enabled: {}", locator, e);
            throw new TimeoutException("Element not enabled within timeout: " + locator, e);
        }
    }
    
    /**
     * Create fluent wait with custom settings
     * @param timeoutInSeconds Total timeout in seconds
     * @param pollingIntervalInSeconds Polling interval in seconds
     * @return FluentWait instance
     */
    public Wait<WebDriver> createFluentWait(int timeoutInSeconds, int pollingIntervalInSeconds) {
        logger.debug("Creating fluent wait with timeout: {}s, polling: {}s", timeoutInSeconds, pollingIntervalInSeconds);
        return new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(timeoutInSeconds))
                .pollingEvery(Duration.ofSeconds(pollingIntervalInSeconds))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
    }
    
    /**
     * Sleep for specified duration (use sparingly)
     * @param milliseconds Duration in milliseconds
     */
    public void sleep(long milliseconds) {
        logger.debug("Sleeping for {} milliseconds", milliseconds);
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            logger.error("Sleep interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Wait for number of windows to be specific count
     * @param expectedNumberOfWindows Expected number of windows
     * @return true when window count matches
     */
    public boolean waitForNumberOfWindows(int expectedNumberOfWindows) {
        logger.debug("Waiting for number of windows to be: {}", expectedNumberOfWindows);
        try {
            boolean windowCountMatches = wait.until(ExpectedConditions.numberOfWindowsToBe(expectedNumberOfWindows));
            logger.debug("Number of windows is now: {}", expectedNumberOfWindows);
            return windowCountMatches;
        } catch (TimeoutException e) {
            logger.error("Timeout waiting for {} windows", expectedNumberOfWindows, e);
            throw new TimeoutException("Window count did not match within timeout: " + expectedNumberOfWindows, e);
        }
    }
}
