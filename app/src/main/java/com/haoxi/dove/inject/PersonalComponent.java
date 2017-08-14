package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.PersonalActivity;

import dagger.Component;

/**
 * Created by lifei on 2017/3/30.
 */
@PerFragment
@Component(dependencies = AppComponent.class,modules = PersonalMoudle.class)
public interface PersonalComponent {
    void inject(PersonalActivity activity);
}
