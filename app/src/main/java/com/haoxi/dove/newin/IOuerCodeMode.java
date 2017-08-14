package com.haoxi.dove.newin;

import com.haoxi.dove.callback.RequestCallback;

import java.util.Map;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public interface IOuerCodeMode<T> {

    void getValidCode(Map<String,String> map, RequestCallback<T> requestCallback);

    void getRequestCode(Map<String,String> map, RequestCallback<T> requestCallback);

    void resetPwd(Map<String,String> map, RequestCallback<T> requestCallback);

    void exitApp(Map<String,String> map, RequestCallback<T> requestCallback);


    //用户相关
    void updateUserInfo(Map<String,String> map, RequestCallback<T> requestCallback);


    //信鸽相关
    void addDove(Map<String,String> map, RequestCallback<T> requestCallback);
    void deleteDove(Map<String,String> map, RequestCallback<T> requestCallback);
    void updateDoveInfo(Map<String,String> map, RequestCallback<T> requestCallback);

    //鸽环相关
    void addRing(Map<String,String> map, RequestCallback<T> requestCallback);
    void deleteRing(Map<String,String> map, RequestCallback<T> requestCallback);
    void updateRingInfo(Map<String,String> map, RequestCallback<T> requestCallback);

    //鸽子和信鸽匹配相关
    void ringWithDove(Map<String,String> map, RequestCallback<T> requestCallback);
    void ringUnbindDove(Map<String,String> map, RequestCallback<T> requestCallback);

    //飞行相关
//    void startFly(Map<String,String> map, RequestCallback<T> requestCallback);
    void endFly(Map<String,String> map, RequestCallback<T> requestCallback);

    //删除飞行记录
    void deleteFly(Map<String,String> map, RequestCallback<T> requestCallback);

    //鸽圈相关
    void addAttention(Map<String,String> map, RequestCallback<T> requestCallback);
    void removeAttention(Map<String,String> map, RequestCallback<T> requestCallback);
    void addCircle(Map<String,String> map, RequestCallback<T> requestCallback);
    void shareCircle(Map<String,String> map, RequestCallback<T> requestCallback);
    void deleteSingleCircle(Map<String,String> map, RequestCallback<T> requestCallback);
    void deleteAllCircle(Map<String,String> map, RequestCallback<T> requestCallback);

    void addComment(Map<String,String> map,RequestCallback<T> requestCallback);
    void addReply(Map<String,String> map,RequestCallback<T> requestCallback);
    void removeComment(Map<String,String> map,RequestCallback<T> requestCallback);
    void addFab(Map<String,String> map,RequestCallback<T> requestCallback);
    void removeFab(Map<String,String> map,RequestCallback<T> requestCallback);

}
