//
//  RNBoostAI.m
//  BoostAI React Native
//
//  Module registration for React Native
//

#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

// Main Module
@interface RCT_EXTERN_REMAP_MODULE(BoostAI, RNBoostAIModule, NSObject)

// Initialization & Configuration
RCT_EXTERN_METHOD(initialize:(NSString *)domain
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(setCustomConfig:(NSDictionary *)config
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(getConfig:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(setLanguageCode:(NSString *)languageCode)

RCT_EXTERN_METHOD(setUserToken:(NSString *)userToken)

RCT_EXTERN_METHOD(setConversationId:(NSString *)conversationId)

RCT_EXTERN_METHOD(getConversationId:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(setCertificatePinningEnabled:(BOOL)enabled)

RCT_EXTERN_METHOD(registerFont:(NSString *)name resourceId:(NSNumber *)resourceId)

// Conversation Control
RCT_EXTERN_METHOD(start:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(stop:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(resume:(NSDictionary *)options
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(delete:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(resetConversation)

// Messaging
RCT_EXTERN_METHOD(sendMessage:(NSString *)text
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(actionButton:(NSString *)buttonId
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(urlButton:(NSString *)buttonId
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(triggerAction:(NSString *)actionId
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(sendSmartReply:(NSString *)value
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(sendHumanChatMessage:(NSString *)value
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(clientTyping:(NSString *)value
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// Feedback
RCT_EXTERN_METHOD(sendFeedback:(NSString *)messageId
                  value:(NSNumber *)value
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(sendConversationFeedback:(NSNumber *)rating
                  text:(NSString *)text
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// Human Chat
RCT_EXTERN_METHOD(poll:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

RCT_EXTERN_METHOD(pollStop:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// Files
RCT_EXTERN_METHOD(sendFiles:(NSArray *)files
                  message:(NSString *)message
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// Download
RCT_EXTERN_METHOD(downloadConversation:(NSString *)userToken
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// State
RCT_EXTERN_METHOD(getMessages:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject)

// UI
RCT_EXTERN_METHOD(openChatModal:(NSDictionary *)customConfig)

@end

// View Managers
@interface RCT_EXTERN_MODULE(BoostAIChatViewManager, RCTViewManager)
RCT_EXTERN_METHOD(close:(nonnull NSNumber *)reactTag)
RCT_EXTERN_METHOD(showSettings:(nonnull NSNumber *)reactTag)
RCT_EXTERN_METHOD(hideSettings:(nonnull NSNumber *)reactTag)
RCT_EXTERN_METHOD(showFeedback:(nonnull NSNumber *)reactTag)
RCT_EXTERN_METHOD(hideFeedback:(nonnull NSNumber *)reactTag)
@end

@interface RCT_EXTERN_MODULE(BoostAIAvatarViewManager, RCTViewManager)
@end
