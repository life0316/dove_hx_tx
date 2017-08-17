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
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;
import com.haoxi.dove.utils.StringUtils;

import java.util.List;
import java.util.Map;
import butterknife.ButterKnife;

public abstract class BaseSrFragment extends Fragment implements MvpView {

    protected Dialog                   mDialog;
    protected String                   token;
    protected String                   userObjId;

    protected boolean netTag = true;
    protected MyApplication mApplication;
    protected List<String> mPigeonCodes;

    protected Map<String, Boolean> numMap;

    public AppComponent getAppComponent(){
        return ((MyApplication)getActivity().getApplication()).getAppComponent();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = MyApplication.getMyBaseApplication();
        mPigeonCodes = mApplication.getmPigeonCodes();
        numMap = mApplication.getNumMap();
        token = SpUtils.getString(getActivity(), SpConstant.USER_TOKEN);
        userObjId = SpUtils.getString(getActivity(), SpConstant.USER_OBJ_ID);

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
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER;
            window.setAttributes(params);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_my_smart,container, false);
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
        String versionName = ApiUtils.getVersionName(getContext());
        if (versionName != null) {
            return "1.0";
        }
        return "1.0";
    }
}
