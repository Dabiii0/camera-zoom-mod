package org.houjix.xiaomi.camera.hooks;

import android.hardware.camera2.CaptureRequest;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.houjix.xiaomi.camera.Preferences;

public class Camera2Hooks {
    private static final String TAG = "[XiaomiCameraMod-houjix-Camera2] ";

    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(
                CaptureRequest.Builder.class,
                "build",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        CaptureRequest.Builder builder = (CaptureRequest.Builder) param.thisObject;
                        applyIspPreferences(builder);
                    }
                }
            );
            XposedBridge.log(TAG + "Successfully hooked CaptureRequest.Builder.build()");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook CaptureRequest.Builder.build(): " + t.getMessage());
        }
    }

    private static void applyIspPreferences(CaptureRequest.Builder builder) {
        try {
            // 1. Noise Reduction Mode
            String nrMode = Preferences.getNoiseReductionMode();
            if ("high_quality".equals(nrMode)) {
                builder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_HIGH_QUALITY);
            } else if ("minimal".equals(nrMode)) {
                builder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_MINIMAL);
            } else if ("standard".equals(nrMode)) {
                builder.set(CaptureRequest.NOISE_REDUCTION_MODE, CaptureRequest.NOISE_REDUCTION_MODE_FAST);
            }

            // 2. Sharpness / Edge Enhancement Mode
            String sharpness = Preferences.getSharpnessMode();
            if ("natural".equals(sharpness)) {
                builder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_OFF);
            } else if ("balanced".equals(sharpness)) {
                builder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_FAST);
            } else if ("enhanced".equals(sharpness)) {
                builder.set(CaptureRequest.EDGE_MODE, CaptureRequest.EDGE_MODE_HIGH_QUALITY);
            }

            // 3. HDR Scene Mode
            if (Preferences.isHdrSceneEnabled()) {
                // If scene mode control is enabled, ensure HDR mode flag is accessible
                Integer currentMode = builder.get(CaptureRequest.CONTROL_MODE);
                if (currentMode != null && currentMode == CaptureRequest.CONTROL_MODE_USE_SCENE_MODE) {
                    builder.set(CaptureRequest.CONTROL_SCENE_MODE, CaptureRequest.CONTROL_SCENE_MODE_HDR);
                }
            }
        } catch (Throwable ignored) {
            // Guard against vendor HAL throwing on unsupported tags
        }
    }
}
