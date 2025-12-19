//
//  RNBoostAIConfigConverter.swift
//  BoostAI React Native
//
//  Converts ChatConfig to Dictionary for JavaScript
//

import Foundation
import BoostAI

class RNBoostAIConfigConverter {

    static func configToDictionary(_ config: ChatConfig?) -> [String: Any] {
        guard let config = config else { return [:] }

        var dict: [String: Any] = [:]

        if let languages = config.messages {
            // Languages contains a dictionary of Messages by language code
            dict["messages"] = languages.languages.mapValues { languageToDictionary($0) }
        }

        if let chatPanel = config.chatPanel {
            dict["chatPanel"] = chatPanelToDictionary(chatPanel)
        }

        return dict
    }

    private static func languageToDictionary(_ messages: Messages) -> [String: Any] {
        return [
            "back": messages.back,
            "close.window": messages.closeWindow,
            "compose.characters.used": messages.composeCharactersUsed,
            "compose.placeholder": messages.composePlaceholder,
            "delete.conversation": messages.deleteConversation,
            "download.conversation": messages.downloadConversation,
            "feedback.placeholder": messages.feedbackPlaceholder,
            "feedback.prompt": messages.feedbackPrompt,
            "feedback.thumbs.down": messages.feedbackThumbsDown,
            "feedback.thumbs.up": messages.feedbackThumbsUp,
            "filter.select": messages.filterSelect,
            "header.text": messages.headerText,
            "logged.in": messages.loggedIn,
            "message.thumbs.down": messages.messageThumbsDown,
            "message.thumbs.up": messages.messageThumbsUp,
            "minimize.window": messages.minimizeWindow,
            "open.menu": messages.openMenu,
            "opens.in.new.tab": messages.opensInNewTab,
            "privacy.policy": messages.privacyPolicy,
            "submit.feedback": messages.submitFeedback,
            "submit.message": messages.submitMessage,
            "text.too.long": messages.textTooLong,
            "upload.file": messages.uploadFile,
            "upload.file.error": messages.uploadFileError,
            "upload.file.progress": messages.uploadFileProgress,
            "upload.file.success": messages.uploadFileSuccess
        ]
    }

    private static func chatPanelToDictionary(_ chatPanel: ChatPanel) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let header = chatPanel.header {
            dict["header"] = headerToDictionary(header)
        }

        if let styling = chatPanel.styling {
            dict["styling"] = stylingToDictionary(styling)
        }

        if let settings = chatPanel.settings {
            dict["settings"] = settingsToDictionary(settings)
        }

        return dict
    }

    private static func headerToDictionary(_ header: Header) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let title = header.title {
            dict["title"] = title
        }
        if let hideMinimize = header.hideMinimizeButton {
            dict["hideMinimizeButton"] = hideMinimize
        }
        if let hideMenu = header.hideMenuButton {
            dict["hideMenuButton"] = hideMenu
        }

        return dict
    }

    private static func stylingToDictionary(_ styling: Styling) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let primaryColor = styling.primaryColor {
            dict["primaryColor"] = colorToHex(primaryColor)
        }
        if let contrastColor = styling.contrastColor {
            dict["contrastColor"] = colorToHex(contrastColor)
        }
        if let bgColor = styling.panelBackgroundColor {
            dict["panelBackgroundColor"] = colorToHex(bgColor)
        }

        if let chatBubbles = styling.chatBubbles {
            var bubblesDict: [String: Any] = [:]
            if let userBg = chatBubbles.userBackgroundColor {
                bubblesDict["userBackgroundColor"] = colorToHex(userBg)
            }
            if let userText = chatBubbles.userTextColor {
                bubblesDict["userTextColor"] = colorToHex(userText)
            }
            if let vaBg = chatBubbles.vaBackgroundColor {
                bubblesDict["vaBackgroundColor"] = colorToHex(vaBg)
            }
            if let vaText = chatBubbles.vaTextColor {
                bubblesDict["vaTextColor"] = colorToHex(vaText)
            }
            dict["chatBubbles"] = bubblesDict
        }

        if let buttons = styling.buttons {
            var buttonsDict: [String: Any] = [:]
            if let bg = buttons.backgroundColor {
                buttonsDict["backgroundColor"] = colorToHex(bg)
            }
            if let text = buttons.textColor {
                buttonsDict["textColor"] = colorToHex(text)
            }
            dict["buttons"] = buttonsDict
        }

        if let composer = styling.composer {
            var composerDict: [String: Any] = [:]
            if let hide = composer.hide {
                composerDict["hide"] = hide
            }
            if let sendBtn = composer.sendButtonColor {
                composerDict["sendButtonColor"] = colorToHex(sendBtn)
            }
            if let textareaBg = composer.textareaBackgroundColor {
                composerDict["textareaBackgroundColor"] = colorToHex(textareaBg)
            }
            if let textareaText = composer.textareaTextColor {
                composerDict["textareaTextColor"] = colorToHex(textareaText)
            }
            dict["composer"] = composerDict
        }

        return dict
    }

    private static func settingsToDictionary(_ settings: Settings) -> [String: Any] {
        var dict: [String: Any] = [:]

        if let conversationId = settings.conversationId {
            dict["conversationId"] = conversationId
        }
        if let userToken = settings.userToken {
            dict["userToken"] = userToken
        }
        if let startLanguage = settings.startLanguage {
            dict["startLanguage"] = startLanguage
        }
        if let skill = settings.skill {
            dict["skill"] = skill
        }

        return dict
    }

    private static func colorToHex(_ color: UIColor) -> String {
        var r: CGFloat = 0
        var g: CGFloat = 0
        var b: CGFloat = 0
        var a: CGFloat = 0

        color.getRed(&r, green: &g, blue: &b, alpha: &a)

        return String(format: "#%02X%02X%02X%02X",
                     Int(a * 255),
                     Int(r * 255),
                     Int(g * 255),
                     Int(b * 255))
    }
}
