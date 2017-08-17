package com.haoxi.dove.modules.loginregist.ui;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurUserInfo;

/**
 * Created by Administrator on 2017\8\16 0016.
 */

public interface IGetInfo extends MvpView {
    void getUserInfo(OurUserInfo userInfo);
}
