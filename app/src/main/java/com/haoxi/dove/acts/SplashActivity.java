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
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;

import java.util.HashMap;
import java.util.Map;

@ActivityFragmentInject(contentViewId = R.layout.activity_splash)
public class SplashActivity extends BaseActivity implements ILoginView {

    private Handler mHandler = new Handler();

    private LoginPresenter presenter;

    private String mPwdsp;

    private int methodType = MethodType.METHOD_TYPE_LOGIN;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        presenter = new LoginPresenter(new LoginModel(this));
        presenter.attachView(this);
        mPwdsp = SpUtils.getString(this, SpConstant.USER_PWD);
        final Boolean isAutoCb = SpUtils.getBoolean(this,SpConstant.IS_AUTO);

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

    public String getUserPhone() {
        return mUserPhone;
    }

    public String getUserPwd() {
        if ("".equals(mPwdsp) || mPwdsp == null || mPwdsp.isEmpty()) {
            ApiUtils.showToast(this, getResources().getString(R.string.user_pwd_empty));
            return "";
        }

        return MD5Tools.MD5(mPwdsp);
    }

    public String getUserId() {

        return SpUtils.getString(this,SpConstant.USER_OBJ_ID);
    }

    public String getToken() {
        return SpUtils.getString(this,SpConstant.USER_TOKEN);
    }

    @Override
    public void toGetDetail(OurUser user) {
        SpUtils.putString(this,SpConstant.USER_OBJ_ID,user.getData().getUserid());
        SpUtils.putString(this,SpConstant.USER_TOKEN,user.getData().getToken());

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void loginFail(String msg) {

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
