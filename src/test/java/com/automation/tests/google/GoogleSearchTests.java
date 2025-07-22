package com.automation.tests.google;

import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.google.GoogleHomePage;
import com.automation.framework.pages.google.GoogleSearchResultsPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test class for Google Search functionality
 * Contains tests that are designed to pass and fail for demonstration
 * 
 * @author Automation Framework
 * @version 1.0
 */
@Epic("Google Search Tests")
@Feature("Search Functionality")
public class GoogleSearchTests extends BaseTest {
    
    private GoogleHomePage googleHomePage;
    
    @BeforeMethod
    public void setupTest() {
        logStep("Initializing Google Search Tests");
        
        // Load test data if needed
        // Map<String, Object> testData = testDataUtils.loadJsonDataAsMap("google-test-data.json");
        
        // Initialize page objects
        googleHomePage = new GoogleHomePage();
        
        // Navigate to Google
        googleHomePage.navigateToGoogle();
        
        // Verify page is loaded
        Assert.assertTrue(googleHomePage.isPageLoaded(), "Google homepage should be loaded");
        
        logStep("Test setup completed - Google homepage loaded");
    }
    
    @Test(description = "Search for 'selenium webdriver' - should pass")
    @Story("Valid Search Operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchSeleniumWebDriver() {
        logStep("Starting search test for 'selenium webdriver'");
        
        String searchQuery = "selenium webdriver";
        
        // Perform search
        GoogleSearchResultsPage resultsPage = googleHomePage.performSearch(searchQuery);
        
        // Verify results page is loaded
        Assert.assertTrue(resultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // Verify search results are present
        Assert.assertTrue(resultsPage.hasSearchResults(), "Search results should be present");
        
        // Verify search results contain expected content
        List<String> resultTitles = resultsPage.getSearchResultTitles();
        boolean containsSelenium = resultTitles.stream()
            .anyMatch(title -> title.toLowerCase().contains("selenium"));
        
        Assert.assertTrue(containsSelenium, "Search results should contain 'selenium' in titles");
        
        logVerification("Search for 'selenium webdriver' completed successfully");
    }
    
    @Test(description = "Search for 'automation testing' - should pass")
    @Story("Valid Search Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchAutomationTesting() {
        logStep("Starting search test for 'automation testing'");
        
        String searchQuery = "automation testing";
        
        // Perform search
        GoogleSearchResultsPage resultsPage = googleHomePage.performSearch(searchQuery);
        
        // Verify results page is loaded
        Assert.assertTrue(resultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // Verify search results are present
        Assert.assertTrue(resultsPage.hasSearchResults(), "Search results should be present");
        
        // Verify search results count
        int resultsCount = resultsPage.getSearchResultsCount();
        Assert.assertTrue(resultsCount > 0, "Should have at least one search result");
        
        // Verify search query is retained in search box
        String currentQuery = resultsPage.getCurrentSearchQuery();
        Assert.assertEquals(currentQuery, searchQuery, "Search query should be retained in search box");
        
        logVerification("Search for 'automation testing' completed successfully");
    }
    
    @Test(description = "Search for 'java programming' - should pass")
    @Story("Valid Search Operations")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchJavaProgramming() {
        logStep("Starting search test for 'java programming'");
        
        String searchQuery = "java programming";
        
        // Perform search
        GoogleSearchResultsPage resultsPage = googleHomePage.performSearch(searchQuery);
        
        // Verify results page is loaded
        Assert.assertTrue(resultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // Verify search results are present
        Assert.assertTrue(resultsPage.hasSearchResults(), "Search results should be present");
        
        // Get result statistics
        String resultStats = resultsPage.getResultStats();
        Assert.assertFalse(resultStats.isEmpty(), "Result statistics should not be empty");
        
        // Verify we can get result snippets
        List<String> snippets = resultsPage.getSearchResultSnippets();
        Assert.assertFalse(snippets.isEmpty(), "Search result snippets should not be empty");
        
        logVerification("Search for 'java programming' completed successfully");
    }
    
    @Test(description = "Search for 'google' but expect wrong text - should fail intentionally")
    @Story("Failing Search Tests")
    @Severity(SeverityLevel.MINOR)
    public void testSearchGoogleExpectWrongText() {
        logStep("Starting intentionally failing search test");
        
        String searchQuery = "google";
        String expectedText = "nonexistenttext123456"; // This text should not exist
        
        // Perform search
        GoogleSearchResultsPage resultsPage = googleHomePage.performSearch(searchQuery);
        
        // Verify results page is loaded
        Assert.assertTrue(resultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // This assertion should fail intentionally
        List<String> resultTitles = resultsPage.getSearchResultTitles();
        boolean containsExpectedText = resultTitles.stream()
            .anyMatch(title -> title.toLowerCase().contains(expectedText.toLowerCase()));
        
        // This assertion will fail - designed to demonstrate failure handling
        Assert.assertTrue(containsExpectedText, 
            "Search results should contain '" + expectedText + "' (this test is designed to fail)");
        
        logVerification("This test should not reach this point");
    }
    
    @Test(description = "Search for 'test' but expect impossible result - should fail intentionally")
    @Story("Failing Search Tests")
    @Severity(SeverityLevel.MINOR)
    public void testSearchTestExpectImpossibleResult() {
        logStep("Starting another intentionally failing search test");
        
        String searchQuery = "test";
        String impossibleText = "impossibleresulttext999";
        
        // Perform search
        GoogleSearchResultsPage resultsPage = googleHomePage.performSearch(searchQuery);
        
        // Verify results page is loaded
        Assert.assertTrue(resultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // Get search results
        List<String> resultTitles = resultsPage.getSearchResultTitles();
        Assert.assertFalse(resultTitles.isEmpty(), "Should have search results");
        
        // This assertion will fail intentionally
        boolean hasImpossibleText = resultTitles.stream()
            .anyMatch(title -> title.contains(impossibleText));
        
        Assert.assertTrue(hasImpossibleText, 
            "Search results should contain impossible text (this test is designed to fail)");
        
        logVerification("This verification should not be reached");
    }
    
    @Test(description = "Search with empty query - edge case test")
    @Story("Edge Case Tests")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchWithEmptyQuery() {
        logStep("Testing search with empty query");
        
        // Try to perform search with empty query
        googleHomePage.enterSearchQuery("");
        
        // Check if search button is enabled/clickable
        boolean searchButtonsVisible = googleHomePage.areSearchButtonsDisplayed();
        
        // This behavior may vary, but we'll check current state
        logVerification("Empty query test completed - buttons visible: " + searchButtonsVisible);
        
        // Add a basic assertion
        Assert.assertTrue(googleHomePage.isSearchBoxDisplayed(), "Search box should still be displayed");
    }
}
