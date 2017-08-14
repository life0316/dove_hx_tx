package com.haoxi.dove.callback;

import android.view.View;

import com.haoxi.dove.newin.bean.InnerDoveData;

public interface MyRvItemClickListener {

    void onItemClick(View view, int position, boolean isShowBox, InnerDoveData innerDoveData);
}
