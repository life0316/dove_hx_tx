package com.haoxi.dove.modules.mvp.presenters;

import android.content.Context;
import android.util.Log;

import com.haoxi.dove.base.BasePresenter;
import com.haoxi.dove.modules.mvp.models.IUploadImage;
import com.haoxi.dove.modules.mvp.models.UploadImageModel;
import com.haoxi.dove.modules.mvp.views.IUpdateImageView;
import com.haoxi.dove.newin.bean.UploadImageBean;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * Created by lifei on 2017/1/14.
 */
public class PersonalPresenter extends BasePresenter<IUpdateImageView, UploadImageBean> implements IPersonalPersenter {

    private IUpdateImageView mViem;

    private Context context;

    private IUploadImage uploadImage;

    public PersonalPresenter(Context context, IUpdateImageView mView) {
        this.context = context;
        this.mViem = mView;
        attachView(mView);

        uploadImage = new UploadImageModel();

    }

    @Override
    public void requestSuccess(UploadImageBean data) {
        super.requestSuccess(data);

        Log.e("UploadImageBean",data.getData());

        getMvpView().toUploadHeadPic(data);

    }

    @Override
    public void uploadImage(Map<String, RequestBody> map) {
        uploadImage.uploageImage(map,this);
    }
}
