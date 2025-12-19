# Boost.ai React Native Setup Guide

## Requirements
- React Native 0.70.0 or higher
- React Native New Architecture enabled (recommended)

## Installation Steps

### 1. Install from npm

The package is published to npm and includes the Boost.ai Android SDK from JitPack:

```bash
npm install @boostai/mobile-sdk-react-native
```

**package.json:**
```json
{
  "dependencies": {
    "@boostai/mobile-sdk-react-native": "^1.1.0"
  }
}
```

### 2. For local development (optional)

If you're developing the package locally:

**package.json:**
```json
{
  "dependencies": {
    "@boostai/mobile-sdk-react-native": "file:../boostai-react-native"
  }
}
```

Then install using the local development script:
```bash
./install-local-boostai.sh
```

### 3. Configure Metro for local dependencies (local development only)

Add to `metro.config.js` to allow Metro to resolve library dependencies:

**metro.config.js:**
```javascript
const { getDefaultConfig, mergeConfig } = require('@react-native/metro-config');
const path = require('path');

const config = {
  watchFolders: [
    path.resolve(__dirname, '..'), // Watch the parent directory
  ],
  resolver: {
    nodeModulesPaths: [
      path.resolve(__dirname, 'node_modules'),
      path.resolve(__dirname, '../node_modules'),
    ],
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
```

**Note**: This step is ONLY needed for local file:// dependencies during development. Published npm packages don't require this.

### 4. Build the app

```bash
cd android
./gradlew assembleDebug
```

## Critical Notes

⚠️ **IMPORTANT**: The package MUST be listed in `package.json` dependencies for autolinking to work. Simply copying files to `node_modules/` is not sufficient - autolinking only processes packages declared in `package.json`.

## What Gets Auto-Linked

Thanks to React Native's autolinking with the proper `react-native.config.js`:

✅ **Automatically handled:**
- `boostai-react-native` TurboModule (BoostAIModule)
- `BoostAIChatView` Fabric component
- CodeGen artifacts generation
- C++ JSI bindings
- CMake configuration
- `boostai-sdk` Android library from JitPack (v1.2.22)
- JitPack repository configuration
- Kotlin Parcelize runtime support

❌ **Manual setup required:**
- None! Everything is automatic.

## Architecture

### Android SDK Integration

The React Native module automatically includes the Boost.ai Android SDK from JitPack:

**From `boostai-react-native/android/build.gradle`:**
```gradle
repositories {
    google()
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'com.facebook.react:react-native:+'

    // Boost.ai SDK from JitPack
    implementation 'com.github.BoostAI:mobile-sdk-android:1.2.22'
}
```

This means:
- ✅ No need to bundle AAR files
- ✅ Always uses the official published SDK
- ✅ Smaller npm package size (47 KB vs 793 KB)
- ✅ Easier maintenance and updates
- ✅ No AAR-in-AAR build issues

### Parcelize Support

The module includes `kotlin-parcelize` plugin to ensure Parcelize runtime classes are available:

```gradle
apply plugin: 'kotlin-parcelize'
```

This fixes the `NoClassDefFoundError: kotlinx.parcelize.Parceler` crash that can occur when deserializing SDK response objects.

## Usage Examples

### Full-screen Chat View

Display the chat view embedded in your app:

```tsx
import { BoostAIChatView } from '@boostai/mobile-sdk-react-native';

function ChatScreen() {
  return (
    <View style={{ flex: 1 }}>
      <BoostAIChatView style={{ flex: 1 }} />
    </View>
  );
}
```

### Modal Chat Trigger

Use `BoostAIModalTouchable` to wrap any component and open chat in a modal when pressed:

#### Simple Avatar Button
```tsx
import { BoostAIModalTouchable } from '@boostai/mobile-sdk-react-native';

function AvatarButton() {
  return (
    <BoostAIModalTouchable>
      <Image
        source={require('./assets/avatar.png')}
        style={{ width: 60, height: 60, borderRadius: 30 }}
      />
    </BoostAIModalTouchable>
  );
}
```

#### Floating Action Button (FAB)
```tsx
import { BoostAIModalTouchable } from '@boostai/mobile-sdk-react-native';

function ChatFAB() {
  return (
    <BoostAIModalTouchable style={styles.fab}>
      <View style={styles.fabInner}>
        <Icon name="chat-bubble" size={24} color="white" />
      </View>
    </BoostAIModalTouchable>
  );
}

const styles = StyleSheet.create({
  fab: {
    position: 'absolute',
    right: 24,
    bottom: 24,
    borderRadius: 32,
    elevation: 6,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 8,
  },
  fabInner: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: '#6200EE',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
```

#### With Custom Configuration
```tsx
import { BoostAIModalTouchable } from '@boostai/mobile-sdk-react-native';
import type { ChatConfig } from '@boostai/mobile-sdk-react-native';

function CustomChatButton() {
  const customConfig: ChatConfig = {
    chatPanel: {
      styling: {
        primaryColor: '#D32F2F',
        contrastColor: '#FFFFFF',
      },
    },
  };

  return (
    <BoostAIModalTouchable
      customConfig={customConfig}
      onPress={() => console.log('Opening chat...')}
    >
      <Text style={styles.buttonText}>Chat with Support</Text>
    </BoostAIModalTouchable>
  );
}
```

### Initialization

Initialize the SDK in your app's entry point:

```tsx
import { BoostAI } from '@boostai/mobile-sdk-react-native';
import type { ChatConfig } from '@boostai/mobile-sdk-react-native';

useEffect(() => {
  const initializeSDK = async () => {
    try {
      // Initialize with your domain
      await BoostAI.initialize('your-domain.boost.ai');

      // Set language (optional)
      await BoostAI.setLanguageCode('en-US');

      // Set custom config (optional)
      const config: ChatConfig = {
        chatPanel: {
          styling: {
            primaryColor: '#6200EE',
            contrastColor: '#FFFFFF',
          },
        },
      };
      await BoostAI.setCustomConfig(config);

      console.log('SDK initialized');
    } catch (error) {
      console.error('Failed to initialize SDK:', error);
    }
  };

  initializeSDK();
}, []);
```

## Verification

The package has been tested and verified to work correctly with React Native 0.70+ apps.

**Build output example:**
```
BUILD SUCCESSFUL in 18s
124 actionable tasks: 41 executed, 83 up-to-date
```

## Key Configuration

The `boostai-react-native/react-native.config.js` enables autolinking:
```javascript
module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'BoostAISpec',
        componentDescriptors: [
          'BoostAIChatViewComponentDescriptor',
          'BoostAIAvatarViewComponentDescriptor',
        ],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
      },
    },
  },
};
```

This tells React Native's autolinking system:
- Library name for CodeGen
- Which Fabric components to register
- Where to find C++ CMake configuration
