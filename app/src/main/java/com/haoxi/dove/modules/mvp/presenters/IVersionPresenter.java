package com.haoxi.dove.modules.mvp.presenters;

import java.util.Map;

public interface IVersionPresenter {
    void updateVar(Map<String,String> map);
    void downloadApk(String apkUrl);
}
