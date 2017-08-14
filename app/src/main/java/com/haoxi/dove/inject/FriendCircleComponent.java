package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.ourcircle.ui.FriendCircleFragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = FriendCircleMoudle.class)
public interface FriendCircleComponent {
    void inject(FriendCircleFragment circleFragment);
}
