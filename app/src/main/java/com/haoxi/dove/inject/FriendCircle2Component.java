package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.ourcircle.ui.FriendCircle2Fragment;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = FriendCircle2Moudle.class)
public interface FriendCircle2Component {
    void inject(FriendCircle2Fragment circleFragment);
}
