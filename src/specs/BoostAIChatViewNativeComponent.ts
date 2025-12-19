/**
 * Fabric component specification for BoostAIChatView
 * This file is used by CodeGen to generate native bindings
 */

import type {ViewProps} from 'react-native';
import type {HostComponent} from 'react-native';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

export interface NativeProps extends ViewProps {
  isDialog?: boolean;
}

export default codegenNativeComponent<NativeProps>('BoostAIChatView') as HostComponent<NativeProps>;
