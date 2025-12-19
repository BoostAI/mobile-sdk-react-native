/**
 * TurboModule specification for Boost.ai SDK
 * This file is used by CodeGen to generate native bindings
 */

import type {TurboModule} from 'react-native';
import {TurboModuleRegistry} from 'react-native';
import type {UnsafeObject} from 'react-native/Libraries/Types/CodegenTypes';

export interface Spec extends TurboModule {
  // ==================== Initialization & Configuration ====================

  initialize(domain: string): Promise<UnsafeObject>;
  setCustomConfig(config: UnsafeObject): Promise<void>;
  getConfig(): Promise<UnsafeObject>;
  setLanguageCode(languageCode: string): void;
  setUserToken(userToken: string | null): void;
  setConversationId(conversationId: string | null): void;
  getConversationId(): Promise<string | null>;
  setCertificatePinningEnabled(enabled: boolean): void;
  registerFont(name: string, resourceId: number): void;

  // ==================== Conversation Control ====================

  start(options: UnsafeObject | null): Promise<UnsafeObject>;
  stop(): Promise<UnsafeObject>;
  resume(options: UnsafeObject | null): Promise<UnsafeObject>;
  delete(): Promise<UnsafeObject>;
  resetConversation(): void;

  // ==================== Messaging ====================

  sendMessage(text: string): Promise<UnsafeObject>;
  actionButton(id: string): Promise<UnsafeObject>;
  urlButton(id: string): Promise<void>;
  triggerAction(id: string): Promise<UnsafeObject>;
  sendSmartReply(value: string): Promise<UnsafeObject>;
  sendHumanChatMessage(value: string): Promise<UnsafeObject>;
  clientTyping(value: string): Promise<UnsafeObject>;

  // ==================== Feedback ====================

  sendFeedback(id: string, value: number): Promise<UnsafeObject>;
  sendConversationFeedback(rating: number, text: string | null): Promise<UnsafeObject>;

  // ==================== Human Chat ====================

  poll(): Promise<UnsafeObject>;
  pollStop(): Promise<UnsafeObject>;

  // ==================== File Upload ====================

  sendFiles(files: UnsafeObject[], message: string | null): Promise<UnsafeObject>;

  // ==================== Download ====================

  downloadConversation(userToken: string | null): Promise<UnsafeObject>;

  // ==================== State Management ====================

  getMessages(): Promise<UnsafeObject[]>;

  // ==================== UI ====================

  openChatModal(customConfig: UnsafeObject | null): void;
}

export default TurboModuleRegistry.getEnforcing<Spec>('BoostAI');
