package com.haoxi.dove.retrofit.ad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class AdUtils {

    public static String getIMEI(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return TextUtils.isEmpty(imei) ? "000000000000000" : imei;
    }

    public static String getOperators(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return TextUtils.isEmpty(tm.getSimOperator()) ? "" : tm.getSimOperator();
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getNetworkType(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return "wifi";
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String subTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
                        // 11
                        return "2g";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
                        // 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
                        // 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
                        // 15
                        return "3g";
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
                        // 13
                        return "4g";
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA") || subTypeName.equalsIgnoreCase("WCDMA")
                                || subTypeName.equalsIgnoreCase("CDMA2000"))
                            return "3g";
                        else
                            return "2g";
                }
            }
        }
        return "";
    }

    public static double getDisplayDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static String getLocalMacAddress(Context context) {
        String mac;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mac = getMachineHardwareAddress();
        } else {
            WifiManager wifi = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
//            mac = info.getMacAddress().replace(":", "");
            mac = info.getMacAddress();
        }

        return mac;
    }

    /**
     * 获取设备的mac地址和IP地址（android6.0以上专用）
     *
     * @return
     */
    public static String getMachineHardwareAddress() {
        Enumeration<NetworkInterface> interfaces = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        String hardWareAddress = null;
        NetworkInterface iF = null;
        while (interfaces.hasMoreElements()) {
            iF = interfaces.nextElement();
            try {
                hardWareAddress = bytesToString(iF.getHardwareAddress());
                if (hardWareAddress == null) continue;
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
        if (iF != null && iF.getName().equals("wlan0")) {
//            hardWareAddress = hardWareAddress.replace(":", "");

        }
        return hardWareAddress;
    }

    /***
     * byte转为String
     * @param bytes
     * @return
     */
    private static String bytesToString(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : bytes) {
            buf.append(String.format("%02X:", b));
        }
        if (buf.length() > 0) {
            buf.deleteCharAt(buf.length() - 1);
        }
        return buf.toString();
    }

    public static Map<String,Object> getParamsMap(Context context,int width,int height, int pt) {
        Map<String,Object> map = new HashMap<>();

        map.put("n", Config.AD_NUM);
        map.put("appid",Config.APP_ID);
//        map.put("pt",Config.BANNER_AD);
        map.put("pt",pt);
        map.put("w",width);
        map.put("h",height);
        map.put("os",Config.ANDORID_OS);
        map.put("bdr", Build.VERSION.RELEASE);
        map.put("tp", Build.MODEL);
        map.put("brd",Build.BRAND);
        map.put("sn", getIMEI(context));
        map.put("nop",getOperators(context));

        map.put("andid",getAndroidId(context));
        map.put("nt",getNetworkType(context));
        map.put("tab",0);
        map.put("tm",Config.DEBUG);
        map.put("pack", ConstantUtils.PACKAGE_NAME);

        map.put("sw",getDisplayWidth(context));
        map.put("sh",getDisplayHeight(context));
        map.put("tp", Build.MODEL);
        map.put("brd",Build.BRAND);
        map.put("deny",getDisplayDensity(context));
        map.put("mc",getLocalMacAddress(context));

        String time = String.valueOf(System.currentTimeMillis());
        map.put("time",time);
        StringBuilder sb = new StringBuilder(Config.APP_ID);
        sb.append(getIMEI(context)).append(Config.ANDORID_OS);
        sb.append(getOperators(context)).append(ConstantUtils.PACKAGE_NAME);
        sb.append(time).append(Config.SECRET_KEY);

        String token = MD5Tools.MD5(String.valueOf(sb));

        map.put("token",token);

        return map;
    }

}