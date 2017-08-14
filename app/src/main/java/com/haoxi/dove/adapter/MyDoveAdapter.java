package com.haoxi.dove.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haoxi.dove.R;
import com.haoxi.dove.base.MyBaseRvAdapter;
import com.haoxi.dove.callback.ToSetHolderListener;
import com.haoxi.dove.holder.MyRouteHolder;

import java.util.ArrayList;
import java.util.List;

public class MyDoveAdapter<T> extends MyBaseRvAdapter<MyRouteHolder> {

    private List<T> datas = new ArrayList<>();
    private Context         mContext;

    private ToSetHolderListener<T> toSetHolderListener;

    public void setToSetHolderListener(ToSetHolderListener toSetHolderListener) {
        this.toSetHolderListener = toSetHolderListener;
    }

    public MyDoveAdapter(Context context) {
        this.mContext = context;
    }

    public void addDatas(List<T> datas) {
        this.datas = datas;
        Log.e("RouteBean",datas.size()+"----datas");
        notifyDataSetChanged();
    }


    @Override
    public MyRouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_route,null);

        return new MyRouteHolder(mContext,view,mItemClickListener,mItemLongClickListener);
    }

    @Override
    public void onBindViewHolder(MyRouteHolder holder, int position) {

        //holder.mTitleTv.setText("信鸽： "+datas.get(position).getFoot_ring());

        toSetHolderListener.toSetHolder(holder,datas.get(position),position);
    }

    @Override
    public int getItemCount() {
        return datas.size() == 0 && datas == null?0:datas.size();
    }


}
