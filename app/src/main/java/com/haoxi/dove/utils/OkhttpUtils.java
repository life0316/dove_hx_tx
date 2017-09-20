package com.haoxi.dove.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.google.gson.internal.$Gson$Types;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Description : OkHttp网络连接封装工具类
 * Created by david jin on 2016/4/27.
 */
public class OkhttpUtils {
    public interface ReqProgressCallBack<T>{
        /**
         * 响应进度更新
         */
        void onProgress(long totla, long current);
    }

    private static final String TAG = "OkHttpUtils";

    private static final MediaType JSON2 = MediaType.parse("application/json;charset=utf-8");

    private static OkhttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private Handler mHandler;

    private OkhttpUtils() {
        /**
         * 构建OkHttpClient
         */
        mOkHttpClient = new OkHttpClient();
        /**
         * 设置连接的超时时间
         */
        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        /**
         * 设置响应的超时时间
         */
        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
        /**
         * 请求的超时时间
         */
        mOkHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
        /**
         * 允许使用Cookie
         */
        mOkHttpClient.setCookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ORIGINAL_SERVER));
        /**
         * 获取主线程的handler
         */
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 通过单例模式构造对象
     * @return OkHttpUtils
     */
    private synchronized static OkhttpUtils getmInstance() {
        if (mInstance == null) {
            mInstance = new OkhttpUtils();
        }
        return mInstance;
    }

    /**
     * 构造Get请求
     * @param url  请求的url
     * @param callback  结果回调的方法
     */
    private void getRequest(String url, final ResultCallback callback) {
        final Request request = new Request.Builder().url(url).build();
        deliveryResult(callback, request);
    }

    /**
     * 构造post 请求
     * @param url 请求的url
     * @param callback 结果回调的方法
     * @param params 请求参数
     */
    private void postRequest(String url, final ResultCallback callback, List<Param> params) {
        Request request = buildPostRequest(url, params);
        deliveryResult(callback, request);
    }

    private void postJsonRequest(String url, ResultCallback callback, String json) {


        RequestBody requestBody = RequestBody.create(JSON2, json);

        Log.e("requestBody", "requestBody---json---"+json);
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Log.e("requestBody", "request------"+request.toString());
                deliveryResult(callback, request);

    }

    /**
     * 处理请求结果的回调
     * @param callback
     * @param request
     */

    String string;
    private void deliveryResult(final ResultCallback callback, final Request request) {

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                sendFailCallback(callback, e);
            }

            @Override
            public void onResponse(Response response) throws IOException {


                Object obj =null;

                try {
                    string = response.body().string();
                    if (callback.mType == String.class) {
                        sendSuccessCallBack(callback, string);
                    } else {

                        String str = string.substring(0,1);

                        if ("[".equals(str)){
                            obj = JSON.parseArray(string);
                        }else{

                            obj = JSON.parseObject(string);
                        }


                        sendSuccessCallBack(callback, obj);
                    }
                } catch (final Exception e) {
                    sendFailCallback(callback, e);
                }

            }
        });
    }

    /**
     * 发送失败的回调
     * @param callback
     * @param e
     */
    private void sendFailCallback(final ResultCallback callback, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * 发送成功的调
     * @param callback
     * @param obj
     */
    private void sendSuccessCallBack(final ResultCallback callback, final Object obj) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (callback != null) {
                    callback.onSuccess(obj);
                }
            }
        });
    }


    /**
     * 异步下载文件
     * @param url
     * @param destFileDir
     * @param callback
     */
    private void _downloadAsyn(final String url, final String destFileDir, final ResultCallback callback, final ReqProgressCallBack progressCallBack){

        final Request request = new Request.Builder().url(url).build();
        final Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                sendFailStringCallback(request,callback,e);
            }

            @Override
            public void onResponse(Response response) throws IOException {

                long total = response.body().contentLength();


                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;

                long current = 0;
                try{

                    is = response.body().byteStream();


                    File file = new File(destFileDir,"pigeon.apk");
                    fos = new FileOutputStream(file);

                    while ((len = is.read(buf))!= -1){
                        current += len;
                        fos.write(buf,0,len);

                        progressCallBack(total,current,progressCallBack);
                    }

                    fos.flush();

                    //如果文件下载成功，第一个参数为文件的绝对路径
                    sendSuccessCallBack(callback,file.getAbsolutePath());


                }catch (IOException e){

                    sendFailStringCallback(request,callback,e);

                }finally {
                    try{
                        if (is != null){
                            is.close();
                        }
                        if (fos != null){
                            fos.close();
                        }
                    }catch (IOException e){

                    }
                }
            }
        });

    }

    private void progressCallBack(long total, long current, ReqProgressCallBack progressCallBack) {

        progressCallBack.onProgress(total,current);

    }

    private String getFileName(String url) {

        int separatorIndex = url.lastIndexOf("/");
        return (separatorIndex < 0)?url : url.substring(separatorIndex+1,url.length());


    }

    private void sendFailStringCallback(Request request, ResultCallback callback, IOException e) {

    }


    /**
     * 构造post请求
     * @param url  请求url
     * @param params 请求的参数
     * @return 返回 Request
     */
    private Request buildPostRequest(String url, List<Param> params) {
        FormEncodingBuilder builder = new FormEncodingBuilder();
        for (Param param : params) {
            builder.add(param.key, param.value);
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }


    /**********************对外接口************************/

    /**
     * get请求
     * @param url  请求url
     * @param callback  请求回调
     */
    public static void get(String url, ResultCallback callback) {
        Log.e("ddddd", "postJson---json---"+url);
        getmInstance().getRequest(url, callback);
    }

    /**
     * post请求
     * @param url       请求url
     * @param callback  请求回调
     * @param params    请求参数
     */
    public static void post(String url, final ResultCallback callback, List<Param> params) {
        getmInstance().postRequest(url, callback, params);
    }


    public static void postJson(String url, final ResultCallback callback, String json){
        Log.e("ddddd", "postJson---json---"+json);
        getmInstance().postJsonRequest(url, callback, json);
    }

    public static void downloadAsyn(String url, String destDir, ResultCallback callback, ReqProgressCallBack reqProgressCallBack){
        getmInstance()._downloadAsyn(url,destDir,callback,reqProgressCallBack);
    }



    /**
     * http请求回调类,回调方法在UI线程中执行
     * @param <T>
     */
    public static abstract class ResultCallback<T> {

        Type mType;

        public ResultCallback(){
            //mType = getSuperclassTypeParameter(getClass());
        }

        static Type getSuperclassTypeParameter(Class<?> subclass) {
            Type superclass = subclass.getGenericSuperclass();//返回父类的类型
            if (superclass instanceof Class) {
                throw new RuntimeException("Missing type parameter.");
            }
            ParameterizedType parameterized = (ParameterizedType) superclass;
            return $Gson$Types.canonicalize(parameterized.getActualTypeArguments()[0]);
        }

        /**
         * 请求成功回调
         * @param response
         */
        public abstract void onSuccess(T response);

        /**
         * 请求失败回调
         * @param e
         */
        public abstract void onFailure(Exception e);
    }

    /**
     * post请求参数类
     */
    public static class Param {

        String key;//请求的参数
        String value;//参数的值

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public Param(Map<String,Object> map){
            for (String key:map.keySet()){
                this.key = key;
                this.value = (String)map.get(key);
            }
        }

    }


}
