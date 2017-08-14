package com.haoxi.dove.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.haoxi.dove.R;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AppComponent;
import com.haoxi.dove.observable.MyCancleObservable;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.AppManager;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.StringUtils;

import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by lifei on 2016/12/26.
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements MvpView {

    protected String mLoginMac     = "";
    protected String mLoginNetwork = "";
    protected String mToken        = "";
    protected String mUserObjId    = "";
    protected String mUserPhone    = "";

    protected T mPresenter;

    private int mContentViewId;

    private int mMenuId;

    private int mToolbarTitle;

    private int mToolbarIndicator;


    protected boolean netTag = true;

    protected Dialog mDialog;

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (ApiUtils.isNetworkConnected(context)) {
                mLoginNetwork = ApiUtils.getConnectedTypeName(context);

            } else {
                ApiUtils.showToast(BaseActivity.this, getString(R.string.net_conn_2));
            }
        }
    };
    protected SharedPreferences        userInfoPf;
    protected SharedPreferences.Editor mEditor;
    protected AppManager               mAppManager;
    protected MyCancleObservable myCancleObservable;
    protected SharedPreferences mPreferences;

    protected Map<String,Boolean> numMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(getLayoutView());

        ApiUtils.setWindowStatusBarColor(this,R.color.colorPrimary);

        mPreferences = getSharedPreferences(ConstantUtils.TRAIL, Context.MODE_PRIVATE);

        numMap = MyApplication.getMyBaseApplication().getNumMap();

        if (getClass().isAnnotationPresent(ActivityFragmentInject.class)){
            ActivityFragmentInject annotation = getClass().getAnnotation(ActivityFragmentInject.class);
            mContentViewId = annotation.contentViewId();
            mMenuId = annotation.menuId();
            mToolbarTitle = annotation.toolbarTitle();
            mToolbarIndicator = annotation.toolbarIndicator();
        }else {
            throw new RuntimeException("类没有进行注解异常");
        }

        setContentView(mContentViewId);


        ButterKnife.bind(this);

        mAppManager = AppManager.getAppManager();

        myCancleObservable = MyCancleObservable.getInstance();

        initInject();
        init();

        if (mMenuId != -1) {
            initToolbar();
        }


        userInfoPf = getSharedPreferences(ConstantUtils.USERINFO, MODE_PRIVATE);
        mEditor = userInfoPf.edit();

        mToken = userInfoPf.getString("user_token", "");
        mUserObjId = userInfoPf.getString("user_objid", "");
        mUserPhone = userInfoPf.getString("user_phone", "");

        mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);//设置对话框不能消失

        View view = getLayoutInflater().inflate(R.layout.progressdialog, null);

        mDialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));


        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver,filter);
        
    }

    private void initToolbar() {

        Toolbar mToolbar = (Toolbar) findViewById(R.id.tl_custom);
        if (mToolbar != null) {
            mToolbar.setContentInsetStartWithNavigation(0);
            setSupportActionBar(mToolbar);

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            if (mToolbarTitle != -1) {
                getSupportActionBar().setTitle(mToolbarTitle);
            }else {
                getSupportActionBar().setTitle("");
            }

            if (mToolbarIndicator != -1) {
                getSupportActionBar().setHomeAsUpIndicator(mToolbarIndicator);
            }else {
                getSupportActionBar().setHomeAsUpIndicator(R.mipmap.btn_back_normal);
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void initToolbar(Toolbar toolbar, boolean homeAsUpEnable, String title){
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnable);
    }

    protected void initToolbar(Toolbar toolbar,boolean homeAsUpEnable,int resTitle){
        initToolbar(toolbar,homeAsUpEnable,getString(resTitle));
    }

    protected abstract void initInject();

    public AppComponent getAppComponent(){
        return ((MyApplication)getApplication()).getAppComponent();
    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        params.gravity = Gravity.CENTER;
        //   params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    protected abstract void init();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        mDialog.dismiss();

    }

    @Override
    public void showProgress() {
        if (mDialog != null) {
            mDialog.show();
        }
        setDialogWindow(mDialog);
    }

    @Override
    public void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    @Override
    public void showErrorMsg(String errorMsg) {
        ApiUtils.showToast(this,errorMsg);
    }

    public String getMacAddress() {

        mLoginMac = ApiUtils.getPros("ro.serialno");
        if ("".equals(mLoginMac)) {
            ApiUtils.showToast(BaseActivity.this,getResources().getString(R.string.mac_address_empty));
        }

        return mLoginMac;
    }

    public String getNetwork() {

        mLoginNetwork = ApiUtils.getConnectedTypeName(BaseActivity.this);

        return mLoginNetwork;
    }

    @Override
    public void setNetTag(boolean netTag) {
        this.netTag = netTag;
    }

    @Override
    public String getTime() {

        long time = StringUtils.tsToString(StringUtils.getNowTimestamp());

        return String.valueOf(time);
    }

    @Override
    public String getSign() {
        String sign = "";
        if (getMethod() != null) {
            sign = MD5Tools.MD5(getMethod()+getTime()+getVersion()+ ConstantUtils.APP_SECRET);
        }

        return sign;
    }

    @Override
    public String getVersion() {

        String appName = ApiUtils.getAppName(this);
        String versionName = ApiUtils.getVersionName(this);
        if (versionName != null) {
           return "1.0";
        }
        return "1.0";
    }
}

