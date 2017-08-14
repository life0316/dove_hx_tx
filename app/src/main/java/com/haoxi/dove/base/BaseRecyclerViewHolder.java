package com.haoxi.dove.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lifei on 2017/4/24.
 */

public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder {

    //用来存储layout里包含的View，以View的id作为key，view对象为value
    protected SparseArray<View> mViews;

    protected Context mContext;


    public BaseRecyclerViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        mViews = new SparseArray<>();
    }


    private <T extends View> T findViewById(int viewId) {
        View view = mViews.get(viewId);

        if (view == null) {
            view = itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    public View getView(int viewId) {
        return findViewById(viewId);
    }


    /***
     * 辅助方法
     ***/
    public BaseRecyclerViewHolder setText(int viewId, String text) {
        TextView tv = findViewById(viewId);
        tv.setText(text);
        return this;
    }

    /***
     * 辅助方法
     ***/
    public BaseRecyclerViewHolder setBtnText(int viewId, String text) {
        Button btn = findViewById(viewId);
        btn.setText(text);
        return this;
    }

    public Button getButton(int viewId) {
        return (Button) findViewById(viewId);
    }

    public ImageView getImageView(int viewId) {
        return (ImageView) findViewById(viewId);
    }

    public RecyclerView getRecyclerView(int viewId){
        return (RecyclerView)findViewById(viewId);
    }

    public TextView getTextView(int viewId){
        return (TextView)findViewById(viewId);
    }
}
