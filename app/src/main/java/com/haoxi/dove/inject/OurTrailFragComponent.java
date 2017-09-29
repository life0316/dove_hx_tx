package com.haoxi.dove.inject;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.traject.OurTrailFragment;

import dagger.Component;
@PerFragment
@Component(dependencies = AppComponent.class,modules = OurTrailFragMoudle.class)
public interface OurTrailFragComponent {
    void inject(OurTrailFragment activity);
}
