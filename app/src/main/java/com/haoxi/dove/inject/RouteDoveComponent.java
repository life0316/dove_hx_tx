package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.ui.RouteDoveActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = RouteDoveMoudle.class)
public interface RouteDoveComponent {
    void inject(RouteDoveActivity activity);
}
