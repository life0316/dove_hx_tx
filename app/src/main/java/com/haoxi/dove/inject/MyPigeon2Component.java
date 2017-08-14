package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.MyPigeonActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = MyPigeon2Moudle.class)
public interface MyPigeon2Component {
    void inject(MyPigeonActivity myPigeonActivity);
}
