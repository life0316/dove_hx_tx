package com.haoxi.dove.acts;

import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.mvp.views.IResetView;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_reset)
public class ResetActivity extends BaseActivity implements IResetView{

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_reset_phone)
    EditText mEtPhone;
    @BindView(R.id.activity_reset_newpassword) EditText mEtNewPwd;
    @BindView(R.id.activity_reset_finish)
    Button mBtnFinish;
    @BindView(R.id.act_login_cb)
    CheckBox mCb;

    private OurCodePresenter codePresenter;
    private static Handler mHandler = new Handler();

    @Override
    public void toDo() {
        SpUtils.putString(this,SpConstant.USER_TELEPHONE,getTelephone());
        SpUtils.putString(this,SpConstant.USER_PWD,"");
        SpUtils.putBoolean(this,SpConstant.IS_AUTO,false);
        SpUtils.putBoolean(this,SpConstant.IS_REM,false);
        finish();
    }

    @OnClick(R.id.activity_reset_finish)
    void reset(){
        if (getTelephone() == null || "".equals(getTelephone())) {
            ApiUtils.showToast(ResetActivity.this,"请输入手机号");
            return;
        }
        if (getNewPwd() == null || "".equals(getNewPwd())) {
            ApiUtils.showToast(ResetActivity.this,"请输入新密码");
            return;
        }
        codePresenter.resetPwd(getParaMap());
    }
    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.USER_TELEPHONE,getTelephone());
        map.put(MethodParams.PARAMS_PWD,MD5Tools.MD5(getNewPwd()));
        return map;
    }
    @Override
    public String getMethod() {
        return MethodConstant.RESET_PASSWORD;
    }

    @Override
    protected void init() {
        codePresenter = new OurCodePresenter(this);
        mTitleTv.setText("找回密码");
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mEtNewPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    mEtNewPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        Intent intent = getIntent();
        if (intent != null) {
            String phone = intent.getStringExtra("userphone");
            if (phone != null) {
                mEtPhone.setText(phone);
            }
        }

        mEtNewPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                mBtnFinish.setBackgroundResource(R.drawable.btn_pigeon_bg2);
                mBtnFinish.setEnabled(false);
            } else {
                mBtnFinish.setEnabled(true);
                mBtnFinish.setBackgroundResource(R.drawable.btn_pigeon_bg);
            }
        }
    };

    @Override
    public String getTelephone() {
        return mEtPhone.getText().toString().trim();
    }

    @Override
    public String getNewPwd() {
        return mEtNewPwd.getText().toString().trim();
    }
}
