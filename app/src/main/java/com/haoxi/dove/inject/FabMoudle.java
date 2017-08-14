package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.ourcircle.ui.FabActivity;
import com.haoxi.dove.newin.ourcircle.presenter.FabPresenter;
import com.haoxi.dove.newin.ourcircle.presenter.PlayerPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class FabMoudle {

    private FabActivity mView;

    private Context mContext;


    public FabMoudle(Context mContext, FabActivity mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public FabPresenter provideFabPresenter(){
        return new FabPresenter(mView);
    }
    @PerFragment
    @Provides
    public PlayerPresenter providePresenter(){
        return new PlayerPresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideCodePresenter(){
        return new OurCodePresenter(mView);
    }
}
