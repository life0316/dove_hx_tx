package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.views.IGetRingView;
import com.haoxi.dove.modules.mvp.models.RingModel;
import com.haoxi.dove.newin.bean.InnerRing;
import com.haoxi.dove.newin.bean.InnerRingDao;
import com.haoxi.dove.newin.bean.OurRingBean;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lifei on 2017/3/30.
 */

public class MyRingPresenter extends BasePresenter<IGetRingView,OurRingBean> implements IGetPresenter {

    private static String TAG = "MyRingPresenter";

    private IGetModel ringModel;

    private boolean isRefrash;

    private String type = "nets";

    public MyRingPresenter(IGetRingView mView) {
        attachView(mView);
        ringModel = new RingModel();
    }

    public void getDatas() {

        getDatasFromDao(getMvpView().getUserObjId());
    }

    @Override
    public void beforeRequest() {
        if (!isRefrash) {
            super.beforeRequest();
        }
    }

    @Override
    public void requestSuccess(OurRingBean ringData) {
        super.requestSuccess(ringData);

        Log.e(TAG,type+"------type");
        Log.e(TAG,ringData.getMsg()+"------msg");
        Log.e(TAG,ringData.getData().size()+"------data");

        if (isRefrash) {
            getMvpView().setRefrash(false);
            isRefrash = false;
//            getMvpView().showErrorMsg("刷新成功");
        }
        getMvpView().setRingData(ringData.getData());

    }

    @Override
    public void requestComplete() {
        super.requestComplete();
        if (isRefrash) {
            getMvpView().setRefrash(false);
            isRefrash = false;
//            getMvpView().showErrorMsg("刷新成功");
        }
    }

    @Override
    public void requestError(String msg) {
       super.requestError(msg);

        switch (msg){
            case "600":
                getMvpView().setRingData(null);
                break;
            case "网络连接超时":
                getDatas();
                break;
        }

        if (isRefrash) {
            isRefrash = false;
        }
    }


    public void deleteDatasFromData(String userObjId){

        MyApplication.getDaoSession().getInnerRingDao()
                .queryBuilder().where(InnerRingDao.Properties.Playerid.eq(userObjId))
                .rx().list()
                .subscribe(new Action1<List<InnerRing>>() {
                    @Override
                    public void call(List<InnerRing> innerRings) {
                        MyApplication.getDaoSession().getInnerRingDao().deleteInTx(innerRings);
                    }
                });
    }

    public void getDatasRefrash(Map<String,String> map) {
        isRefrash = true;
        getDataFromNets(map);
    }

    private void getDatasFromDao(String userObjId) {

        type = "dao";

        MyApplication.getDaoSession().getInnerRingDao()
                .queryBuilder().where(InnerRingDao.Properties.Playerid.eq(userObjId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerRing>>() {
                    @Override
                    public void call(List<InnerRing> innerRings) {

                        OurRingBean ourRingBean = new OurRingBean();
                        ourRingBean.setMsg("从数据库中获取");
                        ourRingBean.setData(innerRings);

                        requestSuccess(ourRingBean);
                    }
                });
    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        type = "nets";

        ringModel.getDatasFromNets(map,this);
    }
}
