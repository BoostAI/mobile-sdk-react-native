/**
 * BoostAIModalTouchable Component
 * Wrapper that opens chat in a modal when pressed
 */

import React from 'react';
import {
  TouchableOpacity,
  TouchableOpacityProps,
} from 'react-native';
import type { ChatConfig } from './types';
import BoostAI from './BoostAIModule';

export interface BoostAIModalTouchableProps extends TouchableOpacityProps {
  /** Custom configuration to pass to the chat modal */
  customConfig?: ChatConfig;
  /** Custom onPress handler (called before opening chat) */
  onPress?: () => void;
}

/**
 * BoostAIModalTouchable - Touchable wrapper for opening chat modal
 *
 * Wraps any React Native components and opens the chat in a modal activity when pressed.
 * Users have full control over what gets rendered.
 *
 * @example
 * ```tsx
 * <BoostAIModalTouchable customConfig={config}>
 *   <Image source={require('./avatar.png')} style={styles.avatar} />
 * </BoostAIModalTouchable>
 * ```
 *
 * @example
 * ```tsx
 * <BoostAIModalTouchable customConfig={config}>
 *   <View style={styles.fab}>
 *     <Icon name="chat-bubble" size={24} />
 *   </View>
 * </BoostAIModalTouchable>
 * ```
 */
export const BoostAIModalTouchable: React.FC<BoostAIModalTouchableProps> = ({
  customConfig,
  onPress,
  children,
  ...touchableProps
}) => {
  const handlePress = () => {
    // Call custom onPress if provided
    onPress?.();

    // Open chat modal
    BoostAI.openChatModal(customConfig);
  };

  return (
    <TouchableOpacity onPress={handlePress} {...touchableProps}>
      {children}
    </TouchableOpacity>
  );
};

export default BoostAIModalTouchable;
