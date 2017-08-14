package com.haoxi.dove.modules.loginregist.model;

import android.content.Context;
import android.util.Log;

import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.newin.bean.OurUser;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2016/12/23.
 */

public class LoginModel extends BaseModel implements ILoginModel<OurUser> {

    private static final String TAG = "LoginModel";

    private Context mContext;

    public LoginModel(Context mContext){
        this.mContext = mContext;
    }


    @Override
    public void getDatasFromNets(Map<String,String> map,final RequestCallback<OurUser> requestCallback) {

        ourNewService.getOurLogin(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurUser, Boolean>() {
                    @Override
                    public Boolean call(OurUser user) {

                        int codes = user.getCode();

                        Log.e(TAG,codes+"----codes");

                        Log.e(TAG,user.getMsg()+"----msg");
                        if (user.getData() != null) {
                            Log.e(TAG,user.getData().getUserid()+"----Userid");
                        }

                        switch (codes){
                            case 3012:
                                requestCallback.requestError("手机号码格式错误");
                                break;
                            case 3013:
                                requestCallback.requestError("密码错误");
                                break;
                            case 3014:
                                requestCallback.requestError("验证码请求单日达到上限");
                                break;
                            case 3015:
                                requestCallback.requestError("验证码验证失败");
                                break;
                            case 3016:
                                requestCallback.requestError("该手机号已被注册");
                                break;
                            case 3017:
                                requestCallback.requestError(" 用户名或密码不正确");
                                break;
                        }
                        return 200 == user.getCode();
                    }
                })
                .subscribe(new BaseSubscriber<OurUser>(requestCallback));
    }

}