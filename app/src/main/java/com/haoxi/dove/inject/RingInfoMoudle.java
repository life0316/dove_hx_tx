package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.MatePigeonAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.RingInfoPresenter;
import com.haoxi.dove.modules.pigeon.RingInfoActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class RingInfoMoudle {

    private RingInfoActivity mView;

    private Context mContext;


    public RingInfoMoudle(Context mContext, RingInfoActivity mView){
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
    public MatePigeonAdapter provideInfoAapter(){
        return new MatePigeonAdapter(mView);
    }

    @PerFragment
    @Provides
    public RingInfoPresenter provideInfoPresenter(){
        return new RingInfoPresenter(mView);
    }

}
