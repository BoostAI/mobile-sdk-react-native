package com.boostai.reactnative

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Choreographer
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentActivity
import com.boostai.reactnative.converters.ConfigConverter
import com.boostai.reactnative.events.EventEmitter
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.ViewGroupManager
import com.facebook.react.uimanager.annotations.ReactProp
import no.boostai.sdk.ChatBackend.Objects.ChatConfig
import no.boostai.sdk.UI.ChatViewFragment
import no.boostai.sdk.UI.Events.BoostUIEvents

/**
 * ViewManager for ChatViewFragment
 * Wraps the native chat UI in a React Native component
 */
class BoostAIChatViewManager(
    private val reactContext: ReactApplicationContext
) : ViewGroupManager<FrameLayout>(), BoostUIEvents.Observer {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var chatViewFragment: ChatViewFragment? = null
    private var customConfig: ChatConfig? = null
    private var isDialog: Boolean = false
    private val eventEmitter = EventEmitter(reactContext)

    companion object {
        const val REACT_CLASS = "BoostAIChatView"
        private const val COMMAND_CLOSE = "close"
        private const val COMMAND_SHOW_SETTINGS = "showSettings"
        private const val COMMAND_HIDE_SETTINGS = "hideSettings"
        private const val COMMAND_SHOW_FEEDBACK = "showFeedback"
        private const val COMMAND_HIDE_FEEDBACK = "hideFeedback"
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(reactContext: ThemedReactContext): FrameLayout {
        Log.d("BoostAIChatView", "createViewInstance called")

        // Create custom FrameLayout that properly handles layout for fragments in Fabric
        val container = object : FrameLayout(reactContext) {
            private var keyboardHeight = 0

            override fun requestLayout() {
                super.requestLayout()

                // Force immediate layout pass for Fabric compatibility
                // Fabric doesn't automatically propagate layout calls to embedded fragments
                post {
                    if (width > 0 && height > 0) {
                        measure(
                            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
                        )
                        layout(left, top, right, bottom)
                    }
                }
            }

            override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
                super.onLayout(changed, left, top, right, bottom)

                // Manually layout all children (the fragment's view)
                // This is critical for Fabric - without it, fragment views have 0x0 size
                val containerWidth = right - left
                val containerHeight = bottom - top

                for (i in 0 until childCount) {
                    val child = getChildAt(i)
                    if (containerWidth > 0 && containerHeight > 0) {
                        // Apply keyboard padding to child so input field stays above keyboard
                        val availableHeight = containerHeight - keyboardHeight

                        child.measure(
                            MeasureSpec.makeMeasureSpec(containerWidth, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(availableHeight, MeasureSpec.EXACTLY)
                        )
                        child.layout(0, 0, containerWidth, availableHeight)
                        Log.d("BoostAIChatView", "Child $i laid out: ${child.width}x${child.height} (keyboard: $keyboardHeight) in container ${containerWidth}x${containerHeight}")
                    }
                }
            }

            fun updateKeyboardHeight(newHeight: Int) {
                if (keyboardHeight != newHeight) {
                    keyboardHeight = newHeight
                    requestLayout()
                }
            }
        }

        container.id = View.generateViewId()
        Log.d("BoostAIChatView", "Container created with id: ${container.id}")

        // Set up window insets listener for keyboard handling
        ViewCompat.setOnApplyWindowInsetsListener(container) { view, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Use IME height if keyboard is visible, otherwise use system bars
            val bottomInset = if (imeInsets.bottom > systemBars.bottom) imeInsets.bottom else 0

            Log.d("BoostAIChatView", "Insets - IME: ${imeInsets.bottom}, SystemBars: ${systemBars.bottom}, Using: $bottomInset")

            container.updateKeyboardHeight(bottomInset)

            insets
        }

        // Register as UI event observer
        BoostUIEvents.addObserver(this)

        // Initialize fragment after view is attached
        container.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                Log.d("BoostAIChatView", "View attached to window, initializing fragment")
                container.removeOnAttachStateChangeListener(this)

                // Request initial window insets to get current keyboard state
                ViewCompat.requestApplyInsets(container)

                mainHandler.post {
                    initializeChatFragment(reactContext, container)
                }
            }

            override fun onViewDetachedFromWindow(v: View) {
                Log.d("BoostAIChatView", "View detached from window")
            }
        })

        return container
    }

    private fun initializeChatFragment(context: ThemedReactContext, container: FrameLayout) {
        Log.d("BoostAIChatView", "initializeChatFragment called")
        val activity = context.currentActivity as? FragmentActivity
        if (activity == null) {
            Log.e("BoostAIChatView", "Current activity is not a FragmentActivity")
            return
        }

        activity.runOnUiThread {
            try {
                Log.d("BoostAIChatView", "Creating ChatViewFragment")
                chatViewFragment = ChatViewFragment(
                    isDialog = isDialog,
                    customConfig = customConfig,
                    delegate = null,
                    chatResponseViewURLHandlingDelegate = null
                )

                val fragmentManager = activity.supportFragmentManager
                val transaction = fragmentManager.beginTransaction()

                chatViewFragment?.let { fragment ->
                    Log.d("BoostAIChatView", "Adding fragment to container with id: ${container.id}")
                    transaction.replace(container.id, fragment, "chat_view_fragment")
                    transaction.commitNow()
                    Log.d("BoostAIChatView", "Fragment transaction committed (commitNow)")

                    // Force layout after fragment is added - critical for Fabric
                    container.post {
                        container.requestLayout()
                        Log.d("BoostAIChatView", "requestLayout called on container after fragment commit")

                        // Also request layout for all children
                        for (i in 0 until container.childCount) {
                            container.getChildAt(i)?.requestLayout()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("BoostAIChatView", "Error initializing chat fragment", e)
                e.printStackTrace()
            }
        }
    }

    @ReactProp(name = "customConfig")
    fun setCustomConfig(view: FrameLayout, config: ReadableMap?) {
        if (config != null) {
            customConfig = ConfigConverter.toKotlinConfig(config)
            chatViewFragment?.let { fragment ->
                // Update fragment config if already initialized
                // This would require exposing a method on ChatViewFragment
            }
        }
    }

    @ReactProp(name = "isDialog")
    fun setIsDialog(view: FrameLayout, isDialog: Boolean) {
        this.isDialog = isDialog
    }

    override fun getCommandsMap(): Map<String, Int> {
        return mapOf(
            COMMAND_CLOSE to 1,
            COMMAND_SHOW_SETTINGS to 2,
            COMMAND_HIDE_SETTINGS to 3,
            COMMAND_SHOW_FEEDBACK to 4,
            COMMAND_HIDE_FEEDBACK to 5
        )
    }

    override fun receiveCommand(root: FrameLayout, commandId: String, args: ReadableArray?) {
        when (commandId) {
            COMMAND_CLOSE -> closeChat()
            COMMAND_SHOW_SETTINGS -> showSettings()
            COMMAND_HIDE_SETTINGS -> hideSettings()
            COMMAND_SHOW_FEEDBACK -> showFeedback()
            COMMAND_HIDE_FEEDBACK -> hideFeedback()
        }
    }

    private fun closeChat() {
        mainHandler.post {
            chatViewFragment?.activity?.finish()
        }
    }

    private fun showSettings() {
        // Would require exposing method on ChatViewFragment
    }

    private fun hideSettings() {
        // Would require exposing method on ChatViewFragment
    }

    private fun showFeedback() {
        // Would require exposing method on ChatViewFragment
    }

    private fun hideFeedback() {
        // Would require exposing method on ChatViewFragment
    }

    override fun onDropViewInstance(view: FrameLayout) {
        super.onDropViewInstance(view)
        BoostUIEvents.removeObserver(this)
        chatViewFragment = null
    }

    // ==================== BoostUIEvents.Observer Implementation ====================

    override fun onUIEventReceived(event: BoostUIEvents.Event, detail: Any?) {
        val params = Arguments.createMap()
        params.putString("event", event.name.lowercase())

        detail?.let {
            when (it) {
                is String -> params.putString("detail", it)
                is Int -> params.putInt("detail", it)
                is Boolean -> params.putBoolean("detail", it)
                is Double -> params.putDouble("detail", it)
                // Add more type conversions as needed
            }
        }

        eventEmitter.sendEvent(EventEmitter.EVENT_UI_EVENT, params)
    }
}
