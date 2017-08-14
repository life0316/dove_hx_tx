package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.modules.mvp.models.IGetModel;
import com.haoxi.dove.modules.mvp.views.IRingInfoView;
import com.haoxi.dove.modules.mvp.models.PigeonModel;
import com.haoxi.dove.modules.pigeon.RingInfoActivity;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.bean.InnerDoveDataDao;
import com.haoxi.dove.newin.bean.OurDoveBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * @创建者 Administrator
 * @创建时间 2017/1/20 10:33
 * @描述
 */
public class RingInfoPresenter extends BasePresenter<IRingInfoView,OurDoveBean> implements IGetPresenter {

    private IGetModel pigeonModel;

    private String type = "nets";

    public RingInfoPresenter(RingInfoActivity activity){

        attachView(activity);
        pigeonModel = new PigeonModel();
    }

    public void getDatasFromDao(String userObjId){
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

    @Override
    public void getDataFromNets(Map<String,String> map) {

        //这里是从网络中获取
        pigeonModel.getDatasFromNets(map,this);
    }

    @Override
    public void requestSuccess(OurDoveBean data) {
        super.requestSuccess(data);

        List<InnerDoveData> doveDatas = new ArrayList<>();

        for (int i = 0; i < data.getData().size(); i++) {
            InnerDoveData doveData = data.getData().get(i);

            if (doveData.getRingid() != null) {
                Log.e("matedfa",doveData.getRingid()+"-------getRingid");
            }

            if (doveData.getRingid() == null || "".equals(doveData) || "-1".equals(doveData)) {
                continue;
            }else {
                doveDatas.add(doveData);
            }
        }
        getMvpView().toSetData(doveDatas);
    }
}
