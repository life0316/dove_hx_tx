package com.haoxi.dove.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.zly.www.easyrecyclerview.adapter.CommonAdapter;
import com.zly.www.easyrecyclerview.adapter.viewholder.BaseViewHolder;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lifei on 2017/5/1.
 */

public class RefrashRvAdapter extends CommonAdapter<InnerCircleBean, RefrashRvAdapter.MyRefrashHolder> {


    private MyItemClickListener myItemClickListener;

    public void setMyItemClickListener(MyItemClickListener myItemClickListener) {
        this.myItemClickListener = myItemClickListener;
    }

    @Override
    public MyRefrashHolder createCustomViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_friend_circle, null);

        MyRefrashHolder holder = new MyRefrashHolder(getContext(),view,myItemClickListener);

        return holder;
    }

    @Override
    public void bindCustomViewHolder(MyRefrashHolder holder, InnerCircleBean innerCircleBean, int position) {

        onHolderListener.toInitHolder(holder,position,innerCircleBean);
    }

    public class MyRefrashHolder extends BaseViewHolder implements View.OnClickListener{

        public MyItemClickListener mItemClickListener;
        public Context mContext;

        public Button mCommentBtn;
        public Button mTranspondBtn;
        public Button mPraiseBtn;
        public Button mAddFriendBtn;

        public TextView mContentTv;

        public CircleImageView mUserIcon;

        public TextView mUserName;

        public TextView mCreateTimeTv;
        public ImageView mDownIv;

        public ImageView mContentImage;

        public RecyclerView mRecyclerView;

        public MyRefrashHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener) {
            super(itemView);

            this.mItemClickListener = myItemClickListener;

            itemView.setOnClickListener(this);

            mTranspondBtn = (Button) itemView.findViewById(R.id.friend_share);
            mPraiseBtn = (Button) itemView.findViewById(R.id.friend_dislike);
            mCommentBtn = (Button) itemView.findViewById(R.id.friend_comments);
            mAddFriendBtn = (Button) itemView.findViewById(R.id.item_dynamic_add_friend);
            mContentTv = (TextView)itemView.findViewById(R.id.friend_text);
            mCreateTimeTv = (TextView)itemView.findViewById(R.id.friend_date);
            mDownIv = (ImageView)itemView.findViewById(R.id.itme_dynamic_down);
            mUserIcon = (CircleImageView) itemView.findViewById(R.id.de_icon);
            mUserName = (TextView)itemView.findViewById(R.id.friend_name);
            mRecyclerView = (RecyclerView) itemView.findViewById(R.id.item_isfriend_rv);

            mContentImage = (ImageView)itemView.findViewById(R.id.item_isfriend_imageview);

        }

        @Override
        public void onClick(View v) {

            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }

    public OnHolderListener<InnerCircleBean, RefrashRvAdapter.MyRefrashHolder> onHolderListener;

    public void setOnHolderListener(OnHolderListener onHolderListener) {
        this.onHolderListener = onHolderListener;
    }
}
