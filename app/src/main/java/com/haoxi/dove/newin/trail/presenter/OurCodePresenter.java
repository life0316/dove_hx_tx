package com.haoxi.dove.newin.trail.presenter;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.base.MvpView;
import com.haoxi.dove.newin.IOuerCodeMode;
import com.haoxi.dove.newin.OurCodeModel;
import com.haoxi.dove.newin.bean.OurCode;
import com.haoxi.dove.retrofit.CodeType;

import java.util.Map;


/**
 * Created by lifei on 2017/3/30.
 */

public class OurCodePresenter extends BasePresenter<MvpView, OurCode> implements IOurCodePresenter {

    private IOuerCodeMode codesModel;

    private int typeCode = 0;

    private String Thrift_TAG1 = "POSITION_MODE";
    private String Thrift_TAG2 = "REPORTED_FREQ";
    private String SWITH_BOOTTIME = "BOOTTIME";
    private String SWITH_SDTIME = "SDTIME";

    private String mThriftTag = "POSITION_MODE";

    public OurCodePresenter(MvpView mView) {
        attachView(mView);
        codesModel = new OurCodeModel();
    }

    @Override
    public void requestError(String msg) {
        super.requestError(msg);
        switch (typeCode) {

            case CodeType.TYPE_START_FLY:
                if ("605".equals(msg)) {
                    getMvpView().toDo();
                }
                break;
        }
    }

    @Override
    public void requestSuccess(OurCode data) {
        super.requestSuccess(data);

        showDifMsg();
    }

    public void showDifMsg() {
        String successMsg = "";
        boolean show = false;

        switch (typeCode) {

            case CodeType.TYPE_DISPOSE:
                successMsg = "已处理";
                break;
            case CodeType.TYPE_REQUEST_ADD_COMMENT:
            case CodeType.TYPE_REQUEST_ADD_REPLY:
                successMsg = "评论成功";
                break;

            case CodeType.TYPE_REQUEST_ADD_FAB:
                successMsg = "赞";

                break;
            case CodeType.TYPE_REQUEST_REMOVE_COMMENT:
                successMsg = "删除评论";
                break;
            case CodeType.TYPE_REQUEST_REMOVE_FAB:
                successMsg = "取消赞";
                break;

            case CodeType.TYPE_UPDATE_INFO:
                successMsg = "修改用户信息";
                break;
            case CodeType.TYPE_REQUEST_RESET_PWD:
                successMsg = "重置密码成功";
                break;
            case CodeType.TYPE_REQUEST_ADD_ATTENTION:
                successMsg = "已关注";
                break;
            case CodeType.TYPE_REQUEST_REMOVE_ATTENTION:
                successMsg = "取消关注";
                break;

            case CodeType.TYPE_ADD_PIGEON:
                successMsg = "添加信鸽成功";
                break;
            case CodeType.TYPE_REQUEST_DOVE_UPDATE:
                successMsg = "修改鸽子信息";
                break;
            case CodeType.TYPE_REQUEST_RING_UPDATE:
                successMsg = "修改鸽环信息";
                break;
            case CodeType.TYPE_ADD_RING:
                successMsg = "添加鸽环成功";
                break;
            case CodeType.TYPE_DELETE_RING:
                successMsg = "删除鸽环成功";
                break;
            case CodeType.TYPE_DELETE_PIGEON:
                successMsg = "删除信鸽成功";
                break;
            case CodeType.TYPE_EXIT:
                successMsg = "退出";
                show = false;
                break;
            case CodeType.TYPE_PIGEON_WITH_RING:
                successMsg = "匹配成功";
                break;
            case CodeType.TYPE_START_FLY:
                successMsg = "开始飞行";
                show = true;
                break;
//            case CodeType.TYPE_END_FLY:
//                successMsg = "结束飞行";
//                show = true;
//                break;
            case CodeType.TYPE_REQUEST_DELETE_FLY:
                successMsg = "删除记录";
                break;
            case CodeType.TYPE_ADD_DYNAMIC:
                successMsg = "发表成功";
                break;
            case CodeType.TYPE_REQUEST_TYPE_SHARE_CIRCLE:
                successMsg = "转发成功";
                show = true;
                break;
            case CodeType.TYPE_ADD_FRIENDREQ:
                successMsg = "已发送添加好友请求";
                break;
            case CodeType.TYPE_PIGEON_UNBIND_RING:
                successMsg = "解绑成功";
                break;
            case CodeType.TYPE_VALID_VER_CODE:
                successMsg = "验证验证码成功";
                break;
            case CodeType.TYPE_REQUEST_VER_CODE:
                successMsg = "获取验证码成功";
                break;
            case CodeType.TYPE_POWER_CONFIG:

                if (Thrift_TAG2.equals(mThriftTag)) {

                    successMsg = "报点设置成功";
                } else {
                    successMsg = "定位设置成功";
                }

                break;
            case CodeType.TYPE_TIMING_SWITH:

                if (SWITH_BOOTTIME.equals(mThriftTag)) {

                    successMsg = "开机设置成功";
                } else if (SWITH_SDTIME.equals(mThriftTag)) {
                    successMsg = "关机设置成功";
                }

                break;
        }
        getMvpView().toDo();
        if (show) {
            getMvpView().showErrorMsg(successMsg);
        }
    }

    @Override
    public void getValidCode(Map map) {

        typeCode = CodeType.TYPE_VALID_VER_CODE;

        codesModel.getValidCode(map,this);

    }

    @Override
    public void getRequestCode(Map map) {
        typeCode = CodeType.TYPE_REQUEST_VER_CODE;

        codesModel.getValidCode(map,this);
    }

    @Override
    public void resetPwd(Map map) {

        typeCode = CodeType.TYPE_REQUEST_RESET_PWD;

        codesModel.resetPwd(map,this);

    }

    @Override
    public void updateUserInfo(Map map) {

        typeCode = CodeType.TYPE_UPDATE_INFO;

        codesModel.updateUserInfo(map,this);

    }

    @Override
    public void addDove(Map map) {

        typeCode = CodeType.TYPE_ADD_PIGEON;

        codesModel.addDove(map,this);

    }

    @Override
    public void updateDoveInfo(Map map) {

        typeCode = CodeType.TYPE_REQUEST_DOVE_UPDATE;

        codesModel.updateDoveInfo(map,this);

    }

    @Override
    public void deleteDove(Map map) {

        typeCode = CodeType.TYPE_DELETE_PIGEON;

        codesModel.deleteDove(map,this);

    }

    @Override
    public void addRing(Map map) {

        typeCode = CodeType.TYPE_ADD_RING;

        codesModel.addRing(map,this);

    }

    @Override
    public void updateRing(Map map) {
        typeCode = CodeType.TYPE_REQUEST_RING_UPDATE;

        codesModel.updateRingInfo(map,this);
    }

    @Override
    public void deleteRing(Map map) {

        typeCode = CodeType.TYPE_DELETE_RING;

        codesModel.deleteRing(map,this);
    }

    @Override
    public void ringMatchDove(Map map) {

        typeCode = CodeType.TYPE_PIGEON_WITH_RING;

        codesModel.ringWithDove(map,this);

    }

    @Override
    public void ringDematchDove(Map map) {
        typeCode = CodeType.TYPE_PIGEON_UNBIND_RING;

        codesModel.ringUnbindDove(map,this);
    }

//    @Override
//    public void startFly(Map map) {
//
//        typeCode = CodeType.TYPE_START_FLY;
//
//    }

    @Override
    public void stopFly(Map map) {
        typeCode = CodeType.TYPE_END_FLY;
        codesModel.endFly(map,this);
    }

    @Override
    public void deleteFly(Map map) {
        typeCode = CodeType.TYPE_REQUEST_DELETE_FLY;

        codesModel.deleteFly(map,this);

    }

    @Override
    public void addAttention(Map map) {

        typeCode = CodeType.TYPE_REQUEST_ADD_ATTENTION;

        codesModel.addAttention(map,this);
    }

    @Override
    public void removeAttention(Map map) {

        typeCode = CodeType.TYPE_REQUEST_REMOVE_ATTENTION;

        codesModel.removeAttention(map,this);

    }

    @Override
    public void addCircle(Map map) {

        typeCode = CodeType.TYPE_REQUEST_ADD_CIRCLE;

        Log.e("OurCodeModel",map.toString()+"---------addc");

        codesModel.addCircle(map,this);

    }

    @Override
    public void deleteSingleCircle(Map map) {
        typeCode = CodeType.TYPE_REQUEST_DELETE_SINGLE_CIRCLE;

        codesModel.deleteSingleCircle(map,this);
    }

    @Override
    public void deleteAllCircle(Map map) {
        typeCode = CodeType.TYPE_REQUEST_DELETE_ALL_CIRCLE;

        codesModel.deleteAllCircle(map,this);

    }

    @Override
    public void addComment(Map map) {

        typeCode = CodeType.TYPE_REQUEST_ADD_COMMENT;

        codesModel.addComment(map,this);
    }

    @Override
    public void removeComment(Map map) {

        typeCode = CodeType.TYPE_REQUEST_REMOVE_COMMENT;

        codesModel.removeComment(map,this);
    }

    @Override
    public void addFab(Map map) {

        typeCode = CodeType.TYPE_REQUEST_ADD_FAB;

        codesModel.addFab(map,this);

    }

    @Override
    public void removeFab(Map map) {

        typeCode = CodeType.TYPE_REQUEST_REMOVE_FAB;

        codesModel.removeFab(map,this);
    }

    @Override
    public void addReply(Map map) {
        typeCode = CodeType.TYPE_REQUEST_ADD_REPLY;
        codesModel.addReply(map,this);
    }

    @Override
    public void exitApp(Map map) {
        typeCode = CodeType.TYPE_EXIT;
        codesModel.exitApp(map,this);
    }

    @Override
    public void shareCircle(Map map) {
        typeCode = CodeType.TYPE_REQUEST_TYPE_SHARE_CIRCLE;
        codesModel.shareCircle(map,this);
    }
}
