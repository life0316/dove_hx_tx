package com.haoxi.dove.acts;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.loginregist.LoginActivity;
import com.haoxi.dove.modules.loginregist.model.LoginModel;
import com.haoxi.dove.modules.loginregist.presenter.LoginPresenter;
import com.haoxi.dove.modules.loginregist.ui.ILoginView;
import com.haoxi.dove.newin.bean.OurUser;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.ytb.logic.interfaces.AdSplashListener;
import com.ytb.logic.view.HmSplashAd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@ActivityFragmentInject(contentViewId = R.layout.activity_ad)
public class AdActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks,ILoginView{

    @BindView(R.id.ad_fl)
    FrameLayout mAdFl;

    private static final int REQUEST_CODE_AD = 0x0001;
    private Handler mHandler = new Handler();
    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE
    };
    private Boolean isAutoCb;
    private LoginPresenter presenter;

    @Override
    public String getMethod() {
        return MethodConstant.LOGIN;
    }

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {
        presenter = new LoginPresenter(new LoginModel(this));
        presenter.attachView(this);
        isAutoCb = SpUtils.getBoolean(this, SpConstant.IS_AUTO);
        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mAdFl.measure(width, height);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("addddddddd","onResume");
        if (SpUtils.getBoolean(AdActivity.this,SpConstant.CLICK_AD)){
            toDo(100);
        }else {
            requestCodeQRCodePermissions();
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_AD)
    private void requestCodeQRCodePermissions(){
        if (!EasyPermissions.hasPermissions(this,needPermissions)) {
            EasyPermissions.requestPermissions(this,"需要的权限",REQUEST_CODE_AD,needPermissions);
        }else {
            loadAd();
        }
    }

    @OnClick(R.id.ad_btn)
    void adBtn(){
        toDo(100);
    }

    private void loadAd(){
        new HmSplashAd(this,mAdFl,new AdSplashListener() {
            @Override
            public void onNoAD(int i) {
                Log.e("addd",i+"---------onNoAD");

                toDo(100);
//                switch (i){
//                    case 1:
//                    case 2:
//                    case 3:
//                        toDo(3000);
//                        break;
//                }
            }
            @Override
            public void onADDismissed() {
                if (!SpUtils.getBoolean(AdActivity.this,SpConstant.CLICK_AD)){
                    toDo(500);
                }
            }

            @Override
            public void onADPresent() {
                Log.e("addd","---------onADPresent");
            }

            @Override
            public void onADClicked() {
                Log.e("addd","---------onADClicked");

                SpUtils.putBoolean(AdActivity.this,SpConstant.CLICK_AD,true);
            }

            @Override
            public void onADTick(long l) {

                Log.e("addd",l+"---------onADTick");
            }
        },"428");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        loadAd();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        toDo(1000);
    }

    public void toDo(int time) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    if (ApiUtils.isNetworkConnected(AdActivity.this)) {
                        if (isAutoCb) {
                            presenter.getDataFromNets(getParaMap());
                        } else {
                            Intent intent = new Intent(AdActivity.this, LoginActivity.class);
                            startActivity(intent);
                            AdActivity.this.finish();
                        }
                    } else {
                        Intent intent = new Intent(AdActivity.this, LoginActivity.class);
                        startActivity(intent);
                        AdActivity.this.finish();
                    }
        } }, time);
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());

        map.put(MethodParams.PARAMS_PWD,getUserPwd());
        map.put(MethodParams.USER_TELEPHONE,getUserPhone());
        return map;
    }

    public String getUserPhone() {
        return SpUtils.getString(this, SpConstant.USER_TELEPHONE);
    }

    public String getUserPwd() {
        return MD5Tools.MD5(SpUtils.getString(this, SpConstant.USER_PWD));
    }
    @Override
    public void onBackPressed() {
    }

    @Override
    public void toGetDetail(OurUser user) {
        SpUtils.putString(this,SpConstant.USER_OBJ_ID,user.getData().getUserid());
        SpUtils.putString(this,SpConstant.USER_TOKEN,user.getData().getToken());

        Intent intent = new Intent(AdActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpUtils.putBoolean(AdActivity.this,SpConstant.CLICK_AD,false);
    }
}
