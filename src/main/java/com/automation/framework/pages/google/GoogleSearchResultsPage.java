package com.automation.framework.pages.google;

import com.automation.framework.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object Model for Google Search Results Page
 * Contains elements and methods specific to Google's search results page
 * 
 * @author Automation Framework
 * @version 1.0
 */
public class GoogleSearchResultsPage extends BasePage {
    
    // Page URL pattern
    private static final String PAGE_URL_PATTERN = ".*google\\.(com|co\\..*)/(search|webhp).*";
    
    // Locators using @FindBy annotation
    @FindBy(name = "q")
    private WebElement searchBox;
    
    @FindBy(css = "input[aria-label='Search']")
    private WebElement searchButton;
    
    @FindBy(css = "div#search")
    private WebElement searchResultsContainer;
    
    @FindBy(css = "div[data-ved] h3")
    private List<WebElement> searchResultTitles;
    
    @FindBy(css = "div[data-ved] a")
    private List<WebElement> searchResultLinks;
    
    @FindBy(css = "div#result-stats")
    private WebElement resultStats;
    
    @FindBy(css = "div#pnnext")
    private WebElement nextPageButton;
    
    @FindBy(css = "div#pnprev")
    private WebElement previousPageButton;
    
    @FindBy(css = "td[role='heading']")
    private List<WebElement> paginationNumbers;
    
    // Additional locators using By
    private final By searchResultsLocator = By.cssSelector("div.g");
    private final By searchResultLinksLocator = By.cssSelector("div.g a[href]:not([role])");
    private final By searchResultSnippetsLocator = By.cssSelector("div.g .VwiC3b");
    private final By noResultsLocator = By.cssSelector("div#topstuff p, div#search p");
    private final By spellingCorrectionLocator = By.cssSelector("a.gL9Hy");
    private final By allImagesLocator = By.cssSelector("img");
    private final By allLinksLocator = By.tagName("a");
    
    /**
     * Constructor
     */
    public GoogleSearchResultsPage() {
        super();
        logger.info("GoogleSearchResultsPage initialized");
    }
    
    /**
     * Check if search results page is loaded
     * @return true if page is loaded, false otherwise
     */
    @Override
    public boolean isPageLoaded() {
        try {
            waitUtils.waitForElementToBeVisible(By.name("q"), 10);
            waitUtils.waitForElementToBeVisible(By.cssSelector("div#search, div#topstuff"), 10);
            logger.info("Google search results page is loaded successfully");
            return true;
        } catch (Exception e) {
            logger.error("Google search results page is not loaded: {}", e.getMessage());
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
     * Get all search results
     * @return List of search result elements
     */
    public List<WebElement> getSearchResults() {
        logger.info("Getting all search results");
        try {
            List<WebElement> results = waitUtils.waitForElementsToBeVisible(searchResultsLocator);
            logger.debug("Found {} search results", results.size());
            return results;
        } catch (Exception e) {
            logger.warn("Could not find search results: {}", e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Get count of search results
     * @return Number of search results
     */
    public int getSearchResultsCount() {
        int count = getSearchResults().size();
        logger.info("Total search results: {}", count);
        return count;
    }
    
    /**
     * Get search result titles
     * @return List of search result title texts
     */
    public List<String> getSearchResultTitles() {
        logger.info("Getting search result titles");
        List<WebElement> titleElements = findElements(By.cssSelector("div.g h3"));
        List<String> titles = titleElements.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isEmpty())
                .collect(Collectors.toList());
        logger.debug("Found {} search result titles", titles.size());
        return titles;
    }
    
    /**
     * Get search result links
     * @return List of search result links
     */
    public List<WebElement> getSearchResultLinks() {
        logger.info("Getting search result links");
        List<WebElement> links = findElements(searchResultLinksLocator);
        logger.debug("Found {} search result links", links.size());
        return links;
    }
    
    /**
     * Get all links on the page
     * @return List of all link elements
     */
    public List<WebElement> getAllLinks() {
        logger.info("Getting all links on the page");
        List<WebElement> links = findElements(allLinksLocator);
        logger.debug("Found {} total links on the page", links.size());
        return links;
    }
    
    /**
     * Get count of all links on the page
     * @return Number of links
     */
    public int getAllLinksCount() {
        int count = getAllLinks().size();
        logger.info("Total links on page: {}", count);
        return count;
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
     * Click on specific search result by title
     * @param titleText Partial or full title text to match
     * @return true if clicked, false if not found
     */
    public boolean clickSearchResultByTitle(String titleText) {
        logger.info("Clicking search result with title containing: {}", titleText);
        List<WebElement> results = getSearchResults();
        
        for (WebElement result : results) {
            try {
                WebElement titleElement = result.findElement(By.cssSelector("h3"));
                if (titleElement.getText().toLowerCase().contains(titleText.toLowerCase())) {
                    WebElement linkElement = result.findElement(By.cssSelector("a"));
                    linkElement.click();
                    logger.debug("Clicked search result with title: {}", titleElement.getText());
                    return true;
                }
            } catch (Exception e) {
                logger.debug("Could not process search result: {}", e.getMessage());
            }
        }
        
        logger.warn("Search result with title containing '{}' not found", titleText);
        return false;
    }
    
    /**
     * Click on search result by index
     * @param index Index of the search result (0-based)
     * @return true if clicked, false if index out of bounds
     */
    public boolean clickSearchResultByIndex(int index) {
        logger.info("Clicking search result at index: {}", index);
        List<WebElement> results = getSearchResults();
        
        if (index >= 0 && index < results.size()) {
            try {
                WebElement result = results.get(index);
                WebElement linkElement = result.findElement(By.cssSelector("a"));
                linkElement.click();
                logger.debug("Clicked search result at index: {}", index);
                return true;
            } catch (Exception e) {
                logger.error("Could not click search result at index {}: {}", index, e.getMessage());
                return false;
            }
        } else {
            logger.warn("Search result index {} is out of bounds. Total results: {}", index, results.size());
            return false;
        }
    }
    
    /**
     * Perform new search from results page
     * @param query New search query
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage performNewSearch(String query) {
        logger.info("Performing new search with query: {}", query);
        waitUtils.waitForElementToBeVisible(By.name("q"));
        searchBox.clear();
        searchBox.sendKeys(query);
        searchBox.submit();
        waitForPageToLoad();
        logger.debug("New search performed successfully");
        return this;
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
     * Get result statistics text
     * @return Result statistics (e.g., "About 1,000,000 results")
     */
    public String getResultStats() {
        try {
            String stats = getText(By.cssSelector("div#result-stats"));
            logger.debug("Result statistics: {}", stats);
            return stats;
        } catch (Exception e) {
            logger.warn("Could not get result statistics: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Check if there are search results
     * @return true if results are present, false otherwise
     */
    public boolean hasSearchResults() {
        boolean hasResults = getSearchResultsCount() > 0;
        logger.debug("Has search results: {}", hasResults);
        return hasResults;
    }
    
    /**
     * Check if "no results" message is displayed
     * @return true if no results message is shown
     */
    public boolean isNoResultsDisplayed() {
        try {
            boolean noResults = isDisplayed(noResultsLocator);
            logger.debug("No results displayed: {}", noResults);
            return noResults;
        } catch (Exception e) {
            logger.debug("Could not check for no results message: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Get spelling correction suggestion if present
     * @return Spelling correction text, or empty string if not present
     */
    public String getSpellingCorrection() {
        try {
            String correction = getText(spellingCorrectionLocator);
            logger.debug("Spelling correction found: {}", correction);
            return correction;
        } catch (Exception e) {
            logger.debug("No spelling correction found: {}", e.getMessage());
            return "";
        }
    }
    
    /**
     * Click spelling correction if present
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage clickSpellingCorrection() {
        logger.info("Clicking spelling correction");
        try {
            click(spellingCorrectionLocator);
            waitForPageToLoad();
            logger.debug("Spelling correction clicked successfully");
        } catch (Exception e) {
            logger.warn("Could not click spelling correction: {}", e.getMessage());
        }
        return this;
    }
    
    /**
     * Navigate to next page of results
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage goToNextPage() {
        logger.info("Navigating to next page of results");
        try {
            click(By.cssSelector("a#pnnext"));
            waitForPageToLoad();
            logger.debug("Navigated to next page successfully");
        } catch (Exception e) {
            logger.warn("Could not navigate to next page: {}", e.getMessage());
        }
        return this;
    }
    
    /**
     * Navigate to previous page of results
     * @return GoogleSearchResultsPage instance
     */
    public GoogleSearchResultsPage goToPreviousPage() {
        logger.info("Navigating to previous page of results");
        try {
            click(By.cssSelector("a#pnprev"));
            waitForPageToLoad();
            logger.debug("Navigated to previous page successfully");
        } catch (Exception e) {
            logger.warn("Could not navigate to previous page: {}", e.getMessage());
        }
        return this;
    }
    
    /**
     * Check if next page button is available
     * @return true if next page is available
     */
    public boolean isNextPageAvailable() {
        boolean available = isDisplayed(By.cssSelector("a#pnnext"));
        logger.debug("Next page available: {}", available);
        return available;
    }
    
    /**
     * Check if previous page button is available
     * @return true if previous page is available
     */
    public boolean isPreviousPageAvailable() {
        boolean available = isDisplayed(By.cssSelector("a#pnprev"));
        logger.debug("Previous page available: {}", available);
        return available;
    }
    
    /**
     * Get search result snippets/descriptions
     * @return List of search result snippet texts
     */
    public List<String> getSearchResultSnippets() {
        logger.info("Getting search result snippets");
        List<WebElement> snippetElements = findElements(searchResultSnippetsLocator);
        List<String> snippets = snippetElements.stream()
                .map(WebElement::getText)
                .filter(text -> !text.isEmpty())
                .toList();
        logger.debug("Found {} search result snippets", snippets.size());
        return snippets;
    }
    
    /**
     * Navigate back to Google homepage
     * @return GoogleHomePage instance
     */
    public GoogleHomePage goToHomePage() {
        logger.info("Navigating back to Google homepage");
        navigateTo(config.getApplicationUrl());
        return new GoogleHomePage();
    }
}
