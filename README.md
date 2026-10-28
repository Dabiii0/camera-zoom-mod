# Xiaomi 13 (fuxi) Leica Camera Mod (LSPosed Module)
**Branding Credit:** `made by houjix`  
**Target Package:** `com.android.camera`  
**Target Device:** Xiaomi 13 (`fuxi` - Snapdragon 8 Gen 2 / HyperOS)

---

## Features
1. **Configurable Zoom Limits:**
   - 30× (Stock Snapdragon 8 Gen 2 Limit)
   - 50× (Extended Telephoto Digital Crop)
   - 100× (Maximum Ultra Digital Crop)
2. **AI & Computational Photography Controls:**
   - AI Scene Enhancement toggle & strength presets (Natural, Balanced, Enhanced)
   - Multi-frame Super-Resolution (SR) toggle on telephoto crop
   - Camera2 ISP Sharpness / Edge Enhancement modes (`EDGE_MODE_OFF`, `EDGE_MODE_FAST`, `EDGE_MODE_HIGH_QUALITY`)
   - Hardware Multi-Frame Noise Reduction (MFNR) algorithms (`FAST`, `HIGH_QUALITY`, `MINIMAL`)
   - HDR Scene dynamic range bracketing
3. **Leica Hardware Integration:**
   - Force unlock of Leica Authentic & Leica Vibrant color profiles (`c8()`)
   - Leica watermarks & filters (`f7()`, `D5()`)
4. **Settings UI:**
   - Native Preferences UI accessible directly from Launcher / LSPosed Manager
   - Injected mod shortcut inside stock Xiaomi Camera Settings (`CameraCommonPreferenceFragment`)

---

## How to Build
### Prerequisites
- JDK 17 (Eclipse Temurin or OpenJDK 17)
- Android SDK Build-Tools 34.0.0 and Platform 34

### Local Gradle Build
```bash
./gradlew assembleDebug
# The output APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

### GitHub Actions (1-Click Cloud Build)
This repository contains `.github/workflows/build.yml`. Simply push this repository to GitHub or run Workflow Dispatch to produce signed and downloadable APK artifacts instantly.

---

## Installation & Rollback
### Installation via KernelSU / LSPosed
1. Ensure **KernelSU** and **LSPosed** (Zygisk or Shamiko) are active.
2. Install `Xiaomi-Camera-Mod-fuxi-houjix.apk`.
3. Open **LSPosed Manager** -> Modules -> enable **Xiaomi 13 Camera Mod**.
4. Ensure the scope is checked for **Camera** (`com.android.camera`).
5. Force stop the Camera app (`am force-stop com.android.camera`) and launch it.
6. Open the mod settings from Launcher or Camera Settings to customize zoom & ISP options.

### Rollback
- Disable or uninstall the module in LSPosed Manager.
- Force stop the camera app. The camera immediately reverts to stock ROM behavior.
