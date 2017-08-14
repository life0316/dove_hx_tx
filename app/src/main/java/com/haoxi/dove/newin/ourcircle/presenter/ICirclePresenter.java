package com.haoxi.dove.newin.ourcircle.presenter;

import java.util.Map;

/**
 * Created by lifei on 2017/4/11.
 */

public interface ICirclePresenter {

    void getDatasFromNets(Map<String,String> map,int tag);

    void getDatasFromDao(String playerId,String userId,boolean isFriend,int tag);

    void refreshFromNets(Map<String,String> map,int tag);

    void loadMoreData(Map<String,String> map,int tag);
}
