package com.boostai.reactnative.events

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule

/**
 * Centralized event emission to React Native
 */
class EventEmitter(private val reactContext: ReactApplicationContext) {

    companion object {
        // Event names
        const val EVENT_MESSAGE_RECEIVED = "BoostAI:onMessageReceived"
        const val EVENT_CONFIG_RECEIVED = "BoostAI:onConfigReceived"
        const val EVENT_BACKEND_EVENT = "BoostAI:onBackendEvent"
        const val EVENT_ERROR = "BoostAI:onError"
        const val EVENT_UI_EVENT = "BoostAI:onUIEvent"
    }

    /**
     * Send an event to JavaScript
     */
    fun sendEvent(eventName: String, params: WritableMap?) {
        if (reactContext.hasActiveCatalystInstance()) {
            reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
        }
    }

    /**
     * Check if React Native context is available
     */
    fun isAvailable(): Boolean {
        return reactContext.hasActiveCatalystInstance()
    }
}
