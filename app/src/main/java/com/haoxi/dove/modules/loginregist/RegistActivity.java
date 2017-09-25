package com.haoxi.dove.modules.loginregist;

import android.content.Intent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.acts.MainActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.DaggerRegistComponent;
import com.haoxi.dove.inject.RegistMoudle;
import com.haoxi.dove.modules.loginregist.presenter.RegistPresenter;
import com.haoxi.dove.modules.loginregist.presenter.RegistPresenter2;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.modules.loginregist.ui.IRegistView;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
@ActivityFragmentInject(contentViewId = R.layout.activity_regist,menuId = 0)
public class RegistActivity extends BaseActivity implements IRegistView<OurUser>,ILoginView {
    private RegistPresenter presenter;
    @BindView(R.id.custom_toolbar_tv) TextView mTabTitleTv;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mTabBackIv;
    @BindView(R.id.act_regist_phone) EditText mUserPhoneEt;
    @BindView(R.id.act_regist_nickname) EditText mUserNameEt;
    @BindView(R.id.act_regist_vercode) EditText mVerCodeEt;
    @BindView(R.id.act_regist_password) EditText mPwdEt;
    @BindView(R.id.act_regist_repassword) EditText mRepwdEt;
    @BindView(R.id.act_regist_tv_sex) TextView mSexTv;
    @BindView(R.id.act_regist_tv_data) TextView mUserBirthTv;
    @BindView(R.id.act_regist_cb) CheckBox mAgreeCb;
    @BindView(R.id.act_regist_confirm) Button mRegistBtn;
    @BindView(R.id.act_regist_sendcode) Button mSendCodeBtn;
    @BindView(R.id.act_regist_user_protocol) TextView mUserProtocol;

    private String verCode = "";

    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    RegistPresenter2 registPresenter;
    @Inject
    RxBus mRxBus;

    private int methodType = MethodType.METHOD_TYPE_REGISTER;

    @Override
    protected void initInject() {
        DaggerRegistComponent.builder()
                .appComponent(getAppComponent())
                .registMoudle(new RegistMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        presenter = new RegistPresenter();
        presenter.attachView(this);
        mTabTitleTv.setText(getResources().getString(R.string.app_user_regist));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String strDate = format.format(date);
        mUserBirthTv.setText(strDate);
    }

    @OnClick(R.id.act_regist_user_protocol)
    void openUserProtocolOncli(){
        Intent  intent = new Intent(this,UserProtocolActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.act_regist_confirm)
    void registOncli(){
        if (!ApiUtils.isNetworkConnected(RegistActivity.this)){
            ApiUtils.showToast(RegistActivity.this,getString(R.string.net_conn_2));
            return;
        }
        if (isValid()){
            if (!mAgreeCb.isChecked()){
                ApiUtils.showToast(this, "您还没有同意用户协议");
                return;
            }
        }
        methodType = MethodType.METHOD_TYPE_REGISTER;
        registPresenter.getDataFromNets(getParaMap());
    }

    private boolean isValid() {
        if ("".equals(getUserPhone())){
            ApiUtils.showToast(this,getString(R.string.user_phone));
            return false;
        }if (!StringUtils.isPhoneNumberValid(getUserPhone())){
            ApiUtils.showToast(this,getString(R.string.user_phone_valid));
            return false;
        }
        if ("".equals(getUserEmail())){
            ApiUtils.showToast(this,getString(R.string.user_name));
            return false;
        }
        if ("".equals(getVerCode())){
            ApiUtils.showToast(this,"请输入短信验证码");
            return false;
        }
        if ("".equals(getUserPwd())){
            ApiUtils.showToast(this,getString(R.string.user_pwd));
            return false;
        }else if (getUserPwd().length() < 6) {
            ApiUtils.showToast(this, getString(R.string.user_pwd_size));
            return false;
        }
        if ("".equals(getUserRepwd())){
            ApiUtils.showToast(this,getString(R.string.user_pwd_re));
            return false;
        }else if(!getUserPwd().equals(getUserRepwd())){
            ApiUtils.showToast(this, getString(R.string.user_pwd_repwd));
            return false;
        }
        if ("".equals(getUserSex())){
            ApiUtils.showToast(this,getString(R.string.user_sex_select));
            return false;
        }
        return true;
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnlic(){
        finish();
    }
    @OnClick(R.id.act_regist_rl_data)
    void setUserAgeOnlic(){
        presenter.setUserAge(this,mUserBirthTv);
    }
    @OnClick(R.id.act_regist_rl_sex)
    void setUserSexOnlic(){
        presenter.setUserSex(this,mSexTv);
    }
    public String isEmptyStr(String checkStr,String showMsg){
        if ("".equals(checkStr)||checkStr == null||checkStr.isEmpty()){
            ApiUtils.showToast(this,showMsg);
            return "";
        }
        return checkStr;
    }
    @Override
    public String getUserPhone() {
        String mUserPhone = mUserPhoneEt.getText().toString().trim();
        mUserPhone = isEmptyStr(mUserPhone,getResources().getString(R.string.user_phone));
        if (!StringUtils.isPhoneNumberValid(mUserPhone)){
            ApiUtils.showToast(this,getResources().getString(R.string.user_phone_valid));
            return "";
        }
        return mUserPhone;
    }

    @Override
    public String getUserEmail() {
        String mUserMail = mUserNameEt.getText().toString().trim();
        mUserMail = isEmptyStr(mUserMail,getResources().getString(R.string.user_name));
        return mUserMail;
    }

    @Override
    public String getUserPwd() {
        String mUserPwd = mPwdEt.getText().toString().trim();
        mUserPwd = isEmptyStr(mUserPwd,getResources().getString(R.string.user_pwd));
        return mUserPwd;
    }

    public String getUserId() {
        return SpUtils.getString(this, SpConstant.USER_OBJ_ID);
    }
    public String getToken() {
        return SpUtils.getString(this, SpConstant.USER_TOKEN);
    }
    @Override
    public void toGetDetail(OurUser user) {

    }

    @Override
    public void loginFail(String msg) {

    }

    @Override
    public String getUserRepwd() {
        return mRepwdEt.getText().toString().trim() ;
    }
    @Override
    public String getUserSex() {
        String mUserSex = mSexTv.getText().toString().trim();
        switch (mUserSex){
            case "男":
                mUserSex = "1";
                break;
            case "女":
                mUserSex = "2";
                break;
        }

        return mUserSex;
    }

    @Override
    public String getUserBirth() {
        return mUserBirthTv.getText().toString().trim();
    }

    @Override
    public void setUserBirth(int year,int month,int day) {
        if (year == -1 || month == -1 || day == -1) {
            ApiUtils.showToast(this, "已超过当前日期,请重新选择");
            return;
        }
        String tempYear = String.valueOf(year);
        String tempMonth = String.valueOf(month);
        String tempDay= String.valueOf(day);
        if (month < 10){
            tempMonth = "0"+ tempMonth;
        }
        if (day < 10){
            tempDay = "0"+tempDay;
        }
        mUserBirthTv.setText(tempYear+"-"+tempMonth+"-"+tempDay);
    }

    @Override
    public void setUserSex(String userSex) {
        mSexTv.setText(userSex);
    }

    @Override
    public void toMainActivity(OurUser user) {
        SpUtils.putString(this,SpConstant.USER_OBJ_ID,user.getData().getUserid());
        SpUtils.putString(this,SpConstant.USER_TOKEN,user.getData().getToken());
        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
        startActivity(intent);
        mRxBus.post("finish",true);
        finish();
    }

    public Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        switch (methodType){
            case MethodType.METHOD_TYPE_REGISTER:
                map.put("gender",getUserSex());
                map.put("nickname",getUserEmail());
                map.put("telephone",getUserPhone());
                map.put("birthday",getUserBirth());
                map.put("password", MD5Tools.MD5(getUserPwd()));
                map.put("ver_code",getVerCode());
                break;
            case MethodType.METHOD_TYPE_USER_DETAIL:
                map.put("token",getToken());
                map.put("userid",getUserId());
                break;
            case MethodType.METHOD_TYPE_REQUEST_VER_CODE:
                map.put("telephone",getUserPhone());
                break;
        }

        return map;
    }

    private String getVerCode() {
        verCode = mVerCodeEt.getText().toString().trim();
        return verCode;
    }

    @Override
    public String getMethod() {
        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_REQUEST_VER_CODE:
                method = MethodConstant.REQUEST_VER_CODE;
                break;
            case MethodType.METHOD_TYPE_REGISTER:
                method =  MethodConstant.REGISTER;
                break;
            case MethodType.METHOD_TYPE_USER_DETAIL:
                method = MethodConstant.USER_INFO_DETAIL;
                break;
        }
        return method;
    }

    public  boolean isChange = false;
    private boolean tag      = true;
    private int     i        = 60;
    Thread mThread = null;

    @OnClick(R.id.act_regist_sendcode)
    void sendCode(){
        if (!isValidate())
            return;
        mSendCodeBtn.setText("获取验证码");
        mSendCodeBtn.setClickable(true);
        isChange = true;
        changeBtnGetCode();
        getValidateCode();
    }

    private boolean isValidate(){
        String mUserPhone = mUserPhoneEt.getText().toString().trim();
        if (!ApiUtils.isNetworkConnected(RegistActivity.this)){
            ApiUtils.showToast(RegistActivity.this,getString(R.string.net_conn_2));
            return false;
        }
        //获取手机号
        if (mUserPhone.isEmpty()){
            ApiUtils.showToast(RegistActivity.this,"请输入手机号");
            return false;
        }
        if (!StringUtils.isPhoneNumberValid(mUserPhone)){
            ApiUtils.showToast(this, getResources().getString(R.string.user_phone_valid));
            return false;
        }
        return true;
    }

    private void changeBtnGetCode() {
        mThread = new Thread(){

            @Override
            public void run() {
                if (tag){
                    while (i > 0){
                        i--;
                        if (RegistActivity.this == null){
                            break;
                        }
                        RegistActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSendCodeBtn.setText("重新获取("+i+")");
                                mSendCodeBtn.setClickable(false);
                            }
                        });
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    tag = false;
                }

                i = 60;
                tag = true;
                    RegistActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSendCodeBtn.setText("获取验证码");
                            mSendCodeBtn.setClickable(true);
                        }
                    });
            }
        };
        mThread.start();
    }
    private boolean getValidateCode() {
        if (getUserPhone().equals("")){
            ApiUtils.showToast(this, "请输入手机号!");
            return false;
        }else {
            mUserPhone = getUserPhone();
            methodType = MethodType.METHOD_TYPE_REQUEST_VER_CODE;
            ourCodePresenter.getRequestCode(getParaMap());
        }
        return true;
    }
}
