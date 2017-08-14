package com.haoxi.dove.callback;

import com.haoxi.dove.holder.MyRouteHolder;

public interface ToSetHolderListener<T> {
    void toSetHolder(MyRouteHolder holder, T data, int position);
}
