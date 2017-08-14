package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.FlyStringAdapter;
import com.haoxi.dove.fragments.AllFlyRecordFragment;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class RouteFlyPMoudle {

    private AllFlyRecordFragment mView;

    private Context mContext;


    public RouteFlyPMoudle(Context mContext, AllFlyRecordFragment mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView);
    }

    @PerFragment
    @Provides
    public RouteTitlePresenter provideAddPigeonPresenter(){
        return new RouteTitlePresenter(mView);
    }

    @PerFragment
    @Provides
    public FlyStringAdapter provideAdapter(){
        return new FlyStringAdapter(mContext);
    }

}
