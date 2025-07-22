@echo off
REM Selenium TestNG Framework Test Runner Script for Windows

echo ========================================
echo   Selenium TestNG Framework Runner
echo ========================================

if "%~1"=="" (
    echo Usage: run-tests.bat [test-type] [browser] [additional-options]
    echo.
    echo Test types:
    echo   smoke      - Run smoke tests
    echo   regression - Run regression tests  
    echo   all        - Run all tests
    echo   parallel   - Run tests in parallel
    echo.
    echo Browsers:
    echo   chrome     - Chrome browser ^(default^)
    echo   firefox    - Firefox browser
    echo   edge       - Edge browser
    echo.
    echo Examples:
    echo   run-tests.bat smoke
    echo   run-tests.bat regression firefox
    echo   run-tests.bat all chrome -Dbrowser.headless=true
    echo   run-tests.bat parallel
    echo.
    goto :end
)

set TEST_TYPE=%1
set BROWSER=%2
set ADDITIONAL_OPTIONS=%3 %4 %5 %6 %7 %8 %9

REM Set default browser if not provided
if "%BROWSER%"=="" set BROWSER=chrome

echo Running %TEST_TYPE% tests with %BROWSER% browser...
echo Additional options: %ADDITIONAL_OPTIONS%
echo.

REM Run tests based on type
if "%TEST_TYPE%"=="smoke" (
    mvn clean test -Dsmoke -Dbrowser=%BROWSER% %ADDITIONAL_OPTIONS%
) else if "%TEST_TYPE%"=="regression" (
    mvn clean test -Dregression -Dbrowser=%BROWSER% %ADDITIONAL_OPTIONS%
) else if "%TEST_TYPE%"=="all" (
    mvn clean test -Dall -Dbrowser=%BROWSER% %ADDITIONAL_OPTIONS%
) else if "%TEST_TYPE%"=="parallel" (
    mvn clean test -Dparallel -Dbrowser=%BROWSER% %ADDITIONAL_OPTIONS%
) else (
    echo Invalid test type: %TEST_TYPE%
    echo Valid options: smoke, regression, all, parallel
    goto :end
)

echo.
echo ========================================
echo Test execution completed!
echo.
echo Check reports in:
echo - ExtentReports: test-output\extent-reports\
echo - Screenshots: test-output\screenshots\
echo - Logs: logs\automation.log
echo.
echo To generate Allure report:
echo   mvn allure:report
echo   mvn allure:serve
echo ========================================

:end
pause
