package com.haoxi.dove.modules.mvp.presenters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.views.IAddPigeonView;
import com.haoxi.dove.modules.pigeon.AddPigeonActivity;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.widget.wheelview.OnWheelChangedListener;
import com.haoxi.dove.widget.wheelview.WheelView;
import com.haoxi.dove.widget.wheelview.adapters.ArrayWheelAdapter;

import java.util.Calendar;

public class AddPigeonPresenter extends BasePresenter<IAddPigeonView,Object>{

    private int       mYearNewVal;
    private int       mMonthNewVal;
    private int       mDayNewVal;
    private String mValue;
    private NumberPicker dayPicker;

    private Context mContext;

    public AddPigeonPresenter(IAddPigeonView mView,Context mContext) {
        this.mContext = mContext;
        attachView(mView);
    }

    public void showEvDialog(AddPigeonActivity context, final String name, TextView mTv) {

        final Dialog mDialog_et = new Dialog(context, R.style.DialogTheme);
        View view = context.getLayoutInflater().inflate(R.layout.pigeon_name_dialog,null);

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
                if (met != null) {
                    if ("color".equals(name)) {
                        getMvpView().setPigeonColor(met);
                    } else {
                        getMvpView().setPigeonBlood(met);
                    }
                    mDialog_et.dismiss();
                } else {
                    getMvpView().showErrorMsg("请输入内容");
                }
            }
        });


        mDialog_et.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog_et.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialog_et.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ApiUtils.setDialogWindow(mDialog_et);
        mDialog_et.show();

    }

    public void setUserSex(AddPigeonActivity context) {

        final Dialog mDialog = new Dialog(context, R.style.DialogTheme);

        View view = context.getLayoutInflater().inflate(R.layout.personal_sex_dialog, null);

        final TextView mMale = (TextView) view.findViewById(R.id.dialog_sex_male);
        final TextView mFemale = (TextView) view.findViewById(R.id.dialog_sex_female);
        LinearLayout llMale = (LinearLayout) view.findViewById(R.id.dialog_sex_ll_male);
        LinearLayout llFemale = (LinearLayout)view.findViewById(R.id.dialog_sex_ll_female);

        mMale.setText("公");
        mFemale.setText("母");

        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMvpView().setPigeonSex("公");
                mDialog.dismiss();
            }
        });
        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMvpView().setPigeonSex("母");
                mDialog.dismiss();
            }
        });

        mDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    public void showAgeDialog(AddPigeonActivity context) {

        final Dialog mDialog = new Dialog(context, R.style.DialogTheme);
        View view = context.getLayoutInflater().inflate(R.layout.personal_birth_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_year);
        final NumberPicker monthPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_month);
        dayPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_day);


        TextView birthCancle = (TextView) view.findViewById(R.id.birth_dialog_cancle);
        TextView birthConfirm = (TextView) view.findViewById(R.id.birth_dialog_confirm);

        birthCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        birthConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("pigeonOld",mDayNewVal+"-----1---dayValue");

                getMvpView().setPigeonOld(mYearNewVal,mMonthNewVal,mDayNewVal);

                mDialog.dismiss();
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        yearPicker.setMaxValue(year);
        yearPicker.setMinValue(2000);
        yearPicker.setValue(year);

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(01);
        monthPicker.setValue(month + 1);

        mYearNewVal = yearPicker.getValue();
        mMonthNewVal = monthPicker.getValue();

        initDay(day);

        mDayNewVal = dayPicker.getValue();
        Log.e("pigeonOld",mDayNewVal+"-----3---dayValue");


        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mYearNewVal = newVal;

                initDay(0);

                if (mYearNewVal > year) {
                    mYearNewVal = -1;
                }else{
                    mMonthNewVal = monthPicker.getValue();
                    mDayNewVal =  dayPicker.getValue();
                }
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mMonthNewVal = newVal;

                initDay(0);

                if (mYearNewVal >= year){
                    if (newVal > month + 1){
                        mMonthNewVal = -1;
                    }else{
                        mDayNewVal =  dayPicker.getValue();
                    }
                }
            }
        });
        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mDayNewVal = newVal;
                if (mYearNewVal >= year){
                    if (mMonthNewVal >= month + 1){
                        if (mDayNewVal > day){
                            mDayNewVal = -1;
                        }
                    }
                }
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    private void initDay(int dayValue) {

        Log.e("pigeonOld",dayValue+"-----2---dayValue");

        if (mMonthNewVal==1||mMonthNewVal==3||mMonthNewVal==5||mMonthNewVal==7||mMonthNewVal==8||mMonthNewVal==10||mMonthNewVal==12){
            dayPicker.setMaxValue(31);
            dayPicker.setMinValue(01);
            if (0 != dayValue) {
                dayPicker.setValue(dayValue);
            }
        }else if(mMonthNewVal==4||mMonthNewVal==6||mMonthNewVal==9||mMonthNewVal==11){
            dayPicker.setMaxValue(30);
            dayPicker.setMinValue(01);
            if (0 != dayValue) {
                dayPicker.setValue(dayValue);
            }
        }else if(mMonthNewVal==2&&mYearNewVal%4==0){
            dayPicker.setMaxValue(29);
            dayPicker.setMinValue(01);
            if (0 != dayValue) {
                dayPicker.setValue(dayValue);
            }
        }else if (mMonthNewVal==2&&mYearNewVal%4!=0){
            dayPicker.setMaxValue(28);
            dayPicker.setMinValue(01);
            if (0 != dayValue) {
                dayPicker.setValue(dayValue);
            }
        }
    }

    public void showEyesDialog(AddPigeonActivity context){

        final Dialog mDialog = new Dialog(context, R.style.DialogTheme);
        View view = context.getLayoutInflater().inflate(R.layout.addpigeon_eyes_dialog, null);

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
                getMvpView().setPigeonEyes(mValue);
                mDialog.dismiss();
            }
        });

        final String[] mDatas = {"云砂","桃红砂","云桃红砂","蓝水桃花","土红砂","黄底红砂","黄底飘红砂","红砂","紫砂","粗红砂","油眼砂","乌眼砂","酱砂"};

        mValue = getMvpView().getPigeonEyes();


        wv.setViewAdapter(new ArrayWheelAdapter<String>(context, mDatas));

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

        mDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }
}
