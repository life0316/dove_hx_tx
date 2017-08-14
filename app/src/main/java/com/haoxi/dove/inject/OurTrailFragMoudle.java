package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.TraAdpter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.presenters.OurFragPresenter;
import com.haoxi.dove.modules.mvp.presenters.SetTriPresenter2;
import com.haoxi.dove.modules.mvp.presenters.TraFragPresenter;
import com.haoxi.dove.modules.traject.OurTrailFragment;
import com.haoxi.dove.newin.ourcircle.presenter.StartFlyPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class OurTrailFragMoudle {

    private OurTrailFragment mView;

    private Context mContext;


    public OurTrailFragMoudle(Context mContext, OurTrailFragment mView){
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
    public MyPigeonPresenter providePigeonPresenter(){
        return new MyPigeonPresenter(mView);
    }

    @PerFragment
    @Provides
    public TraFragPresenter provideTraFragPresenter(){
        return new TraFragPresenter(mView);
    }

    @PerFragment
    @Provides
    public StartFlyPresenter provideStartFlyPresenter(){
        return new StartFlyPresenter(mView);
    }

    @PerFragment
    @Provides
    public SetTriPresenter2 provideSetTriPresenter(){
        return new SetTriPresenter2(mView);
    }

    @PerFragment
    @Provides
    public TraAdpter provideTraAdapter(){
        return new TraAdpter(mContext);
    }

    @PerFragment
    @Provides
    public OurFragPresenter provideOurFragPresenter(){
        return new OurFragPresenter(mView);
    }


}
