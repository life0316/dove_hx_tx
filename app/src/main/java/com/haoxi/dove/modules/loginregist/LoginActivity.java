package com.haoxi.dove.modules.loginregist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.acts.ForgetActivity;
import com.haoxi.dove.acts.MainActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.modules.loginregist.model.LoginModel;
import com.haoxi.dove.modules.loginregist.presenter.LoginPresenter;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.widget.CustomDialog;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Scheduler;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_login,menuId = 0)
public class LoginActivity extends BaseActivity implements ILoginView, CompoundButton.OnCheckedChangeListener {

    private LoginPresenter presenter;

//    private UserInfoPresenter infoPresenter;

    @BindView(R.id.act_login_username)
    EditText mUsernameEt;
    @BindView(R.id.act_login_password)
    EditText mPasswordEt;
    @BindView(R.id.act_login_remenber)
    CheckBox mRemenberPwdCb;
    @BindView(R.id.act_login_auto)
    CheckBox mAutoCb;
    @BindView(R.id.act_login_cb)
    CheckBox mPwdCb;
    @BindView(R.id.act_login_regist)
    TextView mRegistTv;
    @BindView(R.id.act_login_forget_password)
    TextView mForgetPwdTv;
    @BindView(R.id.act_login_phone_login)
    TextView mPhoneLoginTv;

    @BindView(R.id.act_login_loginbtn)
    Button mLoginBtn;

    private static Handler mHandler = new Handler();
    private Observable<Boolean> exitObservable;

    private int methodType = MethodType.METHOD_TYPE_LOGIN;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        ButterKnife.bind(this);

        exitObservable = RxBus.getInstance().register("exit", Boolean.class);

        exitObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean isExit) {
                if (isExit) {
                    LoginActivity.this.finish();
                }
            }
        });

        String usernamesp = SpUtils.getString(this, SpConstant.USER_TELEPHONE);
        String pwdsp = SpUtils.getString(this,SpConstant.USER_PWD);
        Boolean isRemCb = SpUtils.getBoolean(this,SpConstant.IS_REM);
        Boolean isAutoCb = SpUtils.getBoolean(this,SpConstant.IS_AUTO);

        mRemenberPwdCb.setChecked(isRemCb);
        mAutoCb.setChecked(isAutoCb);
        mRemenberPwdCb.setOnCheckedChangeListener(this);
        mPwdCb.setOnCheckedChangeListener(this);
        mAutoCb.setOnCheckedChangeListener(this);

        if (!"".equals(usernamesp)) {
            mUsernameEt.setText(usernamesp);
            mUsernameEt.setSelection(usernamesp.length());

        }
        if (!"".equals(pwdsp)) {
            mPasswordEt.setText(pwdsp);
            mPasswordEt.setSelection(pwdsp.length());
        }

        presenter = new LoginPresenter(new LoginModel(this));
        presenter.attachView(this);

        String mUsername = mUsernameEt.getText().toString().trim();
        String mUserpwd = mPasswordEt.getText().toString().trim();

        if ("".equals(mUsername) && "".equals(mUserpwd)) {
            mLoginBtn.setBackgroundResource(R.drawable.btn_pigeon_bg2);
            mLoginBtn.setEnabled(false);

        } else {
            mLoginBtn.setBackgroundResource(R.drawable.btn_pigeon_bg);
            mLoginBtn.setEnabled(true);
        }


        mPasswordEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun != null) {
                    mHandler.removeCallbacks(delayRun);
                }

                editPwd = s.toString();

                mHandler.postDelayed(delayRun, 500);

            }
        });
    }

    private String editPwd;

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {

            if (editPwd.length() == 0 || "".equals(editPwd) || editPwd == null) {
                mLoginBtn.setBackgroundResource(R.drawable.btn_pigeon_bg2);
                mLoginBtn.setEnabled(false);
            } else {

                mLoginBtn.setEnabled(true);
                mLoginBtn.setBackgroundResource(R.drawable.btn_pigeon_bg);
            }
        }
    };

//    private AlertDialog.Builder builder;
//    private AlertDialog dialog;

    @OnClick(R.id.act_login_loginbtn)
    void login() {

        if ("".equals(getUserPhone()) || "".equals(getUserPwd())) {
            return;
        } else {

            SpUtils.putString(this,SpConstant.USER_TELEPHONE, getUserPhone());

            SpUtils.putBoolean(this,SpConstant.IS_REM, mRemenberPwdCb.isChecked());
            SpUtils.putBoolean(this,SpConstant.IS_AUTO, mAutoCb.isChecked());

            if (mRemenberPwdCb.isChecked()) {
                SpUtils.putString(this, SpConstant.USER_PWD, mPasswordEt.getText().toString().trim());
            } else {
                SpUtils.putString(this, SpConstant.USER_PWD, "");
            }

            doLoginImpl();
        }
    }

    @OnClick(R.id.act_login_forget_password)
    void forget(){
        Intent intent = new Intent(this, ForgetActivity.class);
        startActivity(intent);
    }
    void doLoginImpl() {

        methodType = MethodType.METHOD_TYPE_LOGIN;
        presenter.getDataFromNets(getParaMap());
    }

    @OnClick(R.id.act_login_regist)
    void regist() {

        Intent intent = new Intent(this, RegistActivity.class);
        startActivity(intent);
    }

    public String getUserPhone() {

        String mUsername = mUsernameEt.getText().toString().trim();

        if ("".equals(mUsername) || mUsername.isEmpty()) {
            ApiUtils.showToast(this, getResources().getString(R.string.user_phone));
            return "";
        }

//        if (!StringUtils.isPhoneNumberValid(mUsername)) {
//            ApiUtils.showToast(this, getResources().getString(R.string.user_phone_valid));
//            return "";
//        }

        return mUsername;
    }

    public String getUserPwd() {

        String mPwd = mPasswordEt.getText().toString().trim();

        if ("".equals(mPwd) || mPwd.isEmpty()) {
            ApiUtils.showToast(this, getResources().getString(R.string.user_pwd_empty));
            return "";
        }

        return MD5Tools.MD5(mPwd);

    }

    public String getUserId() {

        return SpUtils.getString(this,SpConstant.USER_OBJ_ID);
    }

    public String getToken() {
        return SpUtils.getString(this,SpConstant.USER_TOKEN);
    }

    @Override
    public void toGetDetail(OurUser user) {

        SpUtils.putBoolean(this, SpConstant.MAIN_EXIT,true);

        SpUtils.putString(this,SpConstant.USER_OBJ_ID,user.getData().getUserid());
        SpUtils.putString(this,SpConstant.USER_TOKEN,user.getData().getToken());
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }


    @Override
    public void loginFail(final String msg) {

        SpUtils.putString(this,SpConstant.USER_TELEPHONE,"");
        SpUtils.putString(this,SpConstant.USER_PWD,"");
        SpUtils.putBoolean(this,SpConstant.IS_AUTO,false);
        SpUtils.putBoolean(this,SpConstant.IS_REM,false);

        final CustomDialog dialog = new CustomDialog(this, "登录失败", msg+",请重新输入", "确定", "取消");
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {

                switch (msg){
                    case "用户不存在":
                        mUsernameEt.setText("");
                        mPasswordEt.setText("");
                        mUsernameEt.setFocusableInTouchMode(true);
                        mUsernameEt.setFocusable(true);
                        mUsernameEt.requestFocus();
                        break;
                    case "密码错误":
                        mPasswordEt.setText("");
                        break;
                    default:
                        mPasswordEt.setText("");
                        break;
                }
                dialog.dismiss();
            }

            @Override
            public void doCancel() {

                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        //p 与 v 断开连接
        presenter.detachView();
        super.onDestroy();

        RxBus.getInstance().unregister("exit",exitObservable);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.act_login_cb:
                if (isChecked) {
                    mPasswordEt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mPasswordEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                break;

            case R.id.act_login_remenber:
                if (!isChecked) {
                    mAutoCb.setChecked(false);
                }
                break;

            case R.id.act_login_auto:
                if (isChecked) {
                    mRemenberPwdCb.setChecked(true);
                }
                break;
        }
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
}