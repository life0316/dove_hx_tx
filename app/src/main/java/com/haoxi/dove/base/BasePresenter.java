package com.haoxi.dove.base;

import com.haoxi.dove.callback.RequestCallback;

/**
 * Created by lifei on 2016/12/23.
 */

public class BasePresenter<V extends MvpView,T> implements Presenter<V>,RequestCallback<T>{

    /**
     * 当前的连接
     */
    private V mvpView;

    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        this.mvpView = null;
    }

    /**
     * 获取当前连接的view
     * @return
     */
    public V getMvpView(){
        return mvpView;
    }

    /**
     * 判断 p 是否与 view 建立连接
     *
     * @return
     */
    public boolean isViewAttached(){
        return mvpView != null;
    }

    /**
     *
     * 每次调用业务请求的时候要先调用方法检查是否与view 建立连接，没有则抛出异常
     *
     */
    public void checkViewAttached(){
        if (!isViewAttached()){
            throw new MvpViewNotAttachedException();
        }
    }

    @Override
    public void beforeRequest() {
        checkViewAttached();
        if (isViewAttached()) {
            mvpView.showProgress();
        }
    }

    @Override
    public void requestError(String msg) {
        checkViewAttached();
        if (isViewAttached()) {
            mvpView.hideProgress();
            if (!"600".equals(msg)){
                mvpView.showErrorMsg(msg);
            }
            mvpView.setNetTag(false);
        }
    }

    @Override
    public void requestComplete() {
        checkViewAttached();
        if (isViewAttached()) {
            mvpView.hideProgress();
        }
    }

    @Override
    public void requestSuccess(T data) {
        checkViewAttached();
        if (isViewAttached()) {
            mvpView.hideProgress();
        }
    }


    public static class MvpViewNotAttachedException extends RuntimeException{
        public MvpViewNotAttachedException() {
            super("请求数据前请先调用 attachView(MvpView) 方法与view建立连接");
        }
    }
}
