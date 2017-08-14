package com.haoxi.dove.retrofit;

/**
 * Created by Administrator on 2017\6\29 0029.
 */

public class MethodConstant {

    /**
     * 3.1 用户申请验证码
     */
    public static final String REQUEST_VER_CODE = "/app/user/request_ver_code";

    /**
     * 3.2 验证码验证
     */
    public static final String VALID_VER_CODE = "/app/user/valid_ver_code";

    /**
     * 3.3 用户注册
     */
    public static final String REGISTER = "/app/user/register";

    /**
     * 3.4 用户登录
     */
    public static final String LOGIN = "/app/user/login";

    /**
     * 3.5 忘记密码
     */
    public static final String RESET_PASSWORD = "/app/user/reset_password";

    /**
     * 3.6 获取用户信息
     */
    public static final String USER_INFO_DETAIL = "/app/user/detail";

    /**
     * 3.7 上传图片
     */
    public static final String IMAGE_UPLOAD = "/app/image/upload";

    /**
     * 3.8 修改用户信息
     */
    public static final String USER_INFO_UPDATE = "/app/user/update";

    /**
     * 3.9 用户添加鸽子
     */
    public static final String DOVE_ADD = "/app/dove/add";

    /**
     * 3.10 用户修改鸽子信息
     */
    public static final String DOVE_INFO_UPDATE = "/app/dove/update";

    /**
     * 3.11 用户删除鸽子
     */
    public static final String DOVE_DELETE = "/app/dove/delete";


    /**
     * 3.12 用户读取鸽子列表
     */
    public static final String DOVE_SEARCH = "/app/dove/search";

    /**
     * 3.13 读取鸽环默认配置
     */
    public static final String GET_RING_CONFIG = "/app/config/get_ring_config";

    /**
     * 3.14 用户添加鸽环
     */
    public static final String RING_ADD = "/app/ring/add";

    /**
     * 3.15 读取鸽环列表
     */
    public static final String RING_SEARCH = "/app/ring/search";

    /**
     * 3.16 用户修改鸽环信息
     */
    public static final String RING_INFO_UPDATE = "/app/ring/update";

    /**
     * 3.17 用户删除鸽环
     */
    public static final String RING_DELETE = "/app/ring/delete";

    /**
     * 3.18 匹配鸽环和鸽子
     */
    public static final String RING_MATCH = "/app/ring/match";

    /**
     * 3.19 鸽环和鸽子解除匹配
     */
    public static final String RING_DEMATCH = "/app/ring/dematch";

    /**
     * 3.20 设置鸽子开始飞行
     */
    public static final String FLY_START = "/app/fly/start";

    /**
     * 3.21 设置鸽子结束飞行
     */
    public static final String FLY_STOP = "/app/fly/stop";

    /**
     * 3.22 获取信鸽当前位置信息
     */
    public static final String FLY_CUILOC = "/app/fly/curloc";

    /**
     * 3.23 获取信鸽飞行记录
     *
     * 根据单只信鸽 id 获取飞行记录
     */
    public static final String FLY_SEARCH = "/app/fly/search_by_doveid";

    /**
     * 3.24 删除信鸽飞行记录
     */
    public static final String FLY_DELETE = "/app/fly/delete";

    /**
     * 3.25 获取最新版本信息
     */
    public static final String GET_CURRENT_VERSION = "/app/gversion/get_current_version";

    /**
     * 3.26 关注好友
     */
    public static final String ATTENTION_ADD = "/app/attention/add";

    /**
     * 3.27 取消关注好友
     */
    public static final String ATTENTTION_REMOVE = "/app/attention/remove";

    /**
     * 3.28 获取关注好友列表
     */
    public static final String ATTENTION_SEARCH = "/app/attention/search";

    /**
     * 3.29 发朋友圈消息
     */
    public static final String CIRCLE_ADD = "/app/circle/add";

    /**
     * 3.30 获取指定好友的朋友圈
     */
    public static final String GET_SINGLE_FRIEND_CIRCLES = "/app/circle/get_single_friend_circles";

    /**
     * 3.31 获取好友的朋友圈消息
     */
    public static final String GET_FRIENDS_CIRCLES = "/app/circle/get_friends_circles";

    /**
     * 3.32 获取所有朋友圈消息
     */
    public static final String GET_ALL_CIRCLES = "/app/circle/get_all_circles";

    /**
     * 3.33 删除指定一条朋友圈消息
     */
    public static final String DELETE_SINGLE_CIRCLE = "/app/circle/delete_single";

    /**
     * 3.34 删除自己朋友圈所有消息
     */
    public static final String DELETE_ALL_CIRCLE = "/app/circle/delete_all";

    /**
     * 3.35 朋友圈添加评论
     */
    public static final String ADD_COMMENT = "/app/circle_comment/add_comment";


    /**
     * 3.36 回复朋友圈评论
     */
    public static final String ADD_REPLY = "/app/circle_comment/add_reply";

    /**
     * 3.37 获取朋友圈评论
     */
    public static final String GET_COMMENT = "/app/circle_comment/get_comment";

    /**
     * 3.38 删除朋友圈评论或回复
     */
    public static final String REMOVE_COMMENT = "/app/circle_comment/remove_comment";

    /**
     * 3.39 朋友圈点赞
     */
    public static final String ADD_FAB = "/app/circle_fab/add_fab";

    /**
     * 3.40 获取朋友圈点赞列表
     */
    public static final String GET_FAB = "/app/circle_fab/get_fab";

    /**
     * 3.41 取消朋友圈点赞
     */
    public static final String REMOVE_FAB = "/app/circle_fab/remove_fab";

    /**
     * 3.42 转发朋友圈
     */
    public static final String SHARE_CIRCLE = "/app/circle_share/share";

    /**
     * 3.43 用户退出登录
     */
    public static final String USER_LOGOUT = "/app/user/logout";

    /**
     * 3.44 获取玩家信息
     */
    public static final String GET_PLAYER_INFO= "/app/user/get_player_info";

    /**
     * 3.45 获取好友圈详情
     */
    public static final String GET_CIRCLE_DETAIL = "/app/circle/get_circle_detail";


    /**
     * 3.46 获取信鸽指定飞行记录的轨迹点
     */
    public static final String SEARCH_BY_DOVEID_RECORDID = "/app/fly/search_by_doveid_and_fly_recordid";

    /**
     * 3.47 根据飞行记录获取所有信鸽轨迹点
     */
    public static final String SEARCH_BY_FLY_RECORDID = "/app/fly/search_by_fly_recordid";

    /**
     * 3.48 获取实时信鸽轨迹点
     */
    public static final String GET_CURTIME_POINTS = "/app/fly/get_curtime_points";

    /**
     * 3.49 根据玩家 id 获取所有信鸽的行程
     */
    public static final String SEARCH_BY_PLAYERID = "/app/fly/search_by_playerid";

}
