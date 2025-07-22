package com.automation.framework.listeners;

import com.automation.framework.driver.WebDriverManager;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import io.qameta.allure.listener.TestLifecycleListener;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.TestResult;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

/**
 * Allure TestNG Listener for generating Allure reports
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class AllureListener implements ITestListener, TestLifecycleListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AllureListener.class);
    
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.debug("Starting Allure test tracking for: {}.{}", className, testName);
        
        // Add test information to Allure
        Allure.epic("Automation Test Suite");
        Allure.feature(className);
        Allure.story(testName);
        
        // Add environment information
        addEnvironmentInfo();
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.debug("Test passed - Allure: {}", testName);
        
        // Add success information
        Allure.step("Test completed successfully");
        logTestDuration(result);
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.debug("Test failed - Allure: {}", testName);
        
        // Attach failure information
        attachFailureInfo(result);
        
        // Attach screenshot
        attachScreenshot();
        
        // Attach page source if available
        attachPageSource();
        
        logTestDuration(result);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.debug("Test skipped - Allure: {}", testName);
        
        // Add skip information
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            Allure.step("Test skipped: " + throwable.getMessage());
        } else {
            Allure.step("Test skipped");
        }
        
        logTestDuration(result);
    }
    
    @Override
    public void beforeTestStop(TestResult result) {
        logger.debug("Allure - Before test stop: {}", result.getName());
    }
    
    @Override
    public void afterTestStop(TestResult result) {
        logger.debug("Allure - After test stop: {}", result.getName());
    }
    
    @Override
    public void beforeTestWrite(TestResult result) {
        logger.debug("Allure - Before test write: {}", result.getName());
    }
    
    @Override
    public void afterTestWrite(TestResult result) {
        logger.debug("Allure - After test write: {}", result.getName());
    }
    
    /**
     * Attach screenshot to Allure report
     */
    @Attachment(value = "Screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver != null && driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                logger.debug("Screenshot attached to Allure report");
                return screenshot;
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot to Allure: {}", e.getMessage(), e);
        }
        return new byte[0];
    }
    
    /**
     * Attach page source to Allure report
     */
    @Attachment(value = "Page Source", type = "text/html")
    public String attachPageSource() {
        try {
            WebDriver driver = WebDriverManager.getDriver();
            if (driver != null) {
                String pageSource = driver.getPageSource();
                logger.debug("Page source attached to Allure report");
                return pageSource;
            }
        } catch (Exception e) {
            logger.error("Failed to attach page source to Allure: {}", e.getMessage(), e);
        }
        return "";
    }
    
    /**
     * Attach failure information to Allure report
     */
    @Attachment(value = "Failure Information", type = "text/plain")
    public String attachFailureInfo(ITestResult result) {
        try {
            StringBuilder failureInfo = new StringBuilder();
            failureInfo.append("Test Method: ").append(result.getMethod().getMethodName()).append("\n");
            failureInfo.append("Test Class: ").append(result.getTestClass().getName()).append("\n");
            failureInfo.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
            
            if (result.getThrowable() != null) {
                failureInfo.append("Error Message: ").append(result.getThrowable().getMessage()).append("\n");
                failureInfo.append("Stack Trace:\n").append(getStackTrace(result.getThrowable()));
            }
            
            if (WebDriverManager.hasDriver()) {
                failureInfo.append("\nDriver Info: ").append(WebDriverManager.getDriverInfo());
            }
            
            logger.debug("Failure information attached to Allure report");
            return failureInfo.toString();
        } catch (Exception e) {
            logger.error("Failed to attach failure info to Allure: {}", e.getMessage(), e);
            return "Failed to generate failure information";
        }
    }
    
    /**
     * Attach test logs to Allure report
     */
    @Attachment(value = "Test Logs", type = "text/plain")
    public String attachTestLogs(String logs) {
        logger.debug("Test logs attached to Allure report");
        return logs;
    }
    
    /**
     * Attach custom text to Allure report
     */
    @Attachment(value = "{attachmentName}", type = "text/plain")
    public String attachText(String attachmentName, String text) {
        logger.debug("Custom text attachment '{}' added to Allure report", attachmentName);
        return text;
    }
    
    /**
     * Attach JSON data to Allure report
     */
    @Attachment(value = "{attachmentName}", type = "application/json")
    public String attachJson(String attachmentName, String jsonData) {
        logger.debug("JSON attachment '{}' added to Allure report", attachmentName);
        return jsonData;
    }
    
    /**
     * Attach XML data to Allure report
     */
    @Attachment(value = "{attachmentName}", type = "application/xml")
    public String attachXml(String attachmentName, String xmlData) {
        logger.debug("XML attachment '{}' added to Allure report", attachmentName);
        return xmlData;
    }
    
    /**
     * Add step to Allure report
     */
    public static void step(String stepName) {
        Allure.step(stepName);
        logger.debug("Allure step added: {}", stepName);
    }
    
    /**
     * Add step with status to Allure report
     */
    public static void step(String stepName, Status status) {
        Allure.step(stepName, status);
        logger.debug("Allure step added: {} ({})", stepName, status);
    }
    
    /**
     * Add parameter to Allure report
     */
    public static void parameter(String name, Object value) {
        Allure.parameter(name, value);
        logger.debug("Allure parameter added: {} = {}", name, value);
    }
    
    /**
     * Add link to Allure report
     */
    public static void link(String name, String url) {
        Allure.link(name, url);
        logger.debug("Allure link added: {} -> {}", name, url);
    }
    
    /**
     * Add issue link to Allure report
     */
    public static void issue(String name, String url) {
        Allure.issue(name, url);
        logger.debug("Allure issue link added: {} -> {}", name, url);
    }
    
    /**
     * Add TMS link to Allure report
     */
    public static void tms(String name, String url) {
        Allure.tms(name, url);
        logger.debug("Allure TMS link added: {} -> {}", name, url);
    }
    
    /**
     * Add environment information
     */
    private void addEnvironmentInfo() {
        try {
            // Add browser info if available
            if (WebDriverManager.hasDriver()) {
                WebDriver driver = WebDriverManager.getDriver();
                String browserName = driver.getClass().getSimpleName().replace("Driver", "");
                Allure.parameter("Browser", browserName);
                Allure.parameter("Current URL", driver.getCurrentUrl());
            }
            
            // Add system information
            Allure.parameter("OS", System.getProperty("os.name"));
            Allure.parameter("Java Version", System.getProperty("java.version"));
            Allure.parameter("Thread", Thread.currentThread().getName());
            
        } catch (Exception e) {
            logger.error("Failed to add environment info to Allure: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log test execution duration
     */
    private void logTestDuration(ITestResult result) {
        try {
            long duration = result.getEndMillis() - result.getStartMillis();
            Allure.parameter("Execution Time (ms)", duration);
            logger.debug("Test duration logged: {} ms", duration);
        } catch (Exception e) {
            logger.error("Failed to log test duration to Allure: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get stack trace as string
     */
    private String getStackTrace(Throwable throwable) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
    
    /**
     * Static method to attach byte array to Allure
     */
    public static void attachBytes(String name, byte[] bytes, String type) {
        Allure.addAttachment(name, type, new ByteArrayInputStream(bytes), "");
        logger.debug("Byte array attachment '{}' added to Allure report", name);
    }
    
    /**
     * Static method to attach file to Allure
     */
    public static void attachFile(String name, String filePath) {
        try {
            Allure.addAttachment(name, new ByteArrayInputStream(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(filePath))));
            logger.debug("File attachment '{}' added to Allure report: {}", name, filePath);
        } catch (Exception e) {
            logger.error("Failed to attach file '{}' to Allure: {}", filePath, e.getMessage(), e);
        }
    }
}
