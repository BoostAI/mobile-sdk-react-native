#!/bin/bash
# Script to simulate npm install of boostai-react-native from local directory
# Run this script whenever you make changes to the library
# Supports both Android and iOS development

set -e

echo "📦 Installing boostai-react-native from local directory..."

# Determine platform
PLATFORM="${1:-both}"  # Default to both platforms
if [[ "$PLATFORM" != "android" && "$PLATFORM" != "ios" && "$PLATFORM" != "both" ]]; then
  echo "❌ Invalid platform: $PLATFORM"
  echo "Usage: $0 [android|ios|both]"
  exit 1
fi

echo "🎯 Platform: $PLATFORM"

# Paths
EXAMPLE_ROOT="$(cd "$(dirname "$0")" && pwd)"
LIBRARY_ROOT="$EXAMPLE_ROOT/../boostai-react-native"
NODE_MODULES_TARGET="$EXAMPLE_ROOT/node_modules/boostai-react-native"

# Remove existing installation (symlink or directory)
echo "🗑️  Removing existing installation..."
rm -rf "$NODE_MODULES_TARGET"

# Create target directory
echo "📁 Creating target directory..."
mkdir -p "$NODE_MODULES_TARGET"

# Copy library files (excluding build artifacts and development files)
echo "📋 Copying library files..."
rsync -av \
  --exclude 'node_modules' \
  --exclude 'build' \
  --exclude '.gradle' \
  --exclude '.git' \
  --exclude '.idea' \
  --exclude '*.iml' \
  --exclude '.DS_Store' \
  --exclude 'android/build' \
  --exclude 'android/.gradle' \
  --exclude 'android/.cxx' \
  --exclude 'ios/build' \
  --exclude 'ios/Pods' \
  --exclude 'lib' \
  "$LIBRARY_ROOT/" "$NODE_MODULES_TARGET/"

# Build TypeScript files (always rebuild to catch changes)
echo "🔨 Building TypeScript in source directory..."
cd "$LIBRARY_ROOT"
npm install --silent --legacy-peer-deps
npm run prepare

# Copy built lib directory
echo "📦 Copying built TypeScript artifacts..."
cp -R "$LIBRARY_ROOT/lib" "$NODE_MODULES_TARGET/"

# Generate CodeGen artifacts and install platform-specific dependencies
if [[ "$PLATFORM" == "android" || "$PLATFORM" == "both" ]]; then
  echo "⚙️  Generating Android CodeGen artifacts..."
  cd "$EXAMPLE_ROOT/android"
  ./gradlew :boostai-react-native:generateCodegenArtifactsFromSchema --quiet
  echo "✅ Android setup complete!"
fi

if [[ "$PLATFORM" == "ios" || "$PLATFORM" == "both" ]]; then
  echo "⚙️  Installing iOS CocoaPods dependencies..."
  cd "$EXAMPLE_ROOT/ios"

  # Check if pod command exists
  if ! command -v pod &> /dev/null; then
    echo "⚠️  CocoaPods not found. Install with: sudo gem install cocoapods"
    echo "⏩ Skipping iOS pod install..."
  else
    # Clean and install pods
    echo "🧹 Cleaning iOS build artifacts..."
    rm -rf Pods Podfile.lock build

    echo "📦 Running pod install..."
    pod install --repo-update

    echo "✅ iOS setup complete!"
  fi
fi

echo ""
echo "✅ Done! boostai-react-native installed to node_modules/"
if [[ "$PLATFORM" == "android" ]]; then
  echo "💡 Run 'npm run android' to rebuild the app"
elif [[ "$PLATFORM" == "ios" ]]; then
  echo "💡 Run 'npm run ios' to rebuild the app"
else
  echo "💡 Run 'npm run android' or 'npm run ios' to rebuild the app"
fi
