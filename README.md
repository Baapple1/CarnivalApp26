# Bridgwater Carnival Companion App
This project is an Android and IOS application built for the Bridgwater Carnival. It provides helpful information and functionalities for attendies.

It is made up of Kotlin Multiplatform, for the application structure, and Jetbrains Compose / Compose Multiplatform for the UI. Material2 standard is used for the Compose API.

## Development Setup

1. Each developer should have their own `secrets.properties` file
2. Use development-only API keys for local development
3. Never commit real API keys to the repository
4. If you accidentally commit sensitive information:
   - Immediately rotate the exposed keys
   - Remove the sensitive information from git history
   - Update the `.gitignore` if needed

## Voting System Configuration
You will need uncomment so you can gain  access on help.kt
To enable the voting system and notifications, follow these steps:

1. **Enable Test Mode**:
   - Go to `composeApp/src/commonMain/kotlin/org/bridgwatercarnival/companion/database/FirestoreService.kt`
   - Set `TEST_MODE = true` to enable test voting functionality

2. **Configure Voting Time Window**:
   - Go to `composeApp/src/commonMain/kotlin/org/bridgwatercarnival/companion/util/TimeWindow.kt`
   - Adjust the following variables as needed:
     ```kotlin
     var USE_CURRENT_TIME_FOR_TESTING = true  // Set to true to use current time for testing
     var SHOW_RESULTS_IN_TESTING = true       // Set to true to show results while testing
     var ENABLE_CONCURRENT_VOTING_AND_RESULTS = true  // Enable both voting and results simultaneously
     ```

3. **Enable Voting UI**:
   - Go to `composeApp/src/commonMain/kotlin/org/bridgwatercarnival/companion/pages/Help.kt`
   - Uncomment the voting section in the `ExpandableCategory` component
   - This will make the voting option visible in the app's main menu

4. **Enable Notifications**:
   - Go to `composeApp/src/commonMain/kotlin/org/bridgwatercarnival/companion/pages/Settings.kt`
   - Uncomment the following sections:
     - Voting notification state variable
     - Results notification state variable
     - Voting notification toggle UI
     - Results notification toggle UI
   - Go to `composeApp/src/commonMain/kotlin/org/bridgwatercarnival/companion/notifications/NotificationService.kt`
   - Uncomment the voting and results notification functions in the interface

5. **Enable Notification Receiver**:
   - Go to `composeApp/src/androidMain/kotlin/org/bridgwatercarnival/companion/notifications/CarnivalNotificationReceiver.kt`
   - Uncomment the voting and results notification handling code
   - Uncomment the notification scheduling in the boot completed handler

### Testing the Voting System

1. With test mode enabled, users can vote multiple times
2. The voting window will be based on the current time + 2 hours when `USE_CURRENT_TIME_FOR_TESTING` is true
3. Results can be viewed while voting is still active if `SHOW_RESULTS_IN_TESTING` is true
4. Both voting and results can be active simultaneously if `ENABLE_CONCURRENT_VOTING_AND_RESULTS` is true

### Production Deployment

Before deploying to production:
1. Set `TEST_MODE = false` in `FirestoreService.kt`
2. Set all testing flags to `false` in `TimeWindow.kt`
3. Ensure voting and results notifications are properly configured
4. Test the voting system with real-time constraints

## Using API Keys in Code

Use the `ApiKeyManager` to access API keys:

```kotlin
val apiKey = ApiKeyManager.getApiKey("FIREBASE_API_KEY")
if (ApiKeyManager.hasApiKey("MAPS_API_KEY")) {
    // Use the API key
}
```

## Build Instructions

To test xcode run:
```bash
./gradlew assemble
```

If that doesn't work, run:
```bash
chmod +x ./gradlew
./gradlew assemble
```


