package com.haoxi.dove.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @创建者 Administrator
 * @创建时间 2016/10/9 13:12
 * @描述
 */
public class ApiUtils {

    private static Toast mToast;


    private static long lastClickTime;

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }


    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        if ( time - lastClickTime < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }



    public interface SimpleCallback {
        void onComplete(String result);
    }

    /**
     * 检查权限
     * @param needPermissions
     */
    public static boolean checkPermissions(Activity activity,int requestCode,String... needPermissions) {

        List<String> needRequestPermissonList = findDeniedPermissions(activity,needPermissions);

//        Log.e("location",needRequestPermissonList.size()+"----d---允许----d");

        if (null != needRequestPermissonList&& needRequestPermissonList.size() > 0 ){
            ActivityCompat.requestPermissions(activity,needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]),requestCode);
            return true;
        }

        return false;
    }

    /**
     *
     * 获取权限集中需要申请权限的列表
     *
     * @param needPermissions
     * @return
     */
    public static List<String> findDeniedPermissions(Activity context,String[] needPermissions) {

        List<String> needRequestPermissonList = new ArrayList<>();
        for (String perm : needPermissions){

            if (ContextCompat.checkSelfPermission(context,perm) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.shouldShowRequestPermissionRationale(context,perm)){

                needRequestPermissonList.add(perm);
            }
        }
        return needRequestPermissonList;
    }
    /**
     * 检测是否有的权限已经授权
     * @param grantResults
     * @return
     */
    public static boolean verifyPermissions(int[] grantResults){

        for (int result:grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }




    /**
     * 连接服务器，查看是否有最新版本
     */
    public static void updataVer(final Activity activity) {


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("APP_TYPE", "APK");

        String strUrl = jsonObject.toString().replace("\"", "%22").replace("{", "%7b").replace("}", "%7d");
    }


    private static void installApk(Activity activity, String filename, int INSTALL_REQUESTCODE) {

        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File(filename)),"application/vnd.android.package-archive");
        activity.startActivityForResult(intent,INSTALL_REQUESTCODE);

    }

    /**
     * 获取不同型号手机的相册路径
     * @param context
     * @param uri
     * @return
     */
    public static String getAbsolutePath(final Context context, final Uri uri){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context,uri)){
            if (isExternalStorageDocument(uri)){
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)){
                    return Environment.getExternalStorageDirectory()+"/"+split[1];
                }

            }else if (isDownloadsDocument(uri)){
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context,contentUri,null,null);
            }// MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }


        return null;
    }


    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * 获取应用程序名称
     * @param context
     * @return
     */
    public static String getAppName(Context context){

        int labelRes = getPackageInfo(context).applicationInfo.labelRes;
        String appName = context.getResources().getString(labelRes);
        return appName;

    }


    /**
     * 获取版本名
     * @param context
     * @return
     */
    public static String getVersionName(Context context){

        return getPackageInfo(context).versionName;
    }

    public static int getVersionCode(Context context){
        return getPackageInfo(context).versionCode;
    }


    /**
     * 获取版本信息
     * @param context
     * @return
     */
    private static PackageInfo getPackageInfo(Context context) {

        PackageInfo info = null;

        PackageManager pm = context.getPackageManager();
        try {
            info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);

            return info;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return info;
    }


    /**
     * 获取设备的mac地址
     *
     * @param ac
     * @param callback
     *            成功获取到mac地址之后会回调此方法
     */
    public static void getMacAddress(final Activity ac, final SimpleCallback callback) {
        final WifiManager wm = (WifiManager) ac.getApplicationContext() .getSystemService(Service.WIFI_SERVICE);

        // 如果本次开机后打开过WIFI，则能够直接获取到mac信息。立刻返回数据。
        WifiInfo info = wm.getConnectionInfo();
        if (info != null && info.getMacAddress() != null) {
            if (callback != null) {
                callback.onComplete(info.getMacAddress());
            }
            return;
        }

        // 尝试打开WIFI，并获取mac地址
        if (!wm.isWifiEnabled()) {
            wm.setWifiEnabled(true);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                int tryCount = 0;
                final int MAX_COUNT = 10;

                while (tryCount < MAX_COUNT) {
                    final WifiInfo info = wm.getConnectionInfo();
                    if (info != null && info.getMacAddress() != null) {
                        if (callback != null) {
                            ac.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callback.onComplete(info.getMacAddress());
                                }
                            });
                        }
                        return;
                    }

                    SystemClock.sleep(300);
                    tryCount++;
                }

                // 未获取到mac地址
                if (callback != null) {
                    callback.onComplete(null);
                }
            }
        }).start();
    }


    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context){

        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }

        return false;
    }


    public static boolean isWifiConnected(Context context){

        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                return info.isAvailable();
            }
        }

        return false;
    }


    public static boolean isMobileConnected(Context context){
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                return info.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context){

        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null&&networkInfo.isAvailable()) {
                return networkInfo.getType();
            }
        }

        return -1;
    }

    public static String getConnectedTypeName(Context context){

        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            if (networkInfo != null&&networkInfo.isAvailable()) {
                return networkInfo.getTypeName();
            }
        }

        return "";
    }

    public static void toNetSetting(Context context){

        Intent intent1 = null;

        if(android.os.Build.VERSION.SDK_INT>10){
            intent1 = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        }else{
            intent1 = new Intent();
            ComponentName component = new ComponentName("com.android.settings","com.android.settings.WirelessSettings");
            intent1.setComponent(component);
            intent1.setAction("android.intent.action.VIEW");
        }

        context.startActivity(intent1);

    }



    public static void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    public static String getPros(String key){

        String ret;

        try {
            Method getRet = Class.forName("android.os.SystemProperties").getMethod("get",String.class);

            if ((ret = (String)getRet.invoke(null,key))!=null){
                return ret;
            }


        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return "";
    }

    public static String getLocalMacAddress() {
        String Mac=null;
        try{

            String path="sys/class/net/wlan0/address";
            if((new File(path)).exists())
            {
                FileInputStream fis = new FileInputStream(path);
                byte[] buffer = new byte[8192];
                int byteCount = fis.read(buffer);
                if(byteCount>0)
                {
                    Mac = new String(buffer, 0, byteCount, "utf-8");
                }
            }
            if(Mac==null||Mac.length()==0)
            {
                path="sys/class/net/eth0/address";
                FileInputStream fis_name = new FileInputStream(path);
                byte[] buffer_name = new byte[8192];
                int byteCount_name = fis_name.read(buffer_name);
                if(byteCount_name>0)
                {
                    Mac = new String(buffer_name, 0, byteCount_name, "utf-8");
                }
            }

            if(Mac.length()==0||Mac==null){
                return "";
            }
        }catch(Exception io){
        }

        Log.e("macaddress",Mac+"----mac");

        return Mac.trim();
    }

    public static String getMac() {
        String macSerial = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat/sys/class/net/wlan0/address");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            String line;
            while ((line = input.readLine()) != null) {
                macSerial += line.trim();
            }

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return macSerial;
    }

    public static Toast showToast(Context context, String content){

        if (mToast == null){
            mToast = Toast.makeText(context,content, Toast.LENGTH_SHORT);
        }else{
            mToast.setText(content);
        }
        mToast.show();
        return mToast;
    }

    private static double pi = 3.14159265358979324;
    private static double a = 6378245.0;
    private static  double ee = 0.00669342162296594323;


    public static LatLng transform(double wglat,double wglon){

        double mgLat;
        double mgLon;


        if (outOfChina(wglat,wglon)){

            mgLat = wglat;
            mgLon = wglon;
            return new LatLng(mgLat,mgLon);
        }

        double dLat = transformLat(wglon - 105.0,wglat - 35.0);
        double dLon = transformLon(wglon - 105.0,wglat - 35.0);

        double radLat = wglat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;

        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0)/((a * (1-ee))/(magic * sqrtMagic)*pi);
        dLon = (dLon * 180.0)/(a/sqrtMagic * Math.cos(radLat)*pi);

        mgLat = wglat + dLat;
        mgLon = wglon + dLon;


        return new LatLng(mgLat,mgLon);
    }

    private static double transformLon(double x, double y) {

        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;


    }

    private static double transformLat(double x, double y) {

        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;

    }

    private static boolean outOfChina(double lat, double lon) {

        if (lon < 72.004 || lon > 137.8347)
            return true;

        if (lat < 0.8293 || lat > 55.8271)
        return true;
        return false;
    }

    /**
     * unicode解码（unicode编码转中文）
     *
     * @param theString
     * @return
     */
    public static String unicodeDecode(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);

                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - '0';
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 'a';
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 'A';
                                break;
                            default:
                                throw new IllegalArgumentException(
                                        "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }



    public static int secsBetween2(String startTime,String endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try {
            calendar.setTime(format.parse(startTime));
            time1 = calendar.getTimeInMillis();
            calendar.setTime(format.parse(endTime));
            time2 = calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long betweens_sec = (time2 - time1)/1000;

        return Integer.parseInt(String.valueOf(betweens_sec));

    }
    public static int secsBetween(String startTime,String endTime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Calendar calendar = Calendar.getInstance();
        long time1 = 0;
        long time2 = 0;

        try {
            calendar.setTime(format.parse(startTime));
            time1 = calendar.getTimeInMillis();
            calendar.setTime(format.parse(endTime));
            time2 = calendar.getTimeInMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long betweens_sec = (time2 - time1)/1000;

        return Integer.parseInt(String.valueOf(betweens_sec));

    }

    public static String formatTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        String hms = format.format(time * 1000 - TimeZone.getDefault().getRawOffset());

        return hms;
    }


    public static int getMonth(String startTime,String endTime){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);


            if (startDate.after(endDate)) {
                Date t = startDate;
                startDate = endDate;
                endDate = t;
            }


            Calendar startCalender = Calendar.getInstance();
            startCalender.setTime(startDate);
            Calendar endCalender = Calendar.getInstance();
            endCalender.setTime(endDate);

            Calendar temp = Calendar.getInstance();
            temp.setTime(endDate);
            temp.add(Calendar.DATE,1);

            int year = endCalender.get(Calendar.YEAR) - startCalender.get(Calendar.YEAR);
            int month = endCalender.get(Calendar.MONTH) - startCalender.get(Calendar.MONTH);

            if ((startCalender.get(Calendar.DATE) == Calendar.SUNDAY) && (temp.get(Calendar.DATE) == Calendar.SUNDAY)){

                return year * 12 + month + 1;
            }
            else if ((startCalender.get(Calendar.DATE) != Calendar.SUNDAY) && (temp.get(Calendar.DATE) == Calendar.SUNDAY)){
                return year * 12 + month;
            }
            else if ((startCalender.get(Calendar.DATE) == Calendar.SUNDAY) && (temp.get(Calendar.DATE) != Calendar.SUNDAY)){
                return year * 12 + month;
            }else {
                return (year * 12 + month - 1) < 0?0:(year *12 + month);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;

    }

    public static boolean careTime(String timeOne,String timeTwo){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date oneDate = dateFormat.parse(timeOne);
            Date twoDate = dateFormat.parse(timeTwo);

            return oneDate.getTime() <= twoDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void setWindowStatusBarColor(Activity activity,int colorResId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(colorResId));
        }
    }

}
