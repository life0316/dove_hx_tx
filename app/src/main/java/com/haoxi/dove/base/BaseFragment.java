package com.haoxi.dove.base;

import android.app.Dialog;
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
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment implements MvpView {

    protected boolean netTag = false;
    protected Dialog mDialog;
    protected String token;
    protected String userObjId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initInject();
        token = SpUtils.getString(getActivity(), SpConstant.USER_TOKEN);
        userObjId = SpUtils.getString(getActivity(), SpConstant.USER_OBJ_ID);

        initDialog();
    }

    protected void initInject(){

    }

    private void initDialog() {
        mDialog = new Dialog(getActivity(), R.style.DialogTheme);
        mDialog.setCancelable(false);//设置对话框不能消失
        View view = getActivity().getLayoutInflater().inflate(R.layout.progressdialog,null);
        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        setDialogWindow(mDialog);
    }

    private void setDialogWindow(Dialog mDialog) {
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }
    }

    public AppComponent getAppComponent(){
        return ((MyApplication)getActivity().getApplication()).getAppComponent();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(getLayout(),container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    protected abstract int getLayout();

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
    public void setNetTag(boolean netTag) {
        this.netTag = netTag;
    }

    @Override
    public void showErrorMsg(String errorMsg) {
        ApiUtils.showToast(getContext(),errorMsg);
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
        String versionName = ApiUtils.getVersionName(getContext());
        if (versionName != null) {
            return "1.0";
        }
        return "1.0";
    }

    @Override
    public void toDo() {}

    protected Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        return map;
    }
}
