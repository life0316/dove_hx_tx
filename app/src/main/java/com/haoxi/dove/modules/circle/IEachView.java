package com.haoxi.dove.modules.circle;

import com.haoxi.dove.base.MvpView;

/**
 * Created by Administrator on 2017\8\18 0018.
 */

public interface IEachView<T> extends MvpView {

    void toUpdateEach(T data);
}
