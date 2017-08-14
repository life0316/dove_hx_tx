package com.haoxi.dove.holder;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BasicRvHolder;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.MyItemLongClickListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCircleHolder extends BasicRvHolder {


    @BindView(R.id.item_rv_mycircle_id) public TextView mCircleId;
    @BindView(R.id.item_rv_mycircle_activate) public TextView mActivite;
    @BindView(R.id.item_rv_mycircle_mate) public TextView mMate;

    @BindView(R.id.ch)
    public CheckBox mCheckBox;

    public MyCircleHolder(Context mContext, View itemView, MyItemClickListener myItemClickListener, MyItemLongClickListener myItemLongClickListener) {
        super(mContext, itemView, myItemClickListener, myItemLongClickListener);

        ButterKnife.bind(this, itemView);
        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });
    }
}
