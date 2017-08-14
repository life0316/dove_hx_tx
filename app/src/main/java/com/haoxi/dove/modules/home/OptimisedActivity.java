package com.haoxi.dove.modules.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.User;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerMyOptComponent;
import com.haoxi.dove.inject.OptMoudle;
import com.haoxi.dove.modules.mvp.views.IOptView;
import com.haoxi.dove.modules.loginregist.LoginActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomDialog;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.xiaopan.switchbutton.SwitchButton;

/**
 * Created by lifei on 2017/1/10.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_optimised)
public class OptimisedActivity extends BaseActivity implements IOptView{

    private static final String TAG = "OptimisedActivity";

    @BindView(R.id.activity_optimised_sb)
    SwitchButton mSwitchButton;
    @BindView(R.id.auto_login)
    ImageButton mLoginButton;
    @BindView(R.id.custom_toolbar_iv) ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv) TextView mTitleTv;
    @BindView(R.id.activity_modify_password) TextView mModifyPassword;

    private User user;
    private SharedPreferences loginsp = null;
    private SharedPreferences.Editor loginEditor;

    private Handler mHandler = new Handler();

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;
    private boolean isAuto;

    @Override
    protected void initInject() {

        DaggerMyOptComponent.builder()
                .appComponent(getAppComponent())
                .optMoudle(new OptMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        Intent intent = getIntent();
        if (intent != null) {
            user = (User) intent.getSerializableExtra("userobject");
        }

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("设置");
        
        loginsp = getSharedPreferences(ConstantUtils.LOGINSP, Context.MODE_PRIVATE);
        loginEditor = loginsp.edit();
        mSwitchButton.setChecked(loginsp.getBoolean("autocb",false));
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loginEditor.putBoolean("autocb", mSwitchButton.isChecked());
                loginEditor.commit();
            }
        });

        isAuto = loginsp.getBoolean("autocb",false);

        if (isAuto) {
            mLoginButton.setImageResource(R.mipmap.icon_open);
        }else {
            mLoginButton.setImageResource(R.mipmap.icon_closed);
        }

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isAuto = loginsp.getBoolean("autocb",false);

                if (isAuto) {
                    mLoginButton.setImageResource(R.mipmap.icon_closed);
                    loginEditor.putBoolean("autocb", false);
                }else {
                    mLoginButton.setImageResource(R.mipmap.icon_open);
                    loginEditor.putBoolean("autocb", true);
                }

                loginEditor.commit();
            }
        });

    }


    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli(){
        mRxBus.post("isLoad",false);

        this.finish();
    }

    @OnClick(R.id.activity_optimised_manager)
    void managerOnCli(){
//
//        Intent intent = new Intent(OptimisedActivity.this,PersonalActivity.class);
//        intent.putExtra("userobject",user);
//        startActivity(intent);

    }

    @OnClick(R.id.activity_optimised_currency)
    void currencyOnCli(){

        Intent intent = new Intent(OptimisedActivity.this,CurrencyActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.activity_optimised_about)
    void aboutOnCli(){
        Intent intent = new Intent(OptimisedActivity.this,AboutActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.activity_modify_password)
    void passwordOnCli(){
//        Intent intent = new Intent(OptimisedActivity.this,ModifyPasswordActivity.class);
//        startActivity(intent);
    }

    @OnClick(R.id.activity_optimised_exit)
    void exitApp(){
        showExitDialog();
    }


    public void toLoginActivity(String msg) {

        ApiUtils.showToast(this,msg);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();

        mAppManager.finishAllActivity();
    }

    @Override
    public void showExitDialog() {

        final CustomDialog dialog = new CustomDialog(this,"退出信鸽","确定要退出信鸽？","确定","取消");
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {

//                codePresenter.exit(getUserPhone(),mToken);
                ourCodePresenter.exitApp(getParaMap());

                dialog.dismiss();
            }

            @Override
            public void doCancel() {

                dialog.dismiss();
            }
        });
    }

    public String getUserPhone() {
        return mUserPhone;
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",mToken);

        return map;
    }

    @Override
    public void toDo() {

        String pwdsp = loginsp.getString("password", "");
        Boolean isRemCb = loginsp.getBoolean("remebercb", false);
        Boolean isAutoCb = loginsp.getBoolean("autocb", false);
        String phone = loginsp.getString("username","");

        loginEditor.putString("username",phone);
        loginEditor.putString("password",pwdsp);
        loginEditor.putBoolean("remebercb",isRemCb);
        loginEditor.putBoolean("autocb",isAutoCb);
        loginEditor.commit();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();

        mAppManager.finishAllActivity();
    }

    @Override
    public String getMethod() {
        return "/app/user/logout";
    }

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
