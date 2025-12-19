package com.boostai.reactnative

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.boostai.reactnative.converters.ConfigConverter
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import no.boostai.sdk.ChatBackend.Objects.ChatConfig
import no.boostai.sdk.UI.ChatViewActivity
import no.boostai.sdk.UI.Events.BoostUIEvents

/**
 * ViewManager for Avatar Button
 * Simple button that opens ChatViewActivity when clicked
 */
class BoostAIAvatarViewManager(
    private val reactContext: ReactApplicationContext
) : SimpleViewManager<ImageView>() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var customConfig: ChatConfig? = null
    private var avatarDrawable: Drawable? = null

    companion object {
        const val REACT_CLASS = "BoostAIAvatarView"
    }

    override fun getName(): String = REACT_CLASS

    override fun createViewInstance(reactContext: ThemedReactContext): ImageView {
        val imageView = ImageView(reactContext)

        // Set default avatar if available
        try {
            // Try to load default avatar (matching native sample app)
            val resourceId = reactContext.resources.getIdentifier(
                "agent",
                "mipmap",
                reactContext.packageName
            )
            if (resourceId != 0) {
                avatarDrawable = ContextCompat.getDrawable(reactContext, resourceId)
                imageView.setImageDrawable(avatarDrawable)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Set click listener to open chat
        imageView.setOnClickListener {
            openChat()
        }

        // Make it circular
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.clipToOutline = true

        return imageView
    }

    @ReactProp(name = "avatarImageResource")
    fun setAvatarImageResource(view: ImageView, resourceName: String?) {
        if (resourceName.isNullOrBlank()) return

        mainHandler.post {
            try {
                val resourceId = reactContext.resources.getIdentifier(
                    resourceName,
                    "drawable",
                    reactContext.packageName
                )
                if (resourceId != 0) {
                    avatarDrawable = ContextCompat.getDrawable(reactContext, resourceId)
                    view.setImageDrawable(avatarDrawable)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @ReactProp(name = "customConfig")
    fun setCustomConfig(view: ImageView, config: ReadableMap?) {
        if (config != null) {
            customConfig = ConfigConverter.toKotlinConfig(config)
        }
    }

    private fun openChat() {
        mainHandler.post {
            val activity = reactContext.currentActivity ?: return@post

            try {
                val intent = Intent(activity, ChatViewActivity::class.java).apply {
                    putExtra(ChatViewActivity.IS_DIALOG, true)
                    customConfig?.let {
                        putExtra(ChatViewActivity.CUSTOM_CONFIG, it)
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
}
