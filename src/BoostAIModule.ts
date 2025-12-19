/**
 * React Native Boost.ai SDK
 * JavaScript wrapper for TurboModule
 */

import { NativeEventEmitter, EmitterSubscription } from 'react-native';
import NativeBoostAI from './specs/NativeBoostAI';
import type {
  ChatConfig,
  APIMessage,
  MessageReceivedEvent,
  ConfigReceivedEvent,
  BackendEvent,
  ErrorEvent,
  UIEvent,
  ClientTyping,
} from './types';

// Use TurboModule directly
const BoostAIModuleNative = NativeBoostAI;

// Event emitter for native events
const eventEmitter = new NativeEventEmitter(BoostAIModuleNative as any);

/**
 * Boost.ai SDK for React Native
 */
class BoostAIModuleClass {
  // ==================== Event Listeners ====================

  /**
   * Add listener for message received events
   */
  addMessageListener(listener: (event: MessageReceivedEvent) => void): EmitterSubscription {
    return eventEmitter.addListener('BoostAI:onMessageReceived', listener);
  }

  /**
   * Add listener for config received/updated events
   */
  addConfigListener(listener: (event: ConfigReceivedEvent) => void): EmitterSubscription {
    return eventEmitter.addListener('BoostAI:onConfigReceived', listener);
  }

  /**
   * Add listener for backend events (emitEvent JSON type)
   */
  addBackendEventListener(listener: (event: BackendEvent) => void): EmitterSubscription {
    return eventEmitter.addListener('BoostAI:onBackendEvent', listener);
  }

  /**
   * Add listener for error events
   */
  addErrorListener(listener: (event: ErrorEvent) => void): EmitterSubscription {
    return eventEmitter.addListener('BoostAI:onError', listener);
  }

  /**
   * Add listener for UI events
   */
  addUIEventListener(listener: (event: UIEvent) => void): EmitterSubscription {
    return eventEmitter.addListener('BoostAI:onUIEvent', listener);
  }

  // ==================== Initialization & Configuration ====================

  /**
   * Initialize the SDK with domain
   * @param domain - Your Boost.ai domain (e.g., "your-name.boost.ai")
   * @returns Promise resolving to initial config
   */
  initialize(domain: string): Promise<ChatConfig> {
    return BoostAIModuleNative.initialize(domain);
  }

  /**
   * Set custom configuration (overrides server config)
   * @param config - Custom chat configuration
   */
  setCustomConfig(config: ChatConfig): Promise<void> {
    return BoostAIModuleNative.setCustomConfig(config);
  }

  /**
   * Get current configuration
   * @returns Promise resolving to current config
   */
  getConfig(): Promise<ChatConfig> {
    return BoostAIModuleNative.getConfig();
  }

  /**
   * Set language code
   * @param languageCode - BCP-47 language code (e.g., "en-US")
   */
  setLanguageCode(languageCode: string): void {
    BoostAIModuleNative.setLanguageCode(languageCode);
  }

  /**
   * Set user token for authenticated users
   * @param userToken - User token
   */
  setUserToken(userToken: string | null): void {
    BoostAIModuleNative.setUserToken(userToken);
  }

  /**
   * Set conversation ID to resume
   * @param conversationId - Conversation ID
   */
  setConversationId(conversationId: string | null): void {
    BoostAIModuleNative.setConversationId(conversationId);
  }

  /**
   * Get current conversation ID
   * @returns Promise resolving to conversation ID
   */
  getConversationId(): Promise<string | null> {
    return BoostAIModuleNative.getConversationId();
  }

  /**
   * Enable/disable certificate pinning
   * @param enabled - Whether to enable certificate pinning
   */
  setCertificatePinningEnabled(enabled: boolean): void {
    BoostAIModuleNative.setCertificatePinningEnabled(enabled);
  }

  /**
   * Register a custom font resource
   * @param name - Font name to use in config
   * @param resourceId - Android R.font.* resource ID
   */
  registerFont(name: string, resourceId: number): void {
    BoostAIModuleNative.registerFont(name, resourceId);
  }

  // ==================== Conversation Control ====================

  /**
   * Start a new conversation
   * @param options - Start options (optional)
   * @returns Promise resolving to API message
   */
  start(options?: any): Promise<APIMessage> {
    return BoostAIModuleNative.start(options || null);
  }

  /**
   * Stop/block the conversation
   * @returns Promise resolving to API message
   */
  stop(): Promise<APIMessage> {
    return BoostAIModuleNative.stop();
  }

  /**
   * Resume an existing conversation
   * @param options - Resume options (optional)
   * @returns Promise resolving to API message
   */
  resume(options?: any): Promise<APIMessage> {
    return BoostAIModuleNative.resume(options || null);
  }

  /**
   * Delete conversation data
   * @returns Promise resolving to API message
   */
  delete(): Promise<APIMessage> {
    return BoostAIModuleNative.delete();
  }

  /**
   * Reset conversation (clears local state)
   */
  resetConversation(): void {
    BoostAIModuleNative.resetConversation();
  }

  // ==================== Messaging ====================

  /**
   * Send a text message
   * @param text - Message text
   * @returns Promise resolving to API message
   */
  sendMessage(text: string): Promise<APIMessage> {
    return BoostAIModuleNative.sendMessage(text);
  }

  /**
   * Trigger an action button
   * @param id - Action ID
   * @returns Promise resolving to API message
   */
  actionButton(id: string): Promise<APIMessage> {
    return BoostAIModuleNative.actionButton(id);
  }

  /**
   * Log URL button click
   * @param id - Button ID
   * @returns Promise resolving to null
   */
  urlButton(id: string): Promise<void> {
    return BoostAIModuleNative.urlButton(id);
  }

  /**
   * Trigger an action flow element
   * @param id - Action ID
   * @returns Promise resolving to API message
   */
  triggerAction(id: string): Promise<APIMessage> {
    return BoostAIModuleNative.triggerAction(id);
  }

  /**
   * Send a smart reply
   * @param value - Smart reply value
   * @returns Promise resolving to API message
   */
  sendSmartReply(value: string): Promise<APIMessage> {
    return BoostAIModuleNative.sendSmartReply(value);
  }

  /**
   * Send a human chat message
   * @param value - Message text
   * @returns Promise resolving to API message
   */
  sendHumanChatMessage(value: string): Promise<APIMessage> {
    return BoostAIModuleNative.sendHumanChatMessage(value);
  }

  /**
   * Send client typing indicator
   * @param value - Current input text
   * @returns Promise resolving to typing info (length, maxLength)
   */
  clientTyping(value: string): Promise<ClientTyping> {
    return BoostAIModuleNative.clientTyping(value) as Promise<ClientTyping>;
  }

  // ==================== Feedback ====================

  /**
   * Send message feedback (thumbs up/down)
   * @param id - Message ID
   * @param value - Feedback value (1=positive, -1=negative, 0=remove)
   * @returns Promise resolving to API message
   */
  sendFeedback(id: string, value: number): Promise<APIMessage> {
    return BoostAIModuleNative.sendFeedback(id, value);
  }

  /**
   * Send conversation feedback
   * @param rating - Rating (1-5)
   * @param text - Feedback text (optional)
   * @returns Promise resolving to API message
   */
  sendConversationFeedback(rating: number, text?: string): Promise<APIMessage> {
    return BoostAIModuleNative.sendConversationFeedback(rating, text || null);
  }

  // ==================== Human Chat ====================

  /**
   * Poll for new messages (human chat)
   * @returns Promise resolving to API message
   */
  poll(): Promise<APIMessage> {
    return BoostAIModuleNative.poll();
  }

  /**
   * Stop polling for messages
   * @returns Promise resolving to API message
   */
  pollStop(): Promise<APIMessage> {
    return BoostAIModuleNative.pollStop();
  }

  // ==================== File Upload ====================

  /**
   * Send files
   * @param files - Array of file objects
   * @param message - Optional message text
   * @returns Promise resolving to API message
   */
  sendFiles(files: any[], message?: string): Promise<APIMessage> {
    return BoostAIModuleNative.sendFiles(files, message || null);
  }

  // ==================== Download ====================

  /**
   * Download conversation as text
   * @param userToken - Optional user token
   * @returns Promise resolving to API message with download content
   */
  downloadConversation(userToken?: string): Promise<APIMessage> {
    return BoostAIModuleNative.downloadConversation(userToken || null);
  }

  // ==================== State Management ====================

  /**
   * Get all messages in the current conversation
   * @returns Promise resolving to array of API messages
   */
  getMessages(): Promise<APIMessage[]> {
    return BoostAIModuleNative.getMessages();
  }

  // ==================== UI ====================

  /**
   * Open chat in a modal activity (Android only)
   * @param customConfig - Optional custom configuration
   */
  openChatModal(customConfig?: ChatConfig): void {
    BoostAIModuleNative.openChatModal(customConfig || null);
  }
}

export const BoostAI = new BoostAIModuleClass();
export default BoostAI;
