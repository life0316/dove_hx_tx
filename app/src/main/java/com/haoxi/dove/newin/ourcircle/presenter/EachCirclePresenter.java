package com.haoxi.dove.newin.ourcircle.presenter;


import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.modules.circle.IEachView;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.presenters.IGetPresenter;
import com.haoxi.dove.newin.bean.InnerCircleBeanDao;
import com.haoxi.dove.newin.ourcircle.model.EarchCircleModel;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.CircleComparator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by lifei on 2017/1/17.
 */

public class EachCirclePresenter extends BasePresenter<IEachView,EachCircleBean> implements IGetPresenter {

    private static final String TAG = "EachCirclePresenter";

    private IGetModel earchModel;

    private boolean isRefresh = true;

    private String type = "nets";

    public EachCirclePresenter(IEachView mView) {
        attachView(mView);
        earchModel = new EarchCircleModel();
    }
    @Override
    public void beforeRequest(){}
    @Override
    public void requestSuccess(EachCircleBean circleBean) {
        super.requestSuccess(circleBean);

            getMvpView().toUpdateEach(circleBean);
    }

//    private void getMyCircles(String playerId,String userId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Userid.eq(userId)
//                , InnerCircleBeanDao.Properties.Playerid.eq(playerId))
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//                        CircleBean circleBean = new CircleBean();
//                        circleBean.setMsg("从数据库中获取");
//                        Collections.sort(innerCircleBeans, new CircleComparator());
//                        circleBean.setData(innerCircleBeans);
//
////                        requestSuccess(circleBean);
//                    }
//                });
//    }
//    private void getFriendCircles(String playerId,boolean isFriend){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Is_friend.eq(isFriend)
//                ,InnerCircleBeanDao.Properties.Playerid.eq(playerId)
//                ,InnerCircleBeanDao.Properties.Userid.notEq(playerId)
//        )
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//                        CircleBean circleBean = new CircleBean();
//                        circleBean.setMsg("从数据库中获取");
//
//
//                        Collections.sort(innerCircleBeans, new CircleComparator());
//                        circleBean.setData(innerCircleBeans);
//
////                        requestSuccess(circleBean);
//                    }
//                });
//    }
//    private void getAllCircles(String playerId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId))
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//                        CircleBean circleBean = new CircleBean();
//                        circleBean.setMsg("从数据库中获取");
//
//                        Collections.sort(innerCircleBeans, new CircleComparator());
//
//                        circleBean.setData(innerCircleBeans);
//
////                        requestSuccess(circleBean);
//                    }
//                });
//    }
//
//    public void updateCircle(InnerCircleBean innerCircleBean){
//
//        MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBean);
//
//    }
//
//    public void updateCircleBy(String playerId,String userId,String circleId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
//                ,InnerCircleBeanDao.Properties.Userid.eq(userId)
//                ,InnerCircleBeanDao.Properties.Circleid.eq(circleId)
//        )
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//
//                        MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBeans.get(0));
//                    }
//                });
//    }
//
//    public void deleteCircle(InnerCircleBean innerCircleBean){
//        MyApplication.getDaoSession().getInnerCircleBeanDao().delete(innerCircleBean);
//    }
//
//    public void deleteCircleById(Long id){
//        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteByKey(id);
//    }
//
//    public void deleteCircleBy(String playerId,String userId,String circleId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
//                ,InnerCircleBeanDao.Properties.Userid.eq(userId)
//                ,InnerCircleBeanDao.Properties.Circleid.eq(circleId))
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//
//                        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteInTx(innerCircleBeans.get(0));
//                    }
//                });
//    }
//    public void deleteCircleBy(String playerId,String userId){
//        MyApplication.getDaoSession().getInnerCircleBeanDao()
//                .queryBuilder().where(InnerCircleBeanDao.Properties.Playerid.eq(playerId)
//                ,InnerCircleBeanDao.Properties.Userid.eq(userId))
//                .rx().list()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<List<InnerCircleBean>>() {
//                    @Override
//                    public void call(List<InnerCircleBean> innerCircleBeans) {
//
//                        MyApplication.getDaoSession().getInnerCircleBeanDao().deleteInTx(innerCircleBeans);
//                    }
//                });
//    }

    @Override
    public void getDataFromNets(Map<String, String> map) {
        type = "nets";
        earchModel.getDatasFromNets(map,this);
    }
}
