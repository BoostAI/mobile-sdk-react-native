package com.boostai.reactnative.converters

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import no.boostai.sdk.ChatBackend.Objects.Response.APIMessage
import no.boostai.sdk.ChatBackend.Objects.Response.ConversationResult
import no.boostai.sdk.ChatBackend.Objects.Response.ConversationState
import no.boostai.sdk.ChatBackend.Objects.Response.Element
import no.boostai.sdk.ChatBackend.Objects.Response.Link
import no.boostai.sdk.ChatBackend.Objects.Response.Payload
import no.boostai.sdk.ChatBackend.Objects.Response.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Converts Kotlin SDK message objects to React Native WritableMap
 */
object MessageConverter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    /**
     * Convert APIMessage to WritableMap
     */
    fun toWritableMap(apiMessage: APIMessage?): WritableMap {
        val map = Arguments.createMap()

        if (apiMessage == null) {
            return map
        }

        apiMessage.conversation?.let {
            map.putMap("conversation", conversationResultToMap(it))
        }

        apiMessage.response?.let {
            map.putMap("response", responseToMap(it))
        }

        apiMessage.responses?.let {
            map.putArray("responses", responsesToArray(it))
        }

        apiMessage.smartReplies?.let { smartReply ->
            val smartRepliesMap = Arguments.createMap()

            // Add va array
            val vaArray = Arguments.createArray()
            smartReply.va.forEach { va ->
                val vaMap = Arguments.createMap()
                vaMap.putInt("score", va.score)
                vaMap.putString("subTitle", va.subTitle)

                // Add messages array
                val messagesArray = Arguments.createArray()
                va.messages.forEach { message ->
                    messagesArray.pushString(message)
                }
                vaMap.putArray("messages", messagesArray)

                // Add links array
                val linksArray = Arguments.createArray()
                va.links.forEach { link ->
                    linksArray.pushMap(linkToMap(link))
                }
                vaMap.putArray("links", linksArray)

                vaArray.pushMap(vaMap)
            }
            smartRepliesMap.putArray("va", vaArray)

            // Add important words
            val importantWordsMap = Arguments.createMap()
            val originalArray = Arguments.createArray()
            smartReply.importantWords.original.forEach { originalArray.pushString(it) }
            importantWordsMap.putArray("original", originalArray)

            val processedArray = Arguments.createArray()
            smartReply.importantWords.processed.forEach { processedArray.pushString(it) }
            importantWordsMap.putArray("processed", processedArray)

            smartRepliesMap.putMap("importantWords", importantWordsMap)

            map.putMap("smartReplies", smartRepliesMap)
        }

        apiMessage.postedId?.let {
            map.putInt("postedId", it)
        }

        apiMessage.download?.let {
            map.putString("download", it)
        }

        return map
    }

    /**
     * Convert list of APIMessage to WritableArray
     */
    fun toWritableArray(messages: ArrayList<APIMessage>): WritableArray {
        val array = Arguments.createArray()
        messages.forEach { message ->
            array.pushMap(toWritableMap(message))
        }
        return array
    }

    private fun conversationResultToMap(conversation: ConversationResult): WritableMap {
        val map = Arguments.createMap()

        conversation.id?.let { map.putString("id", it) }
        conversation.reference?.let { map.putString("reference", it) }
        map.putMap("state", conversationStateToMap(conversation.state))

        return map
    }

    private fun conversationStateToMap(state: ConversationState): WritableMap {
        val map = Arguments.createMap()

        // Convert enum to lowercase with underscores
        map.putString("chatStatus", state.chatStatus.name.lowercase())

        state.isBlocked?.let { map.putBoolean("isBlocked", it) }
        state.authenticatedUserId?.let { map.putString("authenticatedUserId", it) }
        state.unauthConversationId?.let { map.putString("unauthConversationId", it) }
        state.privacyPolicyUrl?.let { map.putString("privacyPolicyUrl", it) }
        state.allowDeleteConversation?.let { map.putBoolean("allowDeleteConversation", it) }
        state.allowHumanChatFileUpload?.let { map.putBoolean("allowHumanChatFileUpload", it) }
        state.poll?.let { map.putBoolean("poll", it) }
        state.humanIsTyping?.let { map.putBoolean("humanIsTyping", it) }
        state.maxInputChars?.let { map.putInt("maxInputChars", it) }
        state.skill?.let { map.putString("skill", it) }

        state.awaitingFiles?.let { awaitingFiles ->
            val filesMap = Arguments.createMap()
            awaitingFiles.acceptedTypes?.let { types ->
                val typesArray = Arguments.createArray()
                types.forEach { typesArray.pushString(it) }
                filesMap.putArray("acceptedTypes", typesArray)
            }
            awaitingFiles.maxNumberOfFiles?.let { filesMap.putInt("maxNumberOfFiles", it) }
            map.putMap("awaitingFiles", filesMap)
        }

        return map
    }

    private fun responseToMap(response: Response): WritableMap {
        val map = Arguments.createMap()

        response.id?.let { map.putString("id", it) }
        map.putString("source", response.source.name.lowercase())
        map.putString("language", response.language)
        map.putArray("elements", elementsToArray(response.elements))

        response.avatarUrl?.let { map.putString("avatarUrl", it) }
        response.dateCreated?.let {
            map.putString("dateCreated", dateFormat.format(it))
        }
        response.feedback?.let { map.putString("feedback", it) }
        response.sourceUrl?.let { map.putString("sourceUrl", it) }
        response.linkText?.let { map.putString("linkText", it) }
        response.error?.let { map.putString("error", it) }
        response.vanId?.let { map.putInt("vanId", it) }

        return map
    }

    private fun responsesToArray(responses: ArrayList<Response>): WritableArray {
        val array = Arguments.createArray()
        responses.forEach { response ->
            array.pushMap(responseToMap(response))
        }
        return array
    }

    private fun elementsToArray(elements: List<Element>): WritableArray {
        val array = Arguments.createArray()
        elements.forEach { element ->
            array.pushMap(elementToMap(element))
        }
        return array
    }

    private fun elementToMap(element: Element): WritableMap {
        val map = Arguments.createMap()

        map.putString("type", element.type.name.lowercase())
        map.putMap("payload", payloadToMap(element.payload))

        return map
    }

    private fun payloadToMap(payload: Payload): WritableMap {
        val map = Arguments.createMap()

        payload.html?.let { map.putString("html", it) }
        payload.text?.let { map.putString("text", it) }
        payload.url?.let { map.putString("url", it) }
        payload.source?.let { map.putString("source", it) }
        payload.fullScreen?.let { map.putBoolean("fullScreen", it) }
        payload.json?.let { map.putMap("json", jsonElementToMap(it)) }
        payload.links?.let { map.putArray("links", linksToArray(it)) }

        return map
    }

    private fun linksToArray(links: ArrayList<Link>): WritableArray {
        val array = Arguments.createArray()
        links.forEach { link ->
            array.pushMap(linkToMap(link))
        }
        return array
    }

    private fun linkToMap(link: Link): WritableMap {
        val map = Arguments.createMap()

        map.putString("id", link.id)
        map.putString("text", link.text)
        map.putString("type", link.type.name.lowercase())

        link.function?.let { map.putString("function", it.name.lowercase()) }
        link.question?.let { map.putString("question", it) }
        link.url?.let { map.putString("url", it) }
        link.isAttachment?.let { map.putBoolean("isAttachment", it) }
        link.vanBaseUrl?.let { map.putString("vanBaseUrl", it) }
        link.vanName?.let { map.putString("vanName", it) }
        link.vanOrganization?.let { map.putString("vanOrganization", it) }

        return map
    }

    /**
     * Convert JsonElement to WritableMap/WritableArray
     */
    private fun jsonElementToMap(json: JsonElement): WritableMap {
        return when (json) {
            is JsonObject -> {
                val map = Arguments.createMap()
                json.forEach { (key, value) ->
                    putJsonElement(map, key, value)
                }
                map
            }
            else -> {
                // Wrap non-objects in a value field
                val map = Arguments.createMap()
                putJsonElement(map, "value", json)
                map
            }
        }
    }

    private fun jsonElementToArray(json: JsonElement): WritableArray {
        return when (json) {
            is JsonArray -> {
                val array = Arguments.createArray()
                json.forEach { element ->
                    pushJsonElement(array, element)
                }
                array
            }
            else -> {
                // Single element array
                val array = Arguments.createArray()
                pushJsonElement(array, json)
                array
            }
        }
    }

    private fun putJsonElement(map: WritableMap, key: String, element: JsonElement) {
        when (element) {
            is JsonNull -> map.putNull(key)
            is JsonPrimitive -> {
                when {
                    element.isString -> map.putString(key, element.content)
                    element.content == "true" || element.content == "false" ->
                        map.putBoolean(key, element.content.toBoolean())
                    else -> {
                        // Try as number
                        try {
                            if (element.content.contains(".")) {
                                map.putDouble(key, element.content.toDouble())
                            } else {
                                map.putInt(key, element.content.toInt())
                            }
                        } catch (e: NumberFormatException) {
                            map.putString(key, element.content)
                        }
                    }
                }
            }
            is JsonArray -> map.putArray(key, jsonElementToArray(element))
            is JsonObject -> map.putMap(key, jsonElementToMap(element))
        }
    }

    private fun pushJsonElement(array: WritableArray, element: JsonElement) {
        when (element) {
            is JsonNull -> array.pushNull()
            is JsonPrimitive -> {
                when {
                    element.isString -> array.pushString(element.content)
                    element.content == "true" || element.content == "false" ->
                        array.pushBoolean(element.content.toBoolean())
                    else -> {
                        // Try as number
                        try {
                            if (element.content.contains(".")) {
                                array.pushDouble(element.content.toDouble())
                            } else {
                                array.pushInt(element.content.toInt())
                            }
                        } catch (e: NumberFormatException) {
                            array.pushString(element.content)
                        }
                    }
                }
            }
            is JsonArray -> array.pushArray(jsonElementToArray(element))
            is JsonObject -> array.pushMap(jsonElementToMap(element))
        }
    }
}
