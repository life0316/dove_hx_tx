package com.haoxi.dove.modules.mvp.models;

import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.bean.SetTriBeanDao;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.OurDoveBean;
import com.haoxi.dove.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PigeonModel extends BaseModel implements IGetModel<OurDoveBean> {
    private static final String TAG = "PigeonModel";
    @Override
    public void getDatasFromNets(Map<String, String> map,final RequestCallback<OurDoveBean> requestCallback) {
                ourNewService.searchDove(map)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                requestCallback.beforeRequest();
                            }
                        }).observeOn(AndroidSchedulers.mainThread())
                        .filter(new Func1<OurDoveBean, Boolean>() {
                            @Override
                            public Boolean call(OurDoveBean user) {
                                int codes = user.getCode();
                                if (user.getData() != null) {
                                    for (int i = 0; i < user.getData().size(); i++) {
                                        Log.e(TAG,user.getData().get(i).getDoveid()+"----doveid");
                                        Log.e(TAG,user.getData().get(i).getRing_code()+"----ringcode");
                                        Log.e(TAG,user.getData().get(i).getFly_status()+"----Fly_status");
                                        Log.e(TAG,user.getData().get(i).getFly_recordid()+"----Fly_recordid");
                                    }
                                }
                                if (codes != 200){
                                    requestCallback.requestError(user.getMsg());
                                }
                                return 200 == user.getCode();
                            }
                        })
                .doOnNext(new Action1<OurDoveBean>() {
                    @Override
                    public void call(OurDoveBean ourDoveBean) {
                        MyApplication.getMyBaseApplication().getmPigeonCodes().clear();
                        List<SetTriBean> setTriBeanList = new ArrayList<>();
                        List<InnerDoveData> innerDoveDatas = ourDoveBean.data;
                        for (int i = 0; i < innerDoveDatas.size(); i++) {
                            final InnerDoveData innerDoveData = innerDoveDatas.get(i);
                            if (!"".equals(innerDoveData.getRing_code()) && innerDoveData.getRing_code() != null) {
                                if ("true".equals(innerDoveData.getFly_status()) || innerDoveData.getFly_status()) {
                                    List<SetTriBean> triBeanList = MyApplication.getDaoSession().getSetTriBeanDao()
                                            .queryBuilder()
                                            .where(SetTriBeanDao.Properties.USER_OBJ_ID.eq(innerDoveData.getPlayerid()),
                                                    SetTriBeanDao.Properties.OBJ_ID.eq(innerDoveData.getDoveid()))
                                            .list();

                                    if (triBeanList.size() == 0){
                                        SetTriBean setTriBean = new SetTriBean();
                                        setTriBean.setUSER_OBJ_ID(innerDoveData.getPlayerid());
                                        setTriBean.setOBJ_ID(innerDoveData.getDoveid());
                                        setTriBean.setIsFlying(1);
                                        setTriBean.setTrilPic(SpUtils.getInt(MyApplication.getContext(),"pic",0));
                                        setTriBean.setTrilWidth(SpUtils.getInt(MyApplication.getContext(),"thickness",10));
                                        setTriBean.setTrilColor(SpUtils.getString(MyApplication.getContext(),"color","#00ff00"));
                                        setTriBeanList.add(setTriBean);
                                    }
                                }
                            }
                            MyApplication.getDaoSession().getInnerDoveDataDao().insertOrReplaceInTx(innerDoveData);
                        }
                        if (setTriBeanList.size() != 0) {
                            MyApplication.getDaoSession().getSetTriBeanDao().insertOrReplaceInTx(setTriBeanList);
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}
