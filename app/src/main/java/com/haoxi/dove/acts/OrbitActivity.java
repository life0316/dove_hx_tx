package com.haoxi.dove.acts;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.HorizontalScrollViewAdapter;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.widget.ColorPickerView;
import com.haoxi.dove.widget.MyHorizontalScrollView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_orbit)
public class OrbitActivity extends BaseActivity{

//    private static final String TAG = "OrbitActivity";

//    @BindView(R.id.custom_toolbar_iv)
//    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;

    @BindView(R.id.activity_orbit_thickness)
    TextView  mThicknessTv;
    @BindView(R.id.activity_orbit_color_tv)
    TextView  mColorTv;
    @BindView(R.id.activity_orbit_pic_iv)
    ImageView mPicIv;

    private SharedPreferences.Editor mEditor;
    private Dialog                   dialog;

    private List mDatas2 = new ArrayList<>(Arrays.asList( R.mipmap.icon_img_2, R.mipmap.icon_img_3, R.mipmap.icon_img_4, R.mipmap.icon_img_5,
            R.mipmap.icon_img_6, R.mipmap.icon_img_7, R.mipmap.icon_img_8,R.mipmap.icon_img_9));
    private String[] mDatasStr = {"样式一", "样式二", "样式三", "样式四", "样式五", "样式六", "样式七", "样式八",};
    private String mColor;


    @SuppressLint("CommitPrefEdits")
    @Override
    protected void init() {

        SharedPreferences mPreferences = getSharedPreferences(ConstantUtils.TRAIL, MODE_PRIVATE);
        mEditor = mPreferences.edit();

//        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("轨迹设置");

        int thickness = mPreferences.getInt("thickness", 10);
        mColor = mPreferences.getString("color", "#00ff00");
        int pic = mPreferences.getInt("pic", 0);

        switch (thickness) {
            case 5:
                mThicknessTv.setText("细");
                break;
            case 10:
                mThicknessTv.setText("中");
                break;
            case 20:
                mThicknessTv.setText("粗");
                break;
        }

        mColorTv.setBackgroundColor(Color.parseColor(mColor));

        if (pic != 0) {
            mPicIv.setImageResource(pic);
        }

    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli(){
        this.finish();
    }

    @OnClick(R.id.activity_orbit_thickness_rv)
    void thicknessOncli(){
        setThicknessDialog();
    }

    @OnClick(R.id.activity_orbit_color_rv)
    void colorOnCli(){
        setColorDialog();
    }

    @OnClick(R.id.activity_orbit_pic_rv)
    void picOnCli(){
        setPicDialog();
    }

    private void setPicDialog() {

        dialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.obrit_pic_dialog,null);
        dialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        MyHorizontalScrollView mHorizontalScrollView = (MyHorizontalScrollView)view.findViewById(R.id.id_horizontalScrollView);
        HorizontalScrollViewAdapter mAdapter = new HorizontalScrollViewAdapter(this, mDatas2,mDatasStr);

        mHorizontalScrollView.setOnItemClickListener(new MyHorizontalScrollView.OnItemClickListener()
        {

            @Override
            public void onClick(View view, int position)
            {
                view.setBackgroundColor(Color.parseColor("#AA024DA4"));
                mPicIv.setImageResource((int)mDatas2.get(position));

                mEditor.putInt("pic",(int)mDatas2.get(position));
                mEditor.commit();

                dialog.dismiss();
            }
        });
        //设置适配器
        mHorizontalScrollView.initDatas(mAdapter);

        ApiUtils.setDialogWindow(dialog);
        dialog.show();
    }

    @Override
    protected void initInject() {

    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM;
            //        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.width = (int) getResources().getDimension(R.dimen.DIP_330_DP);
            window.setAttributes(params);
        }
    }

    private void setColorDialog() {

        LinearLayout layout = new LinearLayout(OrbitActivity.this);
        LinearLayout layout2 = new LinearLayout(OrbitActivity.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout2.setOrientation(LinearLayout.HORIZONTAL);
        layout.setBackgroundColor(Color.parseColor("#ffffff"));

        Button mConfirmBtn = new Button(OrbitActivity.this);
        mConfirmBtn.setText("确认");
        Button mConcelBtn = new Button(OrbitActivity.this);
        mConcelBtn.setText("取消");
        Button mBackBtn = new Button(OrbitActivity.this);
        mBackBtn.setText("复原");

        final TextView colorText = new TextView(OrbitActivity.this);
        final ColorPickerView colorPick = new ColorPickerView(OrbitActivity.this, Color.parseColor(mColor), 2,colorText);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.WRAP_CONTENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.topMargin = 20;
        lp2.gravity = Gravity.CENTER_HORIZONTAL;

        layout2.addView(mConcelBtn,lp2);
        layout2.addView(mBackBtn,lp2);
        layout2.addView(mConfirmBtn,lp2);

        lp2.topMargin = 20;

        layout.addView(colorPick, lp);
        layout.addView(colorText,lp2);
        layout.addView(layout2,lp2);

        final Dialog mDialog = new Dialog(OrbitActivity.this,R.style.DialogTheme);
        mDialog.setContentView(layout,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setDialogWindow(mDialog);
        mDialog.show();


        mConcelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPick.setCenterColor(Color.parseColor(mColor));
            }
        });
        mConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mColorTv.setBackgroundColor(Color.parseColor(colorText.getText().toString()));
                mEditor.putString("color",colorText.getText().toString());
                mEditor.commit();
                mDialog.dismiss();
            }
        });
    }

    private void setThicknessDialog() {

        dialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.obrit_thickness_dialog,null);
        dialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mThinTv = (TextView)view.findViewById(R.id.thickness_dialog_thin);
        TextView mMediumTv = (TextView)view.findViewById(R.id.thickness_dialog_medium);
        TextView mCrudeTv = (TextView)view.findViewById(R.id.thickness_dialog_crude);

        TextView mThickCancel = (TextView)view.findViewById(R.id.thickness_dialog_cancel);
        mThinTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThicknessTv.setText("细");
                mEditor.putInt("thickness", 5);
                mEditor.commit();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        mMediumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThicknessTv.setText("中");
                mEditor.putInt("thickness", 10);
                mEditor.commit();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        mCrudeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mThicknessTv.setText("粗");
                mEditor.putInt("thickness", 20);
                mEditor.commit();
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        mThickCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        setDialogWindow(dialog);
        dialog.show();
    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        return null;
    }
}
