package com.haoxi.dove.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2017\6\21 0021.
 */

public class MyRecyclerview extends RecyclerView {
    public MyRecyclerview(Context context) {
        super(context);
    }

    public MyRecyclerview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerview(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 1, MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }
}
