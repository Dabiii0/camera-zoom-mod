package org.houjix.xiaomi.camera.hooks;

import android.util.SparseArray;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import org.houjix.xiaomi.camera.Preferences;

public class DeviceHooks {
    private static final String TAG = "[XiaomiCameraMod-houjix-Device] ";

    public static void init(XC_LoadPackage.LoadPackageParam lpparam) {
        ClassLoader classLoader = lpparam.classLoader;

        // Target class: defpackage.fuxi (Xiaomi 13 / Snapdragon 8 Gen 2 hardware profile)
        Class<?> fuxiClass = XposedHelpers.findClassIfExists("defpackage.fuxi", classLoader);
        if (fuxiClass != null) {
            hookDeviceProfile(fuxiClass);
        } else {
            XposedBridge.log(TAG + "Class defpackage.fuxi not found directly, attempting device_common hook");
            Class<?> commonClass = XposedHelpers.findClassIfExists("defpackage.device_common", classLoader);
            if (commonClass != null) {
                hookDeviceProfile(commonClass);
            } else {
                XposedBridge.log(TAG + "WARNING: Neither defpackage.fuxi nor defpackage.device_common found in classLoader");
            }
        }
    }

    private static void hookDeviceProfile(Class<?> targetClass) {
        // 1. Hook C1() - Photo mode zoom ratios SparseArray
        try {
            XposedHelpers.findAndHookMethod(targetClass, "C1", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    float maxZoom = Preferences.getZoomLimit();
                    Float f06 = 0.6f;
                    Float f10 = 1.0f;
                    Float f20 = 2.0f;
                    Float f32 = 3.2f;
                    Float f100 = 10.0f;
                    Float[] photoZooms;

                    if (maxZoom >= 100.0f) {
                        photoZooms = new Float[]{f06, f10, f20, f32, f100, 30.0f, 60.0f, 100.0f};
                    } else if (maxZoom >= 50.0f) {
                        photoZooms = new Float[]{f06, f10, f20, f32, f100, 30.0f, 50.0f};
                    } else {
                        photoZooms = new Float[]{f06, f10, f20, f32, f100, 30.0f};
                    }

                    SparseArray<SparseArray<Float[]>> root = new SparseArray<>();
                    SparseArray<Float[]> normalMap = new SparseArray<>();
                    normalMap.put(0, photoZooms);

                    // AV_PIX_FMT_GBRAP10LE constant is 167
                    root.put(167, normalMap);

                    // Also preserve video zoom stops (15x default)
                    SparseArray<Float[]> videoMap = new SparseArray<>();
                    Float[] videoZooms;
                    if (maxZoom >= 100.0f) {
                        videoZooms = new Float[]{f06, f10, f20, f32, f100, 15.0f, 30.0f};
                    } else {
                        videoZooms = new Float[]{f06, f10, f20, f32, f100, 15.0f};
                    }
                    videoMap.put(0, videoZooms);
                    // AV_PIX_FMT_GBRAP10BE constant is 168
                    root.put(168, videoMap);

                    param.setResult(root);
                }
            });
            XposedBridge.log(TAG + "Successfully hooked C1() for configurable zoom ratios");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook C1: " + t.getMessage());
        }

        // 2. Hook j1() - Telephoto / Pro zoom stops
        try {
            XposedHelpers.findAndHookMethod(targetClass, "j1", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    float maxZoom = Preferences.getZoomLimit();
                    Float[] zoomStops;
                    if (maxZoom >= 100.0f) {
                        zoomStops = new Float[]{5.0f, 10.0f, 20.0f, 30.0f, 60.0f, 100.0f};
                    } else if (maxZoom >= 50.0f) {
                        zoomStops = new Float[]{5.0f, 10.0f, 20.0f, 30.0f, 50.0f};
                    } else {
                        zoomStops = new Float[]{5.0f, 10.0f, 20.0f, 30.0f};
                    }
                    param.setResult(zoomStops);
                }
            });
            XposedBridge.log(TAG + "Successfully hooked j1() for pro telephoto zoom stops");
        } catch (Throwable t) {
            XposedBridge.log(TAG + "Failed to hook j1: " + t.getMessage());
        }

        // 3. Hook c8() - Leica CV Style (Vibrant & Authentic)
        try {
            XposedHelpers.findAndHookMethod(targetClass, "c8", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Preferences.isForceLeicaEnabled()) {
                        param.setResult(true);
                    }
                }
            });
            XposedBridge.log(TAG + "Successfully hooked c8() for Leica CV style");
        } catch (Throwable t) {
            // Optional method, ignore if not present
        }

        // 4. Hook f7() / D5() - Leica watermark and dual styles
        try {
            XposedHelpers.findAndHookMethod(targetClass, "f7", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Preferences.isForceLeicaEnabled()) {
                        param.setResult(true);
                    }
                }
            });
        } catch (Throwable ignored) {}

        try {
            XposedHelpers.findAndHookMethod(targetClass, "D5", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (Preferences.isForceLeicaEnabled()) {
                        param.setResult(true);
                    }
                }
            });
        } catch (Throwable ignored) {}
    }
}
