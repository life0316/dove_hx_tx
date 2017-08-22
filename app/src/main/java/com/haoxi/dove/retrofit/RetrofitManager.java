package com.haoxi.dove.retrofit;

import com.haoxi.dove.retrofit.RegistService.IOurNewService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * 网络调用的管理类
 *
 *
 * Created by lifei on 2016/12/23.
 */

public class RetrofitManager {

//    private static final String BASE_OUR_URL = "http://118.178.227.194/";
    private static final String BASE_OUR_URL = "http://111.231.54.111/";
    private static final String BASE_AD_URL = "http://open.adview.cn";


    private static IOurNewService ourNewService;
    private static IOurNewService ourNewService2;
    private static IOurNewService adNewService;


    private static Retrofit ourRetrofit;
    private static Retrofit ourRetrofit2;
    private static Retrofit adRetrofit;

    private RetrofitManager() {

    }

    public static void initRetrofit(){
        Gson gson = new GsonBuilder()
                .setLenient().create();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();
        OkHttpClient okHttpClient2 = new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(100, TimeUnit.SECONDS)
                .build();



        ourRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_OUR_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ourRetrofit2 = new Retrofit.Builder()
                .client(okHttpClient2)
                .baseUrl(BASE_OUR_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        adRetrofit = new Retrofit.Builder()
                .client(okHttpClient2)
                .baseUrl(BASE_AD_URL)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        ourNewService = ourRetrofit.create(IOurNewService.class);
        ourNewService2 = ourRetrofit2.create(IOurNewService.class);
        adNewService = adRetrofit.create(IOurNewService.class);

    }

    public static RetrofitManager builder(){
        return new RetrofitManager();
    }


    public Retrofit getOurRetrofit(){return ourRetrofit;}
    public Retrofit getOurRetrofit2(){return ourRetrofit2;}

    public Retrofit getAdRetrofit() {
        return adRetrofit;
    }

    public IOurNewService getAdNewService() {
        return adNewService;
    }

    public IOurNewService getOurNewService() {
        return ourNewService;
    }

    public static IOurNewService getOurNewService2() {
        return ourNewService2;
    }
}
