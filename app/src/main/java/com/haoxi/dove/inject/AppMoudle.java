package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.base.MyApplication;
//import com.gmax.pigeon.bean.DaoSession;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.utils.RxBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class AppMoudle {

    private MyApplication myApplication;
    private DaoSession mDaoSession;
    private RxBus mRxBus;

    public AppMoudle(MyApplication myApplication, DaoSession daoSession,RxBus rxBus){

        this.mDaoSession = daoSession;
        this.myApplication = myApplication;
        this.mRxBus = rxBus;
    }

    @Singleton
    @Provides
    public Context provideApplicationContext(){
        return myApplication.getApplicationContext();
    }

    @Singleton
    @Provides
    public RxBus provideRxBus(){
        return mRxBus;
    }

    @Singleton
    @Provides
    public DaoSession provideDaoSession(){
        return mDaoSession;
    }


}
