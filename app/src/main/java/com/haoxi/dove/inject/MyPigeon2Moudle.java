package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.PigeonEdAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.home.MyPigeonActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class MyPigeon2Moudle {

    private MyPigeonActivity mView1;
    private Context mContext;


    public MyPigeon2Moudle(Context mContext, MyPigeonActivity mView){
        this.mContext = mContext;
        this.mView1 = mView;
    }

    @PerFragment
    @Provides
    public MyPigeonPresenter provideApigeonPresenter(){
        return new MyPigeonPresenter(mView1);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView1);
    }
    @Provides
    public PigeonEdAdapter provideEdAdapter(){
        return new PigeonEdAdapter(mContext);
    }



}
