package com.haoxi.dove.base;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;

public abstract class MyBaseRvAdapter<T extends ViewHolder> extends RecyclerView.Adapter<T> {
    //条目点击监听
    public MyItemClickListener mItemClickListener;
    //长按条目监听
    public MyItemLongClickListener mItemLongClickListener;
    public void setOnItemClickListener(MyItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }
}
