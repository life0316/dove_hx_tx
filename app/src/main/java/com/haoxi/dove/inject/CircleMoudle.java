package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.ourcircle.ui.CircleFragment;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class CircleMoudle {

    private CircleFragment mView;

    private Context mContext;


    public CircleMoudle(Context mContext, CircleFragment mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public InnerCirclePresenter provideDynamicPresenter(){
        return new InnerCirclePresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideCodePresenter(){
        return new OurCodePresenter(mView);
    }

}
