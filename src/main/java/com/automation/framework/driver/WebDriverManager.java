package com.automation.framework.driver;

import com.automation.framework.config.ConfigurationManager;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * WebDriver Manager for thread-safe WebDriver instance management
 * Uses ThreadLocal to ensure each thread has its own WebDriver instance
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class WebDriverManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDriverManager.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    
    // ThreadLocal to store WebDriver instances for each thread
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();
    
    /**
     * Private constructor to prevent instantiation
     */
    private WebDriverManager() {
        throw new UnsupportedOperationException("WebDriverManager should not be instantiated");
    }
    
    /**
     * Get WebDriver instance for current thread
     * Creates new instance if not exists
     * 
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver == null) {
            logger.warn("WebDriver instance is null for thread: {}. Creating new instance.", 
                       Thread.currentThread().getName());
            setDriver(config.getBrowser());
            driver = driverThreadLocal.get();
        }
        return driver;
    }
    
    /**
     * Set WebDriver instance for current thread using default browser
     */
    public static void setDriver() {
        setDriver(config.getBrowser());
    }
    
    /**
     * Set WebDriver instance for current thread with specific browser
     * 
     * @param browser Browser name
     */
    public static void setDriver(String browser) {
        if (driverThreadLocal.get() != null) {
            logger.warn("WebDriver instance already exists for thread: {}. Closing existing instance.", 
                       Thread.currentThread().getName());
            quitDriver();
        }
        
        logger.info("Creating WebDriver instance for thread: {} with browser: {}", 
                   Thread.currentThread().getName(), browser);
        
        WebDriver driver = WebDriverFactory.createDriver(browser);
        driverThreadLocal.set(driver);
        
        logger.info("WebDriver instance created and set for thread: {}", 
                   Thread.currentThread().getName());
    }
    
    /**
     * Check if WebDriver instance exists for current thread
     * 
     * @return true if driver exists, false otherwise
     */
    public static boolean hasDriver() {
        return driverThreadLocal.get() != null;
    }
    
    /**
     * Quit WebDriver instance for current thread
     * Safely closes the browser and removes from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Quitting WebDriver instance for thread: {}", 
                           Thread.currentThread().getName());
                driver.quit();
                logger.info("WebDriver instance quit successfully for thread: {}", 
                           Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("Error occurred while quitting WebDriver for thread: {}", 
                           Thread.currentThread().getName(), e);
            } finally {
                driverThreadLocal.remove();
                logger.debug("WebDriver instance removed from ThreadLocal for thread: {}", 
                           Thread.currentThread().getName());
            }
        } else {
            logger.debug("No WebDriver instance to quit for thread: {}", 
                        Thread.currentThread().getName());
        }
    }
    
    /**
     * Close current browser window (not entire WebDriver session)
     * Useful when multiple windows/tabs are open
     */
    public static void closeCurrentWindow() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                logger.info("Closing current browser window for thread: {}", 
                           Thread.currentThread().getName());
                driver.close();
                logger.info("Current browser window closed successfully for thread: {}", 
                           Thread.currentThread().getName());
            } catch (Exception e) {
                logger.error("Error occurred while closing current window for thread: {}", 
                           Thread.currentThread().getName(), e);
            }
        } else {
            logger.warn("No WebDriver instance available to close window for thread: {}", 
                       Thread.currentThread().getName());
        }
    }
    
    /**
     * Get current thread name for logging purposes
     * 
     * @return Current thread name
     */
    public static String getCurrentThreadName() {
        return Thread.currentThread().getName();
    }
    
    /**
     * Get driver session information
     * 
     * @return Driver session information as string
     */
    public static String getDriverInfo() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                return String.format("Thread: %s, Driver: %s, Current URL: %s", 
                                    Thread.currentThread().getName(),
                                    driver.getClass().getSimpleName(),
                                    driver.getCurrentUrl());
            } catch (Exception e) {
                return String.format("Thread: %s, Driver: %s, Error getting URL: %s", 
                                    Thread.currentThread().getName(),
                                    driver.getClass().getSimpleName(),
                                    e.getMessage());
            }
        }
        return String.format("Thread: %s, Driver: null", Thread.currentThread().getName());
    }
    
    /**
     * Force cleanup of all WebDriver instances
     * Should be used in emergency situations or test cleanup hooks
     */
    public static void forceCleanup() {
        logger.warn("Force cleanup initiated for thread: {}", Thread.currentThread().getName());
        try {
            quitDriver();
        } catch (Exception e) {
            logger.error("Error during force cleanup for thread: {}", 
                        Thread.currentThread().getName(), e);
        }
    }
    
    /**
     * Restart WebDriver with same browser
     * Useful for tests that need a fresh browser session
     */
    public static void restartDriver() {
        String currentBrowser = config.getBrowser();
        restartDriver(currentBrowser);
    }
    
    /**
     * Restart WebDriver with specific browser
     * 
     * @param browser Browser name
     */
    public static void restartDriver(String browser) {
        logger.info("Restarting WebDriver for thread: {} with browser: {}", 
                   Thread.currentThread().getName(), browser);
        quitDriver();
        setDriver(browser);
        logger.info("WebDriver restarted successfully for thread: {}", 
                   Thread.currentThread().getName());
    }
}
