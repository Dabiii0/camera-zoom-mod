package org.houjix.xiaomi.camera.hooks;

import android.content.Context;
import android.content.Intent;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SettingsHooks {
    private static final String TAG = "[XiaomiCameraMod-houjix-Settings] ";

    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {
        ClassLoader classLoader = lpparam.classLoader;
        Class<?> commonFragClass = XposedHelpers.findClassIfExists(
            "com.android.camera.fragment.settings.CameraCommonPreferenceFragment",
            classLoader
        );

        if (commonFragClass == null) {
            commonFragClass = XposedHelpers.findClassIfExists(
                "com.android.camera.fragment.settings.CameraPreferenceFragment",
                classLoader
            );
        }

        if (commonFragClass != null) {
            try {
                XposedHelpers.findAndHookMethod(
                    commonFragClass,
                    "addCurrentPreferences",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            injectModPreference(param.thisObject);
                        }
                    }
                );
                XposedBridge.log(TAG + "Successfully hooked addCurrentPreferences");
            } catch (Throwable t) {
                XposedBridge.log(TAG + "Failed to hook addCurrentPreferences: " + t.getMessage());
            }
        }
    }

    private static void injectModPreference(Object fragmentInstance) {
        try {
            Context context = (Context) XposedHelpers.callMethod(fragmentInstance, "getContext");
            if (context == null) return;

            PreferenceGroup preferenceGroup = (PreferenceGroup) XposedHelpers.getObjectField(
                fragmentInstance,
                "mPreferenceGroup"
            );
            if (preferenceGroup == null) return;

            // Check if already injected
            if (preferenceGroup.findPreference("key_houjix_camera_mod_entry") != null) {
                return;
            }

            // Create Category
            PreferenceCategory category = new PreferenceCategory(context);
            category.setKey("key_category_houjix_camera_mod");
            category.setTitle("Xiaomi 13 Camera Mod (fuxi)");
            category.setIconSpaceReserved(false);
            preferenceGroup.addPreference(category);

            // Create Preference Entry
            Preference modPref = new Preference(context);
            modPref.setKey("key_houjix_camera_mod_entry");
            modPref.setTitle("Camera Mod & Zoom Configuration");
            modPref.setSummary("Configure 30×/50×/100× zoom, AI enhancement, sharpness & HDR • made by houjix");
            modPref.setIconSpaceReserved(false);

            Intent intent = new Intent();
            intent.setClassName("org.houjix.xiaomi.camera", "org.houjix.xiaomi.camera.ui.SettingsActivity");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            modPref.setIntent(intent);

            category.addPreference(modPref);
            XposedBridge.log(TAG + "Injected mod settings entry into Camera settings UI");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Error injecting settings preference: " + t.getMessage());
        }
    }
}
