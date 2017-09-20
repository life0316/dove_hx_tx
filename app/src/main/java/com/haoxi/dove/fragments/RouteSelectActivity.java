package com.haoxi.dove.fragments;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.home.RouteDoveFragment;
import com.haoxi.dove.utils.RxBus;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;

@ActivityFragmentInject(contentViewId = R.layout.activity_route_select)
public class RouteSelectActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.act_main_cvp)
    FrameLayout mCvp;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_setting)
    ImageView mSeletctIv;

    private PopupWindow mPopupWindow;
    private boolean isBack = true;
    RouteDoveFragment  mTab1;
    AllFlyRecordFragment mTab2;
    private Observable<Boolean> backObservable;

    @Override
    protected void init() {
        mTitleTv.setVisibility(View.GONE);
        mBackIv.setVisibility(View.VISIBLE);
        mSeletctIv.setVisibility(View.VISIBLE);
        mSeletctIv.setImageResource(R.mipmap.icon_nav_add);
        mBackIv.setOnClickListener(this);
        mSeletctIv.setOnClickListener(this);
        backObservable = RxBus.getInstance().register("route_back",Boolean.class);
        backObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isBack = aBoolean;
            }
        });
        setSelection(0);
    }

    private void setSelection(int position) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        hideAllFragments(ft);

        switch (position) {
            case 0:
                if (mTab1 == null) {
                    mTab1 = RouteDoveFragment.newInstance("信鸽");
                    ft.add(R.id.act_main_cvp, mTab1);
                } else {
                    ft.show(mTab1);
                }
                break;
            case 1:
                if (mTab2 == null) {
                    mTab2 = AllFlyRecordFragment.newInstance("轨迹");
                    ft.add(R.id.act_main_cvp, mTab2);
                } else {
                    ft.show(mTab2);
                }
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideAllFragments(FragmentTransaction ft) {
        if (mTab1 != null) {
            ft.hide(mTab1);
        }
        if (mTab2 != null) {
            ft.hide(mTab2);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.custom_toolbar_iv:
                RxBus.getInstance().post("isLoad",false);
                finish();
                break;
            case R.id.custom_toolbar_setting:
                showWindow(v);
                break;
        }
    }

    private void showWindow(View v) {
        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.add_popup, null);
            LinearLayout addPigeon = (LinearLayout) view.findViewById(R.id.add_pigeon_ll);
            LinearLayout addCirle = (LinearLayout) view.findViewById(R.id.add_circle_ll);
            LinearLayout addMate = (LinearLayout) view.findViewById(R.id.add_mate_ll);
            TextView mTv1 = (TextView) view.findViewById(R.id.add_pigeon);
            TextView mTv2 = (TextView) view.findViewById(R.id.add_circle);
            mTv1.setText("单只");
            mTv2.setText("全部");
            addMate.setVisibility(View.GONE);
            mPopupWindow = new PopupWindow(view, (int) getResources().getDimension(R.dimen.DIP_150_DP), (int) getResources().getDimension(R.dimen.DIP_120_DP));
            addPigeon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelection(0);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }

                }
            });
            addCirle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelection(1);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });
        }
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int xPos = windowManager.getDefaultDisplay().getWidth() * 1 / 5 - mPopupWindow.getWidth() / 2;
        mPopupWindow.showAsDropDown(v, xPos, 40);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1f;
       getWindow().setAttributes(params);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getWindow().getAttributes();
                params.alpha = 1f;
                getWindow().setAttributes(params);
            }
        });
    }
    @Override
    public String getMethod() {
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unregister("route_back",backObservable);
    }

    @Override
    public void onBackPressed() {
        if (isBack) {
            RxBus.getInstance().post("isLoad",false);
            super.onBackPressed();
        }else {
            RxBus.getInstance().post("close_route",true);
        }
    }
}
