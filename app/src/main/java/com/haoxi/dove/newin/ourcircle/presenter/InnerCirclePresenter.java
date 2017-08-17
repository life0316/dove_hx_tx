package com.haoxi.dove.newin.ourcircle.presenter;


import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.newin.bean.InnerCircleBeanDao;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.ourcircle.model.ICirlcModel;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.model.OurCircleModel;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.CircleComparator;
import com.haoxi.dove.utils.RxBus;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lifei on 2017/1/17.
 */

public class InnerCirclePresenter extends BasePresenter<IMyCircleView,CircleBean> implements ICirclePresenter {

    private static final String TAG = "MyDynamicPresenter";

    private ICirlcModel circleModel;

    private boolean isRefresh = true;

    private String type = "nets";

    public InnerCirclePresenter(IMyCircleView mView) {

        attachView(mView);
        circleModel = new OurCircleModel();
    }

    @Override
    public void requestSuccess(CircleBean circleBean) {
        super.requestSuccess(circleBean);
        Log.e(TAG,(circleBean.getData() != null)+"------null");
        Log.e(TAG,type+"------type");
        if (circleBean.getData() != null) {
            Log.e(TAG,circleBean.getData().size()+"------size");
            for (int i = 0; i < circleBean.getData().size(); i++) {
                Log.e(TAG,circleBean.getData().get(i).getPlayerid()+"------playerid");
                Log.e(TAG,circleBean.getData().get(i).getUserid()+"-------------userid");
            }
            getMvpView().updateCircleList(circleBean,"",isRefresh ? DataLoadType.TYPE_REFRESH_SUCCESS:DataLoadType.TYPE_LOAD_MORE_SUCCESS);
        }
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        getMvpView().setRefrash(false);
    }

    @Override
    public void refreshFromNets(Map<String,String> map,int tag) {

        isRefresh = true;

        getDatasFromNets(map,tag);

    }

    @Override
    public void loadMoreData(Map<String,String> map,int tag) {

        isRefresh = false;

        getDatasFromNets(map,tag);
    }



    @Override
    public void getDatasFromNets(Map<String,String> map,int tag) {
        type = "nets-----"+tag;
        switch (tag){
            case 0:
                circleModel.getAllCircleDatas(map,this);

                break;
            case 1:
                circleModel.getRouteDatasFromNet(map,this);
                break;
            case 2:
                circleModel.getMyCircleDatas(map,this);
                break;
        }
    }

    @Override
    public void getDatasFromDao(String playerId,String userId,boolean isFriend,int tag) {

        isRefresh = true;

        type = "dao-----"+tag;
        switch (tag){
            case 0:
                getAllCircles(playerId);
                break;
            case 1:
                getFriendCircles(playerId,userId,true);
                break;
            case 2:
                getMyCircles(playerId,userId);
                break;
        }
    }

    private void getMyCircles(String playerId,String userId){
        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Userid.eq(userId)
                , InnerCircleBeanDao.Properties.Playerid.eq(playerId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {
                        CircleBean circleBean = new CircleBean();
                        circleBean.setMsg("从数据库中获取");
                        Collections.sort(innerCircleBeans, new CircleComparator());
                        circleBean.setData(innerCircleBeans);

                        for (int i = 0; i < innerCircleBeans.size(); i++) {
                            Log.e("youwu",innerCircleBeans.get(i).getPlayerid()+"---My--"+innerCircleBeans.get(i).getIs_friend());
                        }


                        requestSuccess(circleBean);
                    }
                });
    }
    private void getFriendCircles(String playerId,String userId,boolean isFriend){
        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Is_friend.eq(isFriend)
                ,InnerCircleBeanDao.Properties.Playerid.eq(playerId)
                ,InnerCircleBeanDao.Properties.Userid.notEq(playerId)
        )
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {
                        CircleBean circleBean = new CircleBean();
                        circleBean.setMsg("从数据库中获取");

                        Collections.sort(innerCircleBeans, new CircleComparator());
                        circleBean.setData(innerCircleBeans);

                        for (int i = 0; i < innerCircleBeans.size(); i++) {
                            Log.e("youwu",innerCircleBeans.get(i).getPlayerid()+"---Friend--"+innerCircleBeans.get(i).getIs_friend());
                        }

                        requestSuccess(circleBean);
                    }
                });
    }
    private void getAllCircles(String playerId){
        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {
                        CircleBean circleBean = new CircleBean();
                        circleBean.setMsg("从数据库中获取");

                        Collections.sort(innerCircleBeans, new CircleComparator());

                        circleBean.setData(innerCircleBeans);

                        for (int i = 0; i < innerCircleBeans.size(); i++) {
                            Log.e("youwu",innerCircleBeans.get(i).getPlayerid()+"---all---"+innerCircleBeans.get(i).getIs_friend());
                        }


                        requestSuccess(circleBean);
                    }
                });
    }

    public void updateCircle(InnerCircleBean innerCircleBean){

        MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBean);

    }

    public void updateCircleBy(String playerId, String userId, final boolean isAttention){

        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
                ,InnerCircleBeanDao.Properties.Userid.eq(userId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {

                        for (InnerCircleBean innerCircleBean: innerCircleBeans) {
                            innerCircleBean.setIs_friend(isAttention);
                            MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBean);
                        }
                        RxBus.getInstance().post("update",true);
                    }
                });
    }

    public void deleteCircle(InnerCircleBean innerCircleBean){
        MyApplication.getDaoSession().getInnerCircleBeanDao().delete(innerCircleBean);
    }

    public void deleteCircleById(Long id){
        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteByKey(id);
    }

    public void deleteCircleBy(String playerId,String userId,String circleId){
        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
                ,InnerCircleBeanDao.Properties.Userid.eq(userId)
                ,InnerCircleBeanDao.Properties.Circleid.eq(circleId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {

                        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteInTx(innerCircleBeans.get(0));
                    }
                });
    }
    public void deleteCircleBy(String playerId,String userId){
        MyApplication.getDaoSession().getInnerCircleBeanDao()
                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
                ,InnerCircleBeanDao.Properties.Userid.eq(userId))
                .rx().list()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<InnerCircleBean>>() {
                    @Override
                    public void call(List<InnerCircleBean> innerCircleBeans) {

                        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteInTx(innerCircleBeans);
                    }
                });
    }

}
