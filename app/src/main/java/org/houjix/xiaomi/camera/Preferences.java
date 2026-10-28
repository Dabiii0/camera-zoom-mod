package org.houjix.xiaomi.camera;

import android.content.Context;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;

import java.io.File;

public class Preferences {
    public static final String PACKAGE_NAME = "org.houjix.xiaomi.camera";
    public static final String PREF_FILE_NAME = "camera_mod_preferences";

    // Preference Keys
    public static final String KEY_ZOOM_LIMIT = "pref_zoom_limit";
    public static final String KEY_AI_ENHANCE = "pref_ai_enhance";
    public static final String KEY_AI_STRENGTH = "pref_ai_strength";
    public static final String KEY_ZOOM_SR = "pref_zoom_sr";
    public static final String KEY_SHARPNESS = "pref_sharpness";
    public static final String KEY_NOISE_REDUCTION = "pref_noise_reduction";
    public static final String KEY_HDR_SCENE = "pref_hdr_scene";
    public static final String KEY_FORCE_LEICA = "pref_force_leica";

    private static XSharedPreferences xSharedPreferences;

    public static synchronized void initXSharedPreferences() {
        if (xSharedPreferences == null) {
            xSharedPreferences = new XSharedPreferences(PACKAGE_NAME, PREF_FILE_NAME);
            xSharedPreferences.makeWorldReadable();
        } else {
            xSharedPreferences.reload();
        }
    }

    public static float getZoomLimit() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            String val = xSharedPreferences.getString(KEY_ZOOM_LIMIT, "30");
            try {
                return Float.parseFloat(val);
            } catch (Exception e) {
                return 30.0f;
            }
        }
        return 30.0f;
    }

    public static boolean isAiEnhanceEnabled() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getBoolean(KEY_AI_ENHANCE, true);
        }
        return true;
    }

    public static String getAiStrength() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getString(KEY_AI_STRENGTH, "balanced");
        }
        return "balanced";
    }

    public static boolean isZoomSrEnabled() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getBoolean(KEY_ZOOM_SR, true);
        }
        return true;
    }

    public static String getSharpnessMode() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getString(KEY_SHARPNESS, "default");
        }
        return "default";
    }

    public static String getNoiseReductionMode() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getString(KEY_NOISE_REDUCTION, "high_quality");
        }
        return "high_quality";
    }

    public static boolean isHdrSceneEnabled() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getBoolean(KEY_HDR_SCENE, true);
        }
        return true;
    }

    public static boolean isForceLeicaEnabled() {
        if (xSharedPreferences != null) {
            xSharedPreferences.reload();
            return xSharedPreferences.getBoolean(KEY_FORCE_LEICA, true);
        }
        return true;
    }

    public static void fixPermissions(Context context) {
        try {
            File dataDir = context.getDataDir();
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefFile = new File(prefsDir, PREF_FILE_NAME + ".xml");
            if (dataDir.exists()) dataDir.setReadable(true, false);
            if (dataDir.exists()) dataDir.setExecutable(true, false);
            if (prefsDir.exists()) prefsDir.setReadable(true, false);
            if (prefsDir.exists()) prefsDir.setExecutable(true, false);
            if (prefFile.exists()) prefFile.setReadable(true, false);
        } catch (Throwable t) {
            XposedBridge.log("[XiaomiCameraMod-houjix] Failed to fix shared_prefs permissions: " + t.getMessage());
        }
    }
}
