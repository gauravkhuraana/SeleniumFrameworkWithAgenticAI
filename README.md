# Modern Selenium TestNG Framework

A comprehensive, scalable, and modular test automation framework built with Java 11+, Selenium WebDriver, and TestNG. This framework follows the Page Object Model (POM) design pattern and supports cross-browser testing with parallel execution capabilities.

## 🎥 Watch the Framework in Action
[![Selenium Framework Demo](https://img.youtube.com/vi/YUOrGS7rDDU/maxresdefault.jpg)](https://www.youtube.com/watch?v=YUOrGS7rDDU)

## 🚀 Features

- **Cross-Browser Support**: Chrome, Firefox, Edge
- **Page Object Model (POM)**: Clean separation of test logic and page elements
- **Parallel Execution**: Local and remote test execution support
- **Multiple Reporting**: ExtentReports and Allure integration
- **Cloud Testing**: LambdaTest platform support
- **Comprehensive Logging**: Log4j2 with configurable levels
- **Test Data Management**: JSON-based test data with utility classes
- **Screenshot Capture**: Automatic screenshots on test failures
- **CI/CD Ready**: Maven-based build with configurable profiles

## 📋 Prerequisites

- Java 11 or higher
- Maven 3.6 or higher
- Chrome/Firefox/Edge browser installed
- Git (for version control)

## 🛠️ Framework Structure

```
selenium-testng-framework/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/automation/framework/
│   │   │       ├── base/           # Base test classes
│   │   │       ├── config/         # Configuration management
│   │   │       ├── driver/         # WebDriver management
│   │   │       ├── listeners/      # TestNG listeners
│   │   │       ├── pages/          # Page Object Model classes
│   │   │       └── utils/          # Utility classes
│   │   └── resources/
│   │       ├── config/             # Configuration files
│   │       └── log4j2.xml          # Logging configuration
│   └── test/
│       ├── java/
│       │   └── com/automation/tests/   # Test classes
│       └── resources/
│           ├── testdata/           # Test data files
│           └── testng-suites/      # TestNG suite files
├── test-output/                    # Test reports and screenshots
├── logs/                          # Application logs
├── pom.xml                        # Maven configuration
└── README.md                      # This file
```

## ⚙️ Configuration

### Application Configuration
Edit `src/main/resources/config/application.properties`:

```properties
# Browser Configuration
browser=chrome
browser.headless=false
browser.maximize=true

# Test Execution
test.thread.count=3
test.retry.count=1

# LambdaTest Configuration (if using cloud)
lambdatest.enabled=false
lambdatest.username=${LAMBDATEST_USERNAME}
lambdatest.access.key=${LAMBDATEST_ACCESS_KEY}
```

### Browser Configuration
Supported browsers:
- `chrome` (default)
- `firefox`
- `edge`

### LambdaTest Configuration
1. Set environment variables:
   ```bash
   export LAMBDATEST_USERNAME=your_username
   export LAMBDATEST_ACCESS_KEY=your_access_key
   ```
2. Enable in configuration:
   ```properties
   lambdatest.enabled=true
   ```

## 🏃‍♂️ Running Tests

### Local Execution

#### Run All Tests
```bash
mvn clean test -Dall
```

#### Run Smoke Tests
```bash
mvn clean test -Dsmoke
```

#### Run Regression Tests
```bash
mvn clean test -Dregression
```

#### Run Tests with Specific Browser
```bash
mvn clean test -Dall -Dbrowser=firefox
```

#### Run Tests in Headless Mode
```bash
mvn clean test -Dall -Dbrowser.headless=true
```

### Parallel Execution
```bash
mvn clean test -Dparallel
```

### Cloud Execution (LambdaTest)
```bash
mvn clean test -Dall -Dlambdatest.enabled=true
```

## 📊 Test Reports

### ExtentReports
- Location: `test-output/extent-reports/`
- Rich HTML reports with screenshots
- Test execution timeline and statistics

### Allure Reports
1. Generate Allure report:
   ```bash
   mvn allure:report
   ```
2. Serve report locally:
   ```bash
   mvn allure:serve
   ```

## 🧪 Sample Tests

The framework includes comprehensive tests for Google.com:

### 1. Homepage Validation Tests
- **Image Count Test**: Verifies more than 1 image is present
- **Logo Display Test**: Confirms Google logo is visible
- **Search Box Test**: Validates search functionality
- **Navigation Links Test**: Checks for broken links

### 2. Search Functionality Tests
- **Valid Search Tests**: Successful searches with result validation
- **Edge Case Tests**: Empty queries and special characters
- **Failing Tests**: Intentionally failing tests for demonstration

### 3. Broken Links Tests
- **Homepage Links**: Validates all homepage links are functional
- **Search Result Links**: Checks search result links (sample)
- **Google Services**: Validates core Google service URLs

## 📁 Test Data Management

Test data is stored in JSON format in `src/test/resources/testdata/`:

```json
{
  "searchQueries": {
    "valid": [
      {
        "query": "selenium webdriver",
        "expectedResultsContain": "selenium",
        "shouldPass": true
      }
    ]
  }
}
```

Access test data in tests:
```java
Map<String, Object> testData = testDataUtils.loadJsonDataAsMap("google-test-data.json");
String query = testDataUtils.getStringValue("google-test-data.json", "searchQueries.valid.0.query");
```

## 🔧 Extending the Framework

### Adding New Page Objects
1. Create page class extending `BasePage`
2. Define page elements using `@FindBy`
3. Implement `isPageLoaded()` and `getPageUrlPattern()` methods

Example:
```java
public class NewPage extends BasePage {
    @FindBy(id = "element-id")
    private WebElement element;
    
    @Override
    public boolean isPageLoaded() {
        return isDisplayed(By.id("element-id"));
    }
    
    @Override
    public String getPageUrlPattern() {
        return ".*newpage.*";
    }
}
```

### Adding New Tests
1. Create test class extending `BaseTest`
2. Add appropriate annotations (`@Epic`, `@Feature`, `@Story`)
3. Use page objects for interactions
4. Add logging and assertions

Example:
```java
@Epic("New Feature Tests")
@Feature("New Functionality")
public class NewTests extends BaseTest {
    
    @Test
    @Story("New Test Story")
    public void testNewFunctionality() {
        logStep("Starting new test");
        // Test implementation
        logVerification("Test completed successfully");
    }
}
```

## 🐛 Debugging

### Logging
- Logs are generated in `logs/automation.log`
- Configure log levels in `src/main/resources/log4j2.xml`
- Use different log levels: DEBUG, INFO, WARN, ERROR

### Screenshots
- Automatic screenshots on test failures
- Manual screenshots: `screenshotUtils.takeScreenshot("test-name")`
- Screenshots saved in `test-output/screenshots/`

### Browser Debugging
```bash
# Run with browser visible (non-headless)
mvn clean test -Dall -Dbrowser.headless=false

# Run single test for debugging
mvn clean test -Dtest=GoogleHomePageTests#testGoogleLogoIsDisplayed
```

## 🚀 CI/CD Integration

### GitHub Actions Example
```yaml
name: Test Automation
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 11
        uses: actions/setup-java@v2
        with:
          java-version: '11'
          distribution: 'adopt'
      - name: Run tests
        run: mvn clean test -Dsmoke -Dbrowser.headless=true
      - name: Generate Allure Report
        run: mvn allure:report
      - name: Upload reports
        uses: actions/upload-artifact@v2
        with:
          name: test-reports
          path: target/site/allure-maven-plugin/
```

### Jenkins Pipeline Example
```groovy
pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Test - Smoke') {
            steps {
                sh 'mvn clean test -Dsmoke -Dbrowser.headless=true'
            }
        }
        
        stage('Test - Regression') {
            when {
                branch 'main'
            }
            steps {
                sh 'mvn clean test -Dregression -Dbrowser.headless=true'
            }
        }
        
        stage('Reports') {
            steps {
                sh 'mvn allure:report'
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/allure-maven-plugin',
                    reportFiles: 'index.html',
                    reportName: 'Allure Report'
                ])
            }
        }
    }
}
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Support

For questions and support:
- Create an issue in the repository
- Review the documentation in the code comments
- Check the logs for debugging information

## 🔄 Version History

- **v1.0.0** - Initial release with core framework features
  - Cross-browser support (Chrome, Firefox, Edge)
  - Page Object Model implementation
  - ExtentReports and Allure integration
  - LambdaTest cloud execution support
  - Comprehensive Google.com test suite
  - Parallel execution capabilities

---

**Happy Testing! 🎯**
