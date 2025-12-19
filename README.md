# @boostai/mobile-sdk-react-native

React Native wrapper for Boost.ai SDK.

## Features

- Full-screen embedded chat view
- Flexible modal chat trigger (wrap any component)
- React hooks for SDK operations
- TypeScript support with complete type definitions
- Automatic dependency management via JitPack
- New Architecture support (TurboModules + Fabric)
- Zero manual setup required

## Requirements

- React Native 0.70.0 or higher
- React Native New Architecture enabled (recommended)

## Installation

```sh
npm install @boostai/mobile-sdk-react-native
```

That's it! The package automatically includes:
- Boost.ai Android SDK from JitPack (v1.2.22)
- All necessary native dependencies
- Kotlin Parcelize runtime support
- React Native autolinking configuration

No manual setup or configuration required.

## Usage

### Initialize the SDK

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

### Full-Screen Chat View

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

### Using Hooks

#### useBoostAI

Main hook for SDK initialization and operations:

```tsx
import { useBoostAI } from '@boostai/mobile-sdk-react-native';

function App() {
  const { isInitialized, sendMessage } = useBoostAI({
    domain: 'your-name.boost.ai',
    languageCode: 'en-US',
    autoStart: true
  });

  const handleSend = () => {
    if (isInitialized) {
      sendMessage('Hello!');
    }
  };

  return (
    <View>
      <Button title="Send Message" onPress={handleSend} />
    </View>
  );
}
```

#### useBoostAIMessages

Subscribe to message stream:

```tsx
import { useBoostAIMessages } from '@boostai/mobile-sdk-react-native';

function MessageList() {
  const { messages, isLoading } = useBoostAIMessages();

  return (
    <FlatList
      data={messages}
      renderItem={({ item }) => (
        <Text>{item.response?.elements[0]?.payload.text}</Text>
      )}
    />
  );
}
```

#### useBoostAIEvents

Subscribe to specific UI events:

```tsx
import { useBoostAIEvents } from '@boostai/mobile-sdk-react-native';

function MyComponent() {
  useBoostAIEvents('chatPanelOpened', () => {
    console.log('Chat opened!');
  });

  useBoostAIEvents('messageSent', () => {
    console.log('Message sent!');
  });

  // Multiple events
  useBoostAIEvents(['chatPanelOpened', 'chatPanelClosed'], (detail) => {
    console.log('Chat panel event:', detail);
  });

  return <View />;
}
```

## API Reference

### Module Methods

The main `BoostAI` module provides these methods:

#### Initialization

- `initialize(domain: string): Promise<ChatConfig>` - Initialize SDK with domain
- `setCustomConfig(config: ChatConfig): Promise<void>` - Set custom configuration
- `getConfig(): Promise<ChatConfig>` - Get current configuration
- `setLanguageCode(languageCode: string): void` - Set language code
- `setUserToken(userToken: string | null): void` - Set user authentication token
- `setConversationId(conversationId: string | null): void` - Set conversation ID
- `getConversationId(): Promise<string | null>` - Get current conversation ID

#### Modal

- `openChatModal(customConfig?: ChatConfig): void` - Open chat in modal activity

#### Conversation Control

- `start(options?: any): Promise<APIMessage>` - Start new conversation
- `stop(): Promise<APIMessage>` - Stop current conversation
- `resume(options?: any): Promise<APIMessage>` - Resume conversation
- `delete(): Promise<APIMessage>` - Delete conversation
- `resetConversation(): void` - Reset local state

#### Messaging

- `sendMessage(text: string): Promise<APIMessage>` - Send text message
- `actionButton(id: string): Promise<APIMessage>` - Trigger action button
- `urlButton(id: string): Promise<void>` - Log URL button click
- `triggerAction(id: string): Promise<APIMessage>` - Trigger custom action
- `sendSmartReply(value: string): Promise<APIMessage>` - Send smart reply
- `sendHumanChatMessage(value: string): Promise<APIMessage>` - Send human chat message
- `clientTyping(value: string): Promise<ClientTyping>` - Send typing indicator

#### Feedback

- `sendFeedback(id: string, value: number): Promise<APIMessage>` - Send message feedback
- `sendConversationFeedback(rating: number, text?: string): Promise<APIMessage>` - Send conversation feedback

#### State

- `getMessages(): Promise<APIMessage[]>` - Get all messages in conversation

### Components

#### BoostAIChatView

Full-screen embedded chat view component.

**Props:**
- `customConfig?: ChatConfig` - Custom configuration
- `isDialog?: boolean` - Display as dialog
- `style?: StyleProp<ViewStyle>` - Component style

**Ref methods:**
- `close()` - Close chat
- `showSettings()` - Show settings panel
- `hideSettings()` - Hide settings panel
- `showFeedback()` - Show feedback form
- `hideFeedback()` - Hide feedback form

#### BoostAIModalTouchable

Touchable wrapper that opens chat in a modal activity when pressed. Wraps any React Native components - users have full control over what gets rendered.

**Props:**
- `customConfig?: ChatConfig` - Custom configuration to pass to modal
- `onPress?: () => void` - Custom onPress handler (called before opening chat)
- `...TouchableOpacityProps` - All standard TouchableOpacity props

### Hooks

#### useBoostAI

Main hook for SDK initialization and operations.

```tsx
const {
  isInitialized,
  error,
  conversationId,
  config,
  start,
  stop,
  resume,
  sendMessage,
  resetConversation
} = useBoostAI({
  domain: 'your-name.boost.ai',
  customConfig?: ChatConfig,
  languageCode?: 'en-US',
  userToken?: string,
  autoStart?: boolean
});
```

**Options:**
- `domain: string` - Your Boost.ai domain (required)
- `customConfig?: ChatConfig` - Custom configuration
- `languageCode?: string` - Language code (e.g., 'en-US')
- `userToken?: string` - User authentication token
- `autoStart?: boolean` - Auto-start conversation on mount

**Returns:**
- `isInitialized: boolean` - SDK initialization status
- `error: Error | null` - Initialization error if any
- `conversationId: string | null` - Current conversation ID
- `config: ChatConfig | null` - Current configuration
- `start: (options?) => Promise<APIMessage>` - Start conversation
- `stop: () => Promise<APIMessage>` - Stop conversation
- `resume: (options?) => Promise<APIMessage>` - Resume conversation
- `sendMessage: (text) => Promise<APIMessage>` - Send message
- `resetConversation: () => void` - Reset conversation state

#### useBoostAIMessages

Subscribe to message stream.

```tsx
const { messages, isLoading, error } = useBoostAIMessages({
  fetchInitial?: boolean
});
```

**Options:**
- `fetchInitial?: boolean` - Fetch initial messages on mount

**Returns:**
- `messages: APIMessage[]` - Array of messages
- `isLoading: boolean` - Loading state
- `error: Error | null` - Error if any

#### useBoostAIEvents

Subscribe to specific UI events.

```tsx
useBoostAIEvents(eventType, callback);
useBoostAIEvents(['event1', 'event2'], callback);
useBoostAIAllEvents((event, detail) => { });
```

## Configuration

The `ChatConfig` object supports full customization:

```typescript
const config: ChatConfig = {
  chatPanel: {
    header: {
      title: 'Customer Support',
      hideMenuButton: false
    },
    styling: {
      primaryColor: '#FF5733',
      contrastColor: '#FFFFFF',
      chatBubbles: {
        vaBackgroundColor: '#F0F0F0',
        vaTextColor: '#333333',
        userBackgroundColor: '#FF5733',
        userTextColor: '#FFFFFF'
      },
      buttons: {
        multiline: true,
        variant: 'button'
      }
    },
    settings: {
      startLanguage: 'en-US',
      rememberConversation: true,
      showLinkClickAsChatBubble: true
    }
  }
};
```

## UI Events

Available UI events for `useBoostAIEvents`:

- `chatPanelOpened` - Chat panel opened
- `chatPanelClosed` - Chat panel closed
- `chatPanelMinimized` - Chat panel minimized
- `conversationIdChanged` - Conversation ID changed
- `conversationReferenceChanged` - Conversation reference changed
- `messageSent` - Message sent
- `menuOpened` - Settings menu opened
- `menuClosed` - Settings menu closed
- `privacyPolicyOpened` - Privacy policy opened
- `conversationDownloaded` - Conversation downloaded
- `conversationDeleted` - Conversation deleted
- `positiveMessageFeedbackGiven` - Positive message feedback
- `negativeMessageFeedbackGiven` - Negative message feedback
- `positiveConversationFeedbackGiven` - Positive conversation feedback
- `negativeConversationFeedbackGiven` - Negative conversation feedback
- `conversationFeedbackTextGiven` - Conversation feedback text provided
- `actionLinkClicked` - Action link clicked
- `externalLinkClicked` - External link clicked
- `filterValuesChanged` - Filter values changed

## Architecture

### Android SDK Integration

The React Native module automatically includes the Boost.ai Android SDK from JitPack:

**From `android/build.gradle`:**
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

**Benefits:**
- No need to bundle AAR files
- Always uses the official published SDK
- Smaller npm package size (47 KB vs 793 KB)
- Easier maintenance and updates
- No AAR-in-AAR build issues

### Parcelize Support

The module includes `kotlin-parcelize` plugin to ensure Parcelize runtime classes are available:

```gradle
apply plugin: 'kotlin-parcelize'
```

This fixes the `NoClassDefFoundError: kotlinx.parcelize.Parceler` crash that can occur when deserializing SDK response objects.

### Autolinking

The package uses React Native's autolinking system via `react-native.config.js`:

```javascript
module.exports = {
  dependency: {
    platforms: {
      android: {
        libraryName: 'BoostAISpec',
        componentDescriptors: [
          'BoostAIChatViewComponentDescriptor',
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

## TypeScript

Full TypeScript support included with complete type definitions for all configuration objects, messages, and events.

## License

GPL-3.0

## Repository

https://github.com/Grensesnitt/boostai-mobile-sdk-react-native

## Contributing

See the Android SDK repository for contribution guidelines.
