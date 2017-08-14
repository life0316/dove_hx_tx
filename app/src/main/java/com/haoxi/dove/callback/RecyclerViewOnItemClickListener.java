package com.haoxi.dove.callback;

import android.view.View;

public interface RecyclerViewOnItemClickListener {

    //点击事件
    void onItemClickListener(View view,int position,boolean longClickTag);

    //长按事件
    void onItemLongClickListener(View view,int position);

}
