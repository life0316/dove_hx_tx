package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.MyDoveAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.trail.presenter.RouteTitlePresenter;
import com.haoxi.dove.newin.trail.ui.RouteDetail2Activity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class RouteDetailMoudle {

    private RouteDetail2Activity mView;

    private Context mContext;


    public RouteDetailMoudle(Context mContext, RouteDetail2Activity mView){
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
    public MyDoveAdapter provideAddMyDoveAdapter(){
        return new MyDoveAdapter(mView);
    }

}
