package com.haoxi.dove.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyRouteHolder extends BasicRvHolder{

    @BindView(R.id.item_route_title) public TextView mTitleTv;

    public MyRouteHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
        super(mContext, itemView, myItemClickListener, myItemLongClickListener);
        ButterKnife.bind(this,itemView);
    }
}
