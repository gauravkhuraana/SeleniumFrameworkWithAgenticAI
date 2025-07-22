package com.automation.framework.base;

import com.automation.framework.config.ConfigurationManager;
import com.automation.framework.driver.WebDriverManager;
import com.automation.framework.utils.ScreenshotUtils;
import com.automation.framework.utils.TestDataUtils;
import com.automation.framework.listeners.ExtentReportListener;
import com.automation.framework.listeners.AllureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

/**
 * Base Test class that all test classes should extend
 * Provides common test setup, teardown, and utility methods
 * 
 * @author Automation Framework
 * @version 1.0
 */
@Listeners({ExtentReportListener.class, AllureListener.class})
public abstract class BaseTest {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final ConfigurationManager config = ConfigurationManager.getInstance();
    protected TestDataUtils testDataUtils;
    protected ScreenshotUtils screenshotUtils;
    
    /**
     * Setup method executed once before all tests in the class
     */
    @BeforeClass(alwaysRun = true)
    public void setupClass() {
        logger.info("=== Starting test class: {} ===", getClass().getSimpleName());
        
        // Initialize utilities
        testDataUtils = new TestDataUtils();
        screenshotUtils = new ScreenshotUtils();
        
        logger.info("Test class setup completed for: {}", getClass().getSimpleName());
    }
    
    /**
     * Setup method executed before each test method
     * @param method Test method
     */
    @BeforeMethod(alwaysRun = true)
    public void setupMethod(Method method) {
        String testName = method.getName();
        logger.info("=== Starting test method: {} ===", testName);
        
        // Get browser from system property or use default
        String browser = System.getProperty("browser", config.getBrowser());
        logger.info("Initializing WebDriver with browser: {}", browser);
        
        // Initialize WebDriver
        WebDriverManager.setDriver(browser);
        
        // Log test execution details
        logger.info("Test method setup completed for: {}", testName);
        logger.info("Browser: {}, Thread: {}", browser, Thread.currentThread().getName());
    }
    
    /**
     * Teardown method executed after each test method
     * @param result Test result
     */
    @AfterMethod(alwaysRun = true)
    public void teardownMethod(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String status = getTestStatus(result);
        
        logger.info("=== Test method completed: {} - Status: {} ===", testName, status);
        
        try {
            // Take screenshot on failure or if configured to take on pass
            if (shouldTakeScreenshot(result)) {
                String screenshotPath = screenshotUtils.takeScreenshot(testName + "_" + status);
                logger.info("Screenshot captured: {}", screenshotPath);
                
                // Attach screenshot to test result for reporting
                System.setProperty("screenshot.path", screenshotPath);
            }
            
            // Log additional test details
            logTestDetails(result);
            
        } catch (Exception e) {
            logger.error("Error in test teardown for {}: {}", testName, e.getMessage(), e);
        } finally {
            // Always quit the driver
            WebDriverManager.quitDriver();
            logger.info("WebDriver quit for test: {}", testName);
        }
    }
    
    /**
     * Teardown method executed once after all tests in the class
     */
    @AfterClass(alwaysRun = true)
    public void teardownClass() {
        logger.info("=== Test class completed: {} ===", getClass().getSimpleName());
        
        // Perform any class-level cleanup
        try {
            // Force cleanup any remaining drivers
            WebDriverManager.forceCleanup();
        } catch (Exception e) {
            logger.error("Error in class teardown: {}", e.getMessage(), e);
        }
        
        logger.info("Test class teardown completed for: {}", getClass().getSimpleName());
    }
    
    /**
     * Setup method for suite-level configuration
     */
    @BeforeSuite(alwaysRun = true)
    public void setupSuite() {
        logger.info("=== Starting test suite execution ===");
        
        // Log environment information
        logEnvironmentInfo();
        
        // Initialize reporting
        initializeReporting();
        
        logger.info("Test suite setup completed");
    }
    
    /**
     * Teardown method for suite-level cleanup
     */
    @AfterSuite(alwaysRun = true)
    public void teardownSuite() {
        logger.info("=== Test suite execution completed ===");
        
        // Perform suite-level cleanup
        try {
            // Any final cleanup operations
            logger.info("Performing final cleanup operations");
        } catch (Exception e) {
            logger.error("Error in suite teardown: {}", e.getMessage(), e);
        }
        
        logger.info("Test suite teardown completed");
    }
    
    /**
     * Get test status as string
     * @param result Test result
     * @return Status string
     */
    private String getTestStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
    
    /**
     * Determine if screenshot should be taken
     * @param result Test result
     * @return true if screenshot should be taken
     */
    private boolean shouldTakeScreenshot(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE && config.shouldTakeScreenshotOnFailure()) {
            return true;
        }
        return result.getStatus() == ITestResult.SUCCESS && config.shouldTakeScreenshotOnPass();
    }
    
    /**
     * Log test execution details
     * @param result Test result
     */
    private void logTestDetails(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("Test Details - Class: {}, Method: {}, Duration: {}ms", 
                   className, testName, duration);
        
        if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
            logger.error("Test failure reason: {}", result.getThrowable().getMessage());
        }
        
        if (WebDriverManager.hasDriver()) {
            logger.info("Driver Info: {}", WebDriverManager.getDriverInfo());
        }
    }
    
    /**
     * Log environment and configuration information
     */
    private void logEnvironmentInfo() {
        logger.info("=== Environment Information ===");
        logger.info("Java Version: {}", System.getProperty("java.version"));
        logger.info("OS: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));
        logger.info("User: {}", System.getProperty("user.name"));
        logger.info("Working Directory: {}", System.getProperty("user.dir"));
        
        logger.info("=== Test Configuration ===");
        logger.info("Application URL: {}", config.getApplicationUrl());
        logger.info("Default Browser: {}", config.getBrowser());
        logger.info("Headless Mode: {}", config.isHeadless());
        logger.info("Grid Enabled: {}", config.isGridEnabled());
        logger.info("LambdaTest Enabled: {}", config.isLambdaTestEnabled());
        logger.info("Thread Count: {}", config.getThreadCount());
        logger.info("Retry Count: {}", config.getRetryCount());
        
        if (config.isGridEnabled()) {
            logger.info("Grid Hub URL: {}", config.getGridHubUrl());
        }
        
        if (config.isLambdaTestEnabled()) {
            logger.info("LambdaTest Username: {}", config.getLambdaTestUsername());
            logger.info("LambdaTest Grid URL: {}", config.getLambdaTestGridUrl());
        }
    }
    
    /**
     * Initialize reporting systems
     */
    private void initializeReporting() {
        logger.info("Initializing reporting systems");
        
        try {
            // Set system properties for reporting
            System.setProperty("extent.report.path", config.getExtentReportPath());
            System.setProperty("extent.report.title", config.getExtentReportTitle());
            System.setProperty("extent.report.name", config.getExtentReportName());
            System.setProperty("allure.results.directory", config.getAllureResultsDirectory());
            
            logger.info("Reporting systems initialized successfully");
        } catch (Exception e) {
            logger.error("Error initializing reporting systems: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Navigate to application URL
     */
    protected void navigateToApplication() {
        String url = config.getApplicationUrl();
        logger.info("Navigating to application URL: {}", url);
        WebDriverManager.getDriver().navigate().to(url);
    }
    
    /**
     * Get current test method name
     * @return Test method name
     */
    protected String getCurrentTestMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
    
    /**
     * Wait for specified duration
     * @param seconds Duration in seconds
     */
    protected void waitFor(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Wait interrupted: {}", e.getMessage());
        }
    }
    
    /**
     * Log test step
     * @param step Step description
     */
    protected void logStep(String step) {
        logger.info("Test Step: {}", step);
    }
    
    /**
     * Log test verification
     * @param verification Verification description
     */
    protected void logVerification(String verification) {
        logger.info("Verification: {}", verification);
    }
    
    /**
     * Restart browser session
     */
    protected void restartBrowser() {
        logger.info("Restarting browser session");
        WebDriverManager.restartDriver();
    }
    
    /**
     * Restart browser with specific browser
     * @param browser Browser name
     */
    protected void restartBrowser(String browser) {
        logger.info("Restarting browser session with: {}", browser);
        WebDriverManager.restartDriver(browser);
    }
}
