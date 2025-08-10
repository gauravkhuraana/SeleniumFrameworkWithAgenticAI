# Chrome Session Conflict Fix - Complete Solution

## Problem Description

**Error Message:**
```
Test skipped: Could not start a new session. Response code 500. Message: session not created: probably user data directory is already in use, please specify a unique value for --user-data-dir argument, or don't use --user-data-dir
```

**Root Cause:**
- Multiple Chrome instances trying to use the same user data directory in CI environments
- Insufficient uniqueness in directory naming causing conflicts during parallel test execution
- Orphaned Chrome processes from previous test runs interfering with new sessions

## Solution Implemented

### 1. Enhanced User Data Directory Uniqueness

**Before:**
```java
String uniqueUserDataDir = System.getProperty("java.io.tmpdir") + "/chrome_user_data_" + System.currentTimeMillis();
```

**After:**
```java
// Create a truly unique directory using timestamp, thread name, and random component
String threadName = Thread.currentThread().getName().replaceAll("[^a-zA-Z0-9]", "_");
String randomComponent = String.valueOf((int)(Math.random() * 100000));
String timestamp = String.valueOf(System.currentTimeMillis());
String processId = String.valueOf(ProcessHandle.current().pid());
String uniqueUserDataDir = System.getProperty("java.io.tmpdir") + 
    "/chrome_user_data_" + timestamp + "_" + processId + "_" + threadName + "_" + randomComponent;
```

**Improvements:**
- ✅ **Process ID**: Unique across different JVM instances
- ✅ **Thread Name**: Unique within the same process
- ✅ **Random Component**: Additional entropy for uniqueness
- ✅ **Timestamp**: Temporal uniqueness
- ✅ **Sanitized Characters**: Safe directory names

### 2. Chrome Process Cleanup

Added automatic cleanup of orphaned Chrome processes before creating new drivers:

```java
public static void cleanupChromeProcesses() {
    if (isRunningInCI()) {
        try {
            logger.info("Attempting to cleanup orphaned Chrome processes in CI environment");
            ProcessBuilder pb = new ProcessBuilder();
            
            // Linux/Unix cleanup command
            if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                pb.command("pkill", "-f", "chrome");
            } else if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                pb.command("taskkill", "/F", "/IM", "chrome.exe");
            }
            
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            logger.warn("Could not cleanup Chrome processes: {}", e.getMessage());
        }
    }
}
```

### 3. Enhanced Chrome Options for Session Isolation

Added critical arguments for better session isolation:

```java
// Critical for session isolation in CI
options.addArguments("--no-first-run");
options.addArguments("--disable-default-apps");
options.addArguments("--disable-sync");

// Additional isolation arguments
options.addArguments("--disable-features=TranslateUI");
options.addArguments("--disable-features=BlinkGenPropertyTrees");
options.addArguments("--disable-browser-side-navigation");
options.addArguments("--disable-background-networking");
```

### 4. Fixed Deprecated API Usage

Updated deprecated URL constructor usage:

**Before:**
```java
URL gridUrl = new URL(config.getGridHubUrl());
```

**After:**
```java
URL gridUrl = URI.create(config.getGridHubUrl()).toURL();
```

## Files Modified

1. **`src/main/java/com/automation/framework/driver/WebDriverFactory.java`**
   - Enhanced user data directory uniqueness
   - Added Chrome process cleanup
   - Added session isolation arguments
   - Fixed deprecated API usage

## Testing the Fix

### 1. Run Smoke Tests
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-suites/smoke-tests.xml -Dbrowser=chrome
```

### 2. Run Parallel Tests
```bash
mvn clean test -DsuiteXmlFile=src/test/resources/testng-suites/all-tests.xml -Dbrowser=chrome -Dthread.count=3
```

### 3. Monitor Logs
Check logs for:
- Unique user data directory creation
- Process cleanup messages
- No session conflict errors

## Expected Outcomes

✅ **No more session conflict errors**  
✅ **Successful parallel test execution**  
✅ **Clean process management in CI**  
✅ **Improved test stability**  
✅ **Better resource utilization**  

## Verification Commands

### Check Chrome Processes (Linux/CI)
```bash
ps aux | grep chrome
```

### Check User Data Directories
```bash
ls -la /tmp/chrome_user_data_*
```

### Monitor Test Execution
```bash
tail -f logs/automation.log
```

## Additional Notes

- **Process ID Component**: Ensures uniqueness across different JVM instances
- **Thread Safety**: Each thread gets its own unique directory
- **CI Environment Detection**: Cleanup only runs in CI environments
- **Cross-Platform**: Works on both Linux (CI) and Windows (local)
- **Resource Management**: Automatic cleanup prevents resource accumulation

## Troubleshooting

If issues persist:

1. **Check CI Environment Variables**:
   ```bash
   echo $CI
   echo $GITHUB_ACTIONS
   ```

2. **Verify Directory Permissions**:
   ```bash
   ls -la /tmp/
   ```

3. **Manual Chrome Process Cleanup**:
   ```bash
   pkill -f chrome
   ```

4. **Check Available Disk Space**:
   ```bash
   df -h /tmp
   ```

This fix provides a comprehensive solution to Chrome session conflicts in CI environments while maintaining compatibility with local development.
