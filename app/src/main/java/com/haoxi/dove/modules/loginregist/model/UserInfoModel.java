package com.haoxi.dove.modules.loginregist.model;

import android.content.Context;
import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurUserInfo;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2016/12/23.
 */

public class UserInfoModel extends BaseModel implements ILoginModel<OurUserInfo> {

    private static final String TAG = "UserInfoModel";

    private Context mContext;

    public UserInfoModel(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<OurUserInfo> requestCallback) {
        ourNewService.getDetailInfo(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurUserInfo, Boolean>() {
                    @Override
                    public Boolean call(OurUserInfo userInfo) {

                        int codes = userInfo.getCode();

                        Log.e(TAG,codes+"----codes");

                        Log.e(TAG,userInfo.getMsg()+"----msg");
                        if (userInfo.getData() != null) {
                            Log.e(TAG,userInfo.getData().getUserid()+"----Userid");
                            Log.e(TAG,userInfo.getData().getHeadpic()+"----headpic");
                            Log.e(TAG,userInfo.getData().getNickname()+"----nickname");
                            Log.e(TAG,userInfo.getData().getAge()+"----age");
                            Log.e(TAG,userInfo.getData().getGender()+"----gender");
                            Log.e(TAG,userInfo.getData().getLoftname()+"----loftname");

                            Log.e(TAG,userInfo.getData().getTelephone()+"----telephone");
                            Log.e(TAG,userInfo.getData().getExperience()+"----Experience");
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
                                requestCallback.requestError("登录失败");
                                break;

                        }
                        return 200 == userInfo.getCode();
                    }
                })
                .subscribe(new BaseSubscriber<OurUserInfo>(requestCallback));
    }
}