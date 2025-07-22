package com.automation.framework.pages.google;

import com.automation.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * Page Object Model for Google Homepage
 * Contains elements and methods specific to Google's main page
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class GoogleHomePage extends BasePage {
    
    // Page URL pattern
    private static final String PAGE_URL_PATTERN = ".*google\\.(com|co\\..*)/?$";
    
    // Locators using @FindBy annotation
    @FindBy(name = "q")
    private WebElement searchBox;
    
    @FindBy(css = "input[value='Google Search']")
    private WebElement googleSearchButton;
    
    @FindBy(css = "input[value=\"I'm Feeling Lucky\"]")
    private WebElement feelingLuckyButton;
    
    @FindBy(css = "div[role='button'][aria-label='Google Search']")
    private WebElement searchButtonAlternate;
    
    @FindBy(css = "a[aria-label='Google apps']")
    private WebElement googleAppsButton;
    
    @FindBy(linkText = "Gmail")
    private WebElement gmailLink;
    
    @FindBy(linkText = "Images")
    private WebElement imagesLink;
    
    @FindBy(css = "img[alt='Google']")
    private WebElement googleLogo;
    
    @FindBy(css = "div.FPdoLc.lJ9FBc input[name='btnI']")
    private WebElement feelingLuckyButtonVisible;
    
    @FindBy(css = "div.FPdoLc.lJ9FBc input[name='btnK']")
    private WebElement googleSearchButtonVisible;
    
    // Additional locators using By
    private final By searchSuggestionsLocator = By.cssSelector("ul[role='listbox'] li");
    private final By footerLinksLocator = By.cssSelector("div#fsl a");
    private final By allImagesLocator = By.cssSelector("img");
    private final By allLinksLocator = By.tagName("a");
    private final By searchButtonsContainerLocator = By.cssSelector("div.FPdoLc.lJ9FBc");
    
    /**
     * Constructor
     */
    public GoogleHomePage() {
        super();
        logger.info("GoogleHomePage initialized");
    }
    
    /**
     * Navigate to Google homepage
     * @return GoogleHomePage instance
     */
    public GoogleHomePage navigateToGoogle() {
        String url = config.getApplicationUrl();
        logger.info("Navigating to Google homepage: {}", url);
        navigateTo(url);
        waitForPageToLoad();
        return this;
    }
    
    /**
     * Check if Google homepage is loaded
     * @return true if page is loaded, false otherwise
     */
    @Override
    public boolean isPageLoaded() {
        try {
            waitUtils.waitForElementToBeVisible(By.name("q"), 10);
            waitUtils.waitForElementToBeVisible(By.cssSelector("img[alt='Google']"), 10);
            logger.info("Google homepage is loaded successfully");
            return true;
        } catch (Exception e) {
            logger.error("Google homepage is not loaded: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get page URL pattern
     * @return URL pattern
     */
    @Override
    public String getPageUrlPattern() {
        return PAGE_URL_PATTERN;
    }
    
    /**
     * Enter search query in search box
     * @param query Search query
     * @return GoogleHomePage instance
     */
    public GoogleHomePage enterSearchQuery(String query) {
        logger.info("Entering search query: {}", query);
        waitUtils.waitForElementToBeVisible(By.name("q"));
        searchBox.clear();
        searchBox.sendKeys(query);
        logger.debug("Search query entered successfully");
        return this;
    }
    
    /**
     * Click Google Search button
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage clickGoogleSearch() {
        logger.info("Clicking Google Search button");
        try {
            // Try to click the visible search button first
            if (isDisplayed(By.cssSelector("div.FPdoLc.lJ9FBc input[name='btnK']"))) {
                click(By.cssSelector("div.FPdoLc.lJ9FBc input[name='btnK']"));
            } else {
                // Fallback to the other search button
                click(By.cssSelector("input[value='Google Search']"));
            }
            logger.debug("Google Search button clicked successfully");
        } catch (Exception e) {
            logger.warn("Could not click search button, trying Enter key: {}", e.getMessage());
            searchBox.sendKeys(Keys.ENTER);
        }
        return new GoogleSearchResultsPage();
    }
    
    /**
     * Click I'm Feeling Lucky button
     * @return Current page (varies based on lucky result)
     */
    public BasePage clickFeelingLucky() {
        logger.info("Clicking I'm Feeling Lucky button");
        try {
            if (isDisplayed(By.cssSelector("div.FPdoLc.lJ9FBc input[name='btnI']"))) {
                click(By.cssSelector("div.FPdoLc.lJ9FBc input[name='btnI']"));
            } else {
                click(By.cssSelector("input[value=\"I'm Feeling Lucky\"]"));
            }
            logger.debug("I'm Feeling Lucky button clicked successfully");
        } catch (Exception e) {
            logger.error("Could not click I'm Feeling Lucky button: {}", e.getMessage());
            throw e;
        }
        return this;
    }
    
    /**
     * Perform search using Enter key
     * @param query Search query
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage searchUsingEnterKey(String query) {
        logger.info("Performing search using Enter key with query: {}", query);
        enterSearchQuery(query);
        searchBox.sendKeys(Keys.ENTER);
        logger.debug("Search performed using Enter key");
        return new GoogleSearchResultsPage();
    }
    
    /**
     * Perform complete search operation
     * @param query Search query
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage performSearch(String query) {
        logger.info("Performing complete search with query: {}", query);
        enterSearchQuery(query);
        return clickGoogleSearch();
    }
    
    /**
     * Get search suggestions
     * @return List of search suggestions
     */
    public List<WebElement> getSearchSuggestions() {
        logger.info("Getting search suggestions");
        try {
            List<WebElement> suggestions = waitUtils.waitForElementsToBeVisible(searchSuggestionsLocator);
            logger.debug("Found {} search suggestions", suggestions.size());
            return suggestions;
        } catch (Exception e) {
            logger.warn("Could not find search suggestions: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Click on specific search suggestion
     * @param suggestionText Text of the suggestion to click
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage clickSearchSuggestion(String suggestionText) {
        logger.info("Clicking search suggestion: {}", suggestionText);
        List<WebElement> suggestions = getSearchSuggestions();
        for (WebElement suggestion : suggestions) {
            if (suggestion.getText().toLowerCase().contains(suggestionText.toLowerCase())) {
                suggestion.click();
                logger.debug("Clicked search suggestion: {}", suggestionText);
                return new GoogleSearchResultsPage();
            }
        }
        logger.warn("Search suggestion not found: {}", suggestionText);
        throw new RuntimeException("Search suggestion not found: " + suggestionText);
    }
    
    /**
     * Click Gmail link
     */
    public void clickGmailLink() {
        logger.info("Clicking Gmail link");
        click(By.linkText("Gmail"));
        logger.debug("Gmail link clicked");
    }
    
    /**
     * Click Images link
     */
    public void clickImagesLink() {
        logger.info("Clicking Images link");
        click(By.linkText("Images"));
        logger.debug("Images link clicked");
    }
    
    /**
     * Click Google Apps button
     */
    public void clickGoogleApps() {
        logger.info("Clicking Google Apps button");
        try {
            click(By.cssSelector("a[aria-label='Google apps']"));
            logger.debug("Google Apps button clicked");
        } catch (Exception e) {
            logger.warn("Could not click Google Apps button: {}", e.getMessage());
        }
    }
    
    /**
     * Get all images on the page
     * @return List of image elements
     */
    public List<WebElement> getAllImages() {
        logger.info("Getting all images on the page");
        List<WebElement> images = findElements(allImagesLocator);
        logger.debug("Found {} images on the page", images.size());
        return images;
    }
    
    /**
     * Get count of images on the page
     * @return Number of images
     */
    public int getImageCount() {
        int count = getAllImages().size();
        logger.info("Total images on page: {}", count);
        return count;
    }
    
    /**
     * Get all links on the page
     * @return List of link elements
     */
    public List<WebElement> getAllLinks() {
        logger.info("Getting all links on the page");
        List<WebElement> links = findElements(allLinksLocator);
        logger.debug("Found {} links on the page", links.size());
        return links;
    }
    
    /**
     * Get count of links on the page
     * @return Number of links
     */
    public int getLinkCount() {
        int count = getAllLinks().size();
        logger.info("Total links on page: {}", count);
        return count;
    }
    
    /**
     * Get footer links
     * @return List of footer link elements
     */
    public List<WebElement> getFooterLinks() {
        logger.info("Getting footer links");
        try {
            List<WebElement> footerLinks = findElements(footerLinksLocator);
            logger.debug("Found {} footer links", footerLinks.size());
            return footerLinks;
        } catch (Exception e) {
            logger.warn("Could not find footer links: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Check if Google logo is displayed
     * @return true if logo is displayed
     */
    public boolean isGoogleLogoDisplayed() {
        boolean displayed = isDisplayed(By.cssSelector("img[alt='Google']"));
        logger.debug("Google logo displayed: {}", displayed);
        return displayed;
    }
    
    /**
     * Check if search box is displayed
     * @return true if search box is displayed
     */
    public boolean isSearchBoxDisplayed() {
        boolean displayed = isDisplayed(By.name("q"));
        logger.debug("Search box displayed: {}", displayed);
        return displayed;
    }
    
    /**
     * Check if search buttons are displayed
     * @return true if search buttons are displayed
     */
    public boolean areSearchButtonsDisplayed() {
        try {
            // Focus on search box to make buttons visible
            searchBox.click();
            waitUtils.sleep(500); // Small wait for buttons to appear
            
            boolean buttonsDisplayed = isDisplayed(searchButtonsContainerLocator);
            logger.debug("Search buttons displayed: {}", buttonsDisplayed);
            return buttonsDisplayed;
        } catch (Exception e) {
            logger.warn("Could not check search buttons visibility: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get current search query from search box
     * @return Current search query
     */
    public String getCurrentSearchQuery() {
        String query = getAttribute(By.name("q"), "value");
        logger.debug("Current search query: {}", query);
        return query;
    }
    
    /**
     * Clear search box
     * @return GoogleHomePage instance
     */
    public GoogleHomePage clearSearchBox() {
        logger.info("Clearing search box");
        searchBox.clear();
        logger.debug("Search box cleared");
        return this;
    }
    
    /**
     * Wait for search suggestions to appear
     * @return true if suggestions appeared
     */
    public boolean waitForSearchSuggestions() {
        logger.info("Waiting for search suggestions to appear");
        try {
            waitUtils.waitForElementToBeVisible(searchSuggestionsLocator, 5);
            logger.debug("Search suggestions appeared");
            return true;
        } catch (Exception e) {
            logger.debug("Search suggestions did not appear: {}", e.getMessage());
            return false;
        }
    }
}
