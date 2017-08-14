package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.ourcircle.ui.AttenFriendActivity;
import com.haoxi.dove.newin.ourcircle.presenter.AttentionPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class AttentionMoudle {

    private AttenFriendActivity mView;

    private Context mContext;


    public AttentionMoudle(Context mContext, AttenFriendActivity mView){
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
    public AttentionPresenter provideMainPresenter(){
        return new AttentionPresenter(mView);
    }


}
