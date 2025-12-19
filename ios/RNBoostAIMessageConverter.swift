//
//  RNBoostAIMessageConverter.swift
//  BoostAI React Native
//
//  Converts APIMessage to Dictionary for JavaScript
//

import Foundation
import BoostAI

class RNBoostAIMessageConverter {

    static func messageToDictionary(_ message: APIMessage?) -> [String: Any] {
        guard let message = message else { return [:] }

        var dict: [String: Any] = [:]

        if let conversation = message.conversation {
            dict["conversation"] = conversationToDictionary(conversation)
        }

        if let response = message.response {
            dict["response"] = responseToDictionary(response)
        }

        if let responses = message.responses {
            dict["responses"] = responses.map { responseToDictionary($0) }
        }

        if let smartReplies = message.smartReplies {
            dict["smartreplies"] = smartRepliesToDictionary(smartReplies)
        }

        if let postedId = message.postedId {
            dict["posted_id"] = postedId
        }

        if let download = message.download {
            dict["download"] = download
        }

        return dict
    }

    private static func responseToDictionary(_ response: Response) -> [String: Any] {
        var dict: [String: Any] = [
            "id": response.id,
            "source": response.source.rawValue,
            "language": response.language,
            "elements": response.elements.map { elementToDictionary($0) },
            "is_temp_id": response.isTempId
        ]

        if let avatarUrl = response.avatarUrl {
            dict["avatar_url"] = avatarUrl
        }
        if let dateCreated = response.dateCreated {
            let formatter = ISO8601DateFormatter()
            formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
            dict["date_created"] = formatter.string(from: dateCreated)
        }
        if let feedback = response.feedback {
            dict["feedback"] = feedback
        }
        if let sourceUrl = response.sourceUrl {
            dict["source_url"] = sourceUrl
        }
        if let linkText = response.linkText {
            dict["link_text"] = linkText
        }
        if let error = response.error {
            dict["error"] = error
        }
        if let vanId = response.vanId {
            dict["van_id"] = vanId
        }

        return dict
    }

    private static func elementToDictionary(_ element: Element) -> [String: Any] {
        return [
            "type": element.type.rawValue,
            "payload": payloadToDictionary(element.payload)
        ]
    }

    private static func payloadToDictionary(_ payload: Payload) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let html = payload.html {
            dict["html"] = html
        }
        if let text = payload.text {
            dict["text"] = text
        }
        if let url = payload.url {
            dict["url"] = url
        }
        if let source = payload.source {
            dict["source"] = source
        }
        if let fullScreen = payload.fullScreen {
            dict["fullscreen"] = fullScreen
        }
        if let json = payload.json,
           let jsonObject = try? JSONSerialization.jsonObject(with: json) {
            dict["json"] = jsonObject
        }
        if let links = payload.links {
            dict["links"] = links.map { linkToDictionary($0) }
        }

        return dict
    }

    private static func linkToDictionary(_ link: Link) -> [String: Any] {
        var dict: [String: Any] = [
            "id": link.id,
            "text": link.text,
            "type": link.type.rawValue
        ]

        if let function = link.function {
            dict["function"] = function.rawValue
        }
        if let question = link.question {
            dict["question"] = question
        }
        if let url = link.url {
            dict["url"] = url
        }
        if let isAttachment = link.isAttachment {
            dict["is_attachment"] = isAttachment
        }
        if let vanBaseUrl = link.vanBaseUrl {
            dict["van_base_url"] = vanBaseUrl
        }
        if let vanName = link.vanName {
            dict["van_name"] = vanName
        }
        if let vanOrganization = link.vanOrganization {
            dict["van_organization"] = vanOrganization
        }

        return dict
    }

    private static func conversationToDictionary(_ conversation: ConversationResult) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let id = conversation.id {
            dict["id"] = id
        }
        if let reference = conversation.reference {
            dict["reference"] = reference
        }
        dict["state"] = stateToDictionary(conversation.state)

        return dict
    }

    private static func stateToDictionary(_ state: ConversationState) -> [String: Any] {
        var dict: [String: Any] = [
            "chat_status": state.chatStatus.rawValue
        ]

        if let isBlocked = state.isBlocked {
            dict["is_blocked"] = isBlocked
        }
        if let authenticatedUserId = state.authenticatedUserId {
            dict["authenticated_user_id"] = authenticatedUserId
        }
        if let privacyPolicyUrl = state.privacyPolicyUrl {
            dict["privacy_policy_url"] = privacyPolicyUrl
        }
        if let allowDelete = state.allowDeleteConversation {
            dict["allow_delete_conversation"] = allowDelete
        }
        if let allowUpload = state.allowHumanChatFileUpload {
            dict["allow_human_chat_file_upload"] = allowUpload
        }
        if let poll = state.poll {
            dict["poll"] = poll
        }
        if let humanTyping = state.humanIsTyping {
            dict["human_is_typing"] = humanTyping
        }
        if let maxChars = state.maxInputChars {
            dict["max_input_chars"] = maxChars
        }
        if let skill = state.skill {
            dict["skill"] = skill
        }

        return dict
    }

    private static func smartRepliesToDictionary(_ smartReplies: SmartReply) -> [String: Any] {
        var dict: [String: Any] = [:]

        dict["important_words"] = [
            "original": smartReplies.importantWords.original,
            "processed": smartReplies.importantWords.processed
        ]

        dict["va"] = smartReplies.va.map { va in
            return [
                "links": va.links.map { linkToDictionary($0) },
                "messages": va.messages,
                "score": va.score,
                "sub_title": va.subTitle
            ]
        }

        return dict
    }
}
