package com.haoxi.dove.inject;

import com.haoxi.dove.acts.MainActivity;
import com.haoxi.dove.inject.scopes.PerActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/29.
 */

@PerActivity
@Component(dependencies = AppComponent.class,modules = MainMoudle.class)
public interface MainComponent {
    void inject(MainActivity mainActivity);
}
