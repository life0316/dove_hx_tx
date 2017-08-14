package com.haoxi.dove.modules.mvp.presenters;

import java.util.Map;

/**
 * Created by lifei on 2017/1/14.
 */

public interface IVersionPresenter {

    void updateVar(Map<String,String> map);
    void downloadApk(String apkUrl);

}
