package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.RouteTitleAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.trail.ui.RouteTitleActivity;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class RouteTitleMoudle {

    private RouteTitleActivity mView;

    private Context mContext;


    public RouteTitleMoudle(Context mContext, RouteTitleActivity mView){
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
    public RouteTitleAdapter provideAdapter(){
        return new RouteTitleAdapter(mView);
    }

}
