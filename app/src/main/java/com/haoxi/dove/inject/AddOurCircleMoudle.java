package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.PersonalPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.ourcircle.ui.ReleaseCircleActivity;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class AddOurCircleMoudle {

    private ReleaseCircleActivity mView;

    private Context mContext;


    public AddOurCircleMoudle(Context mContext, ReleaseCircleActivity mView){
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
    public PersonalPresenter provideMainPresenter(){
        return new PersonalPresenter(mContext,mView);
    }


}
