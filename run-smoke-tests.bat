@echo off
echo ========================================
echo    Selenium Framework Test Execution
echo ========================================

REM Set environment variables
set MAVEN_OPTS=-Xmx1024m

echo.
echo Running Smoke Tests...
echo.

REM Run tests with explicit suite file
mvn clean test -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml -Dbrowser=chrome -DforkCount=1 -DreuseForks=false

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo    TESTS COMPLETED SUCCESSFULLY!
    echo ========================================
    echo.
    echo Reports generated:
    echo - ExtentReports: target\extent-reports\index.html
    echo - TestNG: target\surefire-reports\index.html
    echo - Allure: target\allure-results\
    echo.
) else (
    echo.
    echo ========================================
    echo    TESTS COMPLETED WITH ISSUES
    echo ========================================
    echo Some tests may have failed (this is expected for demo)
    echo Check reports for details.
    echo.
)

pause
