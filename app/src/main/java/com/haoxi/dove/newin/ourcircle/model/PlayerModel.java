package com.haoxi.dove.newin.ourcircle.model;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.newin.bean.PlayerBean;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/3/29.
 */

public class PlayerModel extends BaseModel implements IGetModel<PlayerBean> {

    private static final String TAG = "AttentionModel";


    public PlayerModel() {
    }

    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<PlayerBean> requestCallback) {
        ourNewService.getPlayerInfo(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<PlayerBean, Boolean>() {
                    @Override
                    public Boolean call(PlayerBean playerBean) {

                        int codes = playerBean.getCode();

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
                        return 200 == playerBean.getCode();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<PlayerBean>(requestCallback));
    }
}
