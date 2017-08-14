package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.newin.ourcircle.presenter.EachCirclePresenter;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCommentPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.ourcircle.ui.EarchCircleActivity;
import com.haoxi.dove.newin.ourcircle.presenter.FabPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class OurCommentMoudle {

    private EarchCircleActivity mView;

    private Context mContext;


    public OurCommentMoudle(Context mContext, EarchCircleActivity mView){
        this.mView = mView;
        this.mContext = mContext;
    }

    @PerFragment
    @Provides
    public InnerCommentPresenter provideDynamicPresenter(){
        return new InnerCommentPresenter(mView);
    }
    @PerFragment
    @Provides
    public EachCirclePresenter provideEachPresenter(){
        return new EachCirclePresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideCodePresenter(){
        return new OurCodePresenter(mView);
    }

    @PerFragment
    @Provides
    public FabPresenter providePresenter(){
        return new FabPresenter(mView);
    }

}
