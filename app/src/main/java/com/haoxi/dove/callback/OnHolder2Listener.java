package com.haoxi.dove.callback;

import android.support.v7.widget.RecyclerView;

public interface OnHolder2Listener<T,VH extends RecyclerView.ViewHolder>{
    void toInitHolder(VH holder, int position, T data);
}
