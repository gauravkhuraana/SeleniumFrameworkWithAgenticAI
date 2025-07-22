package com.automation.framework.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration Manager to handle application properties
 * Singleton pattern implementation for centralized configuration management
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class ConfigurationManager {
    
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    private static ConfigurationManager instance;
    private final Properties properties;
    
    private static final String CONFIG_FILE = "config/application.properties";
    
    private ConfigurationManager() {
        properties = new Properties();
        loadProperties();
    }
    
    /**
     * Get singleton instance of ConfigurationManager
     * @return ConfigurationManager instance
     */
    public static synchronized ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }
    
    /**
     * Load properties from configuration file
     */
    private void loadProperties() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
                logger.info("Configuration properties loaded successfully from: {}", CONFIG_FILE);
            } else {
                logger.error("Configuration file not found: {}", CONFIG_FILE);
                throw new RuntimeException("Configuration file not found: " + CONFIG_FILE);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration properties: {}", e.getMessage(), e);
            throw new RuntimeException("Error loading configuration properties", e);
        }
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = properties.getProperty(key);
        }
        if (value != null) {
            value = resolvePropertyPlaceholders(value);
        }
        return value;
    }
    
    /**
     * Get property value by key with default value
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }
    
    /**
     * Get property value as boolean
     * @param key Property key
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }
    
    /**
     * Get property value as boolean with default value
     * @param key Property key
     * @param defaultValue Default boolean value
     * @return Boolean value or default value
     */
    public boolean getBooleanProperty(String key, boolean defaultValue) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }
    
    /**
     * Get property value as integer
     * @param key Property key
     * @return Integer value
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }
    
    /**
     * Get property value as integer with default value
     * @param key Property key
     * @param defaultValue Default integer value
     * @return Integer value or default value
     */
    public int getIntProperty(String key, int defaultValue) {
        String value = getProperty(key);
        return value != null ? Integer.parseInt(value) : defaultValue;
    }
    
    /**
     * Set property value
     * @param key Property key
     * @param value Property value
     */
    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        logger.debug("Property set: {} = {}", key, value);
    }
    
    /**
     * Resolve property placeholders in the format ${property.name}
     * @param value Value with potential placeholders
     * @return Resolved value
     */
    private String resolvePropertyPlaceholders(String value) {
        if (value == null) return null;
        
        // Simple placeholder resolution for ${property.name}
        while (value.contains("${") && value.contains("}")) {
            int start = value.indexOf("${");
            int end = value.indexOf("}", start);
            if (start != -1 && end != -1) {
                String placeholder = value.substring(start + 2, end);
                String replacement = getProperty(placeholder);
                if (replacement == null) {
                    replacement = System.getenv(placeholder);
                }
                if (replacement != null) {
                    value = value.replace("${" + placeholder + "}", replacement);
                } else {
                    break; // Avoid infinite loop if placeholder cannot be resolved
                }
            } else {
                break;
            }
        }
        return value;
    }
    
    // Browser Configuration Methods
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("browser.headless", false);
    }
    
    public boolean shouldMaximize() {
        return getBooleanProperty("browser.maximize", true);
    }
    
    public int getImplicitWait() {
        return getIntProperty("browser.implicit.wait", 10);
    }
    
    public int getExplicitWait() {
        return getIntProperty("browser.explicit.wait", 30);
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("browser.page.load.timeout", 30);
    }
    
    // Application Configuration Methods
    public String getApplicationUrl() {
        return getProperty("app.url", "https://www.google.com");
    }
    
    public String getApplicationName() {
        return getProperty("app.name", "Modern Selenium TestNG Framework");
    }
    
    // Grid Configuration Methods
    public boolean isGridEnabled() {
        return getBooleanProperty("grid.enabled", false);
    }
    
    public String getGridHubUrl() {
        return getProperty("grid.hub.url", "http://localhost:4444/wd/hub");
    }
    
    // LambdaTest Configuration Methods
    public boolean isLambdaTestEnabled() {
        return getBooleanProperty("lambdatest.enabled", false);
    }
    
    public String getLambdaTestUsername() {
        return getProperty("lambdatest.username");
    }
    
    public String getLambdaTestAccessKey() {
        return getProperty("lambdatest.access.key");
    }
    
    public String getLambdaTestGridUrl() {
        return getProperty("lambdatest.grid.url");
    }
    
    // Test Execution Configuration Methods
    public int getThreadCount() {
        return getIntProperty("test.thread.count", 1);
    }
    
    public int getRetryCount() {
        return getIntProperty("test.retry.count", 1);
    }
    
    public boolean shouldTakeScreenshotOnFailure() {
        return getBooleanProperty("test.screenshot.on.failure", true);
    }
    
    public boolean shouldTakeScreenshotOnPass() {
        return getBooleanProperty("test.screenshot.on.pass", false);
    }
    
    // Reporting Configuration Methods
    public String getExtentReportPath() {
        return getProperty("extent.report.path", "test-output/extent-reports/extent-report.html");
    }
    
    public String getExtentReportTitle() {
        return getProperty("extent.report.title", "Automation Test Report");
    }
    
    public String getExtentReportName() {
        return getProperty("extent.report.name", "Test Execution Report");
    }
    
    public String getAllureResultsDirectory() {
        return getProperty("allure.results.directory", "target/allure-results");
    }
    
    // Test Data Configuration Methods
    public String getTestDataPath() {
        return getProperty("test.data.path", "src/test/resources/testdata/");
    }
    
    public String getTestDataFormat() {
        return getProperty("test.data.format", "json");
    }
}
