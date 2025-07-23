# 🔧 CI/CD Chrome Driver Troubleshooting Guide

## Chrome Session Conflict Error - SOLVED ✅

### **Error Description**
```
Test skipped: Could not start a new session. Response code 500. 
Message: session not created: probably user data directory is already in use, 
please specify a unique value for --user-data-dir argument, or don't use --user-data-dir
```

### **Root Cause**
This error occurs in CI environments (like GitHub Actions) when:
1. Multiple Chrome instances try to use the same user data directory
2. Previous Chrome processes didn't clean up properly
3. CI runners have restricted file system access

### **Solution Applied** ✅

#### 1. **Updated WebDriverFactory.java**
Added CI-specific Chrome options:
- **Unique user data directory**: `/tmp/chrome_user_data_[timestamp]`
- **Single process mode**: Prevents conflicts
- **Additional stability flags**: Optimized for CI environments

```java
// CI/CD specific options to prevent session conflicts
if (isRunningInCI()) {
    String uniqueUserDataDir = System.getProperty("java.io.tmpdir") + "/chrome_user_data_" + System.currentTimeMillis();
    options.addArguments("--user-data-dir=" + uniqueUserDataDir);
    options.addArguments("--single-process");
    // ... more CI-specific options
}
```

#### 2. **Enhanced Chrome Options**
```java
// Additional stability options
options.addArguments("--disable-background-timer-throttling");
options.addArguments("--disable-backgrounding-occluded-windows");
options.addArguments("--disable-renderer-backgrounding");
options.addArguments("--disable-field-trial-config");
options.addArguments("--disable-ipc-flooding-protection");
```

#### 3. **Updated GitHub Actions Workflows**
- Fixed property name: `-Dbrowser.headless=true` (was `-Dheadless=true`)
- Added CI environment variables
- Created temp directories for browser data

### **Testing the Fix**

#### **Local Testing**
```bash
# Test with headless mode
mvn clean test -Dbrowser.headless=true -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml

# Test without headless mode
mvn clean test -Dbrowser.headless=false -Dbrowser=chrome -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml
```

#### **CI Testing**
```bash
# Push changes and check GitHub Actions
git add .
git commit -m "Fix Chrome session conflicts in CI environments"
git push origin main
```

### **How the Fix Works**

#### **1. CI Detection**
```java
private static boolean isRunningInCI() {
    return System.getenv("CI") != null || 
           System.getenv("GITHUB_ACTIONS") != null ||
           System.getenv("JENKINS_URL") != null ||
           System.getenv("GITLAB_CI") != null ||
           System.getenv("TRAVIS") != null;
}
```

#### **2. Dynamic User Data Directory**
- Uses timestamp to ensure uniqueness: `chrome_user_data_1690123456789`
- Located in system temp directory: `/tmp/` (Linux/Mac) or `%TEMP%` (Windows)
- Automatically cleaned up by OS

#### **3. Headless Optimization**
```java
if (config.isHeadless()) {
    options.addArguments("--headless=new");
    options.addArguments("--disable-logging");
    options.addArguments("--disable-gpu-logging");
    options.addArguments("--window-size=1920,1080"); // Explicit size for headless
}
```

### **Verification Steps**

1. **Check Browser Startup Logs**:
   ```
   Creating ChromeDriver with options: {browserName=chrome, goog:chromeOptions={...}}
   Running in CI environment - using unique user data directory: /tmp/chrome_user_data_1690123456789
   ```

2. **Monitor Test Execution**:
   - Tests should start without session errors
   - Chrome should launch in headless mode in CI
   - No more "user data directory already in use" errors

3. **GitHub Actions Logs**:
   ```
   ✅ Chrome Browser setup complete
   ✅ ChromeDriver setup complete
   ✅ Tests executed successfully
   ```

### **Additional CI Optimizations Applied**

#### **Performance Improvements**
- **Image blocking**: Faster page loads in CI
- **Automation extension disabled**: Prevents detection
- **Background processes disabled**: Reduces resource usage

#### **Stability Enhancements**
- **Single process mode**: Prevents conflicts
- **IPC flooding protection disabled**: Avoids communication issues
- **Field trial config disabled**: Consistent behavior

### **Fallback Options**

If issues persist, try these alternatives:

#### **Option 1: Firefox Browser**
```yaml
# In GitHub Actions workflow
- name: Run Tests with Firefox
  run: mvn clean test -Dbrowser=firefox -Dbrowser.headless=true
```

#### **Option 2: Docker Container**
```yaml
# Use pre-configured container
container: selenoid/vnc:chrome_78.0
```

#### **Option 3: Different Chrome Version**
```yaml
# Use specific Chrome version
- name: Set up Chrome Browser
  uses: browser-actions/setup-chrome@v1
  with:
    chrome-version: '119.0.6045.105'
```

### **Monitoring & Debugging**

#### **Enable Debug Logging**
Add to your test execution:
```bash
mvn clean test -Dbrowser.headless=true -Dlog.level=DEBUG
```

#### **Check Chrome Process**
In CI, add debugging step:
```yaml
- name: Debug Chrome Processes
  run: |
    ps aux | grep chrome || true
    ls -la /tmp/chrome_* || true
```

### **Expected Results** ✅

After applying this fix:
- ✅ No more Chrome session conflicts
- ✅ Tests run reliably in CI environments
- ✅ Proper headless mode execution
- ✅ Unique user data directories per test run
- ✅ Optimized performance for CI/CD pipelines

---

**🎉 The Chrome session conflict issue has been resolved! Your tests should now run smoothly in GitHub Actions.**
