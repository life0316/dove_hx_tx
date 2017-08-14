package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.pigeon.RingInfoActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = RingInfoMoudle.class)
public interface RingInfoComponent {
    void inject(RingInfoActivity activity);
}
