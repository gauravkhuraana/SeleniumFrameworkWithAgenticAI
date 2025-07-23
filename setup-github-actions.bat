@echo off
setlocal enabledelayedexpansion

:: Selenium Framework GitHub Actions Setup Script
:: This script helps you set up GitHub Actions and GitHub Pages for automated testing

echo.
echo 🚀 Setting up Selenium Framework with GitHub Actions...
echo =================================================
echo.

:: Check if we're in a git repository
git rev-parse --git-dir >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: Not in a Git repository. Please run this script from your project root.
    pause
    exit /b 1
)

:: Check if GitHub Actions workflow exists
if not exist ".github\workflows\selenium-tests.yml" (
    echo ❌ Error: GitHub Actions workflow not found. Please ensure .github/workflows/selenium-tests.yml exists.
    pause
    exit /b 1
)

echo ✅ GitHub Actions workflows found!

:: Check if the repository has a remote origin
git remote get-url origin >nul 2>&1
if errorlevel 1 (
    echo ❌ Error: No GitHub remote origin found. Please add your GitHub repository as origin.
    echo    Example: git remote add origin https://github.com/yourusername/yourrepo.git
    pause
    exit /b 1
)

:: Get repository URL
for /f "tokens=*" %%i in ('git remote get-url origin') do set REPO_URL=%%i
echo ✅ Repository URL: !REPO_URL!

:: Extract repository information for GitHub Pages URL
for /f "tokens=* delims=/" %%i in ("!REPO_URL!") do (
    set "temp=%%i"
    set "REPO_NAME=!temp:.git=!"
)

:: Extract GitHub username (simplified)
echo !REPO_URL! | findstr /C:"github.com" >nul
if not errorlevel 1 (
    echo 📊 GitHub repository detected - GitHub Pages will be available after setup
) else (
    echo ⚠️  Warning: Repository doesn't appear to be hosted on GitHub.com
)

echo.
echo 🔧 Setup Instructions:
echo ======================
echo.
echo 1. 📤 Push your changes to GitHub:
echo    git add .
echo    git commit -m "Add GitHub Actions workflows for automated testing"
echo    git push origin main
echo.
echo 2. 🔧 Enable GitHub Pages:
echo    • Go to your repository Settings ^> Pages
echo    • Source: 'Deploy from a branch'
echo    • Branch: 'gh-pages' (will be auto-created)
echo    • Path: '/ (root)'
echo.
echo 3. 🔐 Set Workflow Permissions (IMPORTANT):
echo    • Go to repository Settings ^> Actions ^> General
echo    • Workflow permissions: 'Read and write permissions'
echo    • Check: 'Allow GitHub Actions to create and approve pull requests'
echo    • This fixes permission errors for comments and issues
echo.
echo 4. 🏃‍♂️ Trigger First Run:
echo    • Push to main branch will trigger automatic test execution
echo    • Or go to Actions tab and run 'Test Suite Runner' manually
echo.
echo 5. 📊 Access Reports:
echo    • Reports will be available at your GitHub Pages URL
echo    • Build artifacts available in Actions tab
echo.
echo 🎯 Next Steps:
echo ==============
echo • Customize test suites in src/test/resources/testng-suites/
echo • Modify browser settings in .github/workflows/selenium-tests.yml
echo • Add more test cases to expand coverage
echo • Set up branch protection rules for main branch
echo.
echo ✨ Happy Testing!

:: Create a quick reference file
(
echo # GitHub Actions Setup Reference
echo.
echo ## 🔗 Quick Links
echo - **Repository**: %REPO_URL%
echo - **Actions**: %REPO_URL%/actions
echo - **Settings**: %REPO_URL%/settings
echo.
echo ## 📋 Workflow Files
echo - `.github/workflows/selenium-tests.yml` - Main test workflow
echo - `.github/workflows/manual-test-runner.yml` - Manual test execution
echo.
echo ## 🚀 Triggering Tests
echo 1. **Automatic**: Push to main branch
echo 2. **Manual**: Actions → Test Suite Runner → Run workflow
echo 3. **PR**: Create pull request to main branch
echo.
echo ## 📊 Report Access
echo - **Live Reports**: Available at GitHub Pages URL after setup
echo - **Build Artifacts**: Available in Actions tab for 30 days
echo - **PR Comments**: Automatic links posted on pull requests
echo.
echo ## 🔧 Customization
echo - Modify browser settings in workflow files
echo - Add new TestNG suites in `src/test/resources/testng-suites/`
echo - Adjust reporting in `pom.xml` configuration
echo.
echo Generated on: %date% %time%
) > GITHUB_ACTIONS_SETUP.md

echo.
echo 📝 Quick reference saved to: GITHUB_ACTIONS_SETUP.md
echo.
pause
