package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.MyRingActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = MyRing2Moudle.class)
public interface MyRing2Component {
    void inject(MyRingActivity myRingActivity);
}
