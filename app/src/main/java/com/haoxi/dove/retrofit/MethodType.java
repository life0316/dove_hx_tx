package com.haoxi.dove.retrofit;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
public class MethodType {
    /**
     * 获取验证码
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_REQUEST_VER_CODE = 31;
    /**
     * 验证验证码
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_VALID_VER_CODE = 32;
    /**
     * 退出
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_EXIT = 0;
    /**
     * 用户注册
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_REGISTER = 33;

    /**
     * 用户登录
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_LOGIN = 34;

    /**
     * 忘记密码
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RESET_PWD = 35;

    /**
     * 获取用户信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_USER_DETAIL = 36;

    /**
     * 上传图片
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_IMAGE_UPLOAD = 37;

    /**
     * 修改用户信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_USER_UPDATE = 38;

    /**
     * 用户添加鸽子
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DOVE_ADD = 39;

    /**
     * 用户修改鸽子信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DOVE_UPDATE = 310;

    /**
     * 用户删除鸽子
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DOVE_DELETE = 311;

    /**
     * 用户读取鸽子列表
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DOVE_SEARCH = 312;

    /**
     * 读取鸽环默认配置
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_CONFIG = 313;

    /**
     * 鸽子添加鸽环
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_ADD = 314;

    /**
     * 读取鸽环列表
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_SEARCH = 315;

    /**
     * 用户修改鸽环信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_UPDATE = 316;

    /**
     * 用户删除鸽环
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_DELETE = 317;

    /**
     * 匹配鸽环和鸽子
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_MATCH= 318;

    /**
     * 鸽环和鸽子解除匹配
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_RING_DEMATCH = 319;

    /**
     * 设置鸽子开始飞行
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_START = 320;

    /**
     * 设置鸽子结束飞行
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_STOP = 321;

    /**
     * 获取信鸽当前位置信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_CURLOC = 322;

    /**
     * 获取信鸽飞行记录
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_SEARCH = 323;

    /**
     * 删除信鸽飞行记录
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_DELETE = 324;

    /**
     * 获取最新版本信息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_CURRENT_VERSION = 325;
    /**
     * 关注好友
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ADD_ATTENTION = 326;

    /**
     * 取消关注好友
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_REMOVE_ATTENTION = 327;

    /**
     * 获取关注好友列表
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_SEARCH_ATTENTION = 328;

    /**
     * 发朋友圈消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ADD_CIRCLE= 329;

    /**
     * 获取指定好友的朋友圈消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_SINGLE_CIRCLES = 330;

    /**
     * 获取好友的朋友圈消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FRIENDS_CIRCLES = 331;

    /**
     * 获取所有的朋友圈消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ALL_CIRCLES = 332;

    /**
     * 删除指定一条朋友圈消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DELETE_SINGEL = 333;

    /**
     * 删除自己朋友圈所有消息
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_DELETE_ALL = 334;

    /**
     * 朋友圈添加评论
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ADD_COMMENT = 335;

    /**
     * 回复朋友圈评论
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ADD_REPLY = 336;


    /**
     * 获取朋友圈评论
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_GET_COMMENT = 337;

    /**
     * 删除朋友圈评论或回复
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_REMOVE_COMMENT = 338;

    /**
     * 朋友圈点赞
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_ADD_FAB = 339;

    /**
     * 获取朋友圈点赞列表
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_GET_FAB = 340;

    /**
     * 取消朋友圈点赞
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_REMOVE_FAB = 341;


    /**
     * 转发朋友圈
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_SHARE_CIRCLE = 342;



    /**
     * 获取玩家信息
     *
     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_PLAYER_INFO = 343;


    /**
     * 获取好友圈详情
     *
     * /app/circle/get_circle_detail

     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_CIRCLE_DETAIL = 344;


    /**
     * 获取信鸽指定飞行记录的轨迹点
     *
     * /app/fly/search_by_doveid_fly_recordid

     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_DOVEID_RECORDID = 346;

    /**
     * 根据飞行记录获取所有信鸽轨迹点
     *
     * /app/fly/search_by_fly_recordid

     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_FLY_BY_RECORDID = 347;

    /**
     * 获取实时信鸽轨迹点
     *
     * /app/fly/get_curtime_points

     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_GET_CURTIME_POINTS = 348;

    /**
     * 根据玩家 id 获取所有信鸽的行程
     *
     * /app/fly/search_by_playerid

     */
    @MethodTypeChecked
    public static final int METHOD_TYPE_SEARCH_BY_PLAYERID= 349;

    @IntDef({METHOD_TYPE_REQUEST_VER_CODE,METHOD_TYPE_VALID_VER_CODE,METHOD_TYPE_EXIT,
            METHOD_TYPE_REGISTER,METHOD_TYPE_LOGIN,METHOD_TYPE_RESET_PWD,
            METHOD_TYPE_USER_DETAIL,METHOD_TYPE_IMAGE_UPLOAD,METHOD_TYPE_USER_UPDATE,
            METHOD_TYPE_DOVE_ADD,METHOD_TYPE_DOVE_UPDATE,METHOD_TYPE_DOVE_DELETE,
            METHOD_TYPE_DOVE_SEARCH,METHOD_TYPE_RING_CONFIG,METHOD_TYPE_RING_ADD,
            METHOD_TYPE_RING_SEARCH,METHOD_TYPE_RING_UPDATE,METHOD_TYPE_RING_DELETE,
            METHOD_TYPE_RING_MATCH,METHOD_TYPE_RING_DEMATCH,METHOD_TYPE_FLY_START,
            METHOD_TYPE_FLY_STOP,METHOD_TYPE_FLY_CURLOC,METHOD_TYPE_FLY_SEARCH,
            METHOD_TYPE_FLY_DELETE,METHOD_TYPE_CURRENT_VERSION,METHOD_TYPE_ADD_ATTENTION,
            METHOD_TYPE_REMOVE_ATTENTION,METHOD_TYPE_SEARCH_ATTENTION,METHOD_TYPE_ADD_CIRCLE,
            METHOD_TYPE_SINGLE_CIRCLES,METHOD_TYPE_FRIENDS_CIRCLES,METHOD_TYPE_DELETE_SINGEL,
            METHOD_TYPE_DELETE_ALL,METHOD_TYPE_ALL_CIRCLES,METHOD_TYPE_ADD_COMMENT,
            METHOD_TYPE_GET_COMMENT,METHOD_TYPE_REMOVE_COMMENT,METHOD_TYPE_ADD_FAB,
            METHOD_TYPE_GET_FAB,METHOD_TYPE_REMOVE_FAB,METHOD_TYPE_ADD_REPLY,METHOD_TYPE_SHARE_CIRCLE,
            METHOD_TYPE_CIRCLE_DETAIL,METHOD_TYPE_PLAYER_INFO,METHOD_TYPE_FLY_DOVEID_RECORDID,
            METHOD_TYPE_FLY_BY_RECORDID,METHOD_TYPE_GET_CURTIME_POINTS,METHOD_TYPE_SEARCH_BY_PLAYERID

    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface MethodTypeChecked{}

}
