package com.haoxi.dove.retrofit.ad;

import com.haoxi.dove.base.MvpView;

import java.util.Map;

/**
 * Created by Administrator on 2017\8\18 0018.
 */

public interface IAdView extends MvpView {

    void setOpenAd(AdviewResObj resObj);

    Map<String,Object> getParamsMap();
}
