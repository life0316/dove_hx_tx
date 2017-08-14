package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.RealFlyBean;
import com.haoxi.dove.bean.RealFlyBeanDao;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.models.TraFragModel;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.modules.traject.OurTrailFragment;
import com.haoxi.dove.newin.bean.InnerRouteBean;
import com.haoxi.dove.newin.bean.OurRouteBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class TraFragPresenter extends BasePresenter<ITraFragView,OurRouteBean> implements IGetPresenter {

    private static String TAG = "TraFragPresenter";

    private IGetModel traFragModel;

    private static final String TYPE_FROM_DAO = "FROM_DAO";
    private static final String TYPE_FROM_NETS = "FROM_NETS";

    private String type_from = TYPE_FROM_NETS;


    public TraFragPresenter(OurTrailFragment mView) {

        attachView(mView);

        traFragModel = new TraFragModel();
    }
    @Override
    public void requestSuccess(OurRouteBean ourRouteBean) {
        super.requestSuccess(ourRouteBean);

        Log.e(TAG, ourRouteBean.getData().size() + "----获取飞行轨迹数据---" + TAG);
        Log.e(TAG, type_from + "-----从哪里获取的数据-----" + TAG);

        List<InnerRouteBean> innerRouteBeanList = ourRouteBean.getData();

        if (innerRouteBeanList.size() == 0) {
            //getMvpView().onFailed("无飞行记录");

            getMvpView().trailFromDao(innerRouteBeanList,type_from);

        } else {

//            Collections.sort(realFlyBeanList, new TraComparator());

            getMvpView().trailFromDao(innerRouteBeanList,type_from);

            StringBuffer stringBuffer = new StringBuffer();

//            for (int i = 0; i < realFlyBeanList.size(); i++) {
//
//                RealFlyBean realFlyBean = realFlyBeanList.get(i);
//
//
//                stringBuffer.append(type_from + "--");
//                stringBuffer.append(realFlyBean.getOBJ_ID()+"--")
//                        .append(realFlyBean.getPIGEON_OBJ_ID()+"--")
//                        .append(realFlyBean.getGENERATE_TIME()+"--")
//                        .append(realFlyBean.getLATITUDE()+"--")
//                        .append(realFlyBean.getLONGITUDE()+"\n");
//            }
//
//            LogToFile.e(TAG,stringBuffer.toString(),1);
        }
    }

    @Override
    public void  getDataFromNets(Map<String, String> map) {

        type_from = TYPE_FROM_NETS;

        checkViewAttached();

        traFragModel.getDatasFromNets(map,this);
    }

    @Override
    public void requestError(String msg) {
//        super.requestError(msg);

        getMvpView().onFailed(msg);
    }

    //    public void getDatasFromDao(String userObjId, String pigeonObjId, String ringObjId) {
//        type_from = TYPE_FROM_DAO;
//
//        MyApplication.getDaoSession().getRealFlyBeanDao()
//                .queryBuilder()
//                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId))
//                .rx().list()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscriber<List<RealFlyBean>>(this));
//    }

//    public void getDatasFromDao2(String userObjId) {
//        type_from = TYPE_FROM_DAO;
//
//        MyApplication.getDaoSession().getRealFlyBeanDao()
//                .queryBuilder()
//                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId))
//                .rx().list()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseSubscriber<OurRouteBean>(this));
//    }

    public void deleteAllFromDao() {
        MyApplication.getDaoSession().getRealFlyBeanDao().deleteAll();
    }

    public void deleteFromDaoList(ArrayList<RealFlyBean> list) {
        MyApplication.getDaoSession().getRealFlyBeanDao().deleteInTx(list);
    }
    public void deleteFromDaoFromId(String userObjId,String pigeonObjId) {
        MyApplication.getDaoSession().getRealFlyBeanDao()
                .queryBuilder()
                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId)
                ,RealFlyBeanDao.Properties.PIGEON_OBJ_ID.eq(pigeonObjId))
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
    public void deleteAllFromDaoById(String userObjId) {
        MyApplication.getDaoSession().getRealFlyBeanDao()
                .queryBuilder()
                .where(RealFlyBeanDao.Properties.USER_OBJ_ID.eq(userObjId))
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
