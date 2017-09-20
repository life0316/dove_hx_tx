package com.haoxi.dove.base;

import com.haoxi.dove.callback.RequestCallback;
public class BasePresenter<V extends MvpView,T> implements Presenter<V>,RequestCallback<T>{
    private V mvpView;
    @Override
    public void attachView(V mvpView) {
        this.mvpView = mvpView;
    }
    @Override
    public void detachView() {
        this.mvpView = null;
    }
    public V getMvpView(){
        return mvpView;
    }
    public boolean isViewAttached(){
        return mvpView != null;
    }
    protected void checkViewAttached(){
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
