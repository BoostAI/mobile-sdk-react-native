/**
 * BoostAIAvatarView Component
 * Fabric native avatar button component
 */

import React from 'react';
import {
  StyleProp,
  ViewStyle,
} from 'react-native';
import type { ChatConfig } from './types';
import BoostAIAvatarViewNativeComponent from './specs/BoostAIAvatarViewNativeComponent';

const BoostAIAvatarViewNative = BoostAIAvatarViewNativeComponent as any;

export interface BoostAIAvatarViewProps {
  /** Avatar image resource name (Android drawable name) */
  avatarImageResource?: string;
  /** Custom configuration */
  customConfig?: ChatConfig;
  /** Style */
  style?: StyleProp<ViewStyle>;
}

/**
 * BoostAIAvatarView - Floating avatar button
 *
 * Displays a clickable avatar button that opens the chat in a new activity.
 *
 * @example
 * ```tsx
 * <BoostAIAvatarView
 *   avatarImageResource="my_avatar"
 *   customConfig={config}
 *   style={styles.avatar}
 * />
 * ```
 */
export const BoostAIAvatarView: React.FC<BoostAIAvatarViewProps> = (props) => {
  return <BoostAIAvatarViewNative {...props} />;
};

export default BoostAIAvatarView;
