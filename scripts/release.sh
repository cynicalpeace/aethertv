#!/bin/bash
# AetherTV Release Script
# Builds APK and creates GitHub release with APK only (no source code)

set -e

cd "$(dirname "$0")/.."

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Get version from build.gradle.kts
VERSION=$(grep 'versionName = ' app/build.gradle.kts | head -1 | sed 's/.*"\(.*\)".*/\1/')
VERSION_CODE=$(grep 'versionCode = ' app/build.gradle.kts | head -1 | sed 's/.*= \([0-9]*\).*/\1/')

if [ -z "$VERSION" ]; then
    echo -e "${RED}Could not determine version from build.gradle.kts${NC}"
    exit 1
fi

TAG="v${VERSION}-debug"
APK_NAME="aethertv-v${VERSION}-debug.apk"
APK_PATH="app/build/outputs/apk/debug/${APK_NAME}"

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}AetherTV Release Script${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""
echo -e "Version: ${GREEN}${VERSION}${NC} (code: ${VERSION_CODE})"
echo -e "Tag: ${GREEN}${TAG}${NC}"
echo -e "APK: ${GREEN}${APK_NAME}${NC}"
echo ""

# Check if gh CLI is installed
if ! command -v gh &> /dev/null; then
    echo -e "${RED}GitHub CLI (gh) is not installed.${NC}"
    echo "Install with: sudo apt install gh"
    exit 1
fi

# Check if authenticated
if ! gh auth status &> /dev/null; then
    echo -e "${RED}Not authenticated with GitHub CLI.${NC}"
    echo "Run: gh auth login"
    exit 1
fi

# Build APK
echo -e "${YELLOW}Building debug APK...${NC}"
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
./gradlew assembleDebug

if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}APK not found at ${APK_PATH}${NC}"
    exit 1
fi

APK_SIZE=$(du -h "$APK_PATH" | cut -f1)
echo -e "${GREEN}APK built: ${APK_SIZE}${NC}"

# Copy to releases folder
mkdir -p releases
cp "$APK_PATH" "releases/${APK_NAME}"
echo -e "${GREEN}Copied to releases/${APK_NAME}${NC}"

# Check if release already exists
if gh release view "$TAG" &> /dev/null; then
    echo ""
    echo -e "${YELLOW}Release ${TAG} already exists.${NC}"
    read -p "Delete and recreate? (y/N) " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Deleting existing release..."
        gh release delete "$TAG" --yes
        # Also delete the tag
        git tag -d "$TAG" 2>/dev/null || true
        git push origin ":refs/tags/$TAG" 2>/dev/null || true
    else
        echo "Aborted."
        exit 0
    fi
fi

# Get changelog entry (if exists)
NOTES=""
if [ -f "CHANGELOG.md" ]; then
    # Extract notes for this version
    NOTES=$(awk "/^## .*${VERSION}/,/^## /" CHANGELOG.md | head -n -1 | tail -n +2)
fi

if [ -z "$NOTES" ]; then
    NOTES="Release ${TAG}"
fi

# Create release (APK only, no source tarball)
echo ""
echo -e "${YELLOW}Creating GitHub release...${NC}"
gh release create "$TAG" \
    "$APK_PATH" \
    --title "${TAG}" \
    --notes "$NOTES" \
    --latest

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}Release ${TAG} created successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "View at: https://github.com/cynicalpeace/aethertv/releases/tag/${TAG}"
