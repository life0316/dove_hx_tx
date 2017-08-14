package com.haoxi.dove.modules.mvp.views;

import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.bean.InnerDoveData;

import java.util.List;

/**
 * @创建者 Administrator
 * @创建时间 2017/1/19 17:29
 * @描述
 */
public interface IRingInfoView extends MvpView{

//    String getRingObjId();
//    String getPositionMode();
//    String getReportedFreq();
//    String getPowerOnTime();
//    String getPowerOffTime();
//    String getRingCode();

    String getUserObjId();
    String getToken();

    //String getRingPigeon();

    void toSetData(List<InnerDoveData> object);

    void onMateSuccess(String msg, int pisition);

//    void setSpecification(String spec);
//    void setPositionMode(String posMode);
//    void setReportFreq(String freq);
//    void setPowerOntime(String onTime);
//    void setPowerOffTime(String offTime);

}
