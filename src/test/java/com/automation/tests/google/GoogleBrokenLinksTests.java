package com.automation.tests.google;

import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.google.GoogleHomePage;
import com.automation.framework.pages.google.GoogleSearchResultsPage;
import io.qameta.allure.*;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for checking broken links functionality
 * Validates that links on Google homepage are not broken
 * 
 * @author Automation Framework
 * @version 1.0
 */
@Epic("Google Link Validation Tests")
@Feature("Broken Links Detection")
public class GoogleBrokenLinksTests extends BaseTest {
    
    private GoogleHomePage googleHomePage;
    private GoogleSearchResultsPage searchResultsPage;
    
    @BeforeMethod
    public void setupTest() {
        logStep("Initializing Google Broken Links Tests");
        
        // Initialize page objects
        googleHomePage = new GoogleHomePage();
        
        // Navigate to Google
        googleHomePage.navigateToGoogle();
        
        // Verify page is loaded
        Assert.assertTrue(googleHomePage.isPageLoaded(), "Google homepage should be loaded");
        
        logStep("Test setup completed - Google homepage loaded");
    }
    
    @Test(description = "Check for broken links on Google homepage")
    @Story("Homepage Link Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testHomepageBrokenLinks() {
        logStep("Starting broken links check on Google homepage");
        
        // Get all links from the homepage
        List<WebElement> links = googleHomePage.getAllLinks();
        logStep("Found " + links.size() + " links on homepage");
        
        List<String> brokenLinks = new ArrayList<>();
        List<String> validLinks = new ArrayList<>();
        List<String> skippedLinks = new ArrayList<>();
        
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            
            // Skip empty, null, or javascript links
            if (href == null || href.isEmpty() || 
                href.startsWith("javascript:") || 
                href.startsWith("mailto:") ||
                href.startsWith("#")) {
                skippedLinks.add(href != null ? href : "null/empty");
                continue;
            }
            
            try {
                int responseCode = getResponseCode(href);
                
                if (responseCode >= 200 && responseCode < 400) {
                    validLinks.add(href + " (Status: " + responseCode + ")");
                    logger.debug("Valid link: {} - Status: {}", href, responseCode);
                } else {
                    brokenLinks.add(href + " (Status: " + responseCode + ")");
                    logger.warn("Broken link found: {} - Status: {}", href, responseCode);
                }
                
            } catch (Exception e) {
                brokenLinks.add(href + " (Error: " + e.getMessage() + ")");
                logger.warn("Error checking link: {} - {}", href, e.getMessage());
            }
        }
        
        // Log results
        logStep("Link validation completed:");
        logStep("Total links found: " + links.size());
        logStep("Valid links: " + validLinks.size());
        logStep("Broken links: " + brokenLinks.size());
        logStep("Skipped links: " + skippedLinks.size());
        
        // Log details for reporting
        if (!brokenLinks.isEmpty()) {
            logger.warn("Broken links found:");
            brokenLinks.forEach(link -> logger.warn("  - {}", link));
        }
        
        // Assert that no broken links were found
        Assert.assertTrue(brokenLinks.isEmpty(), 
            "Found " + brokenLinks.size() + " broken links: " + brokenLinks.toString());
        
        logVerification("All homepage links are valid - no broken links found");
    }
    
    @Test(description = "Check for broken links on search results page")
    @Story("Search Results Link Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchResultsBrokenLinks() {
        logStep("Starting broken links check on search results page");
        
        // Perform a search to get to results page
        searchResultsPage = googleHomePage.performSearch("selenium testing");
        
        // Verify we're on search results page
        Assert.assertTrue(searchResultsPage.isPageLoaded(), "Search results page should be loaded");
        
        // Get search result links (limit to first 5 for performance)
        List<WebElement> resultLinks = searchResultsPage.getSearchResultLinks();
        int linksToCheck = Math.min(5, resultLinks.size());
        
        logStep("Found " + resultLinks.size() + " search result links, checking first " + linksToCheck);
        
        List<String> brokenLinks = new ArrayList<>();
        List<String> validLinks = new ArrayList<>();
        
        for (int i = 0; i < linksToCheck; i++) {
            WebElement link = resultLinks.get(i);
            String href = link.getAttribute("href");
            
            // Skip if href is null or empty
            if (href == null || href.isEmpty()) {
                continue;
            }
            
            try {
                int responseCode = getResponseCode(href);
                
                if (responseCode >= 200 && responseCode < 400) {
                    validLinks.add(href + " (Status: " + responseCode + ")");
                    logger.debug("Valid search result link: {} - Status: {}", href, responseCode);
                } else {
                    brokenLinks.add(href + " (Status: " + responseCode + ")");
                    logger.warn("Broken search result link: {} - Status: {}", href, responseCode);
                }
                
            } catch (Exception e) {
                brokenLinks.add(href + " (Error: " + e.getMessage() + ")");
                logger.warn("Error checking search result link: {} - {}", href, e.getMessage());
            }
        }
        
        // Log results
        logStep("Search results link validation completed:");
        logStep("Links checked: " + linksToCheck);
        logStep("Valid links: " + validLinks.size());
        logStep("Broken links: " + brokenLinks.size());
        
        // Assert that no broken links were found
        Assert.assertTrue(brokenLinks.isEmpty(), 
            "Found " + brokenLinks.size() + " broken search result links: " + brokenLinks.toString());
        
        logVerification("All checked search result links are valid - no broken links found");
    }
    
    @Test(description = "Check specific Google service links")
    @Story("Google Services Link Validation")
    @Severity(SeverityLevel.MINOR)
    public void testGoogleServiceLinks() {
        logStep("Testing specific Google service links");
        
        List<String> serviceUrls = List.of(
            "https://www.google.com",
            "https://images.google.com",
            "https://accounts.google.com",
            "https://support.google.com"
        );
        
        List<String> brokenServices = new ArrayList<>();
        List<String> validServices = new ArrayList<>();
        
        for (String url : serviceUrls) {
            try {
                int responseCode = getResponseCode(url);
                
                if (responseCode >= 200 && responseCode < 400) {
                    validServices.add(url + " (Status: " + responseCode + ")");
                    logger.debug("Valid Google service: {} - Status: {}", url, responseCode);
                } else {
                    brokenServices.add(url + " (Status: " + responseCode + ")");
                    logger.warn("Broken Google service: {} - Status: {}", url, responseCode);
                }
                
            } catch (Exception e) {
                brokenServices.add(url + " (Error: " + e.getMessage() + ")");
                logger.warn("Error checking Google service: {} - {}", url, e.getMessage());
            }
        }
        
        // Log results
        logStep("Google services validation completed:");
        logStep("Services checked: " + serviceUrls.size());
        logStep("Valid services: " + validServices.size());
        logStep("Broken services: " + brokenServices.size());
        
        // Assert that Google services are accessible
        Assert.assertTrue(brokenServices.isEmpty(), 
            "Found " + brokenServices.size() + " broken Google services: " + brokenServices.toString());
        
        logVerification("All Google services are accessible");
    }
    
    /**
     * Get HTTP response code for a given URL
     * @param urlString URL to check
     * @return HTTP response code
     * @throws IOException if connection fails
     */
    @SuppressWarnings("deprecation")
    private int getResponseCode(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Set connection properties
        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(5000); // 5 seconds
        connection.setReadTimeout(5000); // 5 seconds
        connection.setInstanceFollowRedirects(true);
        
        // Set user agent to avoid blocking
        connection.setRequestProperty("User-Agent", 
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        
        try {
            connection.connect();
            return connection.getResponseCode();
        } finally {
            connection.disconnect();
        }
    }
}
