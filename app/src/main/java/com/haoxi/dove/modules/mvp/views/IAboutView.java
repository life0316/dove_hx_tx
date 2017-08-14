package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurVerBean;

/**
 * Created by lifei on 2017/1/14.
 */

public interface IAboutView extends MvpView {
    void toJudgeVer(OurVerBean obj);

    void showProgressDialog();
    void hideProgressDialog();

    void setProgressMax(int max);
    void setCuProgress(int progress);

    void installApk(String path, int install);
}
