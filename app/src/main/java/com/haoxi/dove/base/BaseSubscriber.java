package com.haoxi.dove.base;

import android.text.TextUtils;
import android.util.Log;

import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.utils.ApiUtils;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import rx.Subscriber;

/**
 * Created by lifei on 2017/3/29.
 */

public class BaseSubscriber<T> extends Subscriber<T> {

    private RequestCallback<T> mRequestCallBack;

    public BaseSubscriber(RequestCallback<T> callback) {

        mRequestCallBack = callback;
    }


//    @Override
//    public void onStart() {
//        super.onStart();
//        if (mRequestCallBack != null) {
//            mRequestCallBack.beforeRequest();
//        }
//    }


    @Override
    public void onCompleted() {
        if (mRequestCallBack != null) {
            mRequestCallBack.requestComplete();

        }
    }

    @Override
    public void onError(Throwable e) {
        if (mRequestCallBack != null) {
            String errorMsg = null;
            //TODO 返回网络异常、数据错误等异常

            Log.e("errorMsg", e.toString() + "-------e");

            if (e instanceof ConnectException) {

                if (!ApiUtils.isNetworkConnected(MyApplication.getContext())) {

                    errorMsg = "当前网络不可用，请检测您的网络";

                } else {
                    errorMsg = "网络连接超时";
                }

            } else if (e instanceof IllegalStateException) {
                Log.e("errorMsg", e.toString() + "-------600");
                errorMsg = "600";
            } else if (e instanceof SocketTimeoutException) {
                errorMsg = "连接超时";
            } else {

                errorMsg = e.getMessage();
                Log.e("errorMsg", e.toString());
                errorMsg = "";
            }
            if (!TextUtils.equals(errorMsg, "")) {
                mRequestCallBack.requestError(errorMsg);
            }
        }
    }

    @Override
    public void onNext(T t) {

        if (mRequestCallBack != null) {
            mRequestCallBack.requestSuccess(t);
        }
    }
}
