package com.haoxi.dove.inject;

import com.haoxi.dove.acts.MainActivity;

import dagger.Module;

/**
 * Created by lifei on 2017/3/29.
 */

@Module
public class MainMoudle {

    private MainActivity mView;


    public MainMoudle(MainActivity mView){
        this.mView = mView;
    }

}
