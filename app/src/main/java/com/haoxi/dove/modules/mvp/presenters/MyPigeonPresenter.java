package com.haoxi.dove.modules.mvp.presenters;

import android.content.SharedPreferences;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.views.IGetPigeonView;
import com.haoxi.dove.modules.mvp.models.PigeonModel;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerDoveDataDao;
import com.haoxi.dove.newin.bean.OurDoveBean;

import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lifei on 2017/3/30.
 */

public class MyPigeonPresenter extends BasePresenter<IGetPigeonView,OurDoveBean> implements IGetPresenter {

    private static final String TAG = "MyPigeonPresenter";

    private IGetModel pigeonModel;

    private boolean isRefrash;

    private SharedPreferences preferences;

    private String type = "nets";

    public MyPigeonPresenter(IGetPigeonView mView) {
        attachView(mView);

        pigeonModel = new PigeonModel();

    }

    public void getDatas() {

        getDatasFromDao(getMvpView().getUserObjId());
    }

    private void getDatasFromDao(String userObjId) {

        type = "dao";

        MyApplication.getDaoSession().getInnerDoveDataDao()
                .queryBuilder().where(InnerDoveDataDao.Properties.Playerid.eq(userObjId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerDoveData>>() {
                    @Override
                    public void call(List<InnerDoveData> innerDoveData) {

                        OurDoveBean ourDoveBean = new OurDoveBean();
                        ourDoveBean.setMsg("从数据库中获取");
                        ourDoveBean.setData(innerDoveData);

                        requestSuccess(ourDoveBean);
                    }
                });
    }

    public void deleteDatasFromData(String userObjId){

        MyApplication.getDaoSession().getInnerDoveDataDao()
                .queryBuilder().where(InnerDoveDataDao.Properties.Playerid.eq(userObjId))
                .rx().list()
                .subscribe(new Action1<List<InnerDoveData>>() {
                    @Override
                    public void call(List<InnerDoveData> innerDoveData) {
                        MyApplication.getDaoSession().getInnerDoveDataDao().deleteInTx(innerDoveData);
                    }
                });
    }

    @Override
    public void beforeRequest() {
        if (!isRefrash) {
            super.beforeRequest();
        }
    }

    @Override
    public void requestSuccess(OurDoveBean data) {
        super.requestSuccess(data);

        if (isRefrash) {
            getMvpView().setRefrash(false);
            isRefrash = false;
//            getMvpView().showErrorMsg("刷新成功");
        }

        getMvpView().setPigeonData(data.getData());

    }

    public void getDatasRefrash(Map<String,String> map) {

        isRefrash = true;

        getDataFromNets(map);
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
                getMvpView().setPigeonData(null);
                break;
            case "网络连接超时":
                getDatas();
                break;
        }

        if (isRefrash) {
            getMvpView().setRefrash(false);
            isRefrash = false;
        }
    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        type = "nets";

        pigeonModel.getDatasFromNets(map,this);

    }
}
