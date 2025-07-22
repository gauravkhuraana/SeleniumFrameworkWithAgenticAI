package com.automation.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.automation.framework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ExtentReports TestNG Listener for generating detailed HTML reports
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class ExtentReportListener implements ITestListener {
    
    private static final Logger logger = LoggerFactory.getLogger(ExtentReportListener.class);
    private static ExtentReports extentReports;
    private static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private final ConfigurationManager config = ConfigurationManager.getInstance();
    
    /**
     * Initialize ExtentReports before any test execution
     */
    @Override
    public void onStart(org.testng.ITestContext context) {
        logger.info("Initializing ExtentReports");
        
        try {
            String reportPath = getReportPath();
            createReportDirectory(reportPath);
            
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
            configureSparkReporter(sparkReporter);
            
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            setSystemInfo();
            
            logger.info("ExtentReports initialized successfully. Report path: {}", reportPath);
        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Create test entry in ExtentReports before each test method
     */
    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        
        logger.debug("Starting ExtentTest for: {}.{}", className, testName);
        
        try {
            ExtentTest test = extentReports.createTest(testName);
            test.assignCategory(className);
            test.assignAuthor("Automation Framework");
            
            // Add test description if available
            String description = result.getMethod().getDescription();
            if (description != null && !description.isEmpty()) {
                test.info("Test Description: " + description);
            }
            
            extentTest.set(test);
            logger.debug("ExtentTest created for: {}", testName);
        } catch (Exception e) {
            logger.error("Failed to create ExtentTest for {}: {}", testName, e.getMessage(), e);
        }
    }
    
    /**
     * Update test result in ExtentReports after test execution
     */
    @Override
    public void onTestSuccess(ITestResult result) {
        logTestResult(result, Status.PASS, "Test passed successfully");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        String errorMessage = getErrorMessage(result);
        logTestResult(result, Status.FAIL, "Test failed: " + errorMessage);
        
        // Attach screenshot if available
        attachScreenshot(result);
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        String skipReason = getSkipReason(result);
        logTestResult(result, Status.SKIP, "Test skipped: " + skipReason);
    }
    
    /**
     * Finalize ExtentReports after all tests
     */
    @Override
    public void onFinish(org.testng.ITestContext context) {
        logger.info("Finalizing ExtentReports");
        
        try {
            if (extentReports != null) {
                extentReports.flush();
                logger.info("ExtentReports finalized successfully");
            }
        } catch (Exception e) {
            logger.error("Failed to finalize ExtentReports: {}", e.getMessage(), e);
        } finally {
            // Clean up ThreadLocal
            extentTest.remove();
        }
    }
    
    /**
     * Get current ExtentTest instance for manual logging
     * @return Current ExtentTest instance
     */
    public static ExtentTest getCurrentTest() {
        return extentTest.get();
    }
    
    /**
     * Log message to current test
     * @param status Log status
     * @param message Log message
     */
    public static void log(Status status, String message) {
        ExtentTest test = getCurrentTest();
        if (test != null) {
            test.log(status, message);
        }
    }
    
    /**
     * Log info message to current test
     * @param message Info message
     */
    public static void logInfo(String message) {
        log(Status.INFO, message);
    }
    
    /**
     * Log warning message to current test
     * @param message Warning message
     */
    public static void logWarning(String message) {
        log(Status.WARNING, message);
    }
    
    /**
     * Log error message to current test
     * @param message Error message
     */
    public static void logError(String message) {
        log(Status.FAIL, message);
    }
    
    /**
     * Configure ExtentSparkReporter settings
     */
    private void configureSparkReporter(ExtentSparkReporter sparkReporter) {
        try {
            sparkReporter.config().setDocumentTitle(config.getExtentReportTitle());
            sparkReporter.config().setReportName(config.getExtentReportName());
            sparkReporter.config().setTheme(Theme.STANDARD);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");
            
            logger.debug("ExtentSparkReporter configured successfully");
        } catch (Exception e) {
            logger.error("Failed to configure ExtentSparkReporter: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Set system information in the report
     */
    private void setSystemInfo() {
        try {
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("OS", System.getProperty("os.name") + " " + System.getProperty("os.version"));
            extentReports.setSystemInfo("User", System.getProperty("user.name"));
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("Application URL", config.getApplicationUrl());
            extentReports.setSystemInfo("Environment", System.getProperty("environment", "TEST"));
            extentReports.setSystemInfo("Execution Mode", config.isGridEnabled() ? "Grid" : 
                                        config.isLambdaTestEnabled() ? "LambdaTest" : "Local");
            
            logger.debug("System information set in ExtentReports");
        } catch (Exception e) {
            logger.error("Failed to set system information: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Log test result with timing information
     */
    private void logTestResult(ITestResult result, Status status, String message) {
        try {
            ExtentTest test = extentTest.get();
            if (test != null) {
                long duration = result.getEndMillis() - result.getStartMillis();
                test.log(status, message);
                test.info("Execution Time: " + duration + " ms");
                test.info("Thread: " + Thread.currentThread().getName());
                
                // Add test class and method info
                test.info("Test Class: " + result.getTestClass().getName());
                test.info("Test Method: " + result.getMethod().getMethodName());
                
                logger.debug("Test result logged: {} - {}", result.getMethod().getMethodName(), status);
            }
        } catch (Exception e) {
            logger.error("Failed to log test result for {}: {}", 
                        result.getMethod().getMethodName(), e.getMessage(), e);
        }
    }
    
    /**
     * Attach screenshot to failed test
     */
    private void attachScreenshot(ITestResult result) {
        try {
            String screenshotPath = System.getProperty("screenshot.path");
            if (screenshotPath != null && !screenshotPath.isEmpty()) {
                ExtentTest test = extentTest.get();
                if (test != null) {
                    test.addScreenCaptureFromPath(screenshotPath);
                    logger.debug("Screenshot attached to test: {}", result.getMethod().getMethodName());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to attach screenshot for {}: {}", 
                        result.getMethod().getMethodName(), e.getMessage(), e);
        }
    }
    
    /**
     * Get error message from test result
     */
    private String getErrorMessage(ITestResult result) {
        Throwable throwable = result.getThrowable();
        return throwable != null ? throwable.getMessage() : "Unknown error";
    }
    
    /**
     * Get skip reason from test result
     */
    private String getSkipReason(ITestResult result) {
        Throwable throwable = result.getThrowable();
        return throwable != null ? throwable.getMessage() : "Test skipped";
    }
    
    /**
     * Get report file path
     */
    private String getReportPath() {
        String reportPath = config.getExtentReportPath();
        
        // Add timestamp to report name if needed
        if (reportPath.contains(".html")) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            reportPath = reportPath.replace(".html", "_" + timestamp + ".html");
        }
        
        return reportPath;
    }
    
    /**
     * Create report directory if it doesn't exist
     */
    private void createReportDirectory(String reportPath) {
        try {
            File reportFile = new File(reportPath);
            File parentDir = reportFile.getParentFile();
            
            if (parentDir != null && !parentDir.exists()) {
                boolean created = parentDir.mkdirs();
                if (created) {
                    logger.info("Report directory created: {}", parentDir.getAbsolutePath());
                } else {
                    logger.warn("Could not create report directory: {}", parentDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            logger.error("Failed to create report directory: {}", e.getMessage(), e);
        }
    }
}
