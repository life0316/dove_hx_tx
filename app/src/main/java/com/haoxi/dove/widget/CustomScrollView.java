package com.haoxi.dove.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * Created by Administrator on 2017\6\21 0021.
 */
public class CustomScrollView extends ScrollView {
    private static final String TAG = CustomScrollView.class.getSimpleName();
    private TopView mTopView;
    public CustomScrollView(Context context, TopView topView) {
        super(context);
        this.mTopView = topView;
        setVerticalScrollBarEnabled(false);//禁止竖直滑动条出现
    }
    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomScrollView(Context context) {
        super(context);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.e(TAG, "t---------->"+t);
        mTopView.onScroll(t);
    }
}
