module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: './android',
        libraryName: 'BoostAISpec',
        componentDescriptors: [
          'BoostAIChatViewComponentDescriptor',
          'BoostAIAvatarViewComponentDescriptor',
        ],
        cmakeListsPath: 'src/main/jni/CMakeLists.txt',
        // Note: boostai-sdk must be manually added to settings.gradle and app/build.gradle
        // See installation guide for details
      },
      ios: {
        sourceDir: './ios',
        libraryName: 'BoostAISpec',
        componentDescriptors: [
          'BoostAIChatViewComponentDescriptor',
          'BoostAIAvatarViewComponentDescriptor',
        ],
        // Note: Run 'pod install' in ios directory after installation
      },
    },
  },
};
