package com.haoxi.dove.inject;

import com.haoxi.dove.acts.PigeonActivity;
import com.haoxi.dove.inject.scopes.PerFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = PigeonInfoMoudle.class)
public interface PigeonInfoComponent {
    void inject(PigeonActivity pigeonActivity);
}
