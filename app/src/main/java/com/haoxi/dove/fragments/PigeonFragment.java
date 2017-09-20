package com.haoxi.dove.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.haoxi.dove.R;
import com.haoxi.dove.base.MyBaseAdapter;
import com.haoxi.dove.modules.pigeon.MyPigeonFragment;
import com.haoxi.dove.modules.pigeon.MyRingFragment;
import com.haoxi.dove.modules.pigeon.AddPigeonActivity;
import com.haoxi.dove.modules.pigeon.AddRingActivity;
import com.haoxi.dove.modules.pigeon.MateActivity;

import butterknife.OnClick;
import rx.functions.Action1;
public class PigeonFragment extends MyBaseFragment {
    private final String TAG = "PigeonFragment";
    private PopupWindow mPopupWindow;
    @Override
    protected void isPaging(boolean ispag) {
        super.isPaging(true);
    }

    public static PigeonFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        PigeonFragment fragment = new PigeonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setCurrentItem(viewpagerTagNum);
        cancleObservable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean integer) {
                if (integer){
                    mCancleTv.setVisibility(View.GONE);
                    mTabAddIv.setVisibility(View.VISIBLE);
                }else {
                    mCancleTv.setVisibility(View.VISIBLE);
                    mTabAddIv.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void initToolbar(View view) {
        mToolbar.setTitle("");
        mTabTitleTv.setText("信鸽");
        mTabSearchIv.setVisibility(View.GONE);
        mTabAdd2Iv.setVisibility(View.GONE);
        mTabAddIv.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    }

    @Override
    public void setupAdapter(MyBaseAdapter adapter) {
        adapter.clearFragment();
        adapter.addFragment(MyRingFragment.class, "我的鸽环", getBundle("我的鸽环"));
        adapter.addFragment(MyPigeonFragment.class, "我的信鸽", getBundle("我的信鸽"));
        //        adapter.addFragment(MyFriendFragment.class,"好友信鸽",getBundle("好友信鸽"));
    }

    @OnClick(R.id.fragment_base_add)
    void addOnclick(View v) {
        showWindow(v);
    }

    private void showWindow(View v) {
        if (mPopupWindow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.add_popup, null);
            LinearLayout addPigeon = (LinearLayout) view.findViewById(R.id.add_pigeon_ll);
            LinearLayout addCirle = (LinearLayout) view.findViewById(R.id.add_circle_ll);
            LinearLayout addMate = (LinearLayout) view.findViewById(R.id.add_mate_ll);
            mPopupWindow = new PopupWindow(view, (int) getResources().getDimension(R.dimen.DIP_150_DP), (int) getResources().getDimension(R.dimen.DIP_180_DP));
            addPigeon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddPigeonActivity.class);
                    startActivity(intent);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }

                }
            });
            addCirle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), AddRingActivity.class);
                    startActivity(intent);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });

            addMate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), MateActivity.class);
                    intent.putExtra("matelist_size",mateList.size());
                    startActivity(intent);
                    if (mPopupWindow != null) {
                        mPopupWindow.dismiss();
                    }
                }
            });
        }

        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int xPos = windowManager.getDefaultDisplay().getWidth() * 1 / 5 - mPopupWindow.getWidth() / 2;
        mPopupWindow.showAsDropDown(v, xPos, 40);
        WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
        params.alpha = 1f;
        getActivity().getWindow().setAttributes(params);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams params = getActivity().getWindow().getAttributes();
                params.alpha = 1f;
                getActivity().getWindow().setAttributes(params);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        viewpagerTagNum = mViewPager.getCurrentItem();
    }
}
