package com.haoxi.dove.newin;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.OurCode;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public interface IForgetView extends MvpView{

    String getTelephone();
    String getVerCode();

    void getCodeSussess(OurCode ourCode);

}
