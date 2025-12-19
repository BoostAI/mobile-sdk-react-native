/**
 * Type definitions for Boost.ai Events
 * Mirrors the Kotlin SDK event structure
 */

import { APIMessage, ChatConfig } from './';

/**
 * UI events emitted by the chat interface
 */
export type BoostAIEvent =
  | 'chatPanelOpened'
  | 'chatPanelClosed'
  | 'chatPanelMinimized'
  | 'conversationIdChanged'
  | 'conversationReferenceChanged'
  | 'messageSent'
  | 'menuOpened'
  | 'menuClosed'
  | 'privacyPolicyOpened'
  | 'conversationDownloaded'
  | 'conversationDeleted'
  | 'positiveMessageFeedbackGiven'
  | 'negativeMessageFeedbackGiven'
  | 'positiveConversationFeedbackGiven'
  | 'negativeConversationFeedbackGiven'
  | 'conversationFeedbackTextGiven'
  | 'actionLinkClicked'
  | 'externalLinkClicked'
  | 'filterValuesChanged';

/**
 * Event emitted when a message is received from the backend
 */
export interface MessageReceivedEvent {
  message: APIMessage;
}

/**
 * Event emitted when configuration is received or updated
 */
export interface ConfigReceivedEvent {
  config: ChatConfig;
}

/**
 * Backend event emitted via emitEvent JSON type
 */
export interface BackendEvent {
  /** Event type */
  type: string;
  /** Event detail/payload */
  detail?: any;
}

/**
 * Error event
 */
export interface ErrorEvent {
  /** Error message */
  error: string;
  /** Error code (optional) */
  code?: string;
}

/**
 * UI event with optional detail
 */
export interface UIEvent {
  /** Event type */
  event: BoostAIEvent;
  /** Event detail/payload */
  detail?: any;
}

/**
 * Event listener types
 */
export type MessageReceivedListener = (event: MessageReceivedEvent) => void;
export type ConfigReceivedListener = (event: ConfigReceivedEvent) => void;
export type BackendEventListener = (event: BackendEvent) => void;
export type ErrorListener = (event: ErrorEvent) => void;
export type UIEventListener = (event: UIEvent) => void;
