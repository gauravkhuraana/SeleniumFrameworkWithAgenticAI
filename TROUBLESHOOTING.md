# 🔧 GitHub Actions Troubleshooting Guide

## Common Issues and Solutions

### 1. Permission Errors (403 - Resource not accessible by integration)

**Error**: `RequestError [HttpError]: Resource not accessible by integration`

**Causes**:
- Insufficient workflow permissions
- Repository settings blocking GitHub Actions
- Token permissions not configured

**Solutions**:

#### A. Repository Settings (Recommended)
1. Go to **Settings** → **Actions** → **General**
2. Set **Workflow permissions** to: `Read and write permissions`
3. ✅ Check: `Allow GitHub Actions to create and approve pull requests`

#### B. Use Simple Workflow (No Permissions Required)
- Use `.github/workflows/simple-test-runner.yml` instead
- This workflow only uploads artifacts (no comments/issues)
- Still generates all reports, just no automated notifications

#### C. Manual Permission Fix in Workflow
Add explicit permissions to your workflow:
```yaml
permissions:
  contents: read
  pages: write
  id-token: write
  issues: write
  pull-requests: write
  actions: read
```

### 2. GitHub Pages Not Working

**Issue**: Reports not appearing on GitHub Pages

**Solutions**:

1. **Enable GitHub Pages**:
   - Settings → Pages
   - Source: "Deploy from a branch"
   - Branch: `gh-pages` (auto-created)
   - Path: `/ (root)`

2. **Check Workflow Status**:
   - Actions tab → Latest workflow run
   - Look for "Deploy Reports to GitHub Pages" step

3. **Verify Permissions**:
   - Workflow needs `pages: write` permission
   - Repository needs Actions enabled

### 3. Chrome Session Conflicts in CI ⭐ **CRITICAL FIX**

**Error**: `session not created: probably user data directory is already in use`

**Root Cause**: Multiple Chrome instances in CI environments trying to use the same user data directory.

**✅ SOLUTION APPLIED**: 
The framework now automatically detects CI environments and creates unique user data directories:

```java
// Automatic CI detection and unique directory creation
if (isRunningInCI()) {
    String uniqueUserDataDir = System.getProperty("java.io.tmpdir") + "/chrome_user_data_" + System.currentTimeMillis();
    options.addArguments("--user-data-dir=" + uniqueUserDataDir);
    options.addArguments("--single-process");
}
```

**Verification**:
- Check logs for: `"Running in CI environment - using unique user data directory"`
- Tests should start without session errors
- Use correct property: `-Dbrowser.headless=true` (not `-Dheadless=true`)

**Manual Override** (if needed):
```bash
mvn clean test -Dbrowser.headless=true -Duser.data.dir=/tmp/chrome_custom_$(date +%s)
```

### 4. Tests Failing in CI

**Issue**: Tests pass locally but fail in GitHub Actions

**Common Causes & Solutions**:

#### A. Headless Mode Issues
```yaml
# Add to test execution step
- name: Run Tests
  run: mvn clean test -Dheadless=true -Dbrowser=chrome
```

#### B. Browser Setup Problems
```yaml
# Ensure proper browser setup
- name: Set up Chrome Browser
  uses: browser-actions/setup-chrome@v1
  with:
    chrome-version: stable
    
- name: Set up ChromeDriver
  uses: nanasess/setup-chromedriver@v2
```

#### C. Timing Issues
- Add explicit waits in tests
- Increase timeout values for CI environment
- Use `continue-on-error: true` for non-critical failures

### 4. Allure Report Generation Fails

**Issue**: Allure report not generating properly

**Solutions**:

1. **Check Maven Configuration**:
```xml
<plugin>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-maven</artifactId>
    <version>2.14.0</version>
    <configuration>
        <reportVersion>2.29.0</reportVersion>
        <resultsDirectory>${project.build.directory}/allure-results</resultsDirectory>
        <reportDirectory>${project.build.directory}/site/allure-maven-plugin</reportDirectory>
    </configuration>
</plugin>
```

2. **Verify Allure Dependencies**:
```xml
<dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-testng</artifactId>
    <version>2.29.0</version>
</dependency>
```

3. **Check AspectJ Weaver**:
```xml
<argLine>
    -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/1.9.22.1/aspectjweaver-1.9.22.1.jar"
</argLine>
```

### 5. Artifact Upload Issues

**Issue**: Artifacts not uploading or empty

**Solutions**:

1. **Check Path Existence**:
```yaml
- name: Debug Paths
  if: always()
  run: |
    echo "Checking report directories:"
    ls -la test-output/ || echo "test-output not found"
    ls -la target/site/ || echo "target/site not found"
    find . -name "*.html" -type f | head -10
```

2. **Ensure Reports Generated**:
```yaml
- name: Generate Reports
  if: always()
  run: |
    mvn allure:report
    # Verify reports exist before copying
    if [ -d "target/site/allure-maven-plugin" ]; then
      echo "✅ Allure report generated"
    else
      echo "❌ Allure report not found"
    fi
```

### 6. Java Version Conflicts

**Issue**: Compilation or runtime errors due to Java version

**Solution**:
```yaml
- name: Set up JDK 21
  uses: actions/setup-java@v4
  with:
    java-version: '21'
    distribution: 'temurin'
    cache: maven
```

Ensure your `pom.xml` matches:
```xml
<properties>
    <maven.compiler.source>21</maven.compiler.source>
    <maven.compiler.target>21</maven.compiler.target>
</properties>
```

## 🔍 Debugging Steps

### 1. Enable Debug Logging
Add to your workflow:
```yaml
- name: Debug Information
  run: |
    echo "Java Version: $(java -version)"
    echo "Maven Version: $(mvn -version)"
    echo "Current Directory: $(pwd)"
    echo "Directory Contents:"
    ls -la
    echo "Environment Variables:"
    env | grep -E "(GITHUB_|CI)" | sort
```

### 2. Check Workflow Logs
1. Go to Actions tab
2. Click on failed workflow run
3. Expand each step to see detailed logs
4. Look for error messages and stack traces

### 3. Test Locally with Act
Install and use [act](https://github.com/nektos/act) to run GitHub Actions locally:
```bash
# Install act
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Run workflow locally
act push
```

## 🚀 Alternative Approaches

### Option 1: Simple Artifacts Only
Use `simple-test-runner.yml` workflow:
- No permissions required
- Still generates all reports
- Manual download from artifacts

### Option 2: External Reporting Service
- Integrate with services like TestRail, Xray, or Zephyr
- Use third-party report hosting
- Webhook notifications to Slack/Teams

### Option 3: Local GitHub Pages Setup
```bash
# Manual GitHub Pages deployment
git checkout --orphan gh-pages
git rm -rf .
# Copy your reports here
git add .
git commit -m "Deploy reports"
git push origin gh-pages
```

## 📞 Getting Help

1. **Check GitHub Status**: https://www.githubstatus.com/
2. **Review Workflow Files**: Compare with working examples
3. **Test Individual Steps**: Run commands locally first
4. **Community Support**: GitHub Discussions and Stack Overflow

## 📋 Quick Fixes Checklist

- [ ] Repository permissions set to "Read and write permissions"
- [ ] GitHub Pages enabled with `gh-pages` branch
- [ ] Workflow files have proper YAML syntax
- [ ] Java version matches between local and CI
- [ ] Browser setup steps included
- [ ] Headless mode enabled for CI
- [ ] Continue-on-error set for test steps
- [ ] Proper artifact paths configured
- [ ] Maven dependencies and plugins correct

---

**💡 Pro Tip**: Start with the simple workflow first, then add advanced features once basic functionality works!
