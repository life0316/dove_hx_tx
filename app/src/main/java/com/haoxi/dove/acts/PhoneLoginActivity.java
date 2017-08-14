package com.haoxi.dove.acts;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.modules.loginregist.UserProtocolActivity;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.StringUtils;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_phonelogin)
public class PhoneLoginActivity extends BaseActivity{

//    private static final String TAG = "PhoneLoginActivity";

    @BindView(R.id.activity_phonelogin_phone)
    EditText mEtPhone;
    @BindView(R.id.activity_phonelogin_vercode)
    EditText mEtVerCode;
    @BindView(R.id.activity_phonelogin_send)
    Button   mSendCode;
    @BindView(R.id.activity_phonelogin_login)
    Button   mNextBtn;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;
    @BindView(R.id.activity_phonelogin_pro)
    TextView  mPhoneloginPro;

//    private String mUserPhone;


    public  boolean isChange = false;
    private boolean tag      = true;
    private int     i        = 60;
    Thread mThread = null;

    private String editPwd;

    private Handler mHandler = new Handler();

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {

            if (editPwd.length() == 0 || "".equals(editPwd) || editPwd == null) {
                mNextBtn.setBackgroundResource(R.drawable.btn_pigeon_bg2);
                mNextBtn.setEnabled(false);
            } else {

                mNextBtn.setEnabled(true);
                mNextBtn.setBackgroundResource(R.drawable.btn_pigeon_bg);
            }
        }
    };

//    //客户端输入的验证码
//    private        String valicationCode;
    //服务器端获取的验证码
    private static String serverValicationCode;
//    //获取验证码时所带的参数
//    private Map<String, Object> codeParams = new HashMap<String, Object>();

    @OnClick(R.id.custom_toolbar_iv)
    void backIv() {
        this.finish();
    }

    @OnClick(R.id.activity_phonelogin_send)
    void sendCode() {
        if (!isValidate())
            return;
        mSendCode.setText("获取验证码");
        mSendCode.setClickable(true);
        isChange = true;
        changeBtnGetCode();
        getValidateCode();
    }
    @OnClick(R.id.activity_phonelogin_pro)
    void openUserProtocolOncli(){
        Intent  intent = new Intent(getApplicationContext(),UserProtocolActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.activity_phonelogin_login)
    void next() {
        String mPhone = mEtPhone.getText().toString().trim();
        String mCode = mEtVerCode.getText().toString().trim();

        if (mPhone.equals("")) {
            ApiUtils.showToast(PhoneLoginActivity.this, "请输入手机号");
            return;
        } else if (!StringUtils.isPhoneNumberValid(mPhone)) {
            ApiUtils.showToast(PhoneLoginActivity.this, "输入的手机号格式不对");
            return;
        }

        if (mCode.equals("")) {
            ApiUtils.showToast(PhoneLoginActivity.this,"请输入验证码");
            return;
        }
        if (!mCode.equals(serverValicationCode)) {
            ApiUtils.showToast(PhoneLoginActivity.this,"验证码错误");
        }

//        Intent intent = new Intent(this, ResetActivity.class);
//        ApiUtils.showToast(PhoneLoginActivity.this,"验证码正确");
//        intent.putExtra("userphone", mPhone);
//        startActivity(intent);
    }

    private boolean isValidate(){

        //获取手机号
        String mUserPhone = mEtPhone.getText().toString().trim();
        if (mUserPhone.isEmpty()){
            ApiUtils.showToast(PhoneLoginActivity.this,"请输入手机号");
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

                        PhoneLoginActivity.this.runOnUiThread(new Runnable() {
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
                    PhoneLoginActivity.this.runOnUiThread(new Runnable() {
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

    /**
     * 说明：获取验证码
     */
    private void getValidateCode() {

        String phone = mEtPhone.getText().toString().trim();
        if (phone.equals("")){
            ApiUtils.showToast(this, "请输入手机号!");
        }else {
            mUserPhone = phone;
            Thread codeThread = new Thread(codeRunnable);
            codeThread.start();
        }
    }

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {
        mTitleTv.setText("手机登录");
//        mBackIv.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (intent != null && "PhoneLoginActivity".equals(intent.getAction())) {
            String phone = intent.getStringExtra("phone");
            if (!phone.equals("")) {
                mEtPhone.setText(phone);
                mEtPhone.setSelection(phone.length());
            }
        }

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
    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        return null;
    }

//    @Override
//    protected int getLayoutView() {
//        return R.layout.activity_phonelogin;
//    }


    private  class CodeHandler extends Handler {
        //持有对本外部类的弱引用
        private final WeakReference<PhoneLoginActivity> mActivity;

        CodeHandler(PhoneLoginActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

            PhoneLoginActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case 1:
                        serverValicationCode = (String) msg.obj;
                        ApiUtils.showToast(PhoneLoginActivity.this, "获取验证码成功！");
                        //                        activity.runOnUiThread(new Runnable() {
                        //                            @Override
                        //                            public void run() {
                        //                                mEtVerCode.setText(serverValicationCode);
                        //                            }
                        //                        });

                        break;
                    case -1:
                        ApiUtils.showToast(PhoneLoginActivity.this, "获取验证码失败!");
                        break;
                    case 0:
                        ApiUtils.showToast(PhoneLoginActivity.this, "获取验证码失败!");
                        break;
                    default:
                        break;
                }
            }
        }
    }


    private final CodeHandler codeHandler = new CodeHandler(this);

    private Runnable codeRunnable = new Runnable() {
        @Override
        public void run() {

            Message msg = new Message();
//            Map<String, Object> map = new HashMap<>();
//            map.put("userphone", mUserPhone);

            //获取服务器数据
            try {
                serverValicationCode = PhoneLoginActivity.this.getCode();

                //返回true则将消息what值改为1，false则为-1，异常为0;
                if (serverValicationCode.equals("")) {
                    msg.what = -1;
                } else {
                    msg.what = 1;
                    msg.obj = serverValicationCode;
                }
            } catch (Exception e) {

                msg.what = 0;
                e.printStackTrace();
            }

            codeHandler.sendMessage(msg);
        }
    };

    private String getCode() {
        String validateCode = "";


        if (ApiUtils.isNetworkConnected(this)) {
            try {
                validateCode = "去获取服务器端的验证码";
                //TODO  去获取服务器端的验证码
                //                validateCode = getSeviceCode(map);
            } catch (Exception e) {
                if ("".equals(validateCode)) {
                    throw e;
                }
            }
        }
        return validateCode;
    }
}
