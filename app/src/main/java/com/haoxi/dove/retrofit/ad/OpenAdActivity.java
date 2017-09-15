package com.haoxi.dove.retrofit.ad;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.haoxi.dove.R;

import com.haoxi.dove.acts.MainActivity;
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
import java.util.Map.Entry;
import java.util.Set;

import butterknife.BindView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

@ActivityFragmentInject(contentViewId = R.layout.activity_ad)
public class OpenAdActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks,ILoginView,IAdView{

    @BindView(R.id.ad_fl)
    FrameLayout mAdFl;

    @BindView(R.id.ad_image)
    ImageView mAdOpenIv;
    @BindView(R.id.ad_btn)
    Button mAdBtn;

    private String clickAdImg = "";
    private boolean isClick = false;

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

    private OpenAdPresenter adPresenter;
    private int width;
    private int height;

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

        adPresenter = new OpenAdPresenter(this);

        isAutoCb = SpUtils.getBoolean(this, SpConstant.IS_AUTO);
        width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mAdFl.measure(width, height);

        mAdOpenIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(clickAdImg) && clickAdImg.startsWith("http")){

                    isClick = true;
//                    Intent intent = new Intent(OpenAdActivity.this,AdWebActivity.class);
//                    intent.putExtra("title_tag",clickAdImg);
//                    startActivity(intent);
//                    finish();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (adviewResObj != null) {
                                List<String> ec =  adviewResObj.getAd().get(0).getEc();
                                if (ec != null && ec.size() > 0) {

                                    for (String url:ec) {

                                        String getEc = Http.get(url,null);
                                        Log.e("esssss",getEc+"--------ec---------1");
                                    }
                                }
                            }
                        }
                    }).start();

                    SpUtils.putBoolean(OpenAdActivity.this,SpConstant.CLICK_AD,true);
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri contentUri = Uri.parse(clickAdImg);
                    intent.setData(contentUri);
                    startActivity(intent);
                }
            }
        });
        mAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClick = true;
                toDo(10);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("addddddddd","onResume");
        if (SpUtils.getBoolean(OpenAdActivity.this,SpConstant.CLICK_AD)){
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
            //loadAd();

            loadOurAd();
        }
    }

    private void loadOurAd() {
        adPresenter.getOpenAd(getParamsMap());
    }

    private void loadAd(){
        new HmSplashAd(this,mAdFl,new AdSplashListener() {
            @Override
            public void onNoAD(int i) {
                Log.e("addd",i+"---------onNoAD");
                switch (i){
                    case 1:
                    case 2:
                    case 3:
                        toDo(3000);
                        break;
                }
            }
            @Override
            public void onADDismissed() {
                if (!SpUtils.getBoolean(OpenAdActivity.this,SpConstant.CLICK_AD)){
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

                SpUtils.putBoolean(OpenAdActivity.this,SpConstant.CLICK_AD,true);
            }

            @Override
            public void onADTick(long l) {

                Log.e("addd",l+"---------onADTick");
            }
        },"428");
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
//        loadAd();
        loadOurAd();
    }



    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        toDo(1000);
    }

    public void toDo(int time) {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                    if (ApiUtils.isNetworkConnected(OpenAdActivity.this)) {

                        if (isAutoCb) {
                            presenter.getDataFromNets(getParaMap());
                        } else {
                            Intent intent = new Intent(OpenAdActivity.this, LoginActivity.class);
                            startActivity(intent);
                            OpenAdActivity.this.finish();
                        }
                    } else {
                        Intent intent = new Intent(OpenAdActivity.this, LoginActivity.class);
                        startActivity(intent);
                        OpenAdActivity.this.finish();
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

        Intent intent = new Intent(OpenAdActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void loginFail(String msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SpUtils.putBoolean(OpenAdActivity.this,SpConstant.CLICK_AD,false);
    }

    private AdviewResObj adviewResObj;

    @Override
    public void setOpenAd(AdviewResObj resObj) {

        adviewResObj = resObj;

        List<AdviewResObj.AdBody> adBodyList =resObj.getAd();
        if (adBodyList != null) {

            if (adBodyList.size() != 0 && adBodyList.get(0).getApi().size() != 0){

                String imgUrl = adBodyList.get(0).getApi().get(0);

                mAdBtn.setVisibility(View.VISIBLE);
                changeBtnGetCode();

                String al = adBodyList.get(0).getAl();
                if (!TextUtils.isEmpty(al)){
                    clickAdImg = al;
                }

                // http://open.adview.cn/agent/openDisplay.do?st=4&uuidEncType=0&sv=0&src=6&sy=0&nt=wifi&adi=20170818-175741_TC09_175-333-fJNd-169_1&bi=com.haoxi.dove&ai=cOLG9F0BAAB9DnAQXRB9VrVikNMvWZcocfCM9A-sD_3m62mZH5vDPqm073hU19cHxAM-kV07CVpciNoj2FihklnoS_eSlM9-3_0jfbIQaucEVdnkeqTo2PNSLg&ud=359209022793743&andt=0&as=0x0&se=&cv=&rqt=1&ti=1503050266&tm=0&to=eb709119ed76a3e8abb85cd79b90e688&aid=SDK20171109110127y15v5ms7291id7p&ro=1&ca=0
                // http://open.adview.cn/agent/openDisplay.do?st=4&uuidEncType=0&sv=0&src=6&sy=0&nt=wifi&adi=20170818-175741_TC09_175-333-fJNd-169_1&bi=com.haoxi.dove&ai=cOLG9F0BAAB9DnAQXRB9VrVikNMvWZcocfCM9A-sD_3m62mZH5vDPqm073hU19cHxAM-kV07CVpciNoj2FihklnoS_eSlM9-3_0jfbIQaucEVdnkeqTo2PNSLg&ud=359209022793743&andt=0&as=0x0&se=&cv=&rqt=1&ti=1503050266&tm=0&to=eb709119ed76a3e8abb85cd79b90e688&aid=SDK20171109110127y15v5ms7291id7p&ro=1&ca=0
                final Map<String, List<String>> map = resObj.getAd().get(0).getEs();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (map != null && map.size() > 0) {
                            Set<Entry<String,  List<String>>> set = map.entrySet();
                            for (Entry<String, List<String>> entry : set) {
                                List<String> arr = entry.getValue();
                                for (String url : arr) {
                                    Log.e("esssss",url+"----1----es--------2");
                                    String getEc = Http.get(url,null);
                                    Log.e("esssss",getEc+"----1----es--------2");
                                }
                            }
                        }
                    }
                }).start();

                Glide.with(OpenAdActivity.this)
                        .load(imgUrl)
                        .into(mAdOpenIv);
            }
        }
    }
    private final static String PACKAGE_NAME = "com.haoxi.dove";
    @Override
    public Map<String,Object> getParamsMap() {
        Map<String,Object> map = new HashMap<>();

        map.put("n",Config.AD_NUM);
        map.put("appid",Config.APP_ID);
        map.put("pt",Config.AD_PT);
        map.put("w",width);
        map.put("h",height);
        map.put("os",Config.ANDORID_OS);
        map.put("bdr", Build.VERSION.RELEASE);
        map.put("tp", Build.MODEL);
        map.put("brd",Build.BRAND);
        map.put("sn",getIMEI());
        map.put("nop",getOperators());

        map.put("andid",getAndroidId());
        map.put("nt",getNetworkType());
        map.put("tab",0);
        map.put("tm",Config.DEBUG);
        map.put("pack",PACKAGE_NAME);

        String time = String.valueOf(System.currentTimeMillis());
        map.put("time",time);
        StringBuilder sb = new StringBuilder(Config.APP_ID);
        sb.append(getIMEI()).append(Config.ANDORID_OS);
        sb.append(getOperators()).append(PACKAGE_NAME);
        sb.append(time).append(Config.SECRET_KEY);

//        String token = Guardian.md5Encode(String.valueOf(sb));

        String token = MD5Tools.MD5(String.valueOf(sb));
        map.put("token",token);
        return map;
    }
    private String getIMEI() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        return TextUtils.isEmpty(imei) ? "000000000000000" : imei;
    }

    private String getOperators() {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        return TextUtils.isEmpty(tm.getSimOperator()) ? "" : tm.getSimOperator();
    }

    private String getAndroidId() {
        return Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private String getNetworkType() {
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
                return "wifi";
            else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String subTypeName = networkInfo.getSubtypeName();
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: // api<8 : replace by
                        // 11
                        return "2g";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // api<9 : replace by
                        // 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD: // api<11 : replace by
                        // 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: // api<13 : replace by
                        // 15
                        return "3g";
                    case TelephonyManager.NETWORK_TYPE_LTE: // api<11 : replace by
                        // 13
                        return "4g";
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if (subTypeName.equalsIgnoreCase("TD-SCDMA") || subTypeName.equalsIgnoreCase("WCDMA")
                                || subTypeName.equalsIgnoreCase("CDMA2000"))
                            return "3g";
                        else
                            return "2g";
                }
            }
        }
        return "";
    }

    private Thread mThread;
    private int i = 5;
    private void changeBtnGetCode() {

        mThread = new Thread(){
            @Override
            public void run() {

                    while (i > 0){
                        i--;
                        OpenAdActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAdBtn.setText(i+" 跳过广告");
                            }
                        });

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (i == 0 && !isClick){
                        toDo(10);
                    }
                }
        };
        mThread.start();
    }



}
