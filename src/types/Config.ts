/**
 * Type definitions for Boost.ai Chat Configuration
 * Mirrors the Kotlin SDK configuration structure
 */

export type ConversationPace = 'glacial' | 'slower' | 'slow' | 'normal' | 'fast' | 'faster' | 'supersonic';
export type AvatarShape = 'rounded' | 'squared';
export type ButtonType = 'button' | 'bullet';

export interface ChatConfig {
  /** Language-specific UI text strings */
  messages?: { [languageCode: string]: Messages };
  /** Chat panel configuration */
  chatPanel?: ChatPanel;
}

export interface Messages {
  back?: string;
  closeWindow?: string;
  composeCharactersUsed?: string;
  composePlaceholder?: string;
  deleteConversation?: string;
  downloadConversation?: string;
  feedbackPlaceholder?: string;
  feedbackPrompt?: string;
  feedbackThumbsDown?: string;
  feedbackThumbsUp?: string;
  filterSelect?: string;
  headerText?: string;
  loggedIn?: string;
  messageThumbsDown?: string;
  messageThumbsUp?: string;
  minimizeWindow?: string;
  openMenu?: string;
  opensInNewTab?: string;
  privacyPolicy?: string;
  submitFeedback?: string;
  submitMessage?: string;
  textTooLong?: string;
  uploadFile?: string;
  uploadFileError?: string;
  uploadFileProgress?: string;
  uploadFileSuccess?: string;
  chatServiceUnavailable?: string;
  retry?: string;
}

export interface ChatPanel {
  header?: Header;
  styling?: Styling;
  settings?: Settings;
}

export interface Header {
  filters?: Filters;
  /** Chat window title */
  title?: string;
  hideMinimizeButton?: boolean;
  hideMenuButton?: boolean;
}

export interface Filters {
  /** Action filter values */
  filterValues?: string[];
  /** Filter objects */
  options?: Filter[];
}

export interface Filter {
  id: number;
  title: string;
  values: string[];
}

export interface Styling {
  /** Conversation pacing */
  pace?: ConversationPace;
  /** Avatar shape */
  avatarShape?: AvatarShape;
  /** Hide avatar */
  hideAvatar?: boolean;

  // Color properties (hex strings like "#FF5733")
  /** Header bar and menu background color */
  primaryColor?: string;
  /** Header text and input outline color */
  contrastColor?: string;
  /** Panel background color */
  panelBackgroundColor?: string;
  /** Secure chat banner background color */
  secureChatBannerBackgroundColor?: string;
  /** Secure chat banner text color */
  secureChatBannerTextColor?: string;

  // Nested styling objects
  chatBubbles?: ChatBubbles;
  buttons?: Buttons;
  composer?: Composer;
  messageFeedback?: MessageFeedback;
  fonts?: Fonts;
}

export interface ChatBubbles {
  /** Client message background color (hex) */
  userBackgroundColor?: string;
  /** Client message text color (hex) */
  userTextColor?: string;
  /** Virtual agent message background color (hex) */
  vaBackgroundColor?: string;
  /** Virtual agent text color (hex) */
  vaTextColor?: string;
  /** Typing indicator dot color (hex) */
  typingDotColor?: string;
  /** Typing indicator background color (hex) */
  typingBackgroundColor?: string;
}

export interface Buttons {
  /** Button background color (hex) */
  backgroundColor?: string;
  /** Button focus background color (hex) */
  focusBackgroundColor?: string;
  /** Button focus text color (hex) */
  focusTextColor?: string;
  /** Button text color (hex) */
  textColor?: string;
  /** Enable multiline button text */
  multiline?: boolean;
  /** Button style variant */
  variant?: ButtonType;
}

export interface Composer {
  /** Hide the composer (input area) */
  hide?: boolean;
  /** Character count text color (hex) */
  composeLengthColor?: string;
  /** File upload button color (hex) */
  fileUploadButtonColor?: string;
  /** Composer frame background color (hex) */
  frameBackgroundColor?: string;
  /** Send button color (hex) */
  sendButtonColor?: string;
  /** Send button disabled color (hex) */
  sendButtonDisabledColor?: string;
  /** Textarea background color (hex) */
  textareaBackgroundColor?: string;
  /** Textarea border color (hex) */
  textareaBorderColor?: string;
  /** Textarea focus border color (hex) */
  textareaFocusBorderColor?: string;
  /** Textarea focus outline color (hex) */
  textareaFocusOutlineColor?: string;
  /** Textarea text color (hex) */
  textareaTextColor?: string;
  /** Textarea placeholder text color (hex) */
  textareaPlaceholderTextColor?: string;
  /** Top border color (hex) */
  topBorderColor?: string;
  /** Top border focus color (hex) */
  topBorderFocusColor?: string;
}

export interface MessageFeedback {
  /** Hide message feedback thumbs up/down */
  hide?: boolean;
  /** Feedback outline color (hex) */
  outlineColor?: string;
  /** Feedback selected color (hex) */
  selectedColor?: string;
}

export interface Fonts {
  /** Font name for body text (must be registered via registerFont) */
  bodyFont?: string;
  /** Font name for headlines (must be registered via registerFont) */
  headlineFont?: string;
  /** Font name for footnote text (must be registered via registerFont) */
  footnoteFont?: string;
  /** Font name for menu items (must be registered via registerFont) */
  menuItemFont?: string;
}

export interface Settings {
  /** Authentication start trigger action ID */
  authStartTriggerActionId?: number;
  /** Context topic intent ID */
  contextTopicIntentId?: number;
  /** Conversation ID to resume */
  conversationId?: string;
  /** Custom payload forwarded to external APIs */
  customPayload?: any;
  /** File upload service endpoint URL */
  fileUploadServiceEndpointUrl?: string;
  /** File expiration in seconds */
  fileExpirationSeconds?: number;
  /** Show message feedback on first action */
  messageFeedbackOnFirstAction?: boolean;
  /** Remember conversation between sessions */
  rememberConversation?: boolean;
  /** Remember conversation expiration duration */
  rememberConversationExpirationDuration?: string;
  /** Remove remembered conversation on chat panel close */
  removeRememberedConversationOnChatPanelClose?: boolean;
  /** Request conversation feedback */
  requestFeedback?: boolean;
  /** Show link clicks as chat bubbles */
  showLinkClickAsChatBubble?: boolean;
  /** Human chat skill */
  skill?: string;
  /** Start language (BCP47 code, e.g., 'en-US') */
  startLanguage?: string;
  /** Start new conversation on resume failure */
  startNewConversationOnResumeFailure?: boolean;
  /** Start trigger action ID */
  startTriggerActionId?: number;
  /** Trigger action on resume */
  triggerActionOnResume?: boolean;
  /** User token for authenticated users */
  userToken?: string;
  /** Skip welcome message */
  skipWelcomeMessage?: boolean;
}
