package com.haoxi.dove.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.haoxi.dove.R;

/**
 * Created by lifei on 2017/5/23.
 */

public class BottomPopView extends PopupWindow {


    private Context mContext;

    private View view;

    public BottomPopView(Context mContext) {
        super(mContext);
        this.mContext = mContext;


    }

    public BottomPopView(View contentView, int width, int height, Context mContext) {
        super(contentView, width, height);
        this.mContext = mContext;
        this.view = contentView;
    }

    public BottomPopView(int width, int height, Context mContext) {
        super(width, height);
        this.mContext = mContext;

        this.view = LayoutInflater.from(mContext).inflate(R.layout.layout_show_marker2, null);

        initView();

    }

    private void initView() {

        // 设置外部可点击
        this.setOutsideTouchable(true);
//        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
//        this.view.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//
//                int height = view.findViewById(R.id.show_marker_ll).getTop();
//
//                int y = (int) event.getY();
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    if (y < height) {
//                        dismiss();
//                    }
//                }
//                return true;
//            }
//        });

         /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

//        // 实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置弹出窗体的背景
//        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.take_photo_anim);
    }
}
