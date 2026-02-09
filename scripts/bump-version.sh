#!/bin/bash
# Version bump helper for AetherTV

set -e

cd "$(dirname "$0")/.."

GRADLE_FILE="app/build.gradle.kts"

# Get current version
CURRENT_VERSION=$(grep 'versionName = ' "$GRADLE_FILE" | head -1 | sed 's/.*"\(.*\)".*/\1/')
CURRENT_CODE=$(grep 'versionCode = ' "$GRADLE_FILE" | head -1 | sed 's/.*= \([0-9]*\).*/\1/')

echo "Current version: $CURRENT_VERSION (code: $CURRENT_CODE)"

# Parse version parts
IFS='.' read -r MAJOR MINOR PATCH <<< "$CURRENT_VERSION"

# Determine new version based on argument
case "${1:-patch}" in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        ;;
    *)
        echo "Usage: $0 [major|minor|patch]"
        exit 1
        ;;
esac

NEW_VERSION="${MAJOR}.${MINOR}.${PATCH}"
NEW_CODE=$((CURRENT_CODE + 1))

echo "New version: $NEW_VERSION (code: $NEW_CODE)"

# Update build.gradle.kts
sed -i "s/versionCode = $CURRENT_CODE/versionCode = $NEW_CODE/" "$GRADLE_FILE"
sed -i "s/versionName = \"$CURRENT_VERSION\"/versionName = \"$NEW_VERSION\"/" "$GRADLE_FILE"

echo "Updated $GRADLE_FILE"
