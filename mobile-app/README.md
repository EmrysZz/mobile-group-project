# FloodRescue - Android Mobile App

A flood rescue and emergency response Android application built with Kotlin and Jetpack Compose.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Android Studio** (Ladybug or later recommended)
  - Download from: https://developer.android.com/studio
- **JDK 11** or higher
- **Android SDK** with:
  - Compile SDK: 36
  - Min SDK: 26 (Android 8.0 Oreo)
- **Google Maps API Key** (required for map functionality)

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/EmrysZz/mobile-group-project.git
cd mobile-group-project/mobile-app
```

### 2. Open in Android Studio

1. Launch **Android Studio**
2. Select **File > Open**
3. Navigate to the `mobile-app` folder and click **OK**
4. Wait for Gradle sync to complete (this may take a few minutes on first load)

### 3. Configure Google Maps API Key

This app uses Google Maps, which requires an API key.

1. Get a Google Maps API Key:
   - Go to [Google Cloud Console](https://console.cloud.google.com/)
   - Create a new project or select an existing one
   - Enable the **Maps SDK for Android**
   - Go to **Credentials** and create an API key
   - Restrict the key to Android apps (recommended)

2. Add the API key to the project:
   - Create or edit the `local.properties` file in the project root
   - Add the following line:
     ```properties
     MAPS_API_KEY=your_api_key_here
     ```

> **Note:** The `local.properties` file is gitignored and should never be committed to version control.

### 4. Run the App

#### Option A: Using Android Emulator

1. Open **Device Manager** in Android Studio (Tools > Device Manager)
2. Click **Create Device**
3. Select a device (e.g., Pixel 6)
4. Select a system image with **API 26 or higher** (API 34 recommended)
5. Click **Finish** to create the emulator
6. Start the emulator by clicking the play button
7. Click **Run > Run 'app'** or press `Shift + F10`

#### Option B: Using Physical Device

1. Enable **Developer Options** on your Android device:
   - Go to Settings > About Phone
   - Tap "Build Number" 7 times
2. Enable **USB Debugging** in Developer Options
3. Connect your device via USB cable
4. Accept the USB debugging prompt on your device
5. Select your device in Android Studio's device dropdown
6. Click **Run > Run 'app'** or press `Shift + F10`

## Project Structure

```
mobile-app/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/example/fr/    # Kotlin source code
│   │       │   ├── ui/                  # UI components
│   │       │   │   ├── screens/         # Screen composables
│   │       │   │   ├── navigation/      # Navigation setup
│   │       │   │   └── theme/           # App theming
│   │       │   ├── model/               # Data models
│   │       │   ├── viewmodel/           # ViewModels
│   │       │   └── MainActivity.kt      # Main entry point
│   │       ├── res/                     # Resources (layouts, strings, etc.)
│   │       └── AndroidManifest.xml      # App manifest
│   └── build.gradle.kts                 # App-level build config
├── gradle/
│   └── libs.versions.toml               # Dependency versions catalog
├── build.gradle.kts                     # Project-level build config
├── settings.gradle.kts                  # Gradle settings
└── local.properties                     # Local config (API keys - not in git)
```

## Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| Kotlin | 2.0.21 | Programming language |
| Jetpack Compose | BOM 2024.09.00 | Modern UI toolkit |
| Material 3 | Latest | UI design system |
| Navigation Compose | 2.7.7 | Screen navigation |
| ViewModel | 2.10.0 | State management |
| Ktor | 2.3.11 | HTTP client |
| Google Maps | 18.2.0 | Map functionality |
| Maps Compose | 2.11.4 | Maps for Compose |

## Troubleshooting

### Gradle Sync Failed

- Ensure you have a stable internet connection
- Try **File > Invalidate Caches / Restart**
- Check that your Android Studio and SDK are up to date

### Map Not Showing

- Verify your Google Maps API key is correctly set in `local.properties`
- Ensure the Maps SDK for Android is enabled in Google Cloud Console
- Check that your API key restrictions allow your app's package name

### Emulator Not Starting

- Ensure virtualization is enabled in your BIOS (Intel VT-x or AMD-V)
- Check that you have enough disk space (at least 8GB free)
- Try creating a new emulator with a different API level

### Build Errors

- Run **Build > Clean Project** then **Build > Rebuild Project**
- Ensure JDK 11 or higher is configured in Android Studio
- Check that all SDK components are installed via SDK Manager

## Permissions

The app requires the following permissions:

- `INTERNET` - Network access for API calls
- `ACCESS_NETWORK_STATE` - Check network connectivity
- `ACCESS_FINE_LOCATION` - Precise GPS location
- `ACCESS_COARSE_LOCATION` - Approximate location

## Building for Release

1. Generate a signing key:
   ```bash
   keytool -genkey -v -keystore release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-alias
   ```

2. Configure signing in `app/build.gradle.kts`

3. Build the release APK:
   ```bash
   ./gradlew assembleRelease
   ```

The APK will be located at `app/build/outputs/apk/release/app-release.apk`

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is for educational purposes.
