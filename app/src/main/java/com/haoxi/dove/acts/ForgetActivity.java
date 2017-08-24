package com.haoxi.dove.acts;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.newin.IForgetView;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.OurCode;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_forget)
public class ForgetActivity extends BaseActivity implements IForgetView {

    @BindView(R.id.activity_forget_phone) EditText mEtPhone;
    @BindView(R.id.activity_forget_vercode) EditText mEtVerCode;
    @BindView(R.id.activity_forget_send) Button   mSendCode;
    @BindView(R.id.activity_forget_next) Button   mNext;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv) TextView mTitleTv;

    private OurCodePresenter codePresenter;

    public  boolean isChange = false;
    private boolean tag      = true;
    private int     i        = 60;

    private int type =MethodType.METHOD_TYPE_VALID_VER_CODE;

    Thread mThread = null;

    private static Handler mHandler = new Handler();
    private Observable<Boolean> finishObservable;

    @OnClick(R.id.custom_toolbar_iv)
    void backIv(){
        this.finish();
    }

    @OnClick(R.id.activity_forget_send)
    void sendCode(){
        if (!isValidate())
            return;
        mSendCode.setText("获取验证码");
        mSendCode.setClickable(true);
        isChange = true;
        changeBtnGetCode();
        getValidateCode();
    }

    @OnClick(R.id.activity_forget_next)
    void next(){

        if (getTelephone().equals("") && getTelephone() == null) {
            ApiUtils.showToast(ForgetActivity.this,"请输入手机号");
            return;
        } else if (!StringUtils.isPhoneNumberValid(getTelephone())) {
            ApiUtils.showToast(ForgetActivity.this,"输入的手机号格式不对");
            return;
        }

        if (getVerCode().equals("") && getVerCode() == null) {
            ApiUtils.showToast(ForgetActivity.this,"请获取验证码");
            return;
        }

        type = MethodType.METHOD_TYPE_VALID_VER_CODE;

//        Map<String,String> map = new HashMap<>();
//        map.put("method",getMethod());
//        map.put("telephone",getTelephone());
//        map.put("sign",getSign());
//        map.put("ver_code",getVerCode());
//        map.put("time",getTime());
//        map.put("version",getVersion());

        codePresenter.getValidCode(getParamMap());
    }
    private Map<String,String> getParamMap(){

        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("telephone",getTelephone());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());

        switch (type){
            case MethodType.METHOD_TYPE_VALID_VER_CODE:
                map.put("ver_code",getVerCode());
                break;
            case MethodType.METHOD_TYPE_REQUEST_VER_CODE:
                map.put(MethodParams.USER_TELEPHONE,getTelephone());
                break;
        }
        return map;
    }

    private boolean isValidate(){

        //获取手机号
        String mUserPhone = mEtPhone.getText().toString().trim();
        if (mUserPhone.isEmpty()){
            ApiUtils.showToast(ForgetActivity.this,"请输入手机号");
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
                        ForgetActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSendCode.setText("重新获取("+i+")");
                                mSendCode.setClickable(false);
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
                    ForgetActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSendCode.setText("获取验证码");
                            mSendCode.setClickable(true);
                        }
                    });
                }
        };
        mThread.start();

    }
    private void getValidateCode() {

        String phone = mEtPhone.getText().toString().trim();
        if (phone.equals("")){
            ApiUtils.showToast(this, "请输入手机号!");
        }else {
            mUserPhone = phone;
            type = MethodType.METHOD_TYPE_REQUEST_VER_CODE;

//            Map<String,String> map = new HashMap<>();
//            map.put(MethodParams.PARAMS_METHOD,getMethod());
//            map.put(MethodParams.USER_TELEPHONE,getTelephone());
//            map.put(MethodParams.PARAMS_SIGEN,getSign());
//            map.put(MethodParams.PARAMS_TIME,getTime());
//            map.put(MethodParams.PARAMS_VERSION,getVersion());

            codePresenter.getRequestCode(getParamMap());
        }
    }

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {
        mTitleTv.setText("找回密码");
        mBackIv.setVisibility(View.VISIBLE);

        codePresenter = new OurCodePresenter(this);

        finishObservable = RxBus.getInstance().register("finish",Boolean.class);
        finishObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    finish();
                }
            }
        });

        mEtVerCode.addTextChangedListener(new TextWatcher() {
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

        String mPhonenumber = mEtPhone.getText().toString().trim();
        String mVercode = mEtVerCode.getText().toString().trim();

        if ("".equals(mPhonenumber) || "".equals(mVercode)) {
            mNext.setBackgroundResource(R.drawable.btn_pigeon_bg2);
            mNext.setEnabled(false);
        } else {
            mNext.setBackgroundResource(R.drawable.btn_pigeon_bg);
            mNext.setEnabled(true);
        }
    }

    private String editPwd;

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {

            if (editPwd.length() == 0 || "".equals(editPwd) || editPwd == null) {
                mNext.setBackgroundResource(R.drawable.btn_pigeon_bg2);
                mNext.setEnabled(false);
            } else {

                mNext.setEnabled(true);
                mNext.setBackgroundResource(R.drawable.btn_pigeon_bg);
            }
        }
    };

    @Override
    public void toDo() {

        switch (type){
            case MethodType.METHOD_TYPE_REQUEST_VER_CODE:
                break;
            case MethodType.METHOD_TYPE_VALID_VER_CODE:

                Intent intent = new Intent(this, ResetActivity.class);
                intent.putExtra("userphone", getTelephone());
                startActivity(intent);
                break;
        }
    }

    @Override
    public String getMethod() {

        String method = "";
        switch (type){
            case MethodType.METHOD_TYPE_VALID_VER_CODE:
                method = MethodConstant.VALID_VER_CODE;
                break;
            case MethodType.METHOD_TYPE_REQUEST_VER_CODE:
                method = MethodConstant.REQUEST_VER_CODE;
                break;
        }
        return method;
    }

    @Override
    public String getTelephone() {
        return mEtPhone.getText().toString().trim();
    }

    @Override
    public String getVerCode() {
        return mEtVerCode.getText().toString().trim();
    }

    @Override
    public void getCodeSussess(OurCode ourCode) {
        ApiUtils.showToast(ForgetActivity.this,"验证码正确");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister("finish",finishObservable);
    }
}
