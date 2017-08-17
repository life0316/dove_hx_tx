package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.circle.AllCircleFragment;
import com.haoxi.dove.newin.ourcircle.ui.CircleFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = CircleMoudle.class)
public interface CircleComponent {
    void inject(CircleFragment circleFragment);
    void inject(AllCircleFragment allCircleFragment);
}
