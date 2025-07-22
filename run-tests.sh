#!/bin/bash
# Selenium TestNG Framework Test Runner Script for Unix/Linux/macOS

echo "========================================"
echo "  Selenium TestNG Framework Runner"
echo "========================================"

if [ $# -eq 0 ]; then
    echo "Usage: ./run-tests.sh [test-type] [browser] [additional-options]"
    echo ""
    echo "Test types:"
    echo "  smoke      - Run smoke tests"
    echo "  regression - Run regression tests"  
    echo "  all        - Run all tests"
    echo "  parallel   - Run tests in parallel"
    echo ""
    echo "Browsers:"
    echo "  chrome     - Chrome browser (default)"
    echo "  firefox    - Firefox browser"
    echo "  edge       - Edge browser"
    echo ""
    echo "Examples:"
    echo "  ./run-tests.sh smoke"
    echo "  ./run-tests.sh regression firefox"
    echo "  ./run-tests.sh all chrome -Dbrowser.headless=true"
    echo "  ./run-tests.sh parallel"
    echo ""
    exit 1
fi

TEST_TYPE=$1
BROWSER=${2:-chrome}  # Default to chrome if not provided
ADDITIONAL_OPTIONS="${@:3}"

echo "Running $TEST_TYPE tests with $BROWSER browser..."
echo "Additional options: $ADDITIONAL_OPTIONS"
echo ""

# Run tests based on type
case $TEST_TYPE in
    "smoke")
        mvn clean test -Dsmoke -Dbrowser=$BROWSER $ADDITIONAL_OPTIONS
        ;;
    "regression")
        mvn clean test -Dregression -Dbrowser=$BROWSER $ADDITIONAL_OPTIONS
        ;;
    "all")
        mvn clean test -Dall -Dbrowser=$BROWSER $ADDITIONAL_OPTIONS
        ;;
    "parallel")
        mvn clean test -Dparallel -Dbrowser=$BROWSER $ADDITIONAL_OPTIONS
        ;;
    *)
        echo "Invalid test type: $TEST_TYPE"
        echo "Valid options: smoke, regression, all, parallel"
        exit 1
        ;;
esac

echo ""
echo "========================================"
echo "Test execution completed!"
echo ""
echo "Check reports in:"
echo "- ExtentReports: test-output/extent-reports/"
echo "- Screenshots: test-output/screenshots/"
echo "- Logs: logs/automation.log"
echo ""
echo "To generate Allure report:"
echo "  mvn allure:report"
echo "  mvn allure:serve"
echo "========================================"
