package com.haoxi.dove.modules.mvp.models;

import android.os.Environment;
import android.util.Log;
import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.MyProgressCallback;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurVerBean;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AboutModel extends BaseModel implements IAboutModel<OurVerBean> {
    @Override
    public void downloadApk(String apkUrl, final MyProgressCallback progressCallback) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(apkUrl).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @SuppressWarnings("resource")
            @Override
            public void onResponse(Response response) throws IOException {

                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String SDPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                try {
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    progressCallback.onProgressMax(100);
                    File file = new File(SDPath, "dove.apk");
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);

                        progressCallback.onProgressCurrent(progress);
                    }
                    fos.flush();

                    progressCallback.onSuccess(file.getAbsolutePath());


                } catch (Exception e) {

                    progressCallback.onError(e);

                } finally {
                    try {
                        if (is != null)
                            is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();
                    } catch (IOException e) {
                    }
                }
            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {
                progressCallback.onFailure("下载失败");
            }
        });
    }

    @Override
    public void updateVar(Map<String, String> map,final RequestCallback<OurVerBean> requestCallback) {
        ourNewService.getCurrentVersion(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurVerBean, Boolean>() {
                    @Override
                    public Boolean call(OurVerBean codesBean) {
                        Log.e("fadvwasvb",codesBean.getMsg()+"-----"+codesBean.getCode());
                        switch (codesBean.getCode()) {
                            case 500:
                            case 602:
                            case 603:
                                requestCallback.requestError(codesBean.getMsg());
                                break;
                        }
                        return 200 == codesBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}
