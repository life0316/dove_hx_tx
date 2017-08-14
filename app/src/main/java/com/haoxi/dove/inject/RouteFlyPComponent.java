package com.haoxi.dove.inject;

import com.haoxi.dove.fragments.AllFlyRecordFragment;
import com.haoxi.dove.inject.scopes.PerFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = RouteFlyPMoudle.class)
public interface RouteFlyPComponent {
    void inject(AllFlyRecordFragment fragment);
}
