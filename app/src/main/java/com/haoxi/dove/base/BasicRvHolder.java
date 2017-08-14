package com.haoxi.dove.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;


/**
 * @创建者 Administrator
 * @创建时间 2016/9/9 11:28
 * @描述
 */
public class BasicRvHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

    public MyItemClickListener mItemClickListener;
    public MyItemLongClickListener mItemLongClickListener;
    public Context mContext;

    public BasicRvHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
        super(itemView);

        this.mItemClickListener = myItemClickListener;
        this.mItemLongClickListener = myItemLongClickListener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (mItemClickListener != null){
            mItemClickListener.onItemClick(v,getPosition());
        }

    }

    @Override
    public boolean onLongClick(View v) {


        if (mItemLongClickListener != null){
            if (mItemClickListener != null){
                mItemClickListener = null;
            }

            mItemLongClickListener.onItemLongClick(v,getPosition());
        }
        return true;
    }
}
