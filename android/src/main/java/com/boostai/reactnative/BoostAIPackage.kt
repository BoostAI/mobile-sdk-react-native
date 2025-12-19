package com.boostai.reactnative

import android.util.Log
import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import com.facebook.react.uimanager.ViewManager

/**
 * React Native Package for Boost.ai SDK (BaseReactPackage for new architecture compatibility)
 */
class BoostAIPackage : BaseReactPackage() {
    init {
        Log.d("BoostAIPackage", "BoostAIPackage initialized")
    }

    override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
        Log.d("BoostAIPackage", "getModule called for: $name (expecting: ${BoostAIModule.NAME})")
        return when (name) {
            BoostAIModule.NAME -> {
                Log.d("BoostAIPackage", "Creating BoostAIModule")
                BoostAIModule(reactContext)
            }
            else -> null
        }
    }

    override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
        Log.d("BoostAIPackage", "getReactModuleInfoProvider called")
        val moduleList: Array<Class<out NativeModule?>> = arrayOf(BoostAIModule::class.java)
        val reactModuleInfoMap: MutableMap<String, ReactModuleInfo> = HashMap()

        for (moduleClass in moduleList) {
            val reactModule = moduleClass.getAnnotation(ReactModule::class.java) ?: continue
            val moduleInfo = ReactModuleInfo(
                reactModule.name,
                moduleClass.name,
                true,  // canOverrideExistingModule
                reactModule.needsEagerInit,
                reactModule.isCxxModule,
                true  // isTurboModule (new architecture enabled)
            )
            Log.d("BoostAIPackage", "Registering module: ${reactModule.name}, className: ${moduleClass.name}, isTurboModule: true")
            reactModuleInfoMap[reactModule.name] = moduleInfo
        }

        return ReactModuleInfoProvider { reactModuleInfoMap }
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(
            BoostAIChatViewManager(reactContext),
            BoostAIAvatarViewManager(reactContext)
        )
    }
}
