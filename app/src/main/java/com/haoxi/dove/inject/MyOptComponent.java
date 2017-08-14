package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.OptimisedActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = OptMoudle.class)
public interface MyOptComponent {
    void inject(OptimisedActivity activity);
}
