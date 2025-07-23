package com.automation.framework.driver;

import com.automation.framework.config.ConfigurationManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * WebDriver Factory for creating and managing WebDriver instances
 * Supports local, grid, and cloud (LambdaTest) execution
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class WebDriverFactory {
    
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);
    private static final ConfigurationManager config = ConfigurationManager.getInstance();
    
    /**
     * Cleanup any orphaned Chrome processes in CI environments
     * This helps prevent session conflicts
     */
    public static void cleanupChromeProcesses() {
        if (isRunningInCI()) {
            try {
                logger.info("Attempting to cleanup orphaned Chrome processes in CI environment");
                ProcessBuilder pb = new ProcessBuilder();
                
                // Linux/Unix cleanup command
                if (System.getProperty("os.name").toLowerCase().contains("linux") || 
                    System.getProperty("os.name").toLowerCase().contains("unix")) {
                    pb.command("pkill", "-f", "chrome");
                } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    pb.command("taskkill", "/F", "/IM", "chrome.exe");
                }
                
                Process process = pb.start();
                int exitCode = process.waitFor();
                
                // Handle expected exit codes
                if (exitCode == 0) {
                    logger.info("Chrome process cleanup completed successfully - processes terminated");
                } else if (exitCode == 1 || exitCode == 143) {
                    // Exit code 1: No processes found (normal)
                    // Exit code 143: SIGTERM - processes terminated (normal) 
                    logger.info("Chrome process cleanup completed - no processes found or already terminated (exit code: {})", exitCode);
                } else {
                    logger.warn("Chrome process cleanup completed with unexpected exit code: {}", exitCode);
                }
                
            } catch (Exception e) {
                logger.warn("Could not cleanup Chrome processes: {}", e.getMessage());
            }
        }
    }
    
    /**
     * Create WebDriver instance based on configuration
     * @param browser Browser name
     * @return WebDriver instance
     */
    public static WebDriver createDriver(String browser) {
        // Cleanup any orphaned processes before creating new driver
        if ("chrome".equalsIgnoreCase(browser)) {
            cleanupChromeProcesses();
        }
        
        WebDriver driver;
        
        if (config.isLambdaTestEnabled()) {
            driver = createLambdaTestDriver(browser);
        } else if (config.isGridEnabled()) {
            driver = createGridDriver(browser);
        } else {
            driver = createLocalDriver(browser);
        }
        
        configureDriver(driver);
        return driver;
    }
    
    /**
     * Create WebDriver instance using default browser from config
     * @return WebDriver instance
     */
    public static WebDriver createDriver() {
        return createDriver(config.getBrowser());
    }
    
    /**
     * Create local WebDriver instance
     * @param browser Browser name
     * @return WebDriver instance
     */
    private static WebDriver createLocalDriver(String browser) {
        logger.info("Creating local WebDriver for browser: {}", browser);
        
        switch (browser.toLowerCase()) {
            case "chrome":
                return createChromeDriver();
            case "firefox":
                return createFirefoxDriver();
            case "edge":
                return createEdgeDriver();
            default:
                logger.warn("Unsupported browser: {}. Defaulting to Chrome", browser);
                return createChromeDriver();
        }
    }
    
    /**
     * Create Chrome WebDriver
     * @return ChromeDriver instance
     */
    private static WebDriver createChromeDriver() {
        // Selenium Manager handles driver setup automatically
        ChromeOptions options = new ChromeOptions();
        
        // Basic Chrome options
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-web-security");
        options.addArguments("--disable-features=VizDisplayCompositor");
        
        // Critical for session isolation in CI
        options.addArguments("--no-first-run");
        options.addArguments("--disable-default-apps");
        options.addArguments("--disable-sync");
        
        // CI/CD specific options to prevent session conflicts
        options.addArguments("--disable-background-timer-throttling");
        options.addArguments("--disable-backgrounding-occluded-windows");
        options.addArguments("--disable-renderer-backgrounding");
        options.addArguments("--disable-field-trial-config");
        options.addArguments("--disable-ipc-flooding-protection");
        
        // Set unique user data directory for CI environments
        if (isRunningInCI()) {
            // Create a truly unique directory using timestamp, thread name, and random component
            String threadName = Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9]", "_");
            String randomComponent = String.valueOf((int)(Math.random() * 100000));
            String timestamp = String.valueOf(System.currentTimeMillis());
            String processId = String.valueOf(ProcessHandle.current().pid());
            String uniqueUserDataDir = System.getProperty("java.io.tmpdir") + 
                "/chrome_user_data_" + timestamp + "_" + processId + "_" + threadName + "_" + randomComponent;
            
            options.addArguments("--user-data-dir=" + uniqueUserDataDir);
            options.addArguments("--single-process");
            options.addArguments("--disable-background-media-suspend");
            
            // Additional isolation arguments
            options.addArguments("--disable-features=TranslateUI");
            options.addArguments("--disable-features=BlinkGenPropertyTrees");
            options.addArguments("--disable-browser-side-navigation");
            options.addArguments("--disable-background-networking");
            
            logger.info("Running in CI environment - using unique user data directory: {}", uniqueUserDataDir);
        }
        
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
            options.addArguments("--disable-logging");
            options.addArguments("--disable-gpu-logging");
            logger.info("Running Chrome in headless mode");
        }
        
        if (config.shouldMaximize()) {
            if (!config.isHeadless()) {
                options.addArguments("--start-maximized");
            } else {
                // For headless mode, set window size explicitly
                options.addArguments("--window-size=1920,1080");
            }
        }
        
        // Performance optimizations
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.managed_default_content_settings.images", 2); // Block images for faster loading in CI
        options.setExperimentalOption("prefs", prefs);
        
        // Additional options for stability in CI
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        
        logger.info("Creating ChromeDriver with options: {}", options.asMap());
        return new ChromeDriver(options);
    }
    
    /**
     * Check if running in CI environment
     * @return true if running in CI
     */
    private static boolean isRunningInCI() {
        // Check common CI environment variables
        boolean isCI = System.getenv("CI") != null || 
                      System.getenv("GITHUB_ACTIONS") != null ||
                      System.getenv("JENKINS_URL") != null ||
                      System.getenv("GITLAB_CI") != null ||
                      System.getenv("TRAVIS") != null ||
                      System.getenv("CIRCLECI") != null ||
                      System.getenv("TEAMCITY_VERSION") != null ||
                      System.getenv("BUILDKITE") != null;
        
        if (isCI) {
            logger.debug("CI environment detected. CI={}, GITHUB_ACTIONS={}, OS={}", 
                System.getenv("CI"), 
                System.getenv("GITHUB_ACTIONS"),
                System.getProperty("os.name"));
        }
        
        return isCI;
    }
    
    /**
     * Create Firefox WebDriver
     * @return FirefoxDriver instance
     */
    private static WebDriver createFirefoxDriver() {
        // Selenium Manager handles driver setup automatically
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
            logger.info("Running Firefox in headless mode");
        }
        
        // Firefox preferences
        options.addPreference("dom.webnotifications.enabled", false);
        options.addPreference("dom.push.enabled", false);
        options.addPreference("browser.download.folderList", 2);
        
        logger.info("Creating FirefoxDriver with options");
        return new FirefoxDriver(options);
    }
    
    /**
     * Create Edge WebDriver
     * @return EdgeDriver instance
     */
    private static WebDriver createEdgeDriver() {
        // Selenium Manager handles driver setup automatically
        EdgeOptions options = new EdgeOptions();
        
        // Basic Edge options
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
            logger.info("Running Edge in headless mode");
        }
        
        if (config.shouldMaximize()) {
            options.addArguments("--start-maximized");
        }
        
        logger.info("Creating EdgeDriver with options");
        return new EdgeDriver(options);
    }
    
    /**
     * Create Grid WebDriver instance
     * @param browser Browser name
     * @return RemoteWebDriver instance
     */
    private static WebDriver createGridDriver(String browser) {
        logger.info("Creating Grid WebDriver for browser: {}", browser);
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName(browser);
        
        try {
            URL gridUrl = URI.create(config.getGridHubUrl()).toURL();
            logger.info("Connecting to Grid Hub: {}", gridUrl);
            return new RemoteWebDriver(gridUrl, capabilities);
        } catch (MalformedURLException e) {
            logger.error("Invalid Grid Hub URL: {}", config.getGridHubUrl(), e);
            throw new RuntimeException("Invalid Grid Hub URL", e);
        }
    }
    
    /**
     * Create LambdaTest WebDriver instance
     * @param browser Browser name
     * @return RemoteWebDriver instance
     */
    private static WebDriver createLambdaTestDriver(String browser) {
        logger.info("Creating LambdaTest WebDriver for browser: {}", browser);
        
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("browserVersion", "latest");
        capabilities.setCapability("platform", "Windows 11");
        
        // LambdaTest specific capabilities
        Map<String, Object> ltOptions = new HashMap<>();
        ltOptions.put("username", config.getLambdaTestUsername());
        ltOptions.put("accessKey", config.getLambdaTestAccessKey());
        ltOptions.put("project", config.getApplicationName());
        ltOptions.put("build", "Build-" + System.currentTimeMillis());
        ltOptions.put("name", "Test-" + System.currentTimeMillis());
        ltOptions.put("selenium_version", "4.0.0");
        ltOptions.put("w3c", true);
        ltOptions.put("plugin", "java-testNG");
        ltOptions.put("video", true);
        ltOptions.put("screenshot", true);
        ltOptions.put("network", true);
        ltOptions.put("console", true);
        ltOptions.put("visual", true);
        
        capabilities.setCapability("LT:Options", ltOptions);
        
        try {
            URL lambdaTestUrl = URI.create(config.getLambdaTestGridUrl()).toURL();
            logger.info("Connecting to LambdaTest Grid: {}", lambdaTestUrl);
            return new RemoteWebDriver(lambdaTestUrl, capabilities);
        } catch (MalformedURLException e) {
            logger.error("Invalid LambdaTest Grid URL: {}", config.getLambdaTestGridUrl(), e);
            throw new RuntimeException("Invalid LambdaTest Grid URL", e);
        }
    }
    
    /**
     * Configure WebDriver with timeouts and other settings
     * @param driver WebDriver instance
     */
    private static void configureDriver(WebDriver driver) {
        logger.info("Configuring WebDriver timeouts and settings");
        
        // Set timeouts
        driver.manage().timeouts()
            .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()))
            .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        
        // Maximize window if not headless and not already maximized
        if (!config.isHeadless() && config.shouldMaximize()) {
            try {
                driver.manage().window().maximize();
                logger.info("Browser window maximized");
            } catch (Exception e) {
                logger.warn("Could not maximize browser window: {}", e.getMessage());
            }
        }
        
        logger.info("WebDriver configured successfully");
    }
    
    /**
     * Get browser-specific capabilities for remote execution
     * @param browser Browser name
     * @return DesiredCapabilities
     */
    public static DesiredCapabilities getBrowserCapabilities(String browser) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        
        switch (browser.toLowerCase()) {
            case "chrome":
                capabilities.setBrowserName("chrome");
                ChromeOptions chromeOptions = new ChromeOptions();
                capabilities.merge(chromeOptions);
                break;
            case "firefox":
                capabilities.setBrowserName("firefox");
                capabilities.merge(new FirefoxOptions());
                break;
            case "edge":
                capabilities.setBrowserName("MicrosoftEdge");
                EdgeOptions edgeOptions = new EdgeOptions();
                capabilities.merge(edgeOptions);
                break;
            default:
                logger.warn("Unsupported browser for capabilities: {}. Using chrome", browser);
                capabilities.setBrowserName("chrome");
                ChromeOptions defaultChromeOptions = new ChromeOptions();
                capabilities.merge(defaultChromeOptions);
        }
        
        return capabilities;
    }
}
