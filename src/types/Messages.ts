/**
 * Type definitions for Boost.ai Messages and Responses
 * Mirrors the Kotlin SDK message structure
 */

export type ChatStatus = 'virtual_agent' | 'in_human_chat_queue' | 'assigned_to_human';
export type SourceType = 'bot' | 'client';
export type LinkType = 'action_link' | 'external_link';
export type ElementType = 'text' | 'html' | 'image' | 'video' | 'json' | 'links' | 'unknown';
export type FunctionType = 'approve' | 'deny';
export type FeedbackValue = 'positive' | 'remove_positive' | 'negative' | 'remove_negative';

export interface APIMessage {
  /** Conversation result with state */
  conversation?: ConversationResult;
  /** Single response */
  response?: Response;
  /** Array of historic responses */
  responses?: Response[];
  /** Smart reply suggestions */
  smartReplies?: SmartReply;
  /** Posted message ID */
  postedId?: number;
  /** Download content (for download command) */
  download?: string;
}

export interface Response {
  /** Unique response ID */
  id: string | null;
  /** Message source (bot or client) */
  source: SourceType;
  /** Language code (BCP-47) */
  language: string;
  /** Message content elements */
  elements: Element[];
  /** Avatar URL */
  avatarUrl?: string;
  /** Creation date (ISO8601 string) */
  dateCreated?: string;
  /** Feedback status */
  feedback?: string;
  /** Source URL for the response */
  sourceUrl?: string;
  /** Link text */
  linkText?: string;
  /** Error message if any */
  error?: string;
  /** Virtual Agent Network ID */
  vanId?: number;
}

export interface Element {
  /** Element payload */
  payload: Payload;
  /** Element type */
  type: ElementType;
}

export interface Payload {
  /** HTML content */
  html?: string;
  /** Plain text content */
  text?: string;
  /** URL (for video/image) */
  url?: string;
  /** Source URL */
  source?: string;
  /** Fullscreen flag (for video) */
  fullScreen?: boolean;
  /** JSON content (custom payloads) */
  json?: any;
  /** Links/buttons */
  links?: Link[];
}

export interface Link {
  /** Link ID */
  id: string;
  /** Link display text */
  text: string;
  /** Link type */
  type: LinkType;
  /** Function type (for approve/deny actions) */
  function?: FunctionType;
  /** Question text */
  question?: string;
  /** URL (for external links) */
  url?: string;
  /** Is attachment flag */
  isAttachment?: boolean;
  /** Virtual Agent Network base URL */
  vanBaseUrl?: string;
  /** Virtual Agent Network name */
  vanName?: string;
  /** Virtual Agent Network organization */
  vanOrganization?: string;
}

export interface ConversationResult {
  /** Conversation ID */
  id: string | null;
  /** Conversation reference */
  reference: string | null;
  /** Conversation state */
  state: ConversationState;
}

export interface ConversationState {
  /** Current chat status */
  chatStatus: ChatStatus;
  /** Is conversation blocked */
  isBlocked?: boolean;
  /** Authenticated user ID */
  authenticatedUserId?: string;
  /** Unauthenticated conversation ID */
  unauthConversationId?: string;
  /** Privacy policy URL */
  privacyPolicyUrl?: string;
  /** Allow deleting conversation */
  allowDeleteConversation?: boolean;
  /** Allow file upload in human chat */
  allowHumanChatFileUpload?: boolean;
  /** Should poll for updates */
  poll?: boolean;
  /** Is human agent typing */
  humanIsTyping?: boolean;
  /** Maximum input characters */
  maxInputChars?: number;
  /** Human chat skill */
  skill?: string;
  /** Awaiting files configuration */
  awaitingFiles?: ConversationStateFiles;
}

export interface ConversationStateFiles {
  /** Accepted file MIME types */
  acceptedTypes?: string[];
  /** Maximum number of files */
  maxNumberOfFiles?: number;
}

export interface SmartReply {
  /** Smart reply options */
  replies?: SmartReplyOption[];
}

export interface SmartReplyOption {
  /** Reply ID */
  id: string;
  /** Reply text */
  text: string;
}

export interface File {
  /** File name */
  filename: string;
  /** MIME type */
  mimeType: string;
  /** File URL */
  url: string;
  /** Is uploading flag */
  isUploading: boolean;
  /** Has upload error flag */
  hasUploadError: boolean;
}

export interface ClientTyping {
  /** Current text length */
  length: number;
  /** Maximum allowed length */
  maxLength: number;
}
