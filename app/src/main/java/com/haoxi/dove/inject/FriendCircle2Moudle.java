package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.ourcircle.ui.FriendCircle2Fragment;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class FriendCircle2Moudle {

    private FriendCircle2Fragment mView;

    private Context mContext;


    public FriendCircle2Moudle(Context mContext, FriendCircle2Fragment mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public InnerCirclePresenter provideDynamicPresenter(){
        return new InnerCirclePresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideCodePresenter(){
        return new OurCodePresenter(mView);
    }

}
