package com.haoxi.dove.newin.trail.presenter;


import java.util.Map;

/**
 * Created by lifei on 2017/3/30.
 */

public interface IOurCodePresenter<T> {

    void getValidCode(Map<String,String> map);

    void getRequestCode(Map<String,String> map);
    //忘记密码
    void resetPwd(Map<String,String> map);

    //退出登录
    void exitApp(Map<String,String> map);


    //修改用户信息
    void updateUserInfo(Map<String,String> map);

    //用户添加鸽子
    void addDove(Map<String,String> map);

    //用户修改鸽子信息
    void updateDoveInfo(Map<String,String> map);

    //用户删除鸽子
    void deleteDove(Map<String,String> map);

    //用户添加鸽环
    void addRing(Map<String,String> map);

    //用户修改鸽环信息
    void updateRing(Map<String,String> map);

    //用户删除鸽环
    void deleteRing(Map<String,String> map);

    //匹配鸽环和鸽子
    void ringMatchDove(Map<String,String> map);

    //鸽环和鸽子解绑
    void ringDematchDove(Map<String,String> map);

//    //设置鸽子开始飞行
//    void startFly(Map<String,String> map);

    //设置鸽子结束飞行
    void stopFly(Map<String,String> map);

    //删除信鸽飞行记录
    void deleteFly(Map<String,String> map);

    //关注好友
    void addAttention(Map<String,String> map);

    //取消关注
    void removeAttention(Map<String,String> map);

    //发朋友圈消息
    void addCircle(Map<String,String> map);
    //转发
    void shareCircle(Map<String,String> map);

    //删除指定一条朋友圈消息
    void deleteSingleCircle(Map<String,String> map);

    //删除自己朋友圈所有消息
    void deleteAllCircle(Map<String,String> map);

    //朋友圈添加评论
    void addComment(Map<String,String> map);

    //回复朋友圈评论
    void addReply(Map<String,String> map);

//    删除朋友圈评论或回复
    void removeComment(Map<String,String> map);

    //朋友圈点赞
    void addFab(Map<String,String> map);

    //取消朋友圈点赞
    void removeFab(Map<String,String> map);

}
