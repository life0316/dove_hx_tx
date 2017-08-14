package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.MyRingAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.pigeon.MyRingFragment;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class MyRingMoudle {

    private MyRingFragment mView;

    private Context mContext;


    public MyRingMoudle(Context mContext,MyRingFragment mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public MyRingPresenter provideMainPresenter(){
        return new MyRingPresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView);
    }

    @Provides
    public MyRingAdapter provideRingAdapter(){
        return new MyRingAdapter(mContext);
    }

}
