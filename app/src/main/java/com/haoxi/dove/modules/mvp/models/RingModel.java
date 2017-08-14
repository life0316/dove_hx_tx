package com.haoxi.dove.modules.mvp.models;

import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.OurRingBean;

import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/3/30.
 */

public class RingModel extends BaseModel implements IGetModel<OurRingBean> {

    private static String TAG = "RingModel";

    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<OurRingBean> requestCallback) {

        ourNewService.searchRing(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        requestCallback.beforeRequest();
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<OurRingBean, Boolean>() {
                    @Override
                    public Boolean call(OurRingBean user) {

                        int codes = user.getCode();
                        Log.e(TAG,codes+"----codes");
                        Log.e(TAG,user.getMsg()+"----msg");
                        if (user.getData() != null) {
                            for (int i = 0; i < user.getData().size(); i++) {
//                                Log.e(TAG,user.getData().get(i).getRingid()+"----ringid");
//                                Log.e(TAG,user.getData().get(i).getRing_code()+"----ringcode");
//                                Log.e(TAG,user.getData().get(i).getPlayerid()+"----ringcode");
                                Log.e(TAG,user.getData().get(i).getPlayerid()+"----ringcode");
                            }
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
                        return 200 == user.getCode();
                    }
                })
                .doOnNext(new Action1<OurRingBean>() {
                    @Override
                        public void call(OurRingBean ourRingBean) {

//                        MyApplication.getDaoSession().getMyRingBeanDao().deleteAll();
//
//                        for (int i = 0; i < myRingBeen.size(); i++) {
//
//                            Log.e("myRingBeen", myRingBeen.get(i).getRING_CODE());
//
//                            MyRingBean myRingBean = myRingBeen.get(i);
//                            myRingBean.setUSER_OBJ_ID(userObjId);
//
//                            MyApplication.getDaoSession().getMyRingBeanDao().insertOrReplace(myRingBean);
//                        }

                        MyApplication.getDaoSession().getInnerRingDao()
                                .insertOrReplaceInTx(ourRingBean.getData());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<OurRingBean>(requestCallback));

    }
}
