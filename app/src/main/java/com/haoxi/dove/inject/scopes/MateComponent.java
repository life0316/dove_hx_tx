package com.haoxi.dove.inject.scopes;

import com.haoxi.dove.inject.AppComponent;
import com.haoxi.dove.inject.MateMoudle;
import com.haoxi.dove.modules.pigeon.MateActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = MateMoudle.class)
public interface MateComponent {
    void inject(MateActivity activity);
}
