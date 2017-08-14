package com.haoxi.dove.modules.pigeon;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AddRing2Moudle;
import com.haoxi.dove.inject.DaggerAddRing2Component;
import com.haoxi.dove.modules.mvp.views.IAddRingView;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SystemSwitchUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by lifei on 2017/1/12.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_addring)
public class AddRing2Activity extends BaseActivity implements IAddRingView,EasyPermissions.PermissionCallbacks {

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;
    @BindView(R.id.act_addring_ringcode)
    EditText  mRingCodeEt;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;

    private Handler mHandler = new Handler();

    private boolean isAdd = false;

    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    @Override
    protected void initInject() {

        DaggerAddRing2Component.builder()
                .appComponent(getAppComponent())
                .addRing2Moudle(new AddRing2Moudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("添加鸽环");

    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnclic() {

        if (!isAdd) {
            mRxBus.post("isLoad",false);
        }

        this.finish();
    }

    @OnClick(R.id.act_addring_btn)
    void addRingOncli() {

        if ("".equals(getRingCode()) && getRingCode() != null) {
            ApiUtils.showToast(this, getString(R.string.add_ringcode_null));
            return;
        }

        if ((Boolean)numMap.get("ring_num")){

            ApiUtils.showToast(this,"最多只能添加 15 个鸽环");

            return;
        }

//        JSONObject object = new JSONObject();
//        object.put("USER_OBJ_ID",getUserObjId());
//        object.put("RING_CODE",getRingCode());
//        object.put("TOKEN",getToken());
//
//        codePresenter.toAddRing(object);

        ourCodePresenter.addRing(getParaMap());


    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",getUserObjId());
        map.put("token",getToken());
        map.put("playerid",getUserObjId());

        return map;
    }

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA

    };

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    @OnClick(R.id.act_addring_scan)
    void scan(View view){

        requestCodeQRCodePermissions();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {

        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);

    }

    public void tiaoz(){


        if (SystemSwitchUtils.isFlashlightOn()) {

            showErrorMsg("第三方应用开启了相机");
            return;
        }

        Intent intent = new Intent(AddRing2Activity.this,MyScanActivity.class);
        startActivityForResult(intent,REQUEST_CODE_SCAN);

    }

    @Override
    public String getUserObjId() {

        if (mUserObjId != null) {
            return mUserObjId;
        }

        return "";
    }

    @Override
    public String getToken() {
        if (mToken != null) {
            return mToken;
        }
        return "";
    }

    @Override
    public String getRingCode() {

        String ringCode = mRingCodeEt.getText().toString().trim();
        return ringCode;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == AddRing2Activity.RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                //Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                Log.e("content","content:-----"+content);

                if (!"Scan failed!".equals(content)) {
                    if (ApiUtils.isNumeric(content)){
                        if (content.length() > 15) {

                            content = content.substring(0,15);
                        }
                        mRingCodeEt.setText(content);
                        mRingCodeEt.setSelection(content.length());
                    }else {
                        showErrorMsg("二维码不正确");
                    }
                    //mRingCodeEt.setText(content);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isAdd) {
            mRxBus.post("isLoad",false);
        }
        super.onBackPressed();
    }

    @Override
    public void toDo() {

        isAdd = true;
        this.finish();
    }

    @Override
    public String getMethod() {
        return MethodConstant.RING_ADD;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        tiaoz();

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @AfterPermissionGranted(REQUEST_CODE_SCAN)
    private void requestCodeQRCodePermissions(){

        if (!EasyPermissions.hasPermissions(this,needPermissions)) {
            EasyPermissions.requestPermissions(this,"打开相机和闪光灯的权限",REQUEST_CODE_SCAN,needPermissions);
        }else {
            tiaoz();
        }

    }
}
