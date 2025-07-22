@echo off
REM Alternative Test Runner - Direct Class Execution
echo ========================================
echo   Direct TestNG Class Execution
echo ========================================

set MAVEN_OPTS=-Xmx1024m

echo.
echo 1. Running Google Homepage Tests...
mvn clean test -Dtest=GoogleHomePageTests -Dbrowser=chrome

echo.
echo 2. Running Google Search Tests...  
mvn test -Dtest=GoogleSearchTests -Dbrowser=chrome

echo.
echo 3. Running Google Broken Links Tests...
mvn test -Dtest=GoogleBrokenLinksTests -Dbrowser=chrome

echo.
echo ========================================
echo   All Test Classes Executed
echo ========================================

pause
