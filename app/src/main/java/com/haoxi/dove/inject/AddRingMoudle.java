package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.pigeon.AddRingActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class AddRingMoudle {

    private AddRingActivity mView;

    private Context mContext;


    public AddRingMoudle(Context mContext, AddRingActivity mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView);
    }

}
