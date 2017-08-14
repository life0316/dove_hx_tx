package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.RouteDoveFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = RouteDFMoudle.class)
public interface RouteDFComponent {
    void inject(RouteDoveFragment fragment);
}
