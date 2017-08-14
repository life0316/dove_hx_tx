package com.haoxi.dove.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lifei on 2017/1/6.
 */

public class MyPigeonHolder extends BasicRvHolder {

    @BindView(R.id.item_rv_mypigeon_ismate) public TextView mIsMate;
    @BindView(R.id.item_rv_mypigeon_pigeonold) public TextView mPegionOld;
    @BindView(R.id.item_rv_mypigeon_pigeoncolor) public TextView mPegionColor;
    @BindView(R.id.item_rv_mypigeon_circleid) public TextView mCircleId;
    @BindView(R.id.item_rv_mypigeon_pigeonsex) public ImageView mPegionSex;
    @BindView(R.id.item_rv_mypigeon_iv) public ImageView mPegionHead;
    @BindView(R.id.ll) public LinearLayout ll;
    @BindView(R.id.item_rv_mypigeon_cb) public CheckBox mCheckBox;

    public View root;


    public MyPigeonHolder(final Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {

        super(mContext, itemView, myItemClickListener, myItemLongClickListener);

        ButterKnife.bind(this,itemView);
        this.root = itemView;

    }

}
