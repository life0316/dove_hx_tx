package com.haoxi.dove.modules.loginregist.presenter;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.bean.User;
import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.loginregist.ui.IRegistView;
import com.haoxi.dove.modules.loginregist.RegistActivity;
import com.haoxi.dove.utils.ApiUtils;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by lifei on 2016/12/26.
 */

public class RegistPresenter extends BasePresenter<IRegistView,User> implements ILoginPresenter {


    private int yearBirth;
    private int monthBirth;
    private int dayBirth;
    private NumberPicker dayPicker;

    public RegistPresenter() {
    }

    /**
     * 弹出日期对话框，设置出生日期
     * @param context
     * @param mTvAge
     */
    public void setUserAge(RegistActivity context, TextView mTvAge) {
        final Dialog mDialog = new Dialog(context,R.style.DialogTheme);

        View view = context.getLayoutInflater().inflate(R.layout.personal_birth_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_year);
        NumberPicker monthPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_month);
        dayPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_day);
        TextView birthCancle = (TextView) view.findViewById(R.id.birth_dialog_cancle);
        TextView birthConfirm = (TextView) view.findViewById(R.id.birth_dialog_confirm);

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        String birthTime = mTvAge.getText().toString().trim();

        String[] str = birthTime.split("-");

        String timeYear = str[0];
        String timeMonth = str[1];
        String timeDay = str[2];

        yearPicker.setMaxValue(year);
        yearPicker.setMinValue(year-100);
        yearPicker.setValue(Integer.valueOf(timeYear));

        yearBirth = yearPicker.getValue();

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(01);
        monthPicker.setValue(Integer.valueOf(timeMonth));

        monthBirth = monthPicker.getValue();

        initDay(timeDay);
        dayBirth = dayPicker.getValue();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                yearBirth = newVal;
                initDay("");
                if (yearBirth > year) {
                    yearBirth = -1;
                }
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                monthBirth = newVal;
                initDay("");
                if (yearBirth >= year){
                    if (newVal > month + 1){
                        monthBirth = -1;
                    }
                }
            }
        });

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dayBirth = newVal;
                if (yearBirth >= year){
                    if (monthBirth >= month + 1){
                        if (dayBirth > day){
                            dayBirth = -1;
                        }
                    }
                }
            }
        });

        birthCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        birthConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getMvpView().setUserBirth(yearBirth,monthBirth,dayBirth);
                mDialog.dismiss();
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    private void initDay(String dayValue) {

        if (monthBirth==1||monthBirth==3||monthBirth==5||monthBirth==7||monthBirth==8||monthBirth==10||monthBirth==12){
            dayPicker.setMaxValue(31);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        }else if(monthBirth==4||monthBirth==6||monthBirth==9||monthBirth==11){
            dayPicker.setMaxValue(30);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        }else if(monthBirth==2&&yearBirth%4==0){
            dayPicker.setMaxValue(29);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        }else if (monthBirth==2&&yearBirth%4!=0){
            dayPicker.setMaxValue(28);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        }
    }

    public void setUserSex(RegistActivity context, TextView mSexTv) {

        final Dialog mDialog = new Dialog(context,R.style.DialogTheme);

        View  view = context.getLayoutInflater().inflate(R.layout.personal_sex_dialog,null);

        final TextView   mMale = (TextView)view.findViewById(R.id.dialog_sex_male);
        final TextView   mFemale = (TextView)view.findViewById(R.id.dialog_sex_female);
        LinearLayout llMale = (LinearLayout)view.findViewById(R.id.dialog_sex_ll_male);
        LinearLayout llFemale = (LinearLayout)view.findViewById(R.id.dialog_sex_ll_female);

        mMale.setText("男");
        mFemale.setText("女");

        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMvpView().setUserSex(mMale.getText().toString().trim());
                mDialog.dismiss();
            }
        });
        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMvpView().setUserSex(mFemale.getText().toString().trim());
                mDialog.dismiss();
            }
        });

        mDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();

    }
    @Override
    public void getDataFromNets(Map<String, String> map) {

    }
}
