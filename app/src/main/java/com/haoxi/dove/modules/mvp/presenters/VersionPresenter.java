package com.haoxi.dove.modules.mvp.presenters;

import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.callback.MyProgressCallback;
import com.haoxi.dove.modules.mvp.models.IAboutModel;
import com.haoxi.dove.modules.mvp.views.IAboutView;
import com.haoxi.dove.newin.bean.OurVerBean;

import java.util.Map;

/**
 * Created by lifei on 2017/1/14.
 */

public class VersionPresenter extends BasePresenter<IAboutView,OurVerBean> implements IVersionPresenter {

    private IAboutModel aboutModel;

    public VersionPresenter(IAboutModel aboutModel) {
        this.aboutModel = aboutModel;
    }


    @Override
    public void updateVar(Map<String,String> map) {

        checkViewAttached();

        aboutModel.updateVar(map,this);

    }

    @Override
    public void requestSuccess(OurVerBean data) {
        super.requestSuccess(data);

        Log.e("fadvwasvb",data.getMsg()+"-----"+data.getData().getUrl_android()+"-----"+data.getData().getVersion());

        if (isViewAttached()){
            getMvpView().setNetTag(false);
            getMvpView().toJudgeVer(data);
        }
    }

    @Override
    public void downloadApk(String apkUrl) {

        checkViewAttached();
        getMvpView().showProgressDialog();

        aboutModel.downloadApk(apkUrl, new MyProgressCallback() {
            @Override
            public void onProgressMax(int max) {
                if (isViewAttached()){
                    getMvpView().setProgressMax(max);
                }
            }

            @Override
            public void onProgressCurrent(int progress) {
                if (isViewAttached()){
                    getMvpView().setCuProgress(progress);
                }
            }

            @Override
            public void onSuccess(String path) {

                getMvpView().installApk(path,100);
                getMvpView().hideProgressDialog();
            }

            @Override
            public void onFailure(String msg) {
                getMvpView().hideProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                getMvpView().hideProgressDialog();

            }
        });

    }
}
