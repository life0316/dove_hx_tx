package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.pigeon.AddRing2Activity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = AddRing2Moudle.class)
public interface AddRing2Component {
    void inject(AddRing2Activity activity);
}
