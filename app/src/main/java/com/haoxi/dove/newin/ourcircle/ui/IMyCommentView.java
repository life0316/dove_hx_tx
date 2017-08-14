package com.haoxi.dove.newin.ourcircle.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.retrofit.DataLoadType;

/**
 * Created by lifei on 2017/7/2.
 */
public interface IMyCommentView<T> extends MvpView {

    void updateCommentList(T data, String errorMsg, @DataLoadType.DataLoadTypeChecker int type);

}
