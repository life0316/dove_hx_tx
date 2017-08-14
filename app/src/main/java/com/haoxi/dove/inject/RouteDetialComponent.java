package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.ui.RouteDetail2Activity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = RouteDetailMoudle.class)
public interface RouteDetialComponent {
    void inject(RouteDetail2Activity activity);
}
