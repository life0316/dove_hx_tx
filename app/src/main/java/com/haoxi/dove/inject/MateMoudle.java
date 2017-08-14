package com.haoxi.dove.inject;

import android.content.Context;

import com.haoxi.dove.adapter.MatePigeonAdapter;
import com.haoxi.dove.adapter.MatePigeonsAdapter;
import com.haoxi.dove.inject.scopes.PerFragment;
import com.haoxi.dove.modules.mvp.presenters.MyPigeonPresenter;
import com.haoxi.dove.modules.mvp.presenters.MyRingPresenter;
import com.haoxi.dove.modules.pigeon.MateActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class MateMoudle {

    private MateActivity mView;
    private Context mContext;


    public MateMoudle(Context mContext, MateActivity mView){
        this.mContext = mContext;
        this.mView = mView;
    }

    @PerFragment
    @Provides
    public MyPigeonPresenter provideMainPresenter(){
        return new MyPigeonPresenter(mView);
    }

    @PerFragment
    @Provides
    public OurCodePresenter provideOurCodePresenter(){
        return new OurCodePresenter(mView);
    }

    @PerFragment
    @Provides
    public MyRingPresenter provideRingPresenter(){
        return new MyRingPresenter(mView);
    }

    @PerFragment
    @Provides
    public MatePigeonsAdapter providePigeonsAdapter(){
        return new MatePigeonsAdapter(mContext);
    }

    @PerFragment
    @Provides
    public MatePigeonAdapter providePigeonAdapter(){
        return new MatePigeonAdapter(mContext);
    }



}
