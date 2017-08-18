package com.haoxi.dove.retrofit.ad;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;

import java.util.Map;

/**
 * Created by Administrator on 2017\8\18 0018.
 */

public class OpenAdPresenter extends BasePresenter<IAdView,AdviewResObj> {

    private OpenAdModel adModel;

    public OpenAdPresenter(IAdView mView){
        adModel = new OpenAdModel();
        attachView(mView);
    }

    public void getOpenAd(Map<String,Object> map){
        adModel.getDatasFromNets(map,this);
    }

    @Override
    public void requestSuccess(AdviewResObj data) {
        super.requestSuccess(data);


        Log.e("faebppapap",data.getRes()+"-----"+data.getAd().size());
        getMvpView().setOpenAd(data);

    }
}
