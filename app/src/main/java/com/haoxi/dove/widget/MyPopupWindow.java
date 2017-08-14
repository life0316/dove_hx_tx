package com.haoxi.dove.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.haoxi.dove.R;

/**
 * @创建者 Administrator
 * @创建时间 2017/2/8 13:11
 * @描述
 */
public class MyPopupWindow extends View{


    private Paint mPaint;

    public MyPopupWindow(Context context) {
        this(context, null);
    }

    public MyPopupWindow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyPopupWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(getResources().getColor(R.color.province_line_border));
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
    //    super.onDraw(canvas);

        Path path = new Path();
        path.moveTo(0, 25);
                path.lineTo(15, 0);
                path.lineTo(30, 25);
        //        path.quadTo(0, 40, 5, 40);
        //        path.lineTo(10, 40);
        //        path.lineTo(15, 50);
        //        path.lineTo(20, 40);
        //        path.lineTo(55, 40);
        //        path.quadTo(60, 40, 60, 35);
        //        path.lineTo(60, 5);
        //        path.quadTo(60, 0, 55, 0);
        //        path.lineTo(5, 0);
        //        path.quadTo(0, 0, 0, 5);



        canvas.drawPath(path, mPaint);
    }
}
