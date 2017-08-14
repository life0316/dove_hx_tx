package com.haoxi.dove.modules.mvp.presenters;

/**
 * Created by lifei on 2017/4/5.
 */

public interface ISetTriPresenter2 {


    void getTriSetFromDao(String userObjId,String pigeonObjId, int isFly, int mTrailPic, String mTrailColor, int mTrailWidth);
    void getTriSetFromDao2(String userObjId,String pigeonObjId, int isFly, int mTrailPic, String mTrailColor, int mTrailWidth,boolean isOver);


    void getDaoWithObjId(String userObjId);
}
