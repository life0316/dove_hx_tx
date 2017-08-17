package com.haoxi.dove.acts;

import android.app.Dialog;
import android.content.Intent;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerPigeonInfoComponent;
import com.haoxi.dove.inject.PigeonInfoMoudle;
import com.haoxi.dove.modules.mvp.views.IUpdateDoveView;
import com.haoxi.dove.newin.bean.InnerDoveData;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.wheelview.OnWheelChangedListener;
import com.haoxi.dove.widget.wheelview.WheelView;
import com.haoxi.dove.widget.wheelview.adapters.ArrayWheelAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_pigeon)
public class PigeonActivity extends BaseActivity implements IUpdateDoveView {

    private static final int UPDATE_GENDER = 1;
    private static final int UPDATE_AGE = 2;
    private static final int UPDATE_COLOR = 3;
    private static final int UPDATE_EYE = 4;
    private static final int UPDATE_ANCESTRY = 5;
    private static final int UPDATE_FOOR_RING = 6;

    private static int updateParam = UPDATE_GENDER;

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.activity_pigeon_footringcode)
    TextView mFootRingCodeTv;
    @BindView(R.id.activity_pigeon_pigeonlineage)
    TextView mPigeonLineageTv;
    @BindView(R.id.activity_pigeon_plumagecolor)
    TextView mPlumageColorTv;
    @BindView(R.id.activity_pigeon_birthday)
    TextView mPigeonBirthdayTv;
    @BindView(R.id.activity_pigeon_pigeonsex)
    TextView mPigeonSexTv;
    @BindView(R.id.activity_pigeon_eyesand)
    TextView mEyeSandTv;

    private InnerDoveData pigeonBean;

    private boolean isSetting = false;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus rxBus;

    private String doveid;

    private String updateFootRing = "";
    private String updateGender = "";
    private String updateColor = "";
    private String updateEye = "";
    private String updateAncestry = "";
    private Dialog mDialog;
    private String mValue;
    private Dialog mEyeDialog;
//    private Dialog mDialog_et;


    @Override
    protected void initInject() {

        DaggerPigeonInfoComponent.builder()
                .appComponent(getAppComponent())
                .pigeonInfoMoudle(new PigeonInfoMoudle(this,this))
                .build()
                .inject(this);

    }

    @Override
    protected void init() {

        Intent intent = getIntent();
        if (intent != null) {
            pigeonBean = intent.getParcelableExtra("pigeonBean");
            doveid = pigeonBean.getDoveid();
        }

        mTitleTv.setText("信鸽信息");
        mBackIv.setVisibility(View.VISIBLE);

        if (!"".equals(pigeonBean.getFoot_ring()) && !"-1".equals(pigeonBean.getFoot_ring()) &&pigeonBean.getFoot_ring() != null) {
            mFootRingCodeTv.setText(pigeonBean.getFoot_ring());
        }
        if (!"".equals(pigeonBean.getAncestry()) && !"-1".equals(pigeonBean.getAncestry()) &&pigeonBean.getAncestry() != null) {
            mPigeonLineageTv.setText(pigeonBean.getAncestry());
        }
        if (!"".equals(pigeonBean.getColor()) && pigeonBean.getColor() != null) {
            mPlumageColorTv.setText(pigeonBean.getColor());
        }
        if (pigeonBean.getAge() != 0 && pigeonBean.getAge() != -1) {
            mPigeonBirthdayTv.setText(String.valueOf(pigeonBean.getAge()));
        }
        if (!"".equals(pigeonBean.getGender()) && pigeonBean.getGender() != null) {

            if ("1".equals(pigeonBean.getGender()) || "公".equals(pigeonBean.getGender())) {
                mPigeonSexTv.setText("公");
            }
        }
        if (!"".equals(pigeonBean.getEye()) && pigeonBean.getEye() != null) {
            mEyeSandTv.setText(pigeonBean.getEye());
        }

    }

    @OnClick(R.id.custom_toolbar_iv)
    void BackOncli() {

        if (isSetting) {
            RxBus.getInstance().post("isLoad", true);
        } else {
            RxBus.getInstance().post("isLoad", false);
        }

        this.finish();
    }

    @Override
    public void onBackPressed() {

        if (isSetting) {
            RxBus.getInstance().post("isLoad", true);
        } else {
            RxBus.getInstance().post("isLoad", false);
        }

        super.onBackPressed();
    }

    @OnClick(R.id.activity_pigeon_footringcode)
    void footRingOncli() {
        updateParam = UPDATE_FOOR_RING;
        showEvDialog("foot_ring",mFootRingCodeTv);
    }

    @OnClick(R.id.activity_pigeon_pigeonlineage)
    void ancestryOncli() {
        updateParam = UPDATE_ANCESTRY;
        showEvDialog("blood",mPigeonLineageTv);
    }

    @OnClick(R.id.activity_pigeon_plumagecolor)
    void colorOncli(){
        updateParam = UPDATE_COLOR;
        showEvDialog("color",mPlumageColorTv);
    }

    @OnClick(R.id.activity_pigeon_birthday)
    void ageOncli() {

    }

    @OnClick(R.id.activity_pigeon_eyesand)
    void eyeOncli() {

        updateParam = UPDATE_EYE;
        showEyesDialog();
    }

    @OnClick(R.id.activity_pigeon_pigeonsex)
    void sexOncli() {

        updateParam = UPDATE_GENDER;

        setDoveSex();

    }
    public void showEvDialog(final String name, TextView mTv) {

       final Dialog mDialog_et = new Dialog(this, R.style.DialogTheme);
        View view =getLayoutInflater().inflate(R.layout.pigeon_name_dialog,null);

        final EditText mEtDialog = (EditText)view.findViewById(R.id.pigeon_name_dialog_et);
        TextView nameTitle = (TextView)view.findViewById(R.id.pigeon_name_dialog_title);
        TextView nameCancle = (TextView)view.findViewById(R.id.name_dialog_cancle);
        TextView nameConfirm = (TextView)view.findViewById(R.id.name_dialog_confirm);

        switch (name){
            case "color":
                nameTitle.setText("羽毛颜色");
                break;
            case "blood":
                nameTitle.setText("血统");
                break;
            case "foot_ring":
                nameTitle.setText("脚环");
                mEtDialog.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
        }

        mTv.setFocusable(true);
        mTv.requestFocus();
        mEtDialog.setText(mTv.getText().toString().trim());
        mEtDialog.setSelection(mEtDialog.getText().length());

        nameCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog_et.dismiss();
            }
        });
        nameConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String met = mEtDialog.getText().toString().trim();
                    if ("color".equals(name)) {
                        //getMvpView().setPigeonColor(met);
                        updateParam = UPDATE_COLOR;
                        updateColor = met;
                    } else if ("blood".equals(name)) {
                        //getMvpView().setPigeonBlood(met);
                        updateParam = UPDATE_ANCESTRY;
                        updateAncestry = met;
                    }else {
                        updateParam = UPDATE_FOOR_RING;
                        updateFootRing = met;
                    }
                ourCodePresenter.updateDoveInfo(getParaMap());
                mDialog_et.dismiss();
            }
        });

        if (mDialog_et.getWindow() != null) {
            mDialog_et.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            mDialog_et.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            mDialog_et.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ApiUtils.setDialogWindow(mDialog_et);
            mDialog_et.show();
        }
    }


    public void setDoveSex() {

        mDialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.personal_sex_dialog, null);

        final TextView mMale = (TextView) view.findViewById(R.id.dialog_sex_male);
        final TextView mFemale = (TextView) view.findViewById(R.id.dialog_sex_female);
        LinearLayout llMale = (LinearLayout) view.findViewById(R.id.dialog_sex_ll_male);
        LinearLayout llFemale = (LinearLayout)view.findViewById(R.id.dialog_sex_ll_female);

        mMale.setText("公");
        mFemale.setText("母");

        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGender = "1";
                ourCodePresenter.updateDoveInfo(getParaMap());
            }
        });
        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGender = "2";
                ourCodePresenter.updateDoveInfo(getParaMap());
            }
        });

        mDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    public void showEyesDialog(){

        mEyeDialog = new Dialog(this, R.style.DialogTheme);
        View view = getLayoutInflater().inflate(R.layout.addpigeon_eyes_dialog, null);

        WheelView wv = (WheelView)view.findViewById(R.id.addpigeon_wv);
        TextView tvCancle = (TextView)view.findViewById(R.id.addpigeon_cancle);
        TextView tvConfirm = (TextView)view.findViewById(R.id.addpigeon_confirm        );
        tvCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEye = mValue;
                ourCodePresenter.updateDoveInfo(getParaMap());
            }
        });

        final String[] mDatas = {"云砂","桃红砂","云桃红砂","蓝水桃花","土红砂","黄底红砂","黄底飘红砂","红砂","紫砂","粗红砂","油眼砂","乌眼砂","酱砂"};

        mValue = getDoveEye();


        wv.setViewAdapter(new ArrayWheelAdapter<>(this, mDatas));

        for (int i = 0; i < mDatas.length; i++) {

            if (mValue.equals(mDatas[i])){
                wv.setCurrentItem(i);
            }
        }

        wv.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                mValue = mDatas[newValue];
            }
        });

        mEyeDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mEyeDialog);
        mEyeDialog.show();
    }

    @Override
    public String getDoveId() {
        return doveid;
    }

    @Override
    public String getDoveGender() {


        return mPigeonSexTv.getText().toString().trim();
    }

    @Override
    public String getDoveAge() {

        return mPigeonBirthdayTv.getText().toString().trim();
    }

    @Override
    public String getDoveColor() {

        return mPlumageColorTv.getText().toString().trim();
    }

    @Override
    public String getDoveEye() {

        return mEyeSandTv.getText().toString().trim();
    }

    @Override
    public String getDoveAncestry() {

        return mPigeonLineageTv.getText().toString().trim();
    }

    @Override
    public void toDo() {

        isSetting = true;

        switch (updateParam){
            case UPDATE_GENDER:
                mPigeonSexTv.setText(updateGender.equals("1")?"公":"母");
                mDialog.dismiss();
                break;
            case UPDATE_FOOR_RING:
                mFootRingCodeTv.setText(updateFootRing);
                //mDialog_et.dismiss();
                break;
            case UPDATE_AGE:
                mDialog.dismiss();
                break;
            case UPDATE_COLOR:
                mPlumageColorTv.setText(updateColor);
                //mDialog_et.dismiss();
                break;
            case UPDATE_EYE:
                mEyeSandTv.setText(updateEye);
                mEyeDialog.dismiss();
                break;
            case UPDATE_ANCESTRY:
                mPigeonLineageTv.setText(updateAncestry);
                //mDialog_et.dismiss();
                break;
        }

    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",mToken);
        map.put("doveid",getDoveId());

        switch (updateParam){
            case UPDATE_GENDER:
                map.put("gender",updateGender);
                break;
            case UPDATE_FOOR_RING:
                map.put("foot_ring",updateFootRing);
                break;
            case UPDATE_AGE:
                String updateAge = "";
                map.put("age", updateAge);
                break;
            case UPDATE_COLOR:
                map.put("color",updateColor);
                break;
            case UPDATE_EYE:
                map.put("eye",updateEye);
                break;
            case UPDATE_ANCESTRY:
                map.put("ancestry",updateAncestry);
                break;
        }

        return map;
    }

    @Override
    public String getMethod() {
        return "/app/dove/update";
    }
}
