/**
 * BoostAIChatView Component
 * Fabric native chat UI component
 */

import React, { useRef, useCallback, useImperativeHandle, forwardRef } from 'react';
import {
  UIManager,
  findNodeHandle,
  StyleProp,
  ViewStyle,
} from 'react-native';
import type { ChatConfig } from './types';
import BoostAIChatViewNativeComponent from './specs/BoostAIChatViewNativeComponent';

const BoostAIChatViewNative = BoostAIChatViewNativeComponent as any;

export interface BoostAIChatViewProps {
  /** Custom configuration */
  customConfig?: ChatConfig;
  /** Display as dialog */
  isDialog?: boolean;
  /** Style */
  style?: StyleProp<ViewStyle>;
}

export interface BoostAIChatViewRef {
  /** Close the chat */
  close: () => void;
  /** Show settings menu */
  showSettings: () => void;
  /** Hide settings menu */
  hideSettings: () => void;
  /** Show feedback dialog */
  showFeedback: () => void;
  /** Hide feedback dialog */
  hideFeedback: () => void;
}

/**
 * BoostAIChatView - Native chat UI component
 *
 * @example
 * ```tsx
 * const chatRef = useRef<BoostAIChatViewRef>(null);
 *
 * <BoostAIChatView
 *   ref={chatRef}
 *   customConfig={config}
 *   isDialog={false}
 *   style={styles.chat}
 * />
 *
 * // Call methods
 * chatRef.current?.close();
 * ```
 */
export const BoostAIChatView = forwardRef<BoostAIChatViewRef, BoostAIChatViewProps>(
  (props, ref) => {
    const nativeRef = useRef(null);

    const dispatchCommand = useCallback((commandName: string, args: any[] = []) => {
      const handle = findNodeHandle(nativeRef.current);
      if (handle != null) {
        UIManager.dispatchViewManagerCommand(handle, commandName, args);
      }
    }, []);

    useImperativeHandle(ref, () => ({
      close: () => dispatchCommand('close'),
      showSettings: () => dispatchCommand('showSettings'),
      hideSettings: () => dispatchCommand('hideSettings'),
      showFeedback: () => dispatchCommand('showFeedback'),
      hideFeedback: () => dispatchCommand('hideFeedback'),
    }));

    return <BoostAIChatViewNative ref={nativeRef} {...props} />;
  }
);

BoostAIChatView.displayName = 'BoostAIChatView';

export default BoostAIChatView;
