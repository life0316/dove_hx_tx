package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.UploadImageBean;

/**
 * Created by lifei on 2017/1/14.
 */
public interface IPersonalView extends MvpView {

    String getToken();
    String getDovecoteName();
    String getUserSex();
    int getFeedPigeonYear();


    void setUserHead();
    void setDovecoteName(String dovecoteName);
    void setUserAge(String age);
    void setUserSex(String sex);
    void setFeedPigeonYear(int feedPigeonYear);

    void toUploadHeadPic(UploadImageBean uploadImageBean);


}
