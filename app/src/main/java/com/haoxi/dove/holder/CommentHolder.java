package com.haoxi.dove.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;

import de.hdodenhof.circleimageview.CircleImageView;
public class CommentHolder extends BasicRvHolder implements View.OnClickListener{

        public CircleImageView civ;
        public TextView mTextNameTv;
        public TextView mTextContentTv;
        public TextView mTimeTv;
        public MyItemClickListener mItemClickListener;
        public Context mContext;

        public CommentHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener) {
            super(mContext,itemView,myItemClickListener,null);
            this.mItemClickListener = myItemClickListener;
            itemView.setOnClickListener(this);
            civ = (CircleImageView) itemView.findViewById(R.id.our_comments_civ);
            mTextNameTv = (TextView) itemView.findViewById(R.id.our_comments_name);
            mTextContentTv = (TextView) itemView.findViewById(R.id.our_comments_content);
            mTimeTv = (TextView) itemView.findViewById(R.id.our_comments_time);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null){
                mItemClickListener.onItemClick(v,getPosition());
            }
        }
    }
