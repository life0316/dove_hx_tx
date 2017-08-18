package com.haoxi.dove.base;

import com.haoxi.dove.retrofit.RegistService.IOurNewService;
import com.haoxi.dove.retrofit.RetrofitManager;

import retrofit2.Retrofit;

/**
 * Created by lifei on 2016/12/23.
 */

public class BaseModel {

    public RetrofitManager retrofitManager;
    protected final Retrofit retrofit;
    protected IOurNewService adNewService;
//
    protected IOurNewService ourNewService;
    protected IOurNewService ourNewService2;

    protected final Retrofit outRetrofit;
    protected final Retrofit outRetrofit2;

    public BaseModel() {

        //初始化 retrofit
        retrofitManager = RetrofitManager.builder();

        retrofit = retrofitManager.getAdRetrofit();
        adNewService = retrofitManager.getAdNewService();

        outRetrofit = retrofitManager.getOurRetrofit();
        outRetrofit2 = retrofitManager.getOurRetrofit();

        ourNewService = retrofitManager.getOurNewService();
        ourNewService2 = retrofitManager.getOurNewService2();

    }
}


