# @boostai/mobile-sdk-react-native

React Native wrapper for Boost.ai SDK.

## Features

- Full-screen embedded chat view
- Modal chat trigger component
- React hooks for SDK operations
- TypeScript support
- New Architecture support (TurboModules + Fabric)

## Installation

```sh
npm install @boostai/mobile-sdk-react-native
```

**Requirements:** React Native 0.70.0 or higher

## Quick Start

### Initialize the SDK

```tsx
import { useBoostAI } from '@boostai/mobile-sdk-react-native';

function App() {
  const { isInitialized } = useBoostAI({
    domain: 'your-domain.boost.ai',
    languageCode: 'en-US',
    autoStart: true
  });

  if (!isInitialized) return <LoadingScreen />;

  return <YourApp />;
}
```

### Embedded Chat View

```tsx
import { BoostAIChatView } from '@boostai/mobile-sdk-react-native';

function ChatScreen() {
  return <BoostAIChatView style={{ flex: 1 }} />;
}
```

### Modal Chat Trigger

Wrap any component to open chat in a modal:

```tsx
import { BoostAIModalTouchable } from '@boostai/mobile-sdk-react-native';

function ChatButton() {
  return (
    <BoostAIModalTouchable>
      <View style={styles.fab}>
        <Icon name="chat" size={24} color="white" />
      </View>
    </BoostAIModalTouchable>
  );
}
```

### Hooks

**useBoostAI** - Initialize SDK and control conversations:
```tsx
const { isInitialized, sendMessage, start, stop } = useBoostAI({
  domain: 'your-domain.boost.ai',
  autoStart: true
});
```

**useBoostAIMessages** - Subscribe to messages:
```tsx
const { messages, isLoading } = useBoostAIMessages();
```

**useBoostAIEvents** - Listen to events:
```tsx
useBoostAIEvents('chatPanelOpened', () => console.log('Chat opened'));
```

## API Reference

### BoostAI Module

```tsx
BoostAI.initialize(domain: string)
BoostAI.setCustomConfig(config: ChatConfig)
BoostAI.setLanguageCode(code: string)
BoostAI.sendMessage(text: string)
BoostAI.start() / stop() / resume()
BoostAI.getMessages()
```

### Components

**BoostAIChatView** - Embedded chat view
```tsx
<BoostAIChatView style={{ flex: 1 }} customConfig={config} />
```

**BoostAIModalTouchable** - Modal trigger wrapper
```tsx
<BoostAIModalTouchable customConfig={config}>
  {children}
</BoostAIModalTouchable>
```

## Configuration

```tsx
const config: ChatConfig = {
  chatPanel: {
    header: {
      title: 'Customer Support'
    },
    styling: {
      primaryColor: '#6200EE',
      contrastColor: '#FFFFFF'
    },
    settings: {
      startLanguage: 'en-US',
      rememberConversation: true
    }
  }
};
```

## Events

Available events: `chatPanelOpened`, `chatPanelClosed`, `messageSent`, `conversationDeleted`, `actionLinkClicked`, and more.

## License

GPL-3.0

---

**Repository:** https://github.com/Grensesnitt/boostai-mobile-sdk-react-native
