package com.haoxi.dove.base;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.AppManager;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements MvpView {
    protected String mToken        = "";
    protected String mUserObjId    = "";
    protected String mUserPhone    = "";
    private int mToolbarTitle;
    private int mToolbarIndicator;
    protected boolean netTag = true;
    protected Dialog mDialog;

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (ApiUtils.isNetworkConnected(context)) {
//                mLoginNetwork = ApiUtils.getConnectedTypeName(context);
            } else {
                ApiUtils.showToast(BaseActivity.this, getString(R.string.net_conn_2));
            }
        }
    };
    protected AppManager            mAppManager;
    protected Map<String,Boolean> numMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ApiUtils.setWindowStatusBarColor(this,R.color.colorPrimary);
        numMap = MyApplication.getMyBaseApplication().getNumMap();

        int mContentViewId;
        int mMenuId;
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
        initInject();
        init();
        if (mMenuId != -1) {
            initToolbar();
        }
        mToken = SpUtils.getString(this, SpConstant.USER_TOKEN);
        mUserObjId = SpUtils.getString(this, SpConstant.USER_OBJ_ID);
        mUserPhone = SpUtils.getString(this, SpConstant.USER_TELEPHONE);
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
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnable);
        }
    }

    protected void initToolbar(Toolbar toolbar,boolean homeAsUpEnable,int resTitle){
        initToolbar(toolbar,homeAsUpEnable,getString(resTitle));
    }

    protected void initInject(){

    }

    public AppComponent getAppComponent(){
        return ((MyApplication)getApplication()).getAppComponent();
    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }
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
        String versionName = ApiUtils.getVersionName(this);
        if (versionName != null) {
           return "1.0";
        }
        return "1.0";
    }
    @Override
    public void toDo() {
    }

    protected Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        return map;
    }
}

