package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.MyDoveAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.home.RouteDoveFragment;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class RouteDFMoudle {

    private RouteDoveFragment mView;
    private Context mContext;


    public RouteDFMoudle(Context mContext, RouteDoveFragment mView){
        this.mContext = mContext;
        this.mView = mView;
    }

    @PerFragment
    @Provides
    public MyPigeonPresenter provideMainPresenter(){
        return new MyPigeonPresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView);
    }

    @Provides
    public MyDoveAdapter provideAdapter(){
        return new MyDoveAdapter(mContext);
    }

}
