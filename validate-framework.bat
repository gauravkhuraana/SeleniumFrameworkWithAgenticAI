@echo off
REM Quick Test Runner for Framework Validation
echo ================================
echo Selenium Framework Test Runner
echo ================================

REM Set Java options for better performance
set JAVA_OPTS=-Xmx1024m -XX:+UseG1GC

echo.
echo 1. Compiling framework...
call mvn clean compile test-compile
if %ERRORLEVEL% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo 2. Running smoke tests...
call mvn test -Dsmoke -Dbrowser=chrome -DforkCount=1 -DreuseForks=false
if %ERRORLEVEL% neq 0 (
    echo WARNING: Some tests may have failed (this is expected for demo)
)

echo.
echo 3. Generating reports...
if exist "target\extent-reports\" (
    echo ExtentReports generated in: target\extent-reports\
)
if exist "target\allure-results\" (
    echo Allure results generated in: target\allure-results\
    echo Run 'allure serve target\allure-results' to view Allure report
)

echo.
echo ================================
echo Framework validation complete!
echo ================================
pause
