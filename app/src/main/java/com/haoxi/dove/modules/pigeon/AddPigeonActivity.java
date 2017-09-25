package com.haoxi.dove.modules.pigeon;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AddPigeonMoudle;
import com.haoxi.dove.inject.DaggerAddPigeonComponent;
import com.haoxi.dove.modules.mvp.presenters.AddPigeonPresenter;
import com.haoxi.dove.modules.mvp.views.IAddPigeonView;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_addpigeon)
public class AddPigeonActivity extends BaseActivity implements IAddPigeonView {

    @BindView(R.id.act_addpigeon_ringcode) EditText  mRingCodeEt;
    @BindView(R.id.act_addpigeon_sex) TextView  mSexTv;
    @BindView(R.id.act_addpigeon_old) TextView  mOldTv;
    @BindView(R.id.act_addpigeon_color) TextView  mColorTv;
    @BindView(R.id.act_addpigeon_eyes) TextView  mEyesTv;
    @BindView(R.id.act_addpigeon_blood) TextView  mBloodTv;
    @BindView(R.id.custom_toolbar_iv) ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv) TextView  mTitleTv;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    AddPigeonPresenter presenter;
    @Inject
    DaoSession daoSession;
    @Inject
    RxBus mRxBus;

    private List<String> mPigeonCodes;

    private int userAgeYear;
    private int userAgeMonth;
    private int userAgeDay;
    private boolean isAdd = false;
    private boolean isContained = false;
    @Override
    protected void initInject() {
        DaggerAddPigeonComponent.builder().appComponent(getAppComponent())
                .addPigeonMoudle(new AddPigeonMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("添加信鸽");
        mPigeonCodes = MyApplication.getMyBaseApplication().getmPigeonCodes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SpUtils.putInt(this,"yearSp", 0);
        SpUtils.putInt(this,"monthSp", 0);
        SpUtils.putInt(this,"daySp", 0);
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnli() {
        finish();
    }

    @OnClick(R.id.act_addpigeon_confirm)
    void onAddPigeonOnli() {
        if (ApiUtils.isFastDoubleClick()) {
            return;
        }
        if (numMap.get("pigeon_num") != null &&(Boolean)numMap.get("pigeon_num")){
            ApiUtils.showToast(this,"最多只能添加 15 只信鸽");
            return;
        }
        if (isVelidated()) {
            ourCodePresenter.addDove(getParaMap());
        }
    }

    public Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",getUserObjIds());
        map.put("token",getToken());
        map.put("playerid",getUserObjIds());
        map.put("foot_ring",getRingCode());
        map.put("gender",getPigeonSex());
        map.put("age","1");
        map.put("color",getPigeonColor());
        map.put("eye",getPigeonEyes());
        map.put("ancestry",getPigeonBlood());
        return map;
    }

    @OnClick(R.id.act_addpigeon_sex)
    void setSexOncli() {
        presenter.setUserSex(this);
    }
    @OnClick(R.id.act_addpigeon_old)
    void setOldOncli() {
        presenter.showAgeDialog(this);
    }
    @OnClick(R.id.act_addpigeon_color)
    void setColorOncli() {
        presenter.showEvDialog(this, "color", mColorTv);
    }
    @OnClick(R.id.act_addpigeon_blood)
    void setBloodOncli() {
        presenter.showEvDialog(this, "blood", mBloodTv);
    }
    @OnClick(R.id.act_addpigeon_eyes)
    void setEyesOncli() {
        presenter.showEyesDialog(this);
    }
    private boolean isVelidated() {
        if ("".equals(getRingCode())) {
            ApiUtils.showToast(this, getString(R.string.add_ringcode_null));
            return false;
        }
        if (mPigeonCodes.contains(getRingCode())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_code_exist));
            return false;
        }
        if ("".equals(getPigeonSex())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_sex_null));
            return false;
        }
        if ("".equals(getPigeonColor())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_color_null));
            return false;
        } else if (!StringUtils.checkHanZi(getPigeonColor())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_color_check));
            return false;
        }
        if ("".equals(getPigeonOld())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_old_null));
            return false;
        }
        if ("".equals(getPigeonBlood())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_blood_null));
            return false;
        } else if (!StringUtils.checkHanZi(getPigeonBlood())) {
            ApiUtils.showToast(this, getString(R.string.add_pigeon_blood_check));
            return false;
        }
        return true;
    }

    @Override
    public String getUserObjIds() {
        if (mUserObjId != null) {
            return mUserObjId;
        }
        return "";
    }
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
    public String getPigeonSex() {
        String pigeonSex = mSexTv.getText().toString().trim();
        switch (pigeonSex) {
            case "公":
                pigeonSex = "1";
                break;
            case "母":
                pigeonSex = "2";
                break;
        }
        return pigeonSex;
    }

    @Override
    public String getPigeonOld() {
        if (userAgeYear == 0 || userAgeMonth == 0 || userAgeDay == 0) {
            return "";
        }
        String tempYear = String.valueOf(userAgeYear);
        String tempMonth = String.valueOf(userAgeMonth);
        String tempDay = String.valueOf(userAgeDay);
        if (userAgeMonth < 10) tempMonth = "0" + tempMonth;
        if (userAgeDay < 10) tempDay = "0" + tempDay;
        String pigeonOld = tempYear + "-" + tempMonth + "-" + tempDay + " 00:00:00";
        return pigeonOld;
    }

    @Override
    public String getPigeonColor() {
        return mColorTv.getText().toString().trim();
    }

    @Override
    public String getPigeonEyes() {
        return mEyesTv.getText().toString().trim();
    }

    @Override
    public String getPigeonBlood() {
        return mBloodTv.getText().toString().trim();
    }

    @Override
    public void setPigeonSex(String pigeonSex) {
        if (pigeonSex != null) {
            mSexTv.setText(pigeonSex);
        }
    }

    @Override
    public void setPigeonOld(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        if (year == -1 || month == -1 || day == -1) {
            ApiUtils.showToast(this, "已超过当前日期,请重新选择");
            return;
        }
        if (currentYear == year && currentMonth < month) {
            ApiUtils.showToast(this, "已超过当前日期,请重新选择");
            return;
        }
        this.userAgeYear = year;
        this.userAgeMonth = month;
        this.userAgeDay = day;
        SpUtils.putInt(this,"yearSp", year);
        SpUtils.putInt(this,"monthSp", month);
        SpUtils.putInt(this,"daySp", day);
        if (currentYear == year) {
            if (currentMonth == month) {
                mOldTv.setText("1个月");
            }else {
                mOldTv.setText(currentMonth - month + "个月");
                return;
            }
        } else {
            int dxYear = ((currentYear - year) * 12 - (month - currentMonth)) / 12;
            int dxAge = ((currentYear - year) * 12 - (month - currentMonth)) % 12;
            if (dxYear == 0) {
                mOldTv.setText(dxAge + "个月");
            } else {
                if (dxAge == 0){
                    mOldTv.setText(dxYear + "年");
                }else{
                    mOldTv.setText(dxYear + "年" + dxAge + "个月");
                }
            }
        }
    }

    @Override
    public void setPigeonColor(String pigeonColor) {
        if (pigeonColor != null) {
            mColorTv.setText(pigeonColor);
        }
    }

    @Override
    public void setPigeonEyes(String pigeonEyes) {
        if (pigeonEyes != null) {
            mEyesTv.setText(pigeonEyes);
        }
    }

    @Override
    public void setPigeonBlood(String pigeonBlood) {
        if (pigeonBlood != null) {
            mBloodTv.setText(pigeonBlood);
        }
    }

    @Override
    public void toDo() {
        isAdd = true;
        mRxBus.post(ConstantUtils.OBSER_LOAD_DOVE,true);
        this.finish();
    }

    @Override
    public String getMethod() {
        return MethodConstant.DOVE_ADD;
    }
}
