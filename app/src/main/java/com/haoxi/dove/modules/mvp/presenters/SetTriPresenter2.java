package com.haoxi.dove.modules.mvp.presenters;


import android.util.Log;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.bean.SetTriBeanDao;
import com.haoxi.dove.modules.mvp.views.ITraFragView;
import com.haoxi.dove.modules.traject.OurTrailFragment;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class SetTriPresenter2 extends BasePresenter<ITraFragView, List<SetTriBean>> implements ISetTriPresenter2 {

    private static final String TYPE_USERID = "USER_OBJ_ID";
    private static final String TYPE_PIGEON_ID = "PIGEON_ID";
    private static final String TYPE_USER_PIGEON = "USER_PIGEON_ID";
    private static final String TYPE_OVER = "TYPE_OVER";

    private String type = TYPE_PIGEON_ID;
    private List<SetTriBean> tempLists = new ArrayList<>();


    private int isFly = 0;
    private int mTrailWidth = 10;
    private String mTrailColor = "#00ff00";
    private int mTrailPic = R.mipmap.icon_img_2;
    private String pigeonObj;
    private String userObjID;

    private boolean isOver;


    public SetTriPresenter2(OurTrailFragment mView) {

        attachView(mView);
    }

    public void clearTemp(){
        tempLists.clear();
    }

    @Override
    public void requestSuccess(List<SetTriBean> setTriBeen) {
        super.requestSuccess(setTriBeen);

        switch (type) {
            case TYPE_USERID:

                typeUserID(setTriBeen);

                break;
            case TYPE_PIGEON_ID:

                typePigeonId(setTriBeen);

                break;
            case TYPE_OVER:

                typeOver(setTriBeen);

                break;
            case TYPE_USER_PIGEON:
                typeUserPigeon(setTriBeen);

                break;
        }

    }

    private void typeOver(List<SetTriBean> setTriBeen) {

        SetTriBean setTriBean;

        Log.e("TYPE_START_FLY",setTriBeen.size()+"------hehe");

        if (setTriBeen.size() != 0) {
            setTriBean = setTriBeen.get(0);

            Log.e("TYPE_START_FLY",setTriBean.getOBJ_ID()+"-----1-hehe");

            if (setTriBean != null) {
                int trilWidth_db = setTriBean.getTrilWidth();
                String trilColor_db = setTriBean.getTrilColor();
                int trilPic_db = setTriBean.getTrilPic();

                int isFly = setTriBean.getIsFlying();

                getMvpView().setTri(setTriBean);

            }
        } else {

            setTriBean = new SetTriBean();

            setTriBean.setUSER_OBJ_ID(userObjID);

            setTriBean.setOBJ_ID(pigeonObj);
            setTriBean.setIsFlying(isFly);
            setTriBean.setTrilPic(mTrailPic);
            setTriBean.setTrilColor(mTrailColor);
            setTriBean.setTrilWidth(mTrailWidth);


            Log.e("TYPE_START_FLY",setTriBean.getOBJ_ID()+"-----0--hehe");
            MyApplication.getDaoSession().getSetTriBeanDao().insertOrReplace(setTriBean);

            getMvpView().setTri(setTriBean);

        }
    }

    private void typeUserPigeon(List<SetTriBean> setTriBeen) {
    }

    private void typePigeonId(List<SetTriBean> setTriBeen) {

        SetTriBean setTriBean;

        if (setTriBeen.size() != 0) {
            setTriBean = setTriBeen.get(0);

            if (setTriBean != null) {
                int trilWidth_db = setTriBean.getTrilWidth();
                String trilColor_db = setTriBean.getTrilColor();
                int trilPic_db = setTriBean.getTrilPic();

                int isFly = setTriBean.getIsFlying();

                getMvpView().setTri(setTriBean);
            }
        } else {

            setTriBean = new SetTriBean();

            setTriBean.setUSER_OBJ_ID(userObjID);

            setTriBean.setOBJ_ID(pigeonObj);
            setTriBean.setIsFlying(isFly);
            setTriBean.setTrilPic(mTrailPic);
            setTriBean.setTrilColor(mTrailColor);
            setTriBean.setTrilWidth(mTrailWidth);

            MyApplication.getDaoSession().getSetTriBeanDao().insertOrReplace(setTriBean);

            getMvpView().setTri(setTriBean);
        }
    }

    private void typeUserID(List<SetTriBean> setTriBeen) {


        Log.e("----setTriBeen",setTriBeen.size()+"----cao");
        getMvpView().setTriMap(setTriBeen);

    }


    @Override
    public void getTriSetFromDao(String userObjId, String pigeonObj, int isFly, int mTrailPic, String mTrailColor, int mTrailWidth) {

        type = TYPE_PIGEON_ID;

        this.isFly = isFly;
        this.mTrailColor = mTrailColor;
        this.mTrailPic = mTrailPic;
        this.mTrailWidth = mTrailWidth;
        this.pigeonObj = pigeonObj;
        this.userObjID = userObjId;


        MyApplication.getDaoSession().getSetTriBeanDao().queryBuilder()
                .where(SetTriBeanDao.Properties.OBJ_ID.eq(pigeonObj))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<SetTriBean>>(this));

    }

    @Override
    public void getTriSetFromDao2(String userObjId, String pigeonObj, int isFly, int mTrailPic, String mTrailColor, int mTrailWidth, boolean isOver) {

        type = TYPE_OVER;

        this.isFly = isFly;
        this.mTrailColor = mTrailColor;
        this.mTrailPic = mTrailPic;
        this.mTrailWidth = mTrailWidth;
        this.pigeonObj = pigeonObj;
        this.userObjID = userObjId;

        this.isOver = isOver;

        MyApplication.getDaoSession().getSetTriBeanDao().queryBuilder()
                .where(SetTriBeanDao.Properties.OBJ_ID.eq(pigeonObj))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<SetTriBean>>(this));

    }

    @Override
    public void getDaoWithObjId(String userObjId) {

        Log.e("----setTriBeen",userObjId+"-----sett");

        type = TYPE_USERID;

        MyApplication.getDaoSession().getSetTriBeanDao().queryBuilder()
                .where(SetTriBeanDao.Properties.USER_OBJ_ID.eq(userObjId))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<SetTriBean>>(this));

    }

    public void updateWityPigeonId(SetTriBean setTriBean) {

        MyApplication.getDaoSession().getSetTriBeanDao().update(setTriBean);
    }

    public void deleteSetTriBean(SetTriBean setTriBean) {
        MyApplication.getDaoSession().getSetTriBeanDao().delete(setTriBean);
    }
    public void deleteAllSetTriBean(String userObjId) {

        MyApplication.getDaoSession().getSetTriBeanDao()
                .queryBuilder()
                .where(SetTriBeanDao.Properties.USER_OBJ_ID.eq(userObjId))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<SetTriBean>>() {
                    @Override
                    public void call(List<SetTriBean> list) {
                        MyApplication.getDaoSession().getSetTriBeanDao().deleteInTx(list);
                    }
                });
    }


}
