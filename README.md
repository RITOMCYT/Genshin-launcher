# Genshin Impact Launcher - Professional Dev Build

A beautiful custom Android launcher inspired by **Genshin Impact** aesthetics.

## Features
- Elegant dark fantasy UI with gold accents and elemental theme switching
- Real-time Clock widget (Teyvat time)
- Elemental Resonance widget (tap circles to dynamically change the entire app's accent color)
- Adventure Stats widget
- Fully functional app grid with real icons from your device
- Search bar to filter apps
- Set as default system launcher (HOME category)
- Configured to build APK for **armeabi-v7a (armv7)** as requested

## How to Build the APK

1. **Open the project**
   - Copy the entire `genshin_launcher` folder to your computer
   - Open it in **Android Studio** (recommended version 2024.1+ or newer)

2. **Sync Gradle**
   - Let Android Studio download dependencies (first time may take a few minutes)

3. **Build APK**
   - Go to **Build > Build Bundle(s) / APK > Build APK(s)**
   - The APK will be generated at:
     `app/build/outputs/apk/armeabi-v7a/debug/app-armeabi-v7a-debug.apk`

4. **Install on device**
   - Enable "Install from unknown sources"
   - Install the APK
   - When prompted, set it as your default launcher (or go to Settings > Apps > Default apps > Home app)

## Important Notes

### Customization
- **Background**: Currently solid elegant dark color. To add a beautiful Genshin-style wallpaper:
  1. Add your image to `app/src/main/res/drawable/`
  2. Modify `Box` background in `MainActivity.kt` to use `painterResource`

- **Launcher Icon**: Replace the default icon by generating one in Android Studio (Right click res > New > Image Asset)

- **More Widgets**: You can easily add more widgets in the `Row` section of `GenshinHomeScreen`

### Copyright Warning
This project uses **inspired design** only (colors, names, widget concepts). 
**Do not** include official Genshin Impact images, characters, or assets from miHoYo/HoYoverse. 
Create or use royalty-free fantasy anime style assets.

### Architecture
- 100% Kotlin + Jetpack Compose (modern, clean, performant)
- No heavy dependencies
- Real PackageManager integration for launching apps
- Dynamic theme switching (Elemental Resonance)

## Future Improvements (for you to add)
- Drag & drop app repositioning
- Multiple home pages
- Real Android widget hosting (AppWidgetHost)
- Paimon floating assistant button
- Particle effects / Lottie animations
- Cloud backup of layout

## Built with ❤️ in Professional Dev Mode

If you need:
- Additional widgets
- Different elemental themes
- Bug fixes / improvements
- Full widget hosting implementation

Just tell me the next feature to implement!

Enjoy your journey, Traveler! ✨
