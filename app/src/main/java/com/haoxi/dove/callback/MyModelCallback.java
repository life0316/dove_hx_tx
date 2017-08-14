package com.haoxi.dove.callback;


public interface MyModelCallback {
    void onSuccess(Object obj);

    void onFailure(String msg);

    void onError(Throwable e);

}
