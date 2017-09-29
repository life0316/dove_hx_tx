package com.haoxi.dove.modules.circle;

import com.haoxi.dove.base.MvpView;

public interface IEachView<T> extends MvpView {
    void toUpdateEach(T data);
}
