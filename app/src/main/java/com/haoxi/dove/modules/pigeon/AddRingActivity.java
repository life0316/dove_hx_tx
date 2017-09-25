package com.haoxi.dove.modules.pigeon;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AddRingMoudle;
import com.haoxi.dove.inject.DaggerAddRingComponent;
import com.haoxi.dove.modules.mvp.views.IAddRingView;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.zxing.android.CaptureActivity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
@ActivityFragmentInject(contentViewId = R.layout.activity_addring)
public class AddRingActivity extends BaseActivity implements IAddRingView {

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

    private static final int REQUEST_CODE_SCAN = 0x0000;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";

    @Override
    protected void initInject() {
        DaggerAddRingComponent.builder()
                .appComponent(getAppComponent())
                .addRingMoudle(new AddRingMoudle(this,this))
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
        this.finish();
    }
    @OnClick(R.id.act_addring_btn)
    void addRingOncli() {
        if ("".equals(getRingCode()) && getRingCode() != null) {
            ApiUtils.showToast(this, getString(R.string.add_ringcode_null));
            return;
        }
        if (getRingCode().length() != 15) {
            ApiUtils.showToast(this, getString(R.string.add_ringcode_15));
            return;
        }
        if (numMap.get("ring_num") != null && (Boolean)numMap.get("ring_num")){
            ApiUtils.showToast(this,"最多只能添加 15 个鸽环");
            return;
        }
        ourCodePresenter.addRing(getParaMap());
    }
    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        map.put(MethodParams.PARAMS_PLAYER_ID,getUserObjId());
        map.put(MethodParams.PARAMS_RING_CODE,getRingCode());
        return map;
    }

    protected String[] needPermissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA
    };

    private boolean isNeedCheck = true;

    @OnClick(R.id.act_addring_scan)
    void scan(){
        if (isNeedCheck) {
            if (!ApiUtils.checkPermissions(this, ConstantUtils.PERMISSION_REQUESTCODE_1, needPermissions)) {
                tiaoz();
            }
        } else {
            tiaoz();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case ConstantUtils.PERMISSION_REQUESTCODE_1:
                //没有授权
                if (!ApiUtils.verifyPermissions(grantResults)) {
                    isNeedCheck = true;
                } else {
                    isNeedCheck = false;
                    tiaoz();
                }

                break;
            case ConstantUtils.PERMISSION_REQUESTCODE_2:
                //没有授权
                if (!ApiUtils.verifyPermissions(grantResults)) {
                    isNeedCheck = false;
                } else {
                    isNeedCheck = false;
                }
                break;
        }
    }

    public void tiaoz(){
        Intent intent = new Intent(AddRingActivity.this,CaptureActivity.class);
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
        return mRingCodeEt.getText().toString().trim();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == CaptureActivity.RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
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
                }
            }
        }
    }

    @Override
    public void toDo() {
        mRxBus.post(ConstantUtils.OBSER_LOAD_RING,true);
        this.finish();
    }

    @Override
    public String getMethod() {
        return MethodConstant.RING_ADD;
    }
}
