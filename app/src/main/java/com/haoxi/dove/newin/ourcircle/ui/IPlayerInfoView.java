package com.haoxi.dove.newin.ourcircle.ui;

import com.haoxi.dove.base.MvpView;

/**
 * Created by Administrator on 2017\6\30 0030.
 */

public interface IPlayerInfoView<T> extends MvpView{

    String getPlayerId();

    void updatePlayerInfo(T obj);
}
