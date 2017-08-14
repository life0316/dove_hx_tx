package com.haoxi.dove.retrofit.RegistService;

import com.haoxi.dove.newin.bean.OurCommentBean;
import com.haoxi.dove.newin.bean.OurFabBean;
import com.haoxi.dove.newin.bean.OurRouteBean;
import com.haoxi.dove.newin.bean.AttentionBean;
import com.haoxi.dove.newin.bean.OurCode;
import com.haoxi.dove.newin.bean.OurDoveBean;
import com.haoxi.dove.newin.bean.OurRingBean;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.newin.bean.OurUserInfo;
import com.haoxi.dove.newin.bean.OurVerBean;
import com.haoxi.dove.newin.bean.RingConfigBean;
import com.haoxi.dove.newin.bean.StartFlyBean;
import com.haoxi.dove.newin.bean.UploadImageBean;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.bean.PlayerBean;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by Administrator on 2017\6\5 0005.
 */

public interface IOurNewService {


//    @Streaming
//    @GET
//    Call<ResponseBody> downloadImage(@Url String fileUrl);
    @Streaming
    @GET
    Observable<ResponseBody> downloadImage(@Url String fileUrl);

    //3.1用户申请验证码
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> getRequestVerCode(@FieldMap Map<String,String> map);

    //3.2验证码验证
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> getValidVerCode(@FieldMap Map<String,String> map);

    //3.3用户注册
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurUser> getRegister(@FieldMap Map<String,String> map);

    //3.4用户登录
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurUser> getOurLogin(@FieldMap Map<String,String> map);
//    Observable<OurUser> getOurLogin(@PartMap Map<String,RequestBody> map);

    //3.5.用户退出登录
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode>  exitApp(@FieldMap Map<String,String> map);

    //3.5.忘记密码
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> getResetPwd(@FieldMap Map<String,String> map);

    //3.6获取用户信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurUserInfo> getDetailInfo(@FieldMap Map<String,String> map);

    //3.7上传图片
    @Multipart
    @POST("gehuan/")
//    @FormUrlEncoded
//    Observable<UploadImageBean> getUploadPic(@PartMap Map<String, RequestBody> params, @Part("file")  MultipartBody.Part file);
    Observable<UploadImageBean> getUploadPic(@PartMap Map<String, RequestBody> params);

    //3.8.修改用户信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> getUpdateInfo(@FieldMap Map<String,String> map);

    //3.9.用户添加鸽子
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addPigeon(@FieldMap Map<String,String> map);

    //3.10用户修改鸽子信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> updateDove(@FieldMap Map<String,String> map);


    //3.11用户删除鸽子
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> deleteDove(@FieldMap Map<String,String> map);

    //3.12用户读取鸽子列表
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurDoveBean> searchDove(@FieldMap Map<String,String> map);


    //3.13读取鸽环默认配置
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<RingConfigBean> getRingConfig(@FieldMap Map<String,String> map);

    //3.14 鸽子添加鸽环
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addRing(@FieldMap Map<String,String> map);

    //3.15.读取鸽环列表
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurRingBean> searchRing(@FieldMap Map<String,String> map);

    //3.16 用户修改鸽环信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> updateRing(@FieldMap Map<String,String> map);

    //3.17 用户删除鸽环
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> deleteRing(@FieldMap Map<String,String> map);

    //3.18 匹配鸽环和鸽子
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> matchRing(@FieldMap Map<String,String> map);

    //3.19 鸽环和鸽子解除匹配
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> dematchRing(@FieldMap Map<String,String> map);

    //3.20 设置鸽子开始飞行
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<StartFlyBean> startFly(@FieldMap Map<String,String> map);

    //3.21 设置鸽子结束飞行
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> stopFly(@FieldMap Map<String,String> map);

    //3.22 获取信鸽当前位置信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable curlocFly(@FieldMap Map<String,String> map);

    //3.23 获取信鸽飞行记录
    //   根据单只信鸽 id 获取飞行记录
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurRouteBean> searchFly(@FieldMap Map<String,String> map);

    //3.24 删除信鸽飞行记录
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> deleteFly(@FieldMap Map<String,String> map);

    //3.25 获取最新版本信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurVerBean> getCurrentVersion(@FieldMap Map<String,String> map);

    //3.26 关注好友
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> attentionFriend(@FieldMap Map<String,String> map);

    //3.27 取消关注好友
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> removeAttention(@FieldMap Map<String,String> map);

    //3.28 获取关注好友列表
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<AttentionBean> searchAttentionFriend(@FieldMap Map<String,String> map);

    //3.29 发朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addCircle(@FieldMap Map<String,String> map);
    //3.29 转发朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> shareCircle(@FieldMap Map<String,String> map);

    //3.30 获取指定好友的朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<CircleBean> getSingleFriendCircles(@FieldMap Map<String,String> map);

    //3.31 获取好友的朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<CircleBean> getFriendCircles(@FieldMap Map<String,String> map);

    //3.31 获取所有朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<CircleBean> getAllCircles(@FieldMap Map<String,String> map);

    // 3.32.删除指定一条朋友圈消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> deleteSingle(@FieldMap Map<String,String> map);

    //3.33.删除自己朋友圈所有消息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> deleteAll(@FieldMap Map<String,String> map);


    //3.34.帮助信息
    @GET("gehuan/app/html/app_helper")
    Observable appHelper();

    //3.35.App简介
    @GET("gehuan/app/html/app_intro")
    Observable appIntro();

    //3.36.用户协议
    @GET("gehuan/app/html/app_agreement")
    Observable appAgreement();

    //3.35 朋友圈添加评论
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addComment(@FieldMap Map<String,String> map);

    //3.35 回复朋友圈评论
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addReply(@FieldMap Map<String,String> map);

    //3.36 获取朋友圈评论
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCommentBean> getComment(@FieldMap Map<String,String> map);

    //3.37 删除朋友圈评论或回复
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> removeComment(@FieldMap Map<String,String> map);

    //3.38 朋友圈点赞
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> addFab(@FieldMap Map<String,String> map);


    //3.39 获取朋友圈点赞列表
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurFabBean> getFab(@FieldMap Map<String,String> map);


    //3.40 取消朋友圈点赞
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<OurCode> removeFab(@FieldMap Map<String,String> map);


    //3.40 获取玩家信息
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<PlayerBean> getPlayerInfo(@FieldMap Map<String,String> map);


    //3.40 获取单条朋友圈详情
    @POST("gehuan/")
    @FormUrlEncoded
    Observable<EachCircleBean> getCircleDetail(@FieldMap Map<String,String> map);

}
