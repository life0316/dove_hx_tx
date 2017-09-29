package com.haoxi.dove.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.newin.bean.InnerFab;
import com.zly.www.easyrecyclerview.adapter.CommonAdapter;
import com.zly.www.easyrecyclerview.adapter.viewholder.BaseViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

public class RefrashFabRvAdapter extends CommonAdapter<InnerFab, RefrashFabRvAdapter.MyRefrashHolder> {

    private MyItemClickListener myItemClickListener;
    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    @Override
    public MyRefrashHolder createCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_fab, null);
        MyRefrashHolder holder = new MyRefrashHolder(getContext(),view,myItemClickListener);
        return holder;
    }

    @Override
    public void bindCustomViewHolder(MyRefrashHolder holder,InnerFab innerData, int position) {
        onHolderListener.toInitHolder(holder,position,innerData);
    }

    public class MyRefrashHolder extends BaseViewHolder implements View.OnClickListener{
        public MyItemClickListener mItemClickListener;
        public Context mContext;
        public CircleImageView civ;
        public TextView fabName;

        public MyRefrashHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);
            this.mItemClickListener = myItemClickListener;
            itemView.setOnClickListener(this);
            civ = (CircleImageView) itemView.findViewById(R.id.item_fab_civ);
            fabName = (TextView) itemView.findViewById(R.id.item_fab_name);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    public OnHolderListener<InnerFab, MyRefrashHolder> onHolderListener;

    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }
}
