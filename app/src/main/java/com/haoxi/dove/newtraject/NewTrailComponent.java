package com.haoxi.dove.newtraject;

import com.haoxi.dove.inject.AppComponent;
import com.haoxi.dove.inject.OurTrailFragMoudle;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.traject.OurTrailFragment;

import dagger.Component;

@PerFragment
@Component(dependencies = AppComponent.class,modules = NewTrailMoudle.class)
public interface NewTrailComponent {
    void inject(NewTrailFragment activity);
}
