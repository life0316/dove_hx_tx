package com.haoxi.dove.inject;

import android.content.Context;

//import com.gmax.pigeon.bean.DaoSession;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.utils.RxBus;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by lifei on 2017/3/29.
 */
@Singleton
@Component (modules = AppMoudle.class)
public interface AppComponent {

    Context getContext();
    DaoSession getDaoSession();
    RxBus getRxBus();

}
