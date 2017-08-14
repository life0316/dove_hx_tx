package com.haoxi.dove.modules.mvp.presenters;


import com.haoxi.dove.R;
import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.BaseSubscriber;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.SetTriBean;
import com.haoxi.dove.bean.SetTriBeanDao;
import com.haoxi.dove.modules.mvp.views.ITrajectoryView;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by lifei on 2017/1/17.
 */

public class SetTriPresenter extends BasePresenter<ITrajectoryView,List<SetTriBean>> implements ISetTriPresenter {


    @Override
    public void requestSuccess(List<SetTriBean> setTriBeen) {
        super.requestSuccess(setTriBeen);

        SetTriBean setTriBean ;

        if (setTriBeen.size() != 0) {
            setTriBean = setTriBeen.get(0);

            if (setTriBean != null) {
                int trilWidth_db = setTriBean.getTrilWidth();
                String trilColor_db = setTriBean.getTrilColor();
                int trilPic_db = setTriBean.getTrilPic();

                int isFly = setTriBean.getIsFlying();

                getMvpView().setTri(setTriBean,trilWidth_db,trilColor_db,trilPic_db,isFly);
            }
        } else {

            setTriBean = new SetTriBean();
            setTriBean.setUSER_OBJ_ID(userObjID);
            setTriBean.setRING_OBJ_ID(ringObjId);
            setTriBean.setOBJ_ID(pigeonObj);
            setTriBean.setIsFlying(isFly);
            setTriBean.setTrilPic(mTrailPic);
            setTriBean.setTrilColor(mTrailColor);
            setTriBean.setTrilWidth(mTrailWidth);

            getMvpView().setTri(setTriBean,0,null,0,isFly);
            MyApplication.getDaoSession().getSetTriBeanDao().insertOrReplace(setTriBean);

        }
    }

    private int isFly = 0;
    private int mTrailWidth = 10;
    private String mTrailColor = "#00ff00";
    private int mTrailPic = R.mipmap.icon_img_2;
    private String pigeonObj;
    private String userObjID;
    private String ringObjId;


    @Override
    public void getTriSetFromDao(String userObjId,String pigeonObj,String ringObjId,int isFly,int mTrailPic,String mTrailColor,int mTrailWidth) {

        this.isFly = isFly;
        this.mTrailColor = mTrailColor;
        this.mTrailPic = mTrailPic;
        this.mTrailWidth = mTrailWidth;
        this.pigeonObj = pigeonObj;
        this.userObjID = userObjId;
        this.ringObjId = ringObjId;

        MyApplication.getDaoSession().getSetTriBeanDao().queryBuilder()
                .where(SetTriBeanDao.Properties.OBJ_ID.eq(pigeonObj))
                .rx().list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<SetTriBean>>(this));

    }
}
