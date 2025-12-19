//
//  RNBoostAIModule.swift
//  BoostAI React Native
//
//  Main TurboModule implementation in Swift
//

import Foundation
import React
import BoostAI

@objc(RNBoostAIModule)
class RNBoostAIModule: NSObject {

    @objc
    static func moduleName() -> String {
        return "BoostAI"
    }

    @objc
    static func requiresMainQueueSetup() -> Bool {
        return true
    }

    private var bridge: RCTBridge?
    private var observationTokens: [ObservationToken] = []

    @objc
    func setBridge(_ bridge: RCTBridge) {
        self.bridge = bridge
        registerObservers()
    }

    override init() {
        super.init()
    }

    deinit {
        // Clean up observers
        for token in observationTokens {
            token.cancel()
        }
    }

    // MARK: - Observer Registration

    private func registerObservers() {
        // Message Observer
        let messageToken = ChatBackend.shared.addMessageObserver(self) { [weak self] message, error in
            self?.handleMessageReceived(message: message, error: error)
        }
        observationTokens.append(messageToken)

        // Config Observer
        let configToken = ChatBackend.shared.addConfigObserver(self) { [weak self] config, error in
            self?.handleConfigReceived(config: config, error: error)
        }
        observationTokens.append(configToken)

        // Event Observer
        let eventToken = ChatBackend.shared.addEventObserver(self) { [weak self] type, detail in
            self?.handleBackendEvent(type: type, detail: detail)
        }
        observationTokens.append(eventToken)
    }

    private func handleMessageReceived(message: APIMessage?, error: Error?) {
        guard let bridge = bridge else { return }

        if let error = error {
            emitError(error: error)
        } else if let message = message {
            let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
            bridge.eventDispatcher().sendDeviceEvent(withName: "BoostAI:onMessageReceived",
                                                     body: ["message": messageDict])
        }
    }

    private func handleConfigReceived(config: ChatConfig?, error: Error?) {
        guard let bridge = bridge else { return }

        if let error = error {
            emitError(error: error)
        } else if let config = config {
            let configDict = RNBoostAIConfigConverter.configToDictionary(config)
            bridge.eventDispatcher().sendDeviceEvent(withName: "BoostAI:onConfigReceived",
                                                     body: ["config": configDict])
        }
    }

    private func handleBackendEvent(type: String, detail: Any?) {
        guard let bridge = bridge else { return }

        var eventDict: [String: Any] = ["type": type]
        if let detail = detail {
            eventDict["detail"] = detail
        }
        bridge.eventDispatcher().sendDeviceEvent(withName: "BoostAI:onBackendEvent", body: eventDict)
    }

    private func emitError(error: Error) {
        guard let bridge = bridge else { return }
        bridge.eventDispatcher().sendDeviceEvent(withName: "BoostAI:onError",
                                                 body: ["error": error.localizedDescription])
    }

    // MARK: - Initialization & Configuration

    @objc
    func initialize(_ domain: String,
                   resolve: @escaping RCTPromiseResolveBlock,
                   reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.domain = domain
            ChatBackend.shared.onReady { config, error in
                if let error = error {
                    reject("INIT_ERROR", error.localizedDescription, error)
                } else if let config = config {
                    let configDict = RNBoostAIConfigConverter.configToDictionary(config)
                    resolve(configDict)
                } else {
                    reject("INIT_ERROR", "No config received", nil)
                }
            }
        }
    }

    @objc
    func setCustomConfig(_ configDict: [String: Any],
                        resolve: @escaping RCTPromiseResolveBlock,
                        reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            // Set config directly on ChatBackend (simplified for now)
            // Full implementation would convert dictionary to ChatConfig
            resolve(NSNull())
        }
    }

    @objc
    func getConfig(_ resolve: @escaping RCTPromiseResolveBlock,
                   reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            if let config = ChatBackend.shared.config {
                let configDict = RNBoostAIConfigConverter.configToDictionary(config)
                resolve(configDict)
            } else {
                resolve([:])
            }
        }
    }

    @objc
    func setLanguageCode(_ languageCode: String) {
        DispatchQueue.main.async {
            ChatBackend.shared.languageCode = languageCode
        }
    }

    @objc
    func setUserToken(_ userToken: String?) {
        DispatchQueue.main.async {
            ChatBackend.shared.userToken = userToken
        }
    }

    @objc
    func setConversationId(_ conversationId: String?) {
        DispatchQueue.main.async {
            ChatBackend.shared.conversationId = conversationId
        }
    }

    @objc
    func getConversationId(_ resolve: @escaping RCTPromiseResolveBlock,
                          reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            resolve(ChatBackend.shared.conversationId)
        }
    }

    @objc
    func setCertificatePinningEnabled(_ enabled: Bool) {
        DispatchQueue.main.async {
            if #available(iOS 12, *) {
                ChatBackend.shared.isCertificatePinningEnabled = enabled
            }
        }
    }

    @objc
    func registerFont(_ name: String, resourceId: NSNumber) {
        // Not applicable on iOS
    }

    // MARK: - Conversation Control

    @objc
    func start(_ options: [String: Any]?,
              resolve: @escaping RCTPromiseResolveBlock,
              reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.start(message: nil) { message, error in
                if let error = error {
                    reject("START_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("START_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func stop(_ resolve: @escaping RCTPromiseResolveBlock,
             reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.stop(message: nil) { message, error in
                if let error = error {
                    reject("STOP_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("STOP_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func resume(_ options: [String: Any]?,
               resolve: @escaping RCTPromiseResolveBlock,
               reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.resume(message: nil) { message, error in
                if let error = error {
                    reject("RESUME_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("RESUME_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func delete(_ resolve: @escaping RCTPromiseResolveBlock,
               reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.delete(message: nil) { message, error in
                if let error = error {
                    reject("DELETE_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("DELETE_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func resetConversation() {
        DispatchQueue.main.async {
            ChatBackend.shared.conversationId = nil
            ChatBackend.shared.messages.removeAll()
        }
    }

    // MARK: - Messaging

    @objc
    func sendMessage(_ text: String,
                    resolve: @escaping RCTPromiseResolveBlock,
                    reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.message(value: text) { message, error in
                if let error = error {
                    reject("SEND_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("SEND_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func actionButton(_ buttonId: String,
                     resolve: @escaping RCTPromiseResolveBlock,
                     reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.actionButton(id: buttonId) { message, error in
                if let error = error {
                    reject("ACTION_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("ACTION_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func urlButton(_ buttonId: String,
                  resolve: @escaping RCTPromiseResolveBlock,
                  reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.urlButton(id: buttonId) { message, error in
                if let error = error {
                    reject("ACTION_ERROR", error.localizedDescription, error)
                } else {
                    resolve(NSNull())
                }
            }
        }
    }

    @objc
    func triggerAction(_ actionId: String,
                      resolve: @escaping RCTPromiseResolveBlock,
                      reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.triggerAction(id: actionId) { message, error in
                if let error = error {
                    reject("ACTION_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("ACTION_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func sendSmartReply(_ value: String,
                       resolve: @escaping RCTPromiseResolveBlock,
                       reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.smartReply(value: value) { message, error in
                if let error = error {
                    reject("SMART_REPLY_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("SMART_REPLY_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func sendHumanChatMessage(_ value: String,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.humanChatPost(value: value) { message, error in
                if let error = error {
                    reject("SEND_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("SEND_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func clientTyping(_ value: String,
                     resolve: @escaping RCTPromiseResolveBlock,
                     reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            _ = ChatBackend.shared.clientTyping(value: value)
            ChatBackend.shared.typing { message, error in
                if let error = error {
                    reject("TYPING_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("TYPING_ERROR", "No response", nil)
                }
            }
        }
    }

    // MARK: - Feedback

    @objc
    func sendFeedback(_ messageId: String,
                     value: NSNumber,
                     resolve: @escaping RCTPromiseResolveBlock,
                     reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            let feedbackValue: FeedbackValue = value.intValue > 0 ? .positive : .negative
            ChatBackend.shared.feedback(id: messageId, value: feedbackValue) { message, error in
                if let error = error {
                    reject("FEEDBACK_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("FEEDBACK_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func sendConversationFeedback(_ rating: NSNumber,
                                  text: String?,
                                  resolve: @escaping RCTPromiseResolveBlock,
                                  reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.conversationFeedback(rating: rating.intValue, text: text) { message, error in
                if let error = error {
                    reject("FEEDBACK_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("FEEDBACK_ERROR", "No response", nil)
                }
            }
        }
    }

    // MARK: - Human Chat

    @objc
    func poll(_ resolve: @escaping RCTPromiseResolveBlock,
             reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.poll(message: nil) { message, error in
                if let error = error {
                    reject("POLL_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("POLL_ERROR", "No response", nil)
                }
            }
        }
    }

    @objc
    func pollStop(_ resolve: @escaping RCTPromiseResolveBlock,
                 reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.pollStop(message: nil) { message, error in
                if let error = error {
                    reject("POLL_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("POLL_ERROR", "No response", nil)
                }
            }
        }
    }

    // MARK: - Files

    @objc
    func sendFiles(_ filesArray: [[String: Any]],
                   message: String?,
                   resolve: @escaping RCTPromiseResolveBlock,
                   reject: @escaping RCTPromiseRejectBlock) {
        reject("NOT_IMPLEMENTED", "File upload not yet implemented for iOS", nil)
    }

    // MARK: - Download

    @objc
    func downloadConversation(_ userToken: String?,
                             resolve: @escaping RCTPromiseResolveBlock,
                             reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            ChatBackend.shared.download { message, error in
                if let error = error {
                    reject("DOWNLOAD_ERROR", error.localizedDescription, error)
                } else if let message = message {
                    let messageDict = RNBoostAIMessageConverter.messageToDictionary(message)
                    resolve(messageDict)
                } else {
                    reject("DOWNLOAD_ERROR", "No response", nil)
                }
            }
        }
    }

    // MARK: - State

    @objc
    func getMessages(_ resolve: @escaping RCTPromiseResolveBlock,
                    reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            let messagesArray = ChatBackend.shared.messages.map { message in
                RNBoostAIMessageConverter.messageToDictionary(message)
            }
            resolve(messagesArray)
        }
    }

    // MARK: - UI

    @objc
    func openChatModal(_ customConfig: [String: Any]?) {
        DispatchQueue.main.async(execute: {
            guard let rootVC = UIApplication.shared.windows.first?.rootViewController else { return }

            let chatVC = ChatViewController(backend: ChatBackend.shared)

            let navVC = UINavigationController(rootViewController: chatVC)
            navVC.modalPresentationStyle = .fullScreen
            rootVC.present(navVC, animated: true)
        })
    }
}
