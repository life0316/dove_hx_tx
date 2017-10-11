package com.haoxi.dove.newtraject;

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

@Module
public class NewTrailMoudle {
    private NewTrailFragment mView;
    private Context mContext;
    public NewTrailMoudle(Context mContext, NewTrailFragment mView){
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
    public NewTraPresenter provideTraPresenter(){
        return new NewTraPresenter(mView);
    }

    @PerFragment
    @Provides
    public NewSetTriPresenter provideSetTriPresenter(){
        return new NewSetTriPresenter(mView);
    }
    @PerFragment
    @Provides
    public NewStartPresenter provideStartFlyPresenter(){
        return new NewStartPresenter(mView);
    }

    @PerFragment
    @Provides
    public TraAdpter provideTraAdapter(){
        return new TraAdpter(mContext);
    }

}
