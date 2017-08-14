package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.PersonalPresenter;
import com.haoxi.dove.modules.home.PersonalActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class PersonalMoudle {

    private PersonalActivity mView;

    private Context mContext;


    public PersonalMoudle(Context mContext, PersonalActivity mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public PersonalPresenter provideMainPresenter(){
        return new PersonalPresenter(mContext,mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideCodePresenter(){
        return new OurCodePresenter(mView);
    }

}
