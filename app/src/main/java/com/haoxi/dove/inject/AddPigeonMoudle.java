package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.AddPigeonPresenter;
import com.haoxi.dove.modules.pigeon.AddPigeonActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class AddPigeonMoudle {

    private AddPigeonActivity mView;

    private Context mContext;


    public AddPigeonMoudle(Context mContext, AddPigeonActivity mView){
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
    public AddPigeonPresenter provideAddPigeonPresenter(){
        return new AddPigeonPresenter(mView,mContext);
    }

}
