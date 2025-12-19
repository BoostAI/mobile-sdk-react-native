/**
 * Fabric component specification for BoostAIAvatarView
 * This file is used by CodeGen to generate native bindings
 */

import type {ViewProps} from 'react-native';
import type {HostComponent} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface NativeProps extends ViewProps {
  // No custom props - configuration is handled via TurboModule
}

export default codegenNativeComponent<NativeProps>('BoostAIAvatarView') as HostComponent<NativeProps>;
