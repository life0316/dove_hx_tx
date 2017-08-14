package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.RealFlyBean;
import com.haoxi.dove.bean.RealFlyBeanDao;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.views.ITrajectoryView;
import com.haoxi.dove.utils.LogToFile;
import com.haoxi.dove.utils.TraComparator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class TraPresenter extends BasePresenter<ITrajectoryView, List<RealFlyBean>> implements IGetPresenter {

    private static String TAG = "TraPresenter";

    private boolean isRefrash;

    private IGetModel trajectoryModel;

    private static final String TYPE_FROM_DAO = "FROM_DAO";
    private static final String TYPE_FROM_NETS = "FROM_NETS";

    private String type_from = TYPE_FROM_NETS;


    @Override
    public void requestSuccess(List<RealFlyBean> realFlyBeanList) {
        super.requestSuccess(realFlyBeanList);

        Log.e(TAG, realFlyBeanList.size() + "-------"+TAG);
        Log.e(TAG, type_from + "-------"+TAG);

        if (realFlyBeanList.size() == 0) {
            //getMvpView().onFailed("无飞行记录");
        } else {

            Collections.sort(realFlyBeanList, new TraComparator());

            getMvpView().trailFromDao(realFlyBeanList);

            for (int i = 0; i < realFlyBeanList.size(); i++) {
                Log.e(TAG,"这是获取的实时轨迹点的时间  " + realFlyBeanList.get(i).getGENERATE_TIME());

                switch (type_from) {
                    case TYPE_FROM_NETS:
                        //getMvpView().toDrawTrail(realFlyBeanList);
                        LogToFile.e(TAG,"这是获取的实时轨迹点的时间  从网络获取" + realFlyBeanList.get(i).getGENERATE_TIME(),1);
                        break;
                    case TYPE_FROM_DAO:
                        LogToFile.e(TAG,"这是获取的实时轨迹点的时间  从数据库中获取" + realFlyBeanList.get(i).getGENERATE_TIME(),1);

                        break;
                }
            }
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);

        Log.e(TAG,"获取实时轨迹的出现的error： " + msg);

        if ("600".equals(msg)) {
            getMvpView().onFailed("无信鸽飞行记录");
        } else if ("没有网络".equals(msg) || "连接超时".equals(msg)){
            getMvpView().toHandler();
        }
    }


    @Override
    public void getDataFromNets(Map<String, String> map) {

        checkViewAttached();

        if (isRefrash) {
            getMvpView().showProgress();
        }

        type_from = TYPE_FROM_NETS;

        trajectoryModel.getDatasFromNets(map,this);

    }

    public void getDatasFromDao(String userObjId, String pigeonObjId, String ringObjId) {

        type_from = TYPE_FROM_DAO;

        MyApplication.getDaoSession().getRealFlyBeanDao()
                .queryBuilder()
                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId),
                        RealFlyBeanDao.Properties.PIGEON_OBJ_ID.eq(pigeonObjId))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<RealFlyBean>>(this));
    }

    public void removeDatasFromDao(String userObjId, String pigeonObjId) {

        MyApplication.getDaoSession().getRealFlyBeanDao()
                .queryBuilder()
                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId),
                        RealFlyBeanDao.Properties.PIGEON_OBJ_ID.eq(pigeonObjId))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<RealFlyBean>>() {
                    @Override
                    public void call(List<RealFlyBean> list) {
                        MyApplication.getDaoSession().getRealFlyBeanDao().deleteInTx(list);
                    }
                });
    }

}
