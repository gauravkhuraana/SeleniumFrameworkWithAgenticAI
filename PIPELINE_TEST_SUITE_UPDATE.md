# Pipeline Test Suite Configuration Update

## Changes Made

### 1. Updated Main Pipeline (`selenium-tests.yml`)
**Before:**
```yaml
- name: Run Smoke Tests
  run: mvn clean test -Dbrowser.headless=true -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml
```

**After:**
```yaml
- name: Run All Tests (Sequential)
  run: mvn clean test -Dbrowser.headless=true -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/all-tests.xml -Dthread.count=1
```

### 2. Updated Simple Test Runner (`simple-test-runner.yml`)
**Before:**
```yaml
- name: Run Smoke Tests
  run: mvn clean test -Dbrowser.headless=true -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml
```

**After:**
```yaml
- name: Run All Tests
  run: mvn clean test -Dbrowser.headless=true -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/all-tests.xml -Dthread.count=1
```

### 3. Manual Test Runner (No changes needed)
The `manual-test-runner.yml` already supports all test suites through a dropdown selection.

## Test Suites Available

| Suite File | Description | Test Classes Included |
|------------|-------------|----------------------|
| `smoke-tests.xml` | Quick validation tests | GoogleHomePageTests (basic tests) |
| `all-tests.xml` | Comprehensive test suite | GoogleHomePageTests, GoogleSearchTests, GoogleBrokenLinksTests |
| `parallel-tests.xml` | Parallel execution tests | All tests with higher thread count |
| `regression-tests.xml` | Extended regression tests | Full regression test coverage |

## What Will Run Now

### Automatic Triggers (Push/PR):
- **`selenium-tests.yml`**: Runs ALL tests sequentially in CI
- **`simple-test-runner.yml`**: Runs ALL tests sequentially in CI

### Manual Trigger:
- **`manual-test-runner.yml`**: Choose any test suite via dropdown

## Test Classes That Will Execute

1. **GoogleHomePageTests**
   - `testGoogleLogoIsDisplayed`
   - `testSearchBoxIsDisplayed` 
   - `testImageCountGreaterThanOne`

2. **GoogleSearchTests**
   - `testSearchSeleniumWebDriver`

3. **GoogleBrokenLinksTests**
   - `testBrokenLinksOnHomepage`

## Benefits of This Change

✅ **Comprehensive Coverage**: All test classes now run in CI  
✅ **Better Bug Detection**: More tests = higher chance of catching issues  
✅ **Consistent with Local Development**: Same tests run locally and in CI  
✅ **Sequential Execution**: Avoids Chrome session conflicts in CI  
✅ **Proper Reporting**: All test results included in reports  

## Performance Impact

- **Before**: ~1-2 minutes (smoke tests only)
- **After**: ~3-5 minutes (all tests)
- **Trade-off**: Slightly longer CI time for much better test coverage

## Rollback Plan

If the full test suite causes issues, you can easily revert by changing back to:
```yaml
-DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml
```

## Next Steps

1. **Commit and Push** these changes
2. **Monitor CI Pipeline** execution times and stability
3. **Review Test Results** for any new issues
4. **Consider Parallel Execution** once CI stability is confirmed

---
**Status**: ✅ **READY** - All workflows updated to run comprehensive test suite
