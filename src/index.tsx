/**
 * React Native Boost.ai SDK
 * Main export file
 */

// Main module
export { BoostAI, default as BoostAIModule } from './BoostAIModule';

// Components
export { BoostAIChatView } from './BoostAIChatView';
export type { BoostAIChatViewProps, BoostAIChatViewRef } from './BoostAIChatView';

export { BoostAIModalTouchable } from './BoostAIModalTouchable';
export type { BoostAIModalTouchableProps } from './BoostAIModalTouchable';

// Hooks
export { useBoostAI } from './hooks/useBoostAI';
export type { UseBoostAIOptions, UseBoostAIResult } from './hooks/useBoostAI';

export { useBoostAIMessages } from './hooks/useBoostAIMessages';
export type { UseBoostAIMessagesOptions, UseBoostAIMessagesResult } from './hooks/useBoostAIMessages';

export { useBoostAIEvents, useBoostAIAllEvents } from './hooks/useBoostAIEvents';

// Types
export * from './types';
