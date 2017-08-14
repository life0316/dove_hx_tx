package com.haoxi.dove.acts;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;

import com.haoxi.dove.R;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.modules.loginregist.LoginActivity;
import com.haoxi.dove.modules.loginregist.model.LoginModel;
import com.haoxi.dove.modules.loginregist.presenter.LoginPresenter;
import com.haoxi.dove.modules.loginregist.model.UserInfoModel;
import com.haoxi.dove.modules.loginregist.presenter.UserInfoPresenter;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.newin.bean.OurUserInfo;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;

import java.util.HashMap;
import java.util.Map;

@ActivityFragmentInject(contentViewId = R.layout.activity_splash)
public class SplashActivity extends BaseActivity implements ILoginView {

//    private static final String TAG = "SplashActivity";

    private Handler mHandler = new Handler();

    private LoginPresenter presenter;

    private UserInfoPresenter infoPresenter;

    private String mPwdsp;

//    private String mLoginType = "1";

    private int methodType = MethodType.METHOD_TYPE_LOGIN;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        presenter = new LoginPresenter(new LoginModel(this));
        presenter.attachView(this);
        infoPresenter = new UserInfoPresenter(new UserInfoModel(this));
        infoPresenter.attachView(this);

        SharedPreferences loginsp = getSharedPreferences(ConstantUtils.LOGINSP, Context.MODE_PRIVATE);
//        String mUsername = loginsp.getString("username", null);
        mPwdsp = loginsp.getString("password", null);
        final Boolean isAutoCb = loginsp.getBoolean("autocb", false);

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (ApiUtils.isNetworkConnected(SplashActivity.this)) {

                    if (isAutoCb) {

                        methodType = MethodType.METHOD_TYPE_LOGIN;
                        presenter.getDataFromNets(getParaMap());
                    } else {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        //SplashActivity.this.overridePendingTransition(R.anim.left_in,R.anim.right_out);
                        SplashActivity.this.finish();
                    }
                } else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    //SplashActivity.this.overridePendingTransition(R.anim.left_in,R.anim.right_out);
                    SplashActivity.this.finish();
                }
            }
        }, 1000);
    }

    @Override
    public String getUserPhone() {
        return mUserPhone;
    }

    @Override
    public String getUserPwd() {
//
//        byte[] encryptedBytes = RSAHelper.encrypt(mPwdsp.getBytes());
//        String base64EncodedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
////        try {
////            String str = URLEncoder.encode(base64EncodedString, "utf-8");
////            return str;
////        } catch (UnsupportedEncodingException e) {
////            e.printStackTrace();
////        }
//
//        return base64EncodedString;


        if ("".equals(mPwdsp) || mPwdsp == null || mPwdsp.isEmpty()) {
            ApiUtils.showToast(this, getResources().getString(R.string.user_pwd_empty));
            return "";
        }

        return MD5Tools.MD5(mPwdsp);
    }

    @Override
    public String getUserId() {

        mUserObjId = userInfoPf.getString("user_objid", "");

        return mUserObjId;
    }

    @Override
    public String getToken() {
        mToken = userInfoPf.getString("user_token", "");
        return mToken;
    }

    @Override
    public void toGetDetail(OurUser user) {
        mEditor.putString("user_objid", user.getData().getUserid());
        mEditor.putString("user_token", user.getData().getToken());
        mEditor.commit();
        methodType = MethodType.METHOD_TYPE_USER_DETAIL;
        infoPresenter.getDataFromNets(getParaMap());
    }

    @Override
    public void toMainActivity(OurUserInfo userInfo) {

        mEditor.putString("user_phone", userInfo.getData().getTelephone());
        mEditor.putString("nick_name",userInfo.getData().getNickname());
        mEditor.putString("user_headpic", userInfo.getData().getHeadpic());
        mEditor.putString("user_dovecote", userInfo.getData().getLoftname());
        mEditor.putString("user_code", userInfo.getData().getUserid());
        mEditor.putString("user_age", String.valueOf(userInfo.getData().getAge()));
        mEditor.putString("user_sex", String.valueOf(userInfo.getData().getGender()));
        mEditor.putString("user_year",userInfo.getData().getExperience() == null?"1年":String.valueOf(userInfo.getData().getExperience()));
        mEditor.commit();


        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("user_info",userInfo);
        intent.putExtra("LoginActivity", true);
        startActivity(intent);
        finish();

    }


    @Override
    public void loginFail(String msg) {

    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_LOGIN:
                method = MethodConstant.LOGIN;
                break;
            case MethodType.METHOD_TYPE_USER_DETAIL:
                method = MethodConstant.USER_INFO_DETAIL;
                break;
        }
        return method;
    }

    @Override
    public void setNetTag(boolean netTag) {
        super.setNetTag(netTag);

        if (!netTag) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();
        }

    }
    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());

        switch (methodType){
            case MethodType.METHOD_TYPE_LOGIN:
                map.put("password",getUserPwd());
                map.put("telephone",getUserPhone());
                break;
            case MethodType.METHOD_TYPE_USER_DETAIL:
                map.put("token",getToken());
                map.put("userid",getUserId());
                break;
        }

        return map;
    }


    @Override
    public void onBackPressed() {
    }
}
