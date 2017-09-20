package com.haoxi.dove.modules.home;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerMyOptComponent;
import com.haoxi.dove.inject.OptMoudle;
import com.haoxi.dove.modules.mvp.views.IOptView;
import com.haoxi.dove.modules.loginregist.LoginActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.widget.CustomDialog;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.OnClick;
import me.xiaopan.switchbutton.SwitchButton;

@ActivityFragmentInject(contentViewId = R.layout.activity_optimised)
public class OptimisedActivity extends BaseActivity implements IOptView{
    @BindView(R.id.activity_optimised_sb)
    SwitchButton mSwitchButton;
    @BindView(R.id.auto_login)
    ImageButton mLoginButton;
    @BindView(R.id.custom_toolbar_iv) ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv) TextView mTitleTv;
    @BindView(R.id.activity_modify_password) TextView mModifyPassword;
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
        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("设置");
        mSwitchButton.setChecked(SpUtils.getBoolean(this, SpConstant.IS_AUTO));
        mSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SpUtils.putBoolean(OptimisedActivity.this, SpConstant.IS_AUTO, mSwitchButton.isChecked());
            }
        });
        isAuto = SpUtils.getBoolean(this, SpConstant.IS_AUTO);
        if (isAuto) {
            mLoginButton.setImageResource(R.mipmap.icon_open);
        }else {
            mLoginButton.setImageResource(R.mipmap.icon_closed);
        }
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAuto = SpUtils.getBoolean(OptimisedActivity.this, SpConstant.IS_AUTO);
                if (isAuto) {
                    mLoginButton.setImageResource(R.mipmap.icon_closed);
                    SpUtils.putBoolean(OptimisedActivity.this, SpConstant.IS_AUTO, false);
                }else {
                    mLoginButton.setImageResource(R.mipmap.icon_open);
                    SpUtils.putBoolean(OptimisedActivity.this, SpConstant.IS_AUTO, true);
                }
            }
        });
    }
    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli(){
        this.finish();
    }
    @OnClick(R.id.activity_optimised_about)
    void aboutOnCli(){
        Intent intent = new Intent(OptimisedActivity.this,AboutActivity.class);
        startActivity(intent);
    }
    @OnClick(R.id.activity_optimised_exit)
    void exitApp(){
        showExitDialog();
    }

    @Override
    public void showExitDialog() {
        final CustomDialog dialog = new CustomDialog(this,"退出信鸽","确定要退出信鸽？","确定","取消");
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                ourCodePresenter.exitApp(getParaMap());
                dialog.dismiss();
            }
            @Override
            public void doCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,mUserObjId);
        map.put(MethodParams.PARAMS_TOKEN,mToken);
        return map;
    }

    @Override
    public void toDo() {
        String pwdsp = SpUtils.getString(this,SpConstant.USER_PWD);
        Boolean isRemCb = SpUtils.getBoolean(this,SpConstant.IS_REM);
        Boolean isAutoCb = SpUtils.getBoolean(this,SpConstant.IS_AUTO);
        String phone = SpUtils.getString(this,SpConstant.USER_TELEPHONE);
        SpUtils.putString(this,SpConstant.USER_TELEPHONE,phone);
        SpUtils.putString(this,SpConstant.USER_PWD,pwdsp);
        SpUtils.putBoolean(this,SpConstant.IS_REM,isRemCb);
        SpUtils.putBoolean(this,SpConstant.IS_AUTO,isAutoCb);
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        this.finish();
        mAppManager.finishAllActivity();
    }
    @Override
    public String getMethod() {
        return MethodConstant.USER_LOGOUT;
    }

}
