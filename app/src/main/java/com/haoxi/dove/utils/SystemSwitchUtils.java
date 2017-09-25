package com.haoxi.dove.utils;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.util.Log;

public class SystemSwitchUtils {

    private Context context;
    private WifiManager mWifiManager;
    private ConnectivityManager connManager;
    private PowerManager mPowerManager;
    private AudioManager mAudioManager;
    private static Camera camera;
    private Camera.Parameters parameters;


    /**
     * 是否开启了闪光灯
     *
     * @return
     */
    public static boolean isFlashlightOn() {

        boolean openCa = false;


        if (camera == null) {

            try {
                camera = Camera.open();

                camera.release();
                camera = null;

            }catch (Exception e){
                openCa = true;
            }
        }

        return openCa;


//        Parameters parameters = camera.getParameters();
//        String flashMode = parameters.getFlashMode();
//
//        if (flashMode.equals(Parameters.FLASH_MODE_TORCH)) {
//
//            return true;
//        } else {
//            return false;
//        }
    }

    public static void closeCa(){
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }


    /**
     * 闪光灯开关
     */
    public static void flashlightUtils() {
        if (camera == null) {

            Log.e("camera", (camera == null) + "----");

            camera = Camera.open(1);
        }

        Parameters parameters = camera.getParameters();
        // String flashMode = parameters.getFlashMode();

        if (isFlashlightOn()) {

            parameters.setFlashMode(Parameters.FLASH_MODE_OFF);// 关闭
            camera.setParameters(parameters);
            camera.release();
            camera = null;
            //Toast.makeText(context, "关闭手电筒", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 闪光灯开关2
     */
    public void flashUtils() {

        Camera camera = Camera.open();

        Camera.Parameters parameters = camera.getParameters();
        String flashMode = parameters.getFlashMode();
        if (flashMode.equals(Camera.Parameters.FLASH_MODE_TORCH)) {
            camera.stopPreview();
            camera.release();
            camera = null;

        } else {

            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(parameters);
            camera.autoFocus(new Camera.AutoFocusCallback() {
                public void onAutoFocus(boolean success, Camera camera) {
                }
            });
            camera.startPreview();
        }
    }


}
