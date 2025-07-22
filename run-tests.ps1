# PowerShell Test Runner for Selenium Framework
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Selenium Framework Test Execution   " -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan

# Set environment variables
$env:MAVEN_OPTS = "-Xmx1024m"

Write-Host ""
Write-Host "Starting test execution..." -ForegroundColor Green

try {
    # Test 1: Run with explicit suite file
    Write-Host "Method 1: Running with explicit suite file..." -ForegroundColor Yellow
    & mvn clean test "-DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml" "-Dbrowser=chrome"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Suite execution completed successfully!" -ForegroundColor Green
    } else {
        Write-Host "Suite execution had issues, trying alternative method..." -ForegroundColor Yellow
        
        # Test 2: Run individual test classes
        Write-Host "Method 2: Running individual test classes..." -ForegroundColor Yellow
        & mvn test "-Dtest=GoogleHomePageTests" "-Dbrowser=chrome"
    }
    
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Cyan
    Write-Host "   Test Execution Completed            " -ForegroundColor Cyan
    Write-Host "========================================" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "Reports generated:" -ForegroundColor Green
    Write-Host "- ExtentReports: target\extent-reports\index.html" -ForegroundColor White
    Write-Host "- TestNG: target\surefire-reports\index.html" -ForegroundColor White
    Write-Host "- Allure: target\allure-results\" -ForegroundColor White
    
} catch {
    Write-Host "Error during execution: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Press any key to continue..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
