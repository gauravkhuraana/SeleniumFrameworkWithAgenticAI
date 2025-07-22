#!/bin/bash

# Selenium Framework GitHub Actions Setup Script
# This script helps you set up GitHub Actions and GitHub Pages for automated testing

echo "🚀 Setting up Selenium Framework with GitHub Actions..."
echo "================================================="

# Check if we're in a git repository
if ! git rev-parse --git-dir > /dev/null 2>&1; then
    echo "❌ Error: Not in a Git repository. Please run this script from your project root."
    exit 1
fi

# Check if GitHub Actions workflow exists
if [ ! -f ".github/workflows/selenium-tests.yml" ]; then
    echo "❌ Error: GitHub Actions workflow not found. Please ensure .github/workflows/selenium-tests.yml exists."
    exit 1
fi

echo "✅ GitHub Actions workflows found!"

# Check if the repository has a remote origin
if ! git remote get-url origin > /dev/null 2>&1; then
    echo "❌ Error: No GitHub remote origin found. Please add your GitHub repository as origin."
    echo "   Example: git remote add origin https://github.com/yourusername/yourrepo.git"
    exit 1
fi

REPO_URL=$(git remote get-url origin)
echo "✅ Repository URL: $REPO_URL"

# Extract repository name for GitHub Pages URL
REPO_NAME=$(basename -s .git "$REPO_URL")
GITHUB_USER=$(basename $(dirname "$REPO_URL"))

if [[ "$REPO_URL" == *"github.com"* ]]; then
    GITHUB_PAGES_URL="https://${GITHUB_USER}.github.io/${REPO_NAME}/test-reports/"
    echo "📊 Future GitHub Pages URL: $GITHUB_PAGES_URL"
else
    echo "⚠️  Warning: Repository doesn't appear to be hosted on GitHub.com"
fi

echo ""
echo "🔧 Setup Instructions:"
echo "======================"
echo ""
echo "1. 📤 Push your changes to GitHub:"
echo "   git add ."
echo "   git commit -m \"Add GitHub Actions workflows for automated testing\""
echo "   git push origin main"
echo ""
echo "2. 🔧 Enable GitHub Pages:"
echo "   • Go to: https://github.com/${GITHUB_USER}/${REPO_NAME}/settings/pages"
echo "   • Source: 'Deploy from a branch'"
echo "   • Branch: 'gh-pages' (will be auto-created)"
echo "   • Path: '/ (root)'"
echo ""
echo "3. 🔐 Set Workflow Permissions:"
echo "   • Go to: https://github.com/${GITHUB_USER}/${REPO_NAME}/settings/actions"
echo "   • Workflow permissions: 'Read and write permissions'"
echo "   • Check: 'Allow GitHub Actions to create and approve pull requests'"
echo ""
echo "4. 🏃‍♂️ Trigger First Run:"
echo "   • Push to main branch will trigger automatic test execution"
echo "   • Or go to Actions tab and run 'Test Suite Runner' manually"
echo ""
echo "5. 📊 Access Reports:"
echo "   • Reports will be available at: $GITHUB_PAGES_URL"
echo "   • Build artifacts available in Actions tab"
echo ""
echo "🎯 Next Steps:"
echo "=============="
echo "• Customize test suites in src/test/resources/testng-suites/"
echo "• Modify browser settings in .github/workflows/selenium-tests.yml"
echo "• Add more test cases to expand coverage"
echo "• Set up branch protection rules for main branch"
echo ""
echo "✨ Happy Testing!"

# Create a quick reference file
cat > GITHUB_ACTIONS_SETUP.md << EOF
# GitHub Actions Setup Reference

## 🔗 Quick Links
- **Repository**: https://github.com/${GITHUB_USER}/${REPO_NAME}
- **Actions**: https://github.com/${GITHUB_USER}/${REPO_NAME}/actions
- **Settings**: https://github.com/${GITHUB_USER}/${REPO_NAME}/settings
- **GitHub Pages**: $GITHUB_PAGES_URL

## 📋 Workflow Files
- \`.github/workflows/selenium-tests.yml\` - Main test workflow
- \`.github/workflows/manual-test-runner.yml\` - Manual test execution

## 🚀 Triggering Tests
1. **Automatic**: Push to main branch
2. **Manual**: Actions → Test Suite Runner → Run workflow
3. **PR**: Create pull request to main branch

## 📊 Report Access
- **Live Reports**: $GITHUB_PAGES_URL
- **Build Artifacts**: Available in Actions tab for 30 days
- **PR Comments**: Automatic links posted on pull requests

## 🔧 Customization
- Modify browser settings in workflow files
- Add new TestNG suites in \`src/test/resources/testng-suites/\`
- Adjust reporting in \`pom.xml\` configuration

Generated on: $(date)
EOF

echo ""
echo "📝 Quick reference saved to: GITHUB_ACTIONS_SETUP.md"
