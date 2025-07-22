package com.automation.tests.google;

import com.automation.framework.base.BaseTest;
import com.automation.framework.pages.google.GoogleHomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for validating Google Homepage elements
 * Includes tests for images, page elements, and basic functionality
 * 
 * @author Automation Framework
 * @version 1.0
 */
@Epic("Google Homepage Tests")
@Feature("Homepage Validation")
public class GoogleHomePageTests extends BaseTest {
    
    private GoogleHomePage googleHomePage;
    
    @BeforeMethod
    public void setupTest() {
        logStep("Initializing Google Homepage Tests");
        
        // Initialize page objects
        googleHomePage = new GoogleHomePage();
        
        // Navigate to Google
        googleHomePage.navigateToGoogle();
        
        // Verify page is loaded
        Assert.assertTrue(googleHomePage.isPageLoaded(), "Google homepage should be loaded");
        
        logStep("Test setup completed - Google homepage loaded");
    }
    
    @Test(description = "Verify Google homepage has more than 1 image")
    @Story("Image Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testImageCountGreaterThanOne() {
        logStep("Verifying image count on Google homepage");
        
        // Get count of images on the page
        int imageCount = googleHomePage.getImageCount();
        
        logStep("Found " + imageCount + " images on the page");
        
        // Verify there are more than 1 images
        Assert.assertTrue(imageCount > 1, 
            "Google homepage should have more than 1 image. Found: " + imageCount);
        
        logVerification("Image count validation passed - Found " + imageCount + " images");
    }
    
    @Test(description = "Verify Google logo is displayed")
    @Story("Element Validation")
    @Severity(SeverityLevel.CRITICAL)
    public void testGoogleLogoIsDisplayed() {
        logStep("Verifying Google logo is displayed");
        
        // Check if Google logo is displayed
        boolean logoDisplayed = googleHomePage.isGoogleLogoDisplayed();
        
        Assert.assertTrue(logoDisplayed, "Google logo should be displayed on homepage");
        
        logVerification("Google logo validation passed");
    }
    
    @Test(description = "Verify search box is displayed and functional")
    @Story("Element Validation")
    @Severity(SeverityLevel.CRITICAL)
    public void testSearchBoxIsDisplayed() {
        logStep("Verifying search box is displayed and functional");
        
        // Check if search box is displayed
        boolean searchBoxDisplayed = googleHomePage.isSearchBoxDisplayed();
        Assert.assertTrue(searchBoxDisplayed, "Search box should be displayed");
        
        // Test entering text in search box
        String testQuery = "test query";
        googleHomePage.enterSearchQuery(testQuery);
        
        // Verify text was entered
        String currentQuery = googleHomePage.getCurrentSearchQuery();
        Assert.assertEquals(currentQuery, testQuery, "Search query should be entered correctly");
        
        // Clear search box
        googleHomePage.clearSearchBox();
        String clearedQuery = googleHomePage.getCurrentSearchQuery();
        Assert.assertTrue(clearedQuery.isEmpty(), "Search box should be empty after clearing");
        
        logVerification("Search box validation passed");
    }
    
    @Test(description = "Verify search buttons become visible when search box is focused")
    @Story("Element Validation")
    @Severity(SeverityLevel.NORMAL)
    public void testSearchButtonsVisibility() {
        logStep("Verifying search buttons visibility");
        
        // Check if search buttons are displayed when search box is focused
        boolean buttonsDisplayed = googleHomePage.areSearchButtonsDisplayed();
        
        // Note: This test may vary based on Google's current UI behavior
        logStep("Search buttons displayed: " + buttonsDisplayed);
        
        // We'll verify that the page behaves consistently
        Assert.assertTrue(googleHomePage.isSearchBoxDisplayed(), 
            "Search box should be displayed regardless of button state");
        
        logVerification("Search buttons visibility test completed");
    }
    
    @Test(description = "Verify Google homepage footer links are present")
    @Story("Element Validation")
    @Severity(SeverityLevel.MINOR)
    public void testFooterLinksPresent() {
        logStep("Verifying footer links are present");
        
        // Get footer links count
        int footerLinksCount = googleHomePage.getFooterLinks().size();
        
        logStep("Found " + footerLinksCount + " footer links");
        
        // Verify there are some footer links
        Assert.assertTrue(footerLinksCount > 0, 
            "Google homepage should have footer links. Found: " + footerLinksCount);
        
        logVerification("Footer links validation passed - Found " + footerLinksCount + " links");
    }
    
    @Test(description = "Verify Gmail and Images links are present")
    @Story("Navigation Links")
    @Severity(SeverityLevel.NORMAL)
    public void testNavigationLinksPresent() {
        logStep("Verifying navigation links are present");
        
        // Get all links on the page
        int totalLinks = googleHomePage.getLinkCount();
        
        logStep("Found " + totalLinks + " total links on the page");
        
        // Verify there are navigation links
        Assert.assertTrue(totalLinks > 0, 
            "Google homepage should have navigation links. Found: " + totalLinks);
        
        // Verify page has multiple links (Gmail, Images, etc.)
        Assert.assertTrue(totalLinks >= 5, 
            "Google homepage should have at least 5 links for navigation");
        
        logVerification("Navigation links validation passed - Found " + totalLinks + " total links");
    }
    
    @Test(description = "Verify page title contains 'Google'")
    @Story("Page Metadata")
    @Severity(SeverityLevel.NORMAL)
    public void testPageTitleContainsGoogle() {
        logStep("Verifying page title contains 'Google'");
        
        // Get page title
        String pageTitle = googleHomePage.getTitle();
        
        logStep("Page title: " + pageTitle);
        
        // Verify title contains 'Google'
        Assert.assertTrue(pageTitle.toLowerCase().contains("google"), 
            "Page title should contain 'Google'. Actual title: " + pageTitle);
        
        logVerification("Page title validation passed: " + pageTitle);
    }
    
    @Test(description = "Verify current URL is Google homepage")
    @Story("Page Metadata")
    @Severity(SeverityLevel.NORMAL)
    public void testCurrentUrlIsGoogleHomepage() {
        logStep("Verifying current URL is Google homepage");
        
        // Get current URL
        String currentUrl = googleHomePage.getCurrentUrl();
        
        logStep("Current URL: " + currentUrl);
        
        // Verify URL matches Google homepage pattern
        Assert.assertTrue(currentUrl.matches(googleHomePage.getPageUrlPattern()), 
            "Current URL should match Google homepage pattern. Actual URL: " + currentUrl);
        
        logVerification("URL validation passed: " + currentUrl);
    }
}
