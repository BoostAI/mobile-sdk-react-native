/**
 * Boost.ai React Native Example App
 * Replicates the native Android sample app functionality
 */

import React, { useState, useEffect } from 'react';
import {
    StatusBar,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
    useColorScheme,
} from 'react-native';
import { SafeAreaView, SafeAreaProvider } from 'react-native-safe-area-context';
import { BoostAI, BoostAIChatView, BoostAIModalTouchable } from '@boostai/mobile-sdk-react-native';
import type { ChatConfig } from '@boostai/mobile-sdk-react-native';

type Tab = 'fullscreen' | 'avatar';

function App(): React.JSX.Element {
    const isDarkMode = useColorScheme() === 'dark';
    const [activeTab, setActiveTab] = useState<Tab>('fullscreen');
    const [isConfigured, setIsConfigured] = useState(false);
    const [primaryColor, setPrimaryColor] = useState('#6200EE');
    const [contrastColor, setContrastColor] = useState('#FFFFFF');

    useEffect(() => {
        const subscriptions: any[] = [];

        // Initialize the SDK - replicating MainActivity.kt initialization
        const initializeSDK = async () => {
            try {
                console.log('Initializing Boost.ai SDK...');

                // Set domain and language (from MainActivity.kt line 50-51)
                await BoostAI.initialize('sdk.boost.ai');
                await BoostAI.setLanguageCode('en-US');

                // Set custom configuration (from MainActivity.kt line 53-74)
                const customConfig: ChatConfig = {
                    chatPanel: {
                        styling: {
                            // Uncomment to customize colors (from MainActivity.kt line 57-65)
                            // primaryColor: '#D32F2F', // holo_red_dark
                            // contrastColor: '#FFA726', // holo_orange_light
                            // chatBubbles: {
                            //   vaTextColor: '#FFFFFF',
                            //   vaBackgroundColor: '#4CAF50', // holo_green_dark
                            // },
                            // buttons: {
                            //   multiline: true,
                            // },
                        },
                        settings: {
                            // Uncomment to set conversation settings (from MainActivity.kt line 68-69)
                            // conversationId: '[stored-conversation-id]', // Resume conversation
                            // startLanguage: 'en-US', // Preferred language for welcome message
                        },
                    },
                };

                await BoostAI.setCustomConfig(customConfig);

                // Listen for config updates (from MainActivity.kt line 149-151)
                subscriptions.push(
                    BoostAI.addConfigListener((event) => {
                        console.log('Config received:', event.config);
                        // Update app styling based on config (from MainActivity.kt line 133-147)
                        if (event.config.chatPanel?.styling?.primaryColor) {
                            setPrimaryColor(event.config.chatPanel.styling.primaryColor);
                        }
                        if (event.config.chatPanel?.styling?.contrastColor) {
                            setContrastColor(event.config.chatPanel.styling.contrastColor);
                        }
                    })
                );

                // Listen for backend events (from MainActivity.kt line 155-157)
                subscriptions.push(
                    BoostAI.addBackendEventListener((event) => {
                        console.log('Boost backend event:', event.type, 'detail:', event.detail);
                    })
                );

                // Listen for UI events (from MainActivity.kt line 159-161)
                subscriptions.push(
                    BoostAI.addUIEventListener(({detail, event}) => {
                        console.log('Boost UI event:', event, 'detail:', detail);
                    })
                );

                // Listen for errors (from MainActivity.kt line 153)
                subscriptions.push(
                    BoostAI.addErrorListener((event) => {
                        console.error('Boost.ai error:', event.error);
                    })
                );

                setIsConfigured(true);
                console.log('Boost.ai SDK initialized successfully');
            } catch (error) {
                console.error('Failed to initialize Boost.ai SDK:', error);
            }
        };

        initializeSDK();

        // Cleanup observers (from MainActivity.kt line 106-112)
        return () => {
            subscriptions.forEach(sub => sub.remove());
        };
    }, []);

    const backgroundStyle = {
        backgroundColor: isDarkMode ? '#000000' : '#FFFFFF',
    };

    if (!isConfigured) {
        return (
            <SafeAreaView style={[styles.container, backgroundStyle]}>
                <StatusBar
                    barStyle={isDarkMode ? 'light-content' : 'dark-content'}
                    backgroundColor={primaryColor}
                />
                <View style={styles.loadingContainer}>
                    <Text style={styles.loadingText}>Initializing Boost.ai SDK...</Text>
                </View>
            </SafeAreaView>
        );
    }

    return (
        <SafeAreaView style={[styles.container, backgroundStyle]}>
            <StatusBar
                barStyle="light-content"
                backgroundColor={primaryColor}
            />

            {/* Toolbar (from MainActivity.kt line 76) */}
            <View style={[styles.toolbar, { backgroundColor: primaryColor }]}>
                <Text style={[styles.toolbarTitle, { color: contrastColor }]}>
                    Boost.ai Example
                </Text>
            </View>

            {/* Tab Layout (from MainActivity.kt line 78-95) */}
            <View style={[styles.tabLayout, { backgroundColor: primaryColor }]}>
                <TouchableOpacity
                    style={[
                        styles.tab,
                        activeTab === 'fullscreen' && styles.tabActive,
                    ]}
                    onPress={() => setActiveTab('fullscreen')}>
                    <Text style={[styles.tabText, { color: contrastColor }]}>
                        FULLSCREEN
                    </Text>
                    {activeTab === 'fullscreen' && (
                        <View style={[styles.tabIndicator, { backgroundColor: contrastColor }]} />
                    )}
                </TouchableOpacity>

                <TouchableOpacity
                    style={[
                        styles.tab,
                        activeTab === 'avatar' && styles.tabActive,
                    ]}
                    onPress={() => setActiveTab('avatar')}>
                    <Text style={[styles.tabText, { color: contrastColor }]}>
                        AVATAR
                    </Text>
                    {activeTab === 'avatar' && (
                        <View style={[styles.tabIndicator, { backgroundColor: contrastColor }]} />
                    )}
                </TouchableOpacity>
            </View>

            {/* Content - ViewPager with fragments (from MainActivity.kt line 83-92) */}
            <View style={styles.content}>
                {activeTab === 'fullscreen' ? (
                    // ChatViewFragment (MainActivity.kt line 86-88)
                    <BoostAIChatView
                        style={styles.chatView}
                    />
                ) : (
                    // Floating Action Button to open chat modal
                    <View style={styles.avatarContainer}>
                        <BoostAIModalTouchable
                            style={styles.fab}
                            activeOpacity={0.8}
                        >
                            <View style={[styles.fabInner, { backgroundColor: primaryColor }]}>
                                {/* Chat bubble icon */}
                                <View style={styles.chatIcon}>
                                    <View style={[styles.chatBubble, { borderColor: contrastColor }]}>
                                        <View style={[styles.chatDot, { backgroundColor: contrastColor }]} />
                                        <View style={[styles.chatDot, { backgroundColor: contrastColor }]} />
                                        <View style={[styles.chatDot, { backgroundColor: contrastColor }]} />
                                    </View>
                                </View>
                            </View>
                        </BoostAIModalTouchable>
                    </View>
                )}
            </View>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
    },
    loadingContainer: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    loadingText: {
        fontSize: 16,
        color: '#666',
    },
    toolbar: {
        height: 56,
        justifyContent: 'center',
        paddingHorizontal: 16,
        elevation: 4,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
    },
    toolbarTitle: {
        fontSize: 20,
        fontWeight: 'bold',
    },
    tabLayout: {
        flexDirection: 'row',
        elevation: 4,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.2,
        shadowRadius: 4,
    },
    tab: {
        flex: 1,
        height: 48,
        justifyContent: 'center',
        alignItems: 'center',
        position: 'relative',
    },
    tabActive: {
        // Active tab styling
    },
    tabText: {
        fontSize: 14,
        fontWeight: '500',
    },
    tabIndicator: {
        position: 'absolute',
        bottom: 0,
        left: 0,
        right: 0,
        height: 3,
    },
    content: {
        flex: 1,
    },
    chatView: {
        flex: 1,
    },
    avatarContainer: {
        flex: 1,
        justifyContent: 'flex-end',
        alignItems: 'flex-end',
        backgroundColor: '#f5f5f5',
        padding: 24,
    },
    fab: {
        borderRadius: 56,
        elevation: 6,
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 4 },
        shadowOpacity: 0.3,
        shadowRadius: 8,
    },
    fabInner: {
        width: 64,
        height: 64,
        borderRadius: 32,
        justifyContent: 'center',
        alignItems: 'center',
    },
    chatIcon: {
        width: 32,
        height: 32,
        justifyContent: 'center',
        alignItems: 'center',
    },
    chatBubble: {
        width: 28,
        height: 24,
        borderWidth: 2.5,
        borderRadius: 14,
        borderBottomLeftRadius: 4,
        justifyContent: 'center',
        alignItems: 'center',
        flexDirection: 'row',
        gap: 3,
    },
    chatDot: {
        width: 4,
        height: 4,
        borderRadius: 2,
    },
});

// Wrap with SafeAreaProvider for proper safe area handling
function AppWithSafeArea(): React.JSX.Element {
    return (
        <SafeAreaProvider>
            <App />
        </SafeAreaProvider>
    );
}

export default AppWithSafeArea;
