package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.RingEdAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.modules.home.MyRingActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class MyRing2Moudle {

    private MyRingActivity mView;

    private Context mContext;


    public MyRing2Moudle(Context mContext, MyRingActivity mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public MyRingPresenter provideMainPresenter(){
        return new MyRingPresenter(mView);
    }

    @Provides
    public RingEdAdapter provideRingAdapter(){
        return new RingEdAdapter(mContext);
    }

}
