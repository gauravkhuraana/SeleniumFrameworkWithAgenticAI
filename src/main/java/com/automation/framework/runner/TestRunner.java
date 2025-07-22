package com.automation.framework.runner;

import org.testng.TestNG;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;
import org.testng.xml.XmlClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Programmatic TestNG Runner
 * Allows running tests programmatically without XML files
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class TestRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(TestRunner.class);
    
    public static void main(String[] args) {
        logger.info("Starting TestNG execution programmatically");
        
        // Determine which tests to run based on arguments
        String testType = args.length > 0 ? args[0] : "smoke";
        String browser = args.length > 1 ? args[1] : "chrome";
        
        logger.info("Test type: {}, Browser: {}", testType, browser);
        
        // Set system property for browser
        System.setProperty("browser", browser);
        
        TestNG testng = new TestNG();
        
        switch (testType.toLowerCase()) {
            case "smoke":
                runSmokeTests(testng);
                break;
            case "regression":
                runRegressionTests(testng);
                break;
            case "all":
                runAllTests(testng);
                break;
            case "homepage":
                runHomepageTests(testng);
                break;
            case "search":
                runSearchTests(testng);
                break;
            case "links":
                runLinksTests(testng);
                break;
            default:
                logger.error("Unknown test type: {}", testType);
                System.exit(1);
        }
        
        logger.info("TestNG execution completed");
    }
    
    private static void runSmokeTests(TestNG testng) {
        logger.info("Running smoke tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Smoke Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.NONE);
        
        XmlTest test = new XmlTest(suite);
        test.setName("Smoke Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        
        // Add specific test methods for smoke testing
        XmlClass homepageClass = new XmlClass("com.automation.tests.google.GoogleHomePageTests");
        homepageClass.setIncludedMethods(List.of(
            createXmlInclude("testGoogleLogoIsDisplayed"),
            createXmlInclude("testSearchBoxIsDisplayed"),
            createXmlInclude("testImageCountGreaterThanOne")
        ));
        classes.add(homepageClass);
        
        XmlClass searchClass = new XmlClass("com.automation.tests.google.GoogleSearchTests");
        searchClass.setIncludedMethods(List.of(
            createXmlInclude("testSearchSeleniumWebDriver")
        ));
        classes.add(searchClass);
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static void runRegressionTests(TestNG testng) {
        logger.info("Running regression tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Regression Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.CLASSES);
        suite.setThreadCount(2);
        
        XmlTest test = new XmlTest(suite);
        test.setName("Regression Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.automation.tests.google.GoogleHomePageTests"));
        classes.add(new XmlClass("com.automation.tests.google.GoogleSearchTests"));
        classes.add(new XmlClass("com.automation.tests.google.GoogleBrokenLinksTests"));
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static void runAllTests(TestNG testng) {
        logger.info("Running all tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Complete Test Suite");
        suite.setParallel(XmlSuite.ParallelMode.METHODS);
        suite.setThreadCount(3);
        
        XmlTest test = new XmlTest(suite);
        test.setName("All Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.automation.tests.google.GoogleHomePageTests"));
        classes.add(new XmlClass("com.automation.tests.google.GoogleSearchTests"));
        classes.add(new XmlClass("com.automation.tests.google.GoogleBrokenLinksTests"));
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static void runHomepageTests(TestNG testng) {
        logger.info("Running homepage tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Homepage Test Suite");
        
        XmlTest test = new XmlTest(suite);
        test.setName("Homepage Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.automation.tests.google.GoogleHomePageTests"));
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static void runSearchTests(TestNG testng) {
        logger.info("Running search tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Search Test Suite");
        
        XmlTest test = new XmlTest(suite);
        test.setName("Search Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.automation.tests.google.GoogleSearchTests"));
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static void runLinksTests(TestNG testng) {
        logger.info("Running broken links tests");
        
        XmlSuite suite = new XmlSuite();
        suite.setName("Broken Links Test Suite");
        
        XmlTest test = new XmlTest(suite);
        test.setName("Broken Links Tests");
        
        List<XmlClass> classes = new ArrayList<>();
        classes.add(new XmlClass("com.automation.tests.google.GoogleBrokenLinksTests"));
        
        test.setXmlClasses(classes);
        
        List<XmlSuite> suites = new ArrayList<>();
        suites.add(suite);
        testng.setXmlSuites(suites);
        testng.run();
    }
    
    private static org.testng.xml.XmlInclude createXmlInclude(String methodName) {
        org.testng.xml.XmlInclude include = new org.testng.xml.XmlInclude();
        include.setName(methodName);
        return include;
    }
}
