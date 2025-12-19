/**
 * useBoostAIMessages hook
 * Subscribe to message stream
 */

import { useState, useEffect } from 'react';
import { BoostAI } from '../BoostAIModule';
import type { APIMessage } from '../types';

export interface UseBoostAIMessagesOptions {
  /** Whether to fetch initial messages on mount */
  fetchInitial?: boolean;
}

export interface UseBoostAIMessagesResult {
  /** Array of messages */
  messages: APIMessage[];
  /** Whether messages are loading */
  isLoading: boolean;
  /** Error if any */
  error: Error | null;
}

/**
 * Hook to subscribe to message stream
 *
 * @example
 * ```tsx
 * const { messages, isLoading } = useBoostAIMessages();
 *
 * return (
 *   <FlatList
 *     data={messages}
 *     renderItem={({ item }) => <Message message={item} />}
 *   />
 * );
 * ```
 */
export function useBoostAIMessages(
  options: UseBoostAIMessagesOptions = {}
): UseBoostAIMessagesResult {
  const { fetchInitial = true } = options;

  const [messages, setMessages] = useState<APIMessage[]>([]);
  const [isLoading, setIsLoading] = useState(fetchInitial);
  const [error, setError] = useState<Error | null>(null);

  // Fetch initial messages
  useEffect(() => {
    if (!fetchInitial) return;

    let mounted = true;

    const fetchMessages = async () => {
      try {
        const initialMessages = await BoostAI.getMessages();
        if (mounted) {
          setMessages(initialMessages);
          setIsLoading(false);
        }
      } catch (err) {
        if (mounted) {
          setError(err as Error);
          setIsLoading(false);
        }
      }
    };

    fetchMessages();

    return () => {
      mounted = false;
    };
  }, [fetchInitial]);

  // Subscribe to new messages
  useEffect(() => {
    const subscription = BoostAI.addMessageListener((event) => {
      setMessages((prev) => [...prev, event.message]);
    });

    return () => {
      subscription.remove();
    };
  }, []);

  return {
    messages,
    isLoading,
    error,
  };
}
