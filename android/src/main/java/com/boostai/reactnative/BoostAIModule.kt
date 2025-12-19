package com.boostai.reactnative

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.annotation.FontRes
import com.boostai.reactnative.converters.ConfigConverter
import com.boostai.reactnative.converters.MessageConverter
import com.boostai.reactnative.events.EventEmitter
import com.facebook.react.bridge.*
import com.facebook.react.module.annotations.ReactModule
import kotlinx.serialization.json.JsonElement
import no.boostai.sdk.ChatBackend.ChatBackend
import no.boostai.sdk.ChatBackend.Objects.Response.APIMessage
import no.boostai.sdk.ChatBackend.Objects.ChatConfig
import no.boostai.sdk.ChatBackend.Objects.FeedbackValue
import no.boostai.sdk.UI.ChatViewActivity
import no.boostai.sdk.UI.Events.BoostUIEvents

/**
 * React Native TurboModule for Boost.ai SDK
 * Bridges ChatBackend singleton to React Native via TurboModule
 */
@ReactModule(name = BoostAIModule.NAME)
class BoostAIModule(reactContext: ReactApplicationContext) :
    NativeBoostAISpec(reactContext),
    ChatBackend.MessageObserver,
    ChatBackend.ConfigObserver,
    ChatBackend.EventObserver {

    private val eventEmitter = EventEmitter(reactContext)
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        const val NAME = "BoostAI"
    }

    override fun getName(): String = NAME

    init {
        android.util.Log.d("BoostAIModule", "BoostAIModule initialized - TurboModule NAME: $NAME")
        // Register observers
        ChatBackend.addMessageObserver(this)
        ChatBackend.addConfigObserver(this)
        ChatBackend.addEventObserver(this)
    }

    override fun onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy()
        // Clean up observers
        ChatBackend.removeMessageObserver(this)
        ChatBackend.removeConfigObserver(this)
        ChatBackend.removeEventObserver(this)
    }

    // ==================== Observer Implementations ====================

    override fun onMessageReceived(backend: ChatBackend, message: APIMessage) {
        val params = Arguments.createMap()
        params.putMap("message", MessageConverter.toWritableMap(message))
        eventEmitter.sendEvent(EventEmitter.EVENT_MESSAGE_RECEIVED, params)
    }

    override fun onConfigReceived(backend: ChatBackend, config: ChatConfig) {
        val params = Arguments.createMap()
        params.putMap("config", ConfigConverter.toWritableMap(config))
        eventEmitter.sendEvent(EventEmitter.EVENT_CONFIG_RECEIVED, params)
    }

    override fun onBackendEventReceived(backend: ChatBackend, type: String, detail: JsonElement?) {
        val params = Arguments.createMap()
        params.putString("type", type)
        // detail conversion would require JsonElement serialization
        // For now, we'll skip it or convert to string
        eventEmitter.sendEvent(EventEmitter.EVENT_BACKEND_EVENT, params)
    }

    override fun onFailure(backend: ChatBackend, error: Exception) {
        val params = Arguments.createMap()
        params.putString("error", error.message ?: "Unknown error")
        error.cause?.message?.let { params.putString("code", it) }
        eventEmitter.sendEvent(EventEmitter.EVENT_ERROR, params)
    }

    // ==================== Initialization & Configuration ====================

    override fun initialize(domain: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.domain = domain
                ChatBackend.onReady(object : ChatBackend.ConfigReadyListener {
                    override fun onReady(config: ChatConfig) {
                        promise.resolve(ConfigConverter.toWritableMap(config))
                    }

                    override fun onFailure(exception: Exception) {
                        promise.reject("INIT_ERROR", exception.message, exception)
                    }
                })
            } catch (e: Exception) {
                promise.reject("INIT_ERROR", e.message, e)
            }
        }
    }

    override fun setCustomConfig(configMap: ReadableMap, promise: Promise) {
        runOnMainThread {
            try {
                val config = ConfigConverter.toKotlinConfig(configMap)
                ChatBackend.customConfig = config
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("CONFIG_ERROR", e.message, e)
            }
        }
    }

    override fun getConfig(promise: Promise) {
        runOnMainThread {
            try {
                val config = ChatBackend.config
                promise.resolve(ConfigConverter.toWritableMap(config))
            } catch (e: Exception) {
                promise.reject("CONFIG_ERROR", e.message, e)
            }
        }
    }

    override fun setLanguageCode(languageCode: String) {
        runOnMainThread {
            ChatBackend.languageCode = languageCode
        }
    }

    override fun setUserToken(userToken: String?) {
        runOnMainThread {
            ChatBackend.userToken = userToken
        }
    }

    override fun setConversationId(conversationId: String?) {
        runOnMainThread {
            ChatBackend.conversationId = conversationId
        }
    }

    override fun getConversationId(promise: Promise) {
        runOnMainThread {
            promise.resolve(ChatBackend.conversationId)
        }
    }

    override fun setCertificatePinningEnabled(enabled: Boolean) {
        runOnMainThread {
            ChatBackend.isCertificatePinningEnabled = enabled
        }
    }

    override fun registerFont(name: String, resourceId: Double) {
        ConfigConverter.registerFont(name, resourceId.toInt())
    }

    // ==================== Conversation Control ====================

    override fun start(optionsMap: ReadableMap?, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.start(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("START_ERROR", e.message, e)
            }
        }
    }

    override fun stop(promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.stop(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("STOP_ERROR", e.message, e)
            }
        }
    }

    override fun resume(optionsMap: ReadableMap?, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.resume(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("RESUME_ERROR", e.message, e)
            }
        }
    }

    override fun delete(promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.delete(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("DELETE_ERROR", e.message, e)
            }
        }
    }

    override fun resetConversation() {
        runOnMainThread {
            ChatBackend.conversationId = null
            ChatBackend.messages.clear()
        }
    }

    // ==================== Messaging ====================

    override fun sendMessage(text: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.message(text, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("SEND_ERROR", e.message, e)
            }
        }
    }

    override fun actionButton(id: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.actionButton(id, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("ACTION_ERROR", e.message, e)
            }
        }
    }

    override fun urlButton(id: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.urlButton(id, createMessageResponseListener(promise))
                promise.resolve(null)
            } catch (e: Exception) {
                promise.reject("URL_ERROR", e.message, e)
            }
        }
    }

    override fun triggerAction(id: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.triggerAction(id, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("TRIGGER_ERROR", e.message, e)
            }
        }
    }

    override fun sendSmartReply(value: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.smartReply(value, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("SMART_REPLY_ERROR", e.message, e)
            }
        }
    }

    override fun sendHumanChatMessage(value: String, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.humanChatPost(value, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("HUMAN_CHAT_ERROR", e.message, e)
            }
        }
    }

    override fun clientTyping(value: String, promise: Promise) {
        runOnMainThread {
            try {
                val typing = ChatBackend.clientTyping(value)
                val result = Arguments.createMap()
                result.putInt("length", typing.length)
                result.putInt("maxLength", typing.maxLength)
                promise.resolve(result)
            } catch (e: Exception) {
                promise.reject("TYPING_ERROR", e.message, e)
            }
        }
    }

    // ==================== Feedback ====================

    override fun sendFeedback(id: String, value: Double, promise: Promise) {
        runOnMainThread {
            try {
                val feedbackValue = when (value.toInt()) {
                    1 -> FeedbackValue.POSITIVE
                    -1 -> FeedbackValue.NEGATIVE
                    0 -> FeedbackValue.REMOVE_POSITIVE
                    else -> FeedbackValue.REMOVE_NEGATIVE
                }
                ChatBackend.feedback(id, feedbackValue, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("FEEDBACK_ERROR", e.message, e)
            }
        }
    }

    override fun sendConversationFeedback(rating: Double, text: String?, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.conversationFeedback(rating.toInt(), text, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("CONVERSATION_FEEDBACK_ERROR", e.message, e)
            }
        }
    }

    // ==================== Human Chat ====================

    override fun poll(promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.poll(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("POLL_ERROR", e.message, e)
            }
        }
    }

    override fun pollStop(promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.pollStop(listener = createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("POLL_STOP_ERROR", e.message, e)
            }
        }
    }

    // ==================== File Upload ====================

    override fun sendFiles(filesArray: ReadableArray, message: String?, promise: Promise) {
        runOnMainThread {
            try {
                // File handling would require ContentResolver and URI handling
                // This is a placeholder - full implementation would parse URIs
                promise.reject("NOT_IMPLEMENTED", "File upload not yet implemented")
            } catch (e: Exception) {
                promise.reject("FILE_ERROR", e.message, e)
            }
        }
    }

    // ==================== Download ====================

    override fun downloadConversation(userToken: String?, promise: Promise) {
        runOnMainThread {
            try {
                ChatBackend.download(userToken, createMessageResponseListener(promise))
            } catch (e: Exception) {
                promise.reject("DOWNLOAD_ERROR", e.message, e)
            }
        }
    }

    // ==================== State Management ====================

    override fun getMessages(promise: Promise) {
        runOnMainThread {
            try {
                val messages = ChatBackend.messages
                promise.resolve(MessageConverter.toWritableArray(messages))
            } catch (e: Exception) {
                promise.reject("MESSAGES_ERROR", e.message, e)
            }
        }
    }

    // ==================== UI ====================

    override fun openChatModal(customConfig: ReadableMap?) {
        runOnMainThread {
            val activity = reactApplicationContext.currentActivity ?: return@runOnMainThread

            try {
                val intent = Intent(activity, ChatViewActivity::class.java).apply {
                    putExtra(ChatViewActivity.IS_DIALOG, true)
                    customConfig?.let {
                        val config = ConfigConverter.toKotlinConfig(it)
                        putExtra(ChatViewActivity.CUSTOM_CONFIG, config)
                    }
                }

                activity.startActivity(intent)

                // Emit UI event
                BoostUIEvents.notifyObservers(BoostUIEvents.Event.chatPanelOpened, null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ==================== Helper Methods ====================

    private fun runOnMainThread(action: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action()
        } else {
            mainHandler.post(action)
        }
    }

    private fun createMessageResponseListener(promise: Promise): ChatBackend.APIMessageResponseListener {
        return object : ChatBackend.APIMessageResponseListener {
            override fun onResponse(apiMessage: APIMessage) {
                promise.resolve(MessageConverter.toWritableMap(apiMessage))
            }

            override fun onFailure(exception: Exception) {
                promise.reject("API_ERROR", exception.message, exception)
            }
        }
    }
}
