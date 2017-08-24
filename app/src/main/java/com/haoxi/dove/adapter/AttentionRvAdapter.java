package com.haoxi.dove.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.newin.bean.InnerAttention;
import com.zly.www.easyrecyclerview.adapter.CommonAdapter;
import com.zly.www.easyrecyclerview.adapter.viewholder.BaseViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttentionRvAdapter extends CommonAdapter<InnerAttention,AttentionRvAdapter.MyRefrashHolder> {

    private MyItemClickListener myItemClickListener;
    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    @Override
    public MyRefrashHolder createCustomViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_attention, null);
        return new MyRefrashHolder(getContext(),view,myItemClickListener);
    }

    @Override
    public void bindCustomViewHolder(MyRefrashHolder holder, InnerAttention innerAttention, int position) {
        onHolderListener.toInitHolder(holder,position,innerAttention);
    }

    public class MyRefrashHolder extends BaseViewHolder implements View.OnClickListener{

        public MyItemClickListener mItemClickListener;
        public Context mContext;

        public CircleImageView mUserIcon;

        public TextView mUserName;

        public TextView mUserPhone;

        public MyRefrashHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            this.mItemClickListener = myItemClickListener;

            mUserIcon = (CircleImageView) itemView.findViewById(R.id.de_icon);
            mUserName = (TextView)itemView.findViewById(R.id.friend_name);
            mUserPhone = (TextView)itemView.findViewById(R.id.friend_phone);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getAdapterPosition());
            }
        }
    }

    private OnHolderListener<InnerAttention,AttentionRvAdapter.MyRefrashHolder> onHolderListener;
    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }
}
