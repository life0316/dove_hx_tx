package com.haoxi.dove.retrofit;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by lifei on 2017/4/6.
 */

public class CodeType {


    @IntDef({TYPE_REQUEST_RESET_PWD,TYPE_REQUEST_VER_CODE,TYPE_VALID_VER_CODE,TYPE_EXIT,TYPE_DELETE_RING,TYPE_DELETE_PIGEON,TYPE_ADD_PIGEON,TYPE_ADD_RING,
            TYPE_PIGEON_WITH_RING,TYPE_PIGEON_UNBIND_RING,TYPE_START_FLY,TYPE_END_FLY
            ,TYPE_UPDATE_INFO,TYPE_POWER_CONFIG,TYPE_TIMING_SWITH,TYPE_APP_VERSION,TYPE_ADD_DYNAMIC
            ,TYPE_REQUEST_ADD_COMMENT,TYPE_REQUEST_REMOVE_COMMENT,TYPE_REQUEST_ADD_FAB,TYPE_REQUEST_REMOVE_FAB,
            TYPE_REQUEST_ADD_REPLY,TYPE_REQUEST_TYPE_SHARE_CIRCLE
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface CodeTypeChecked{}

    /**
     * 忘记密码
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_RESET_PWD = 102;

    /**
     * 获取验证码
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_VER_CODE = 100;
    /**
     * 验证验证码
     */
    @CodeTypeChecked
    public static final int TYPE_VALID_VER_CODE = 101;

    /**
     * 用户修改鸽子信息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_DOVE_UPDATE = 103;

    /**
     * 用户修改鸽环信息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_RING_UPDATE = 104;

    /**
     * 关注好友
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_ADD_ATTENTION = 106;

    /**
     * 删除信鸽飞行记录
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_DELETE_FLY = 105;
    /**
     * 取消关注好友
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_REMOVE_ATTENTION = 107;

    /**
     * 发朋友圈消息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_ADD_CIRCLE = 108;

    /**
     * 发朋友圈消息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_TYPE_SHARE_CIRCLE = 111;

    /**
     * 删除指定一条朋友圈消息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_DELETE_SINGLE_CIRCLE= 109;

    /**
     * 删除所有朋友圈消息
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_DELETE_ALL_CIRCLE = 110;

    /**
     * 朋友圈评论
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_ADD_COMMENT = 120;

    /**
     * 回复朋友圈评论
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_ADD_REPLY = 124;

    /**
     * 删除朋友圈评论或评论
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_REMOVE_COMMENT = 121;

    /**
     * 朋友圈点赞
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_ADD_FAB = 122;

    /**
     * 取消朋友圈点赞
     */
    @CodeTypeChecked
    public static final int TYPE_REQUEST_REMOVE_FAB = 123;


    /**
     * 退出
     */
    @CodeTypeChecked
    public static final int TYPE_EXIT = 0;

    /**
     * 删除鸽环
     */
    @CodeTypeChecked
    public static final int TYPE_DELETE_RING = 1;

    /**
     * 删除信鸽
     */
    @CodeTypeChecked
    public static final int TYPE_DELETE_PIGEON = 2;


    /**
     * 新增信鸽
     */
    @CodeTypeChecked
    public static final int TYPE_ADD_PIGEON = 3;
    /**
     * 新增鸽环
     */
    @CodeTypeChecked
    public static final int TYPE_ADD_RING = 4;
    /**
     * 信鸽与鸽环绑定
     */
    @CodeTypeChecked
    public static final int TYPE_PIGEON_WITH_RING = 5;
    /**
     * 信鸽与鸽环解绑
     */
    @CodeTypeChecked
    public static final int TYPE_PIGEON_UNBIND_RING = 6;
    /**
     * 开始飞行
     */
    @CodeTypeChecked
    public static final int TYPE_START_FLY = 7;
    /**
     * 结束飞行
     */
    @CodeTypeChecked
    public static final int TYPE_END_FLY = 8;

    /**
     * 修改用户信息
     */
    @CodeTypeChecked
    public static final int TYPE_UPDATE_INFO = 9;
    /**
     * 省电模式
     */
    @CodeTypeChecked
    public static final int TYPE_POWER_CONFIG = 10;
    /**
     * 定时开关机
     */
    @CodeTypeChecked
    public static final int TYPE_TIMING_SWITH = 11;
    /**
     * 获取app最新版本
     */
    @CodeTypeChecked
    public static final int TYPE_APP_VERSION = 12;


    /**
     * 发布动态
     */
    @CodeTypeChecked
    public static final int TYPE_ADD_DYNAMIC = 13;
    /**
     * 点赞
     */
    @CodeTypeChecked
    public static final int TYPE_DO_AGREE = 14;
    /**
     * 转发
     */
    @CodeTypeChecked
    public static final int TYPE_TRANSPOND = 15;
    /**
     * 新增评论
     */
    @CodeTypeChecked
    public static final int TYPE_ADD_COMMENT = 16;
    /**
     * 请求添加好友
     */
    @CodeTypeChecked
    public static final int TYPE_ADD_FRIENDREQ = 17;
    /**
     * 处理添加好友请求
     */
    @CodeTypeChecked
    public static final int TYPE_DISPOSE = 18;
    /**
     * 发送私信
     */
    @CodeTypeChecked
    public static final int TYPE_SEND_MESSAGE = 19;
}
