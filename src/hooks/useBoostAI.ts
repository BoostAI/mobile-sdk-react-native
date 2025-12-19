/**
 * Main useBoostAI hook
 * Provides initialization and common chat operations
 */

import { useState, useEffect, useCallback } from 'react';
import { BoostAI } from '../BoostAIModule';
import type { ChatConfig, APIMessage } from '../types';

export interface UseBoostAIOptions {
  /** Boost.ai domain */
  domain: string;
  /** Custom configuration */
  customConfig?: ChatConfig;
  /** Language code (BCP-47) */
  languageCode?: string;
  /** User token for authenticated users */
  userToken?: string;
  /** Auto-start conversation on initialization */
  autoStart?: boolean;
}

export interface UseBoostAIResult {
  /** Whether SDK is initialized */
  isInitialized: boolean;
  /** Initialization error if any */
  error: Error | null;
  /** Current conversation ID */
  conversationId: string | null;
  /** Current configuration */
  config: ChatConfig | null;
  /** Start a new conversation */
  start: () => Promise<APIMessage>;
  /** Stop the conversation */
  stop: () => Promise<APIMessage>;
  /** Resume conversation */
  resume: () => Promise<APIMessage>;
  /** Send a text message */
  sendMessage: (text: string) => Promise<APIMessage>;
  /** Reset conversation */
  resetConversation: () => void;
}

/**
 * Main hook for Boost.ai SDK
 *
 * @example
 * ```tsx
 * const { isInitialized, sendMessage } = useBoostAI({
 *   domain: 'your-name.boost.ai',
 *   languageCode: 'en-US',
 *   autoStart: true
 * });
 *
 * if (isInitialized) {
 *   sendMessage('Hello!');
 * }
 * ```
 */
export function useBoostAI(options: UseBoostAIOptions): UseBoostAIResult {
  const { domain, customConfig, languageCode, userToken, autoStart = false } = options;

  const [isInitialized, setIsInitialized] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const [conversationId, setConversationId] = useState<string | null>(null);
  const [config, setConfig] = useState<ChatConfig | null>(null);

  // Initialize SDK
  useEffect(() => {
    let mounted = true;

    const initialize = async () => {
      try {
        // Set optional configurations
        if (languageCode) {
          BoostAI.setLanguageCode(languageCode);
        }
        if (userToken) {
          BoostAI.setUserToken(userToken);
        }

        // Initialize with domain
        const initialConfig = await BoostAI.initialize(domain);

        if (!mounted) return;

        // Set custom config if provided
        if (customConfig) {
          await BoostAI.setCustomConfig(customConfig);
          setConfig(customConfig);
        } else {
          setConfig(initialConfig);
        }

        setIsInitialized(true);

        // Auto-start if requested
        if (autoStart) {
          await BoostAI.start();
          const convId = await BoostAI.getConversationId();
          if (mounted) {
            setConversationId(convId);
          }
        }
      } catch (err) {
        if (mounted) {
          setError(err as Error);
          setIsInitialized(false);
        }
      }
    };

    initialize();

    return () => {
      mounted = false;
    };
  }, [domain, customConfig, languageCode, userToken, autoStart]);

  // Listen for conversation ID changes
  useEffect(() => {
    const updateConversationId = async () => {
      try {
        const convId = await BoostAI.getConversationId();
        setConversationId(convId);
      } catch (err) {
        console.error('Failed to get conversation ID:', err);
      }
    };

    const subscription = BoostAI.addUIEventListener((event) => {
      if (event.event === 'conversationIdChanged') {
        updateConversationId();
      }
    });

    return () => {
      subscription.remove();
    };
  }, []);

  const start = useCallback(async () => {
    const message = await BoostAI.start();
    const convId = await BoostAI.getConversationId();
    setConversationId(convId);
    return message;
  }, []);

  const stop = useCallback(() => {
    return BoostAI.stop();
  }, []);

  const resume = useCallback(async () => {
    const message = await BoostAI.resume();
    const convId = await BoostAI.getConversationId();
    setConversationId(convId);
    return message;
  }, []);

  const sendMessage = useCallback((text: string) => {
    return BoostAI.sendMessage(text);
  }, []);

  const resetConversation = useCallback(() => {
    BoostAI.resetConversation();
    setConversationId(null);
  }, []);

  return {
    isInitialized,
    error,
    conversationId,
    config,
    start,
    stop,
    resume,
    sendMessage,
    resetConversation,
  };
}
