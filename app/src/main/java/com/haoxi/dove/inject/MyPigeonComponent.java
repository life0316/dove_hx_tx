package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.pigeon.MyPigeonFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = MyPigeonMoudle.class)
public interface MyPigeonComponent {
    void inject(MyPigeonFragment myPigeonFragment);
}
