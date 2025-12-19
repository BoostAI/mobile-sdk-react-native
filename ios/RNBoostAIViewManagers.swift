//
//  RNBoostAIViewManagers.swift
//  BoostAI React Native
//
//  View managers for ChatViewController and AgentAvatarView
//

import Foundation
import React
import BoostAI

@objc(BoostAIChatViewManager)
class BoostAIChatViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        let container = RNChatViewContainer()
        return container
    }

    @objc
    func close(_ reactTag: NSNumber) {
        // Close command
    }

    @objc
    func showSettings(_ reactTag: NSNumber) {
        // Show settings
    }

    @objc
    func hideSettings(_ reactTag: NSNumber) {
        // Hide settings
    }

    @objc
    func showFeedback(_ reactTag: NSNumber) {
        // Show feedback
    }

    @objc
    func hideFeedback(_ reactTag: NSNumber) {
        // Hide feedback
    }
}

class RNChatViewContainer: UIView {
    private var chatViewController: ChatViewController?
    private weak var parentViewController: UIViewController?

    override func didMoveToWindow() {
        super.didMoveToWindow()

        if window != nil && chatViewController == nil {
            setupChatViewController()
        }
    }

    private func setupChatViewController() {
        // Find parent view controller
        var responder: UIResponder? = next
        while responder != nil {
            if let viewController = responder as? UIViewController {
                parentViewController = viewController
                break
            }
            responder = responder?.next
        }

        guard let parentVC = parentViewController else {
            print("[RNBoostAIChatViewManager] Warning: Could not find parent view controller")
            return
        }

        // Create ChatViewController
        let chatVC = ChatViewController(backend: ChatBackend.shared)

        // Add as child view controller
        parentVC.addChild(chatVC)
        addSubview(chatVC.view)

        chatVC.view.translatesAutoresizingMaskIntoConstraints = false
        NSLayoutConstraint.activate([
            chatVC.view.topAnchor.constraint(equalTo: topAnchor),
            chatVC.view.leadingAnchor.constraint(equalTo: leadingAnchor),
            chatVC.view.trailingAnchor.constraint(equalTo: trailingAnchor),
            chatVC.view.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])

        chatVC.didMove(toParent: parentVC)
        chatViewController = chatVC
    }

    override func removeFromSuperview() {
        if let chatVC = chatViewController {
            chatVC.willMove(toParent: nil)
            chatVC.view.removeFromSuperview()
            chatVC.removeFromParent()
            chatViewController = nil
        }
        super.removeFromSuperview()
    }
}

@objc(BoostAIAvatarViewManager)
class BoostAIAvatarViewManager: RCTViewManager {

    override static func requiresMainQueueSetup() -> Bool {
        return true
    }

    override func view() -> UIView! {
        let avatarView = AgentAvatarView(backend: ChatBackend.shared)
        avatarView.avatarSize = 60.0

        // Add tap gesture to present chat modally
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(handleAvatarTap(_:)))
        avatarView.addGestureRecognizer(tapGesture)

        return avatarView
    }

    @objc
    private func handleAvatarTap(_ gesture: UITapGestureRecognizer) {
        DispatchQueue.main.async(execute: {
            guard let window = UIApplication.shared.windows.first,
                  let rootVC = window.rootViewController else { return }

            let chatVC = ChatViewController(backend: ChatBackend.shared)

            let navVC = UINavigationController(rootViewController: chatVC)
            navVC.modalPresentationStyle = .fullScreen

            var presentingVC = rootVC
            while let presented = presentingVC.presentedViewController {
                presentingVC = presented
            }

            presentingVC.present(navVC, animated: true) {
                BoostUIEvents.shared.publishEvent(event: .chatPanelOpened, detail: nil)
            }
        })
    }
}
