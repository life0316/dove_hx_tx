package com.haoxi.dove.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by lifei on 2017/1/6.
 */

public class SwipeMenu extends FrameLayout {

    private ViewDragHelper mDragHelper;
    private ViewGroup mBackView;
    private ViewGroup mFrontView;
    private float downX;
    private float x;

    public static enum Status {
        Close, Open, Draging;
    }

    public interface OnItemClickListener{
        void onItemClicker();
    }

    private OnItemClickListener onItemClickListener;


    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnDragChangeListener{
        // 关闭
        void onClose(SwipeMenu mSwipeLayout);
        // 将要关闭
        void onStartClose(SwipeMenu mSwipeLayout);
        // 打开
        void onOpen(SwipeMenu mSwipeLayout);
        // 将要打开
        void onStartOpen(SwipeMenu mSwipeLayout);
        // 拖拽
        void onDraging(SwipeMenu mSwipeLayout);
    }
    private Status status = Status.Close;
    private OnDragChangeListener dragChangeListener;

    public OnDragChangeListener getDragChangeListener() {
        return dragChangeListener;
    }
    public void setDragChangeListener(OnDragChangeListener dragChangeListener) {
        this.dragChangeListener = dragChangeListener;
    }
    public ViewDragHelper.Callback getCallback() {
        return callback;
    }

    public void setCallback(ViewDragHelper.Callback callback) {
        this.callback = callback;
    }

    public SwipeMenu(Context context) {
        this(context, null);
    }

    public SwipeMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // a. 在这里进行初始化操作
        mDragHelper = ViewDragHelper.create(this, callback);
    }

    ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            // child 触摸到的View
            // pointerId 多点触摸手指id
            return true;
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return mRange;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            // 修正移动到的位置，child是被拖拽的子View, left是建议值, dx是变化量

            if(child == getFrontView()){
                if(left < -mRange){
                    return -mRange;
                }else if (left > 0) {
                    return 0;
                }
            }else if (child == getBackView()) {
                if(left < mWidth - mRange){
                    return mWidth - mRange;
                }else if (left > mWidth) {
                    return mWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            // changedView被拖拽的View, left当前的位置，dx是变化量
            if(changedView == getFrontView()){
                getBackView().offsetLeftAndRight(dx);
            }else if (changedView == getBackView()) {
                getFrontView().offsetLeftAndRight(dx);
            }

            dispatchDragEvent();

            invalidate();
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            // 松手之后，做动画
            // releasedChild被释放的View，
            // xvel 松手时横向的速度, 向左为- 向右为+
            // yvel 松手时纵向的速度, 向上为- 向下为+
            if(xvel == 0 && getFrontView().getLeft() < -mRange / 2.0f){
                open();
            }else if (xvel < 0) {
                // 有向左的速度
                open();
            }else {
                close();
            }

        }

    };

    protected void dispatchDragEvent() {
        Status preStatus = status;
        status = updateStatus();

        if(dragChangeListener != null){
            dragChangeListener.onDraging(this);
        }

        if (preStatus != status) {
            // 状态发生了改变
            if (dragChangeListener == null) {
                return;
            }
            if (status == Status.Open) {
                dragChangeListener.onOpen(this);
            } else if (status == Status.Close) {
                dragChangeListener.onClose(this);
            } else if (status == Status.Draging) {
                if (preStatus == Status.Close) {
                    // 将要打开
                    dragChangeListener.onStartOpen(this);
                } else if (preStatus == Status.Open) {
                    // 将要关闭
                    dragChangeListener.onStartClose(this);
                }
            }
        }
    }

    /**
     * 获取最新的状态
     * @return
     */
    private Status updateStatus() {
        int left = getFrontView().getLeft();
        if(left == -mRange){
            return Status.Open;
        }else if (left == 0) {
            return Status.Close;
        }
        return Status.Draging;
    }
    /**
     * 关闭
     */
    public void close() {
        close(true);
    }

    public void close(boolean isSmooth){
        if(isSmooth){
            // a.触发平滑动画，移动到指定位置
            if(mDragHelper.smoothSlideViewTo(getFrontView(), 0, 0)){
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }else {
            layoutContent(false);
        }
    }

    /**
     * 打开
     */
    public void open() {
        open(true);
    }

    public void open(boolean isSmooth) {
        if(isSmooth){
            // a.触发平滑动画，移动到指定位置
            if(mDragHelper.smoothSlideViewTo(getFrontView(), -mRange, 0)){
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }else {
            layoutContent(true);
        }
    }
    @Override
    public void computeScroll() {
        super.computeScroll();

        // b.持续动画的执行
        if(mDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }


    private int mRange;
    private int mWidth;
    private int mHeight;

    public ViewGroup getBackView() {
        return mBackView;
    }

    public ViewGroup getFrontView() {
        return mFrontView;
    }

    @Override
    public boolean onInterceptTouchEvent(android.view.MotionEvent ev) {



        if (mDragHelper.shouldInterceptTouchEvent(ev)){
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.i("test", "ACTION_DOWN");
            x = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            Log.i("test", "ACTION_UP");
            float daltX = Math.abs(x - event.getX());
            if (daltX < 10) {


//                Toast.makeText(getContext(), "iteam点击。。。。",
//                        Toast.LENGTH_SHORT).show();


                this.onItemClickListener.onItemClicker();

            }
        }



        try {
            // 包裹异常
            mDragHelper.processTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }



        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mRange = getBackView().getMeasuredWidth();
        mWidth = getFrontView().getMeasuredWidth();
        mHeight = getFrontView().getMeasuredHeight();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 默认是关闭的
        layoutContent(false);
    }

    /**
     * 放置子View
     * @param
     */
    private void layoutContent(boolean isOpen) {
        // 放置前面板View
        Rect rect = computeFrontView(isOpen); // 矩形，长方形
        getFrontView().layout(rect.left, rect.top, rect.right, rect.bottom);

        // 放置后面板View
        Rect backRect = computeBackViewFromFront(rect);
        getBackView().layout(backRect.left, backRect.top, backRect.right, backRect.bottom);

        // 将前面板View前置
        bringChildToFront(getFrontView());
    }

    /**
     * 根据前View的矩形，得到后View的位置
     * @return
     */
    private Rect computeBackViewFromFront(Rect rect) {
        int left = rect.right;
        return new Rect(left, 0, left + mRange, 0 + mHeight);
    }

    /**
     * 根据开启状态，得到前View的位置
     * @param isOpen 是否开启
     * @return
     */
    private Rect computeFrontView(boolean isOpen) {
        int left = 0;
        if(isOpen){
            left = 0 - mRange;
        }
        return new Rect(left, 0, left + mWidth, 0 + mHeight);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mBackView = (ViewGroup) getChildAt(0);
        mFrontView = (ViewGroup) getChildAt(1);
    }

}

