package com.boostai.reactnative.converters

import android.content.res.Resources
import androidx.annotation.FontRes
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import no.boostai.sdk.ChatBackend.Objects.*

/**
 * Converts configuration between React Native ReadableMap and Kotlin SDK Config objects
 */
object ConfigConverter {
    // Font registry for mapping font names to resource IDs
    private val fontRegistry = mutableMapOf<String, Int>()

    /**
     * Register a font resource ID with a name for JavaScript access
     */
    fun registerFont(name: String, @FontRes resourceId: Int) {
        fontRegistry[name] = resourceId
    }

    /**
     * Get font resource ID by name
     */
    @FontRes
    fun getFontResource(name: String?): Int? {
        if (name.isNullOrBlank()) return null
        return fontRegistry[name]
    }

    /**
     * Convert ReadableMap to ChatConfig
     */
    fun toKotlinConfig(map: ReadableMap?): ChatConfig? {
        if (map == null) return null

        return ChatConfig(
            messages = map.getMapOrNull("messages")?.let { messagesMap ->
                val messagesMapResult = mutableMapOf<String, Messages>()
                messagesMap.toHashMap().forEach { (lang, value) ->
                    if (value is Map<*, *>) {
                        @Suppress("UNCHECKED_CAST")
                        val langMap = Arguments.makeNativeMap(value as Map<String, Any?>)
                        messagesMapResult[lang] = toMessages(langMap)
                    }
                }
                messagesMapResult
            },
            chatPanel = map.getMapOrNull("chatPanel")?.let { toChatPanel(it) }
        )
    }

    /**
     * Convert ChatConfig to WritableMap for JavaScript
     */
    fun toWritableMap(config: ChatConfig?): WritableMap {
        val map = Arguments.createMap()
        if (config == null) return map

        config.messages?.let { messages ->
            val messagesMap = Arguments.createMap()
            messages.forEach { (lang, msgs) ->
                messagesMap.putMap(lang, messagesToMap(msgs))
            }
            map.putMap("messages", messagesMap)
        }

        config.chatPanel?.let {
            map.putMap("chatPanel", chatPanelToMap(it))
        }

        return map
    }

    private fun toMessages(map: ReadableMap): Messages {
        return Messages(
            back = map.getStringOrNull("back") ?: "Back",
            closeWindow = map.getStringOrNull("closeWindow") ?: "Close",
            composeCharactersUsed = map.getStringOrNull("composeCharactersUsed") ?: "{0} out of {1} characters used",
            composePlaceholder = map.getStringOrNull("composePlaceholder") ?: "Type in here",
            deleteConversation = map.getStringOrNull("deleteConversation") ?: "Delete conversation",
            downloadConversation = map.getStringOrNull("downloadConversation") ?: "Download conversation",
            feedbackPlaceholder = map.getStringOrNull("feedbackPlaceholder") ?: "Write in your feedback here",
            feedbackPrompt = map.getStringOrNull("feedbackPrompt") ?: "Do you want to give me feedback?",
            feedbackThumbsDown = map.getStringOrNull("feedbackThumbsDown") ?: "Not satisfied with conversation",
            feedbackThumbsUp = map.getStringOrNull("feedbackThumbsUp") ?: "Satisfied with conversation",
            filterSelect = map.getStringOrNull("filterSelect") ?: "Select user group",
            headerText = map.getStringOrNull("headerText") ?: "Conversational AI",
            loggedIn = map.getStringOrNull("loggedIn") ?: "Secure chat",
            messageThumbsDown = map.getStringOrNull("messageThumbsDown") ?: "Not satisfied with answer",
            messageThumbsUp = map.getStringOrNull("messageThumbsUp") ?: "Satisfied with answer",
            minimizeWindow = map.getStringOrNull("minimizeWindow") ?: "Minimize window",
            openMenu = map.getStringOrNull("openMenu") ?: "Open menu",
            opensInNewTab = map.getStringOrNull("opensInNewTab") ?: "Opens in new tab",
            privacyPolicy = map.getStringOrNull("privacyPolicy") ?: "Privacy policy",
            submitFeedback = map.getStringOrNull("submitFeedback") ?: "Send",
            submitMessage = map.getStringOrNull("submitMessage") ?: "Send",
            textTooLong = map.getStringOrNull("textTooLong") ?: "The message cannot be longer than {0} characters",
            uploadFile = map.getStringOrNull("uploadFile") ?: "Upload image",
            uploadFileError = map.getStringOrNull("uploadFileError") ?: "Upload failed",
            uploadFileProgress = map.getStringOrNull("uploadFileProgress") ?: "Uploading ...",
            uploadFileSuccess = map.getStringOrNull("uploadFileSuccess") ?: "Upload successful",
            chatServiceUnavailable = map.getStringOrNull("chatServiceUnavailable") ?: "Chat service is currently unavailable.\nPlease try again later.",
            retry = map.getStringOrNull("retry") ?: "Retry"
        )
    }

    private fun messagesToMap(messages: Messages): WritableMap {
        val map = Arguments.createMap()
        messages.back?.let { map.putString("back", it) }
        messages.closeWindow?.let { map.putString("closeWindow", it) }
        messages.composeCharactersUsed?.let { map.putString("composeCharactersUsed", it) }
        messages.composePlaceholder?.let { map.putString("composePlaceholder", it) }
        messages.deleteConversation?.let { map.putString("deleteConversation", it) }
        messages.downloadConversation?.let { map.putString("downloadConversation", it) }
        messages.feedbackPlaceholder?.let { map.putString("feedbackPlaceholder", it) }
        messages.feedbackPrompt?.let { map.putString("feedbackPrompt", it) }
        messages.feedbackThumbsDown?.let { map.putString("feedbackThumbsDown", it) }
        messages.feedbackThumbsUp?.let { map.putString("feedbackThumbsUp", it) }
        messages.filterSelect?.let { map.putString("filterSelect", it) }
        messages.headerText?.let { map.putString("headerText", it) }
        messages.loggedIn?.let { map.putString("loggedIn", it) }
        messages.messageThumbsDown?.let { map.putString("messageThumbsDown", it) }
        messages.messageThumbsUp?.let { map.putString("messageThumbsUp", it) }
        messages.minimizeWindow?.let { map.putString("minimizeWindow", it) }
        messages.openMenu?.let { map.putString("openMenu", it) }
        messages.opensInNewTab?.let { map.putString("opensInNewTab", it) }
        messages.privacyPolicy?.let { map.putString("privacyPolicy", it) }
        messages.submitFeedback?.let { map.putString("submitFeedback", it) }
        messages.submitMessage?.let { map.putString("submitMessage", it) }
        messages.textTooLong?.let { map.putString("textTooLong", it) }
        messages.uploadFile?.let { map.putString("uploadFile", it) }
        messages.uploadFileError?.let { map.putString("uploadFileError", it) }
        messages.uploadFileProgress?.let { map.putString("uploadFileProgress", it) }
        messages.uploadFileSuccess?.let { map.putString("uploadFileSuccess", it) }
        messages.chatServiceUnavailable?.let { map.putString("chatServiceUnavailable", it) }
        messages.retry?.let { map.putString("retry", it) }
        return map
    }

    private fun toChatPanel(map: ReadableMap): ChatPanel {
        return ChatPanel(
            header = map.getMapOrNull("header")?.let { toHeader(it) },
            styling = map.getMapOrNull("styling")?.let { toStyling(it) },
            settings = map.getMapOrNull("settings")?.let { toSettings(it) }
        )
    }

    private fun chatPanelToMap(chatPanel: ChatPanel): WritableMap {
        val map = Arguments.createMap()
        chatPanel.header?.let { map.putMap("header", headerToMap(it)) }
        chatPanel.styling?.let { map.putMap("styling", stylingToMap(it)) }
        chatPanel.settings?.let { map.putMap("settings", settingsToMap(it)) }
        return map
    }

    private fun toHeader(map: ReadableMap): Header {
        return Header(
            filters = map.getMapOrNull("filters")?.let { toFilters(it) },
            title = map.getStringOrNull("title"),
            hideMinimizeButton = map.getBooleanOrNull("hideMinimizeButton"),
            hideMenuButton = map.getBooleanOrNull("hideMenuButton")
        )
    }

    private fun headerToMap(header: Header): WritableMap {
        val map = Arguments.createMap()
        header.filters?.let { map.putMap("filters", filtersToMap(it)) }
        header.title?.let { map.putString("title", it) }
        header.hideMinimizeButton?.let { map.putBoolean("hideMinimizeButton", it) }
        header.hideMenuButton?.let { map.putBoolean("hideMenuButton", it) }
        return map
    }

    private fun toFilters(map: ReadableMap): Filters {
        return Filters(
            filterValues = map.getArrayOrNull("filterValues")?.toArrayList()?.mapNotNull { it as? String },
            options = map.getArrayOrNull("options")?.toArrayList()?.mapNotNull { filterMap ->
                (filterMap as? Map<*, *>)?.let { toFilter(Arguments.makeNativeMap(it as Map<String, Any?>)) }
            }
        )
    }

    private fun filtersToMap(filters: Filters): WritableMap {
        val map = Arguments.createMap()
        filters.filterValues?.let { values ->
            val array = Arguments.createArray()
            values.forEach { array.pushString(it) }
            map.putArray("filterValues", array)
        }
        filters.options?.let { options ->
            val array = Arguments.createArray()
            options.forEach { filter ->
                val filterMap = Arguments.createMap()
                filterMap.putInt("id", filter.id)
                filterMap.putString("title", filter.title)
                val valuesArray = Arguments.createArray()
                filter.values.forEach { valuesArray.pushString(it) }
                filterMap.putArray("values", valuesArray)
                array.pushMap(filterMap)
            }
            map.putArray("options", array)
        }
        return map
    }

    private fun toFilter(map: ReadableMap): Filter {
        return Filter(
            id = map.getInt("id"),
            title = map.getString("title") ?: "",
            values = map.getArrayOrNull("values")?.toArrayList()?.mapNotNull { it as? String } ?: emptyList()
        )
    }

    private fun toStyling(map: ReadableMap): Styling {
        return Styling(
            pace = map.getStringOrNull("pace")?.let { parseConversationPace(it) },
            avatarShape = map.getStringOrNull("avatarShape")?.let { parseAvatarShape(it) },
            hideAvatar = map.getBooleanOrNull("hideAvatar"),
            primaryColor = ColorConverter.hexToColorInt(map.getStringOrNull("primaryColor")),
            contrastColor = ColorConverter.hexToColorInt(map.getStringOrNull("contrastColor")),
            panelBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("panelBackgroundColor")),
            secureChatBannerBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("secureChatBannerBackgroundColor")),
            secureChatBannerTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("secureChatBannerTextColor")),
            chatBubbles = map.getMapOrNull("chatBubbles")?.let { toChatBubbles(it) },
            buttons = map.getMapOrNull("buttons")?.let { toButtons(it) },
            composer = map.getMapOrNull("composer")?.let { toComposer(it) },
            messageFeedback = map.getMapOrNull("messageFeedback")?.let { toMessageFeedback(it) },
            fonts = map.getMapOrNull("fonts")?.let { toFonts(it) }
        )
    }

    private fun stylingToMap(styling: Styling): WritableMap {
        val map = Arguments.createMap()
        styling.pace?.let { map.putString("pace", it.name.lowercase()) }
        styling.avatarShape?.let { map.putString("avatarShape", it.name.lowercase()) }
        styling.hideAvatar?.let { map.putBoolean("hideAvatar", it) }
        styling.primaryColor?.let { map.putString("primaryColor", ColorConverter.colorIntToHex(it)) }
        styling.contrastColor?.let { map.putString("contrastColor", ColorConverter.colorIntToHex(it)) }
        styling.panelBackgroundColor?.let { map.putString("panelBackgroundColor", ColorConverter.colorIntToHex(it)) }
        styling.secureChatBannerBackgroundColor?.let {
            map.putString("secureChatBannerBackgroundColor", ColorConverter.colorIntToHex(it))
        }
        styling.secureChatBannerTextColor?.let {
            map.putString("secureChatBannerTextColor", ColorConverter.colorIntToHex(it))
        }
        styling.chatBubbles?.let { map.putMap("chatBubbles", chatBubblesToMap(it)) }
        styling.buttons?.let { map.putMap("buttons", buttonsToMap(it)) }
        styling.composer?.let { map.putMap("composer", composerToMap(it)) }
        styling.messageFeedback?.let { map.putMap("messageFeedback", messageFeedbackToMap(it)) }
        styling.fonts?.let { map.putMap("fonts", fontsToMap(it)) }
        return map
    }

    private fun toChatBubbles(map: ReadableMap): ChatBubbles {
        return ChatBubbles(
            userBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("userBackgroundColor")),
            userTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("userTextColor")),
            vaBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("vaBackgroundColor")),
            vaTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("vaTextColor")),
            typingDotColor = ColorConverter.hexToColorInt(map.getStringOrNull("typingDotColor")),
            typingBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("typingBackgroundColor"))
        )
    }

    private fun chatBubblesToMap(chatBubbles: ChatBubbles): WritableMap {
        val map = Arguments.createMap()
        chatBubbles.userBackgroundColor?.let { map.putString("userBackgroundColor", ColorConverter.colorIntToHex(it)) }
        chatBubbles.userTextColor?.let { map.putString("userTextColor", ColorConverter.colorIntToHex(it)) }
        chatBubbles.vaBackgroundColor?.let { map.putString("vaBackgroundColor", ColorConverter.colorIntToHex(it)) }
        chatBubbles.vaTextColor?.let { map.putString("vaTextColor", ColorConverter.colorIntToHex(it)) }
        chatBubbles.typingDotColor?.let { map.putString("typingDotColor", ColorConverter.colorIntToHex(it)) }
        chatBubbles.typingBackgroundColor?.let {
            map.putString("typingBackgroundColor", ColorConverter.colorIntToHex(it))
        }
        return map
    }

    private fun toButtons(map: ReadableMap): Buttons {
        return Buttons(
            backgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("backgroundColor")),
            focusBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("focusBackgroundColor")),
            focusTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("focusTextColor")),
            textColor = ColorConverter.hexToColorInt(map.getStringOrNull("textColor")),
            multiline = map.getBooleanOrNull("multiline"),
            variant = map.getStringOrNull("variant")?.let { parseButtonType(it) }
        )
    }

    private fun buttonsToMap(buttons: Buttons): WritableMap {
        val map = Arguments.createMap()
        buttons.backgroundColor?.let { map.putString("backgroundColor", ColorConverter.colorIntToHex(it)) }
        buttons.focusBackgroundColor?.let {
            map.putString("focusBackgroundColor", ColorConverter.colorIntToHex(it))
        }
        buttons.focusTextColor?.let { map.putString("focusTextColor", ColorConverter.colorIntToHex(it)) }
        buttons.textColor?.let { map.putString("textColor", ColorConverter.colorIntToHex(it)) }
        buttons.multiline?.let { map.putBoolean("multiline", it) }
        buttons.variant?.let { map.putString("variant", it.name.lowercase()) }
        return map
    }

    private fun toComposer(map: ReadableMap): Composer {
        return Composer(
            hide = map.getBooleanOrNull("hide"),
            composeLengthColor = ColorConverter.hexToColorInt(map.getStringOrNull("composeLengthColor")),
            fileUploadButtonColor = ColorConverter.hexToColorInt(map.getStringOrNull("fileUploadButtonColor")),
            frameBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("frameBackgroundColor")),
            sendButtonColor = ColorConverter.hexToColorInt(map.getStringOrNull("sendButtonColor")),
            sendButtonDisabledColor = ColorConverter.hexToColorInt(map.getStringOrNull("sendButtonDisabledColor")),
            textareaBackgroundColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaBackgroundColor")),
            textareaBorderColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaBorderColor")),
            textareaFocusBorderColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaFocusBorderColor")),
            textareaFocusOutlineColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaFocusOutlineColor")),
            textareaTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaTextColor")),
            textareaPlaceholderTextColor = ColorConverter.hexToColorInt(map.getStringOrNull("textareaPlaceholderTextColor")),
            topBorderColor = ColorConverter.hexToColorInt(map.getStringOrNull("topBorderColor")),
            topBorderFocusColor = ColorConverter.hexToColorInt(map.getStringOrNull("topBorderFocusColor"))
        )
    }

    private fun composerToMap(composer: Composer): WritableMap {
        val map = Arguments.createMap()
        composer.hide?.let { map.putBoolean("hide", it) }
        composer.composeLengthColor?.let { map.putString("composeLengthColor", ColorConverter.colorIntToHex(it)) }
        composer.fileUploadButtonColor?.let {
            map.putString("fileUploadButtonColor", ColorConverter.colorIntToHex(it))
        }
        composer.frameBackgroundColor?.let {
            map.putString("frameBackgroundColor", ColorConverter.colorIntToHex(it))
        }
        composer.sendButtonColor?.let { map.putString("sendButtonColor", ColorConverter.colorIntToHex(it)) }
        composer.sendButtonDisabledColor?.let {
            map.putString("sendButtonDisabledColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaBackgroundColor?.let {
            map.putString("textareaBackgroundColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaBorderColor?.let {
            map.putString("textareaBorderColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaFocusBorderColor?.let {
            map.putString("textareaFocusBorderColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaFocusOutlineColor?.let {
            map.putString("textareaFocusOutlineColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaTextColor?.let {
            map.putString("textareaTextColor", ColorConverter.colorIntToHex(it))
        }
        composer.textareaPlaceholderTextColor?.let {
            map.putString("textareaPlaceholderTextColor", ColorConverter.colorIntToHex(it))
        }
        composer.topBorderColor?.let { map.putString("topBorderColor", ColorConverter.colorIntToHex(it)) }
        composer.topBorderFocusColor?.let {
            map.putString("topBorderFocusColor", ColorConverter.colorIntToHex(it))
        }
        return map
    }

    private fun toMessageFeedback(map: ReadableMap): MessageFeedback {
        return MessageFeedback(
            hide = map.getBooleanOrNull("hide"),
            outlineColor = ColorConverter.hexToColorInt(map.getStringOrNull("outlineColor")),
            selectedColor = ColorConverter.hexToColorInt(map.getStringOrNull("selectedColor"))
        )
    }

    private fun messageFeedbackToMap(messageFeedback: MessageFeedback): WritableMap {
        val map = Arguments.createMap()
        messageFeedback.hide?.let { map.putBoolean("hide", it) }
        messageFeedback.outlineColor?.let {
            map.putString("outlineColor", ColorConverter.colorIntToHex(it))
        }
        messageFeedback.selectedColor?.let {
            map.putString("selectedColor", ColorConverter.colorIntToHex(it))
        }
        return map
    }

    private fun toFonts(map: ReadableMap): Fonts {
        return Fonts(
            bodyFont = getFontResource(map.getStringOrNull("bodyFont")),
            headlineFont = getFontResource(map.getStringOrNull("headlineFont")),
            footnoteFont = getFontResource(map.getStringOrNull("footnoteFont")),
            menuItemFont = getFontResource(map.getStringOrNull("menuItemFont"))
        )
    }

    private fun fontsToMap(fonts: Fonts): WritableMap {
        val map = Arguments.createMap()
        // Note: We can't reliably convert resource IDs back to names
        // This would require reverse lookup in fontRegistry
        return map
    }

    private fun toSettings(map: ReadableMap): Settings {
        return Settings(
            authStartTriggerActionId = map.getIntOrNull("authStartTriggerActionId"),
            contextTopicIntentId = map.getIntOrNull("contextTopicIntentId"),
            conversationId = map.getStringOrNull("conversationId"),
            customPayload = null, // Cannot be parceled, handled separately if needed
            fileUploadServiceEndpointUrl = map.getStringOrNull("fileUploadServiceEndpointUrl"),
            fileExpirationSeconds = map.getIntOrNull("fileExpirationSeconds"),
            messageFeedbackOnFirstAction = map.getBooleanOrNull("messageFeedbackOnFirstAction"),
            rememberConversation = map.getBooleanOrNull("rememberConversation"),
            rememberConversationExpirationDuration = map.getStringOrNull("rememberConversationExpirationDuration"),
            removeRememberedConversationOnChatPanelClose = map.getBooleanOrNull("removeRememberedConversationOnChatPanelClose"),
            requestFeedback = map.getBooleanOrNull("requestFeedback"),
            showLinkClickAsChatBubble = map.getBooleanOrNull("showLinkClickAsChatBubble"),
            skill = map.getStringOrNull("skill"),
            startLanguage = map.getStringOrNull("startLanguage"),
            startNewConversationOnResumeFailure = map.getBooleanOrNull("startNewConversationOnResumeFailure"),
            startTriggerActionId = map.getIntOrNull("startTriggerActionId"),
            triggerActionOnResume = map.getBooleanOrNull("triggerActionOnResume"),
            userToken = map.getStringOrNull("userToken"),
            skipWelcomeMessage = map.getBooleanOrNull("skipWelcomeMessage")
        )
    }

    private fun settingsToMap(settings: Settings): WritableMap {
        val map = Arguments.createMap()
        settings.authStartTriggerActionId?.let { map.putInt("authStartTriggerActionId", it) }
        settings.contextTopicIntentId?.let { map.putInt("contextTopicIntentId", it) }
        settings.conversationId?.let { map.putString("conversationId", it) }
        settings.fileUploadServiceEndpointUrl?.let { map.putString("fileUploadServiceEndpointUrl", it) }
        settings.fileExpirationSeconds?.let { map.putInt("fileExpirationSeconds", it) }
        settings.messageFeedbackOnFirstAction?.let { map.putBoolean("messageFeedbackOnFirstAction", it) }
        settings.rememberConversation?.let { map.putBoolean("rememberConversation", it) }
        settings.rememberConversationExpirationDuration?.let {
            map.putString("rememberConversationExpirationDuration", it)
        }
        settings.removeRememberedConversationOnChatPanelClose?.let {
            map.putBoolean("removeRememberedConversationOnChatPanelClose", it)
        }
        settings.requestFeedback?.let { map.putBoolean("requestFeedback", it) }
        settings.showLinkClickAsChatBubble?.let { map.putBoolean("showLinkClickAsChatBubble", it) }
        settings.skill?.let { map.putString("skill", it) }
        settings.startLanguage?.let { map.putString("startLanguage", it) }
        settings.startNewConversationOnResumeFailure?.let {
            map.putBoolean("startNewConversationOnResumeFailure", it)
        }
        settings.startTriggerActionId?.let { map.putInt("startTriggerActionId", it) }
        settings.triggerActionOnResume?.let { map.putBoolean("triggerActionOnResume", it) }
        settings.userToken?.let { map.putString("userToken", it) }
        settings.skipWelcomeMessage?.let { map.putBoolean("skipWelcomeMessage", it) }
        return map
    }

    // Enum parsers
    private fun parseConversationPace(value: String): ConversationPace? {
        return try {
            ConversationPace.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun parseAvatarShape(value: String): AvatarShape? {
        return try {
            AvatarShape.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    private fun parseButtonType(value: String): ButtonType? {
        return try {
            ButtonType.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    // Helper extensions for ReadableMap
    private fun ReadableMap.getStringOrNull(key: String): String? =
        if (hasKey(key)) getString(key) else null

    private fun ReadableMap.getIntOrNull(key: String): Int? =
        if (hasKey(key)) getInt(key) else null

    private fun ReadableMap.getBooleanOrNull(key: String): Boolean? =
        if (hasKey(key)) getBoolean(key) else null

    private fun ReadableMap.getMapOrNull(key: String): ReadableMap? =
        if (hasKey(key)) getMap(key) else null

    private fun ReadableMap.getArrayOrNull(key: String): com.facebook.react.bridge.ReadableArray? =
        if (hasKey(key)) getArray(key) else null
}
