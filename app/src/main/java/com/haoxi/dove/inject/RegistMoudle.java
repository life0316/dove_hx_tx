package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.loginregist.RegistActivity;
import com.haoxi.dove.modules.loginregist.presenter.RegistPresenter2;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import dagger.Module;
import dagger.Provides;

@Module
public class RegistMoudle {

    private RegistActivity mView;

    private Context mContext;


    public RegistMoudle(Context mContext, RegistActivity mView){
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
    public RegistPresenter2 providePresenter(){
        return new RegistPresenter2(mView);
    }
}
