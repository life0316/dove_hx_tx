package com.haoxi.dove.newin.ourcircle.model;

import android.content.Context;
import android.util.Log;

import com.haoxi.dove.base.BaseModel;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.RequestCallback;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBeanDao;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class OurCircleModel extends BaseModel implements ICirlcModel<CircleBean> {

    private static final String TAG = "OurCircleModel";

    private Context mContext;

    @Override
    public void getRouteDatasFromNet(final Map<String,String> map, final RequestCallback<CircleBean> requestCallback) {

        ourNewService.getFriendCircles(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CircleBean, Boolean>() {
                    @Override
                    public Boolean call(CircleBean myDynamicBean) {

                        Log.e(TAG,myDynamicBean.getMsg()+"======="+myDynamicBean.getCode());
                        switch (myDynamicBean.getCode()){
                            case 500:
                                requestCallback.requestError("600");
                                break;
                        }

                        return myDynamicBean.getCode() == 200;
                    }
                })
                .doOnNext(new Action1<CircleBean>() {
                    @Override
                    public void call(CircleBean circleBean) {
                        for (final InnerCircleBean innerCircle: circleBean.getData()) {
                            MyApplication.getDaoSession().getInnerCircleBeanDao()
                                    .queryBuilder().where(InnerCircleBeanDao
                                            .Properties.Playerid.eq(innerCircle.getPlayerid())
                                    ,InnerCircleBeanDao.Properties.Userid.eq(innerCircle.getUserid())
                                    ,InnerCircleBeanDao.Properties.Circleid.eq(innerCircle.getCircleid())
                            )
                                    .rx().list()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<InnerCircleBean>>() {
                                        @Override
                                        public void call(List<InnerCircleBean> innerCircleBeans) {

                                            Log.e("fasdfefasdf",innerCircleBeans.size()+"-------");

                                            if (innerCircleBeans.size() > 0){
                                                Log.e("",innerCircleBeans.get(0).getId()+"-------id");
                                                innerCircle.setId(innerCircleBeans.get(0).getId());
                                                innerCircle.setIs_friend(true);
                                                MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircle);
                                            }else {
                                                MyApplication.getDaoSession().getInnerCircleBeanDao()
                                                        .insertOrReplaceInTx(innerCircle);
                                            }
                                        }
                                    });
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));

    }

    @Override
    public void getMyCircleDatas(Map<String, String> map, RequestCallback<CircleBean> requestCallback) {

        ourNewService.getSingleFriendCircles(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CircleBean, Boolean>() {
                    @Override
                    public Boolean call(CircleBean myDynamicBean) {

                        return myDynamicBean.getCode() == 200;
                    }
                })
                .doOnNext(new Action1<CircleBean>() {
                    @Override
                    public void call(CircleBean circleBean) {

                        for (final InnerCircleBean innerCircle: circleBean.getData()) {
                            MyApplication.getDaoSession().getInnerCircleBeanDao()
                                    .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(innerCircle.getPlayerid())
                                    ,InnerCircleBeanDao.Properties.Userid.eq(innerCircle.getUserid())
                                    ,InnerCircleBeanDao.Properties.Circleid.eq(innerCircle.getCircleid())
                            )
                                    .rx().list()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<InnerCircleBean>>() {
                                        @Override
                                        public void call(List<InnerCircleBean> innerCircleBeans) {

                                            Log.e("fasdfefasdf",innerCircleBeans.size()+"-------");

                                            if (innerCircleBeans.size() > 0){
                                                Log.e("",innerCircleBeans.get(0).getId()+"-------id");
                                                innerCircle.setId(innerCircleBeans.get(0).getId());
                                                innerCircle.setIs_friend(true);
                                                MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircle);
                                            }else {
                                                MyApplication.getDaoSession().getInnerCircleBeanDao()
                                                        .insertOrReplaceInTx(innerCircle);
                                            }
                                        }
                                    });
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }

    @Override
    public void getAllCircleDatas(Map<String, String> map, RequestCallback<CircleBean> requestCallback) {

        ourNewService.getAllCircles(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<CircleBean, Boolean>() {
                    @Override
                    public Boolean call(CircleBean myDynamicBean) {

                        Log.e(TAG,myDynamicBean.getMsg()+"======="+myDynamicBean.getCode());

                        return myDynamicBean.getCode() == 200;
                    }
                })
                .doOnNext(new Action1<CircleBean>() {
                    @Override
                    public void call(CircleBean circleBean) {

                        for (final InnerCircleBean innerCircle: circleBean.getData()) {
                            MyApplication.getDaoSession().getInnerCircleBeanDao()
                                    .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(innerCircle.getPlayerid())
                                    ,InnerCircleBeanDao.Properties.Userid.eq(innerCircle.getUserid())
                                    ,InnerCircleBeanDao.Properties.Circleid.eq(innerCircle.getCircleid())
                            )
                                    .rx().list()
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action1<List<InnerCircleBean>>() {
                                        @Override
                                        public void call(List<InnerCircleBean> innerCircleBeans) {

                                            Log.e("fasdfefasdf",innerCircleBeans.size()+"-------");

                                            if (innerCircleBeans.size() > 0){
                                                Log.e("",innerCircleBeans.get(0).getId()+"-------id");
                                                innerCircle.setId(innerCircleBeans.get(0).getId());

                                                MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircle);
                                            }else {
                                                MyApplication.getDaoSession().getInnerCircleBeanDao()
                                                        .insertOrReplaceInTx(innerCircle);
                                            }
                                        }
                                    });
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<>(requestCallback));
    }
}
