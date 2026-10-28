package org.houjix.xiaomi.camera;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.houjix.xiaomi.camera.hooks.Camera2Hooks;
import org.houjix.xiaomi.camera.hooks.DeviceHooks;
import org.houjix.xiaomi.camera.hooks.SettingsHooks;

public class Main implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    private static final String TAG = "[XiaomiCameraMod-houjix] ";

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Preferences.initXSharedPreferences();
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!"com.android.camera".equals(lpparam.packageName)) {
            return;
        }

        XposedBridge.log(TAG + "Targeting com.android.camera process: " + lpparam.processName);
        Preferences.initXSharedPreferences();

        // Safe SystemProperties fix (replaces broken reference equality from 1.0)
        try {
            Class<?> sysPropClass = XposedHelpers.findClassIfExists("android.os.SystemProperties", lpparam.classLoader);
            if (sysPropClass != null) {
                XposedHelpers.findAndHookMethod(sysPropClass, "get", String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String key = (String) param.args[0];
                        if ("ro.miui.customized.region".equals(key)) {
                            param.setResult("");
                        }
                    }
                });
            }
        } catch (Throwable t) {
            XposedBridge.log(TAG + "SystemProperties hook notice: " + t.getMessage());
        }

        // Initialize Xiaomi 13 (fuxi) device hardware profile hooks
        try {
            DeviceHooks.init(lpparam);
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Fatal in DeviceHooks: " + t.getMessage());
        }

        // Initialize Camera2 ISP parameter hooks (noise reduction, sharpness, HDR)
        try {
            Camera2Hooks.init(lpparam);
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Fatal in Camera2Hooks: " + t.getMessage());
        }

        // Initialize settings UI injection hooks
        try {
            SettingsHooks.init(lpparam);
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Fatal in SettingsHooks: " + t.getMessage());
        }

        XposedBridge.log(TAG + "All hooks registered successfully for Xiaomi 13 (fuxi)");
    }
}
