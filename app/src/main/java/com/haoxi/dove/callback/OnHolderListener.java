package com.haoxi.dove.callback;

import com.zly.www.easyrecyclerview.adapter.viewholder.BaseViewHolder;

public interface OnHolderListener<T,VH extends BaseViewHolder>{
    void toInitHolder(VH holder, int position, T data);
}
