/**
 * useBoostAIEvents hook
 * Subscribe to specific UI events
 */

import { useEffect, useCallback } from 'react';
import { BoostAI } from '../BoostAIModule';
import type { BoostAIEvent } from '../types';

/**
 * Hook to subscribe to specific UI events
 *
 * @example
 * ```tsx
 * useBoostAIEvents('chatPanelOpened', () => {
 *   console.log('Chat opened!');
 * });
 *
 * useBoostAIEvents('messageSent', () => {
 *   console.log('Message sent!');
 * });
 * ```
 */
export function useBoostAIEvents(
  eventType: BoostAIEvent | BoostAIEvent[],
  callback: (detail?: any) => void
): void {
  const memoizedCallback = useCallback(callback, [callback]);

  useEffect(() => {
    const eventTypes = Array.isArray(eventType) ? eventType : [eventType];

    const subscription = BoostAI.addUIEventListener((event) => {
      if (eventTypes.includes(event.event)) {
        memoizedCallback(event.detail);
      }
    });

    return () => {
      subscription.remove();
    };
  }, [eventType, memoizedCallback]);
}

/**
 * Hook to subscribe to all UI events
 *
 * @example
 * ```tsx
 * useBoostAIAllEvents((event, detail) => {
 *   console.log('Event:', event, 'Detail:', detail);
 * });
 * ```
 */
export function useBoostAIAllEvents(
  callback: (event: BoostAIEvent, detail?: any) => void
): void {
  const memoizedCallback = useCallback(callback, [callback]);

  useEffect(() => {
    const subscription = BoostAI.addUIEventListener((eventData) => {
      memoizedCallback(eventData.event, eventData.detail);
    });

    return () => {
      subscription.remove();
    };
  }, [memoizedCallback]);
}
