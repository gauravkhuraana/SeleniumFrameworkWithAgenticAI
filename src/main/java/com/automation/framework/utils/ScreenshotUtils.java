package com.automation.framework.utils;

import com.automation.framework.driver.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for capturing and managing screenshots
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LoggerFactory.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_DIR = "test-output/screenshots";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss-SSS");
    
    /**
     * Constructor
     */
    public ScreenshotUtils() {
        createScreenshotDirectory();
    }
    
    /**
     * Take screenshot with auto-generated filename
     * @return Screenshot file path
     */
    public String takeScreenshot() {
        return takeScreenshot("screenshot_" + getCurrentTimestamp());
    }
    
    /**
     * Take screenshot with custom filename
     * @param fileName Custom filename (without extension)
     * @return Screenshot file path
     */
    public String takeScreenshot(String fileName) {
        logger.debug("Taking screenshot with filename: {}", fileName);
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver == null) {
                logger.warn("WebDriver is null, cannot take screenshot");
                return null;
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String screenshotPath = SCREENSHOT_DIR + File.separator + fileName + ".png";
            File destFile = new File(screenshotPath);
            
            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot captured successfully: {}", screenshotPath);
            return destFile.getAbsolutePath();
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Take screenshot and save to specific directory
     * @param fileName Custom filename (without extension)
     * @param directory Custom directory path
     * @return Screenshot file path
     */
    public String takeScreenshot(String fileName, String directory) {
        logger.debug("Taking screenshot with filename: {} in directory: {}", fileName, directory);
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver == null) {
                logger.warn("WebDriver is null, cannot take screenshot");
                return null;
            }
            
            // Create directory if it doesn't exist
            File dir = new File(directory);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    logger.warn("Could not create directory: {}", directory);
                }
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            
            String screenshotPath = directory + File.separator + fileName + ".png";
            File destFile = new File(screenshotPath);
            
            FileUtils.copyFile(sourceFile, destFile);
            
            logger.info("Screenshot captured successfully: {}", screenshotPath);
            return destFile.getAbsolutePath();
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot: {}", e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Take screenshot as byte array
     * @return Screenshot as byte array
     */
    public byte[] takeScreenshotAsBytes() {
        logger.debug("Taking screenshot as byte array");
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver == null) {
                logger.warn("WebDriver is null, cannot take screenshot");
                return new byte[0];
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            byte[] screenshot = takesScreenshot.getScreenshotAs(OutputType.BYTES);
            
            logger.debug("Screenshot captured as byte array, size: {} bytes", screenshot.length);
            return screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as bytes: {}", e.getMessage(), e);
            return new byte[0];
        }
    }
    
    /**
     * Take screenshot as base64 string
     * @return Screenshot as base64 string
     */
    public String takeScreenshotAsBase64() {
        logger.debug("Taking screenshot as base64 string");
        
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver == null) {
                logger.warn("WebDriver is null, cannot take screenshot");
                return "";
            }
            
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            String base64Screenshot = takesScreenshot.getScreenshotAs(OutputType.BASE64);
            
            logger.debug("Screenshot captured as base64, length: {} characters", base64Screenshot.length());
            return base64Screenshot;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot as base64: {}", e.getMessage(), e);
            return "";
        }
    }
    
    /**
     * Create screenshot directory if it doesn't exist
     */
    private void createScreenshotDirectory() {
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            boolean created = screenshotDir.mkdirs();
            if (created) {
                logger.info("Screenshot directory created: {}", SCREENSHOT_DIR);
            } else {
                logger.warn("Could not create screenshot directory: {}", SCREENSHOT_DIR);
            }
        }
    }
    
    /**
     * Get current timestamp for screenshot naming
     * @return Formatted timestamp string
     */
    private String getCurrentTimestamp() {
        return LocalDateTime.now().format(DATE_FORMAT);
    }
    
    /**
     * Clean up old screenshots (optional maintenance method)
     * @param daysOld Delete screenshots older than specified days
     */
    public void cleanupOldScreenshots(int daysOld) {
        logger.info("Cleaning up screenshots older than {} days", daysOld);
        
        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists() || !screenshotDir.isDirectory()) {
            logger.warn("Screenshot directory does not exist: {}", SCREENSHOT_DIR);
            return;
        }
        
        File[] files = screenshotDir.listFiles();
        if (files == null) {
            logger.warn("Could not list files in screenshot directory");
            return;
        }
        
        long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L);
        int deletedCount = 0;
        
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".png") && file.lastModified() < cutoffTime) {
                boolean deleted = file.delete();
                if (deleted) {
                    deletedCount++;
                    logger.debug("Deleted old screenshot: {}", file.getName());
                }
            }
        }
        
        logger.info("Cleanup completed. Deleted {} old screenshots", deletedCount);
    }
    
    /**
     * Get screenshot directory path
     * @return Screenshot directory path
     */
    public String getScreenshotDirectory() {
        return SCREENSHOT_DIR;
    }
    
    /**
     * Check if screenshot capability is available
     * @return true if driver supports screenshots
     */
    public boolean isScreenshotSupported() {
        try {
            WebDriver driver = WebDriverManager.getDriver();
            return driver != null && driver instanceof TakesScreenshot;
        } catch (Exception e) {
            logger.debug("Screenshot capability check failed: {}", e.getMessage());
            return false;
        }
    }
}
