package com.haoxi.dove.base;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.haoxi.dove.R;
import com.haoxi.dove.inject.AppComponent;
import com.haoxi.dove.observable.MyCancleObservable;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.StringUtils;

import java.util.List;

import butterknife.ButterKnife;

/**
 * Created by lifei on 2017/1/10.
 */

public abstract class BaseReFrashRvFragment<T extends Presenter<MvpView>> extends Fragment implements MvpView {

    protected Dialog                   mDialog;
    protected SharedPreferences        preferences;
    protected SharedPreferences.Editor editor;
    protected String                   token;
    protected String                   userObjId;

    protected boolean netTag = true;
    protected MyApplication mApplication;
    protected List<String> mPigeonCodes;

    protected MyCancleObservable mCancleObservable;


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mCancleObservable = MyCancleObservable.getInstance();
    }

    public AppComponent getAppComponent(){
        return ((MyApplication)getActivity().getApplication()).getAppComponent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = MyApplication.getMyBaseApplication();

        if (mCancleObservable == null) {
            mCancleObservable = MyCancleObservable.getInstance();
        }

        mPigeonCodes = mApplication.getmPigeonCodes();


        preferences = getActivity().getSharedPreferences(ConstantUtils.USERINFO, Context.MODE_PRIVATE);
        editor = preferences.edit();
        token = preferences.getString("user_token", "");
        userObjId = preferences.getString("user_objid", "");

        inject();

        initDialog();
    }

    protected abstract void inject();

    private void initDialog() {
        mDialog = new Dialog(getActivity(), R.style.DialogTheme);
        mDialog.setCancelable(false);//设置对话框不能消失
        View view = getActivity().getLayoutInflater().inflate(R.layout.progressdialog,null);
        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setDialogWindow(mDialog);
    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        //   params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.activity_base_refrash_rv,container, false);

        ButterKnife.bind(this,view);

        return view;
    }



    public String getUserObjId() {

        if ("".equals(userObjId)){
            return "";
        }

        return userObjId;
    }

    public String getToken() {

        if ("".equals(token)){
            return "";
        }

        return token;
    }


    @Override
    public void showProgress() {
        mDialog.show();
    }

    @Override
    public void hideProgress() {
        mDialog.dismiss();
    }

    @Override
    public void showErrorMsg(String errorMsg) {

        ApiUtils.showToast(getActivity(),errorMsg);

    }

    @Override
    public void setNetTag(boolean netTag) {
        this.netTag = netTag;
    }

    @Override
    public String getTime() {

        long time = StringUtils.tsToString(StringUtils.getNowTimestamp());

        return String.valueOf(time);
    }

    @Override
    public String getSign() {
        String sign = "";
        if (getMethod() != null) {
            sign = MD5Tools.MD5(getMethod()+getTime()+getVersion()+ ConstantUtils.APP_SECRET);
        }

        return sign;
    }

    @Override
    public String getVersion() {

        String appName = ApiUtils.getAppName(getContext());
        String versionName = ApiUtils.getVersionName(getContext());
        if (versionName != null) {
            return "1.0";
        }
        return "1.0";
    }
}
