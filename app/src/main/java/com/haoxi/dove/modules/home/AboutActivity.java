package com.haoxi.dove.modules.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.modules.mvp.models.AboutModel;
import com.haoxi.dove.modules.mvp.presenters.VersionPresenter;
import com.haoxi.dove.modules.mvp.views.IAboutView;
import com.haoxi.dove.newin.bean.OurVerBean;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.widget.CustomDialog;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_about)
public class AboutActivity extends BaseActivity implements IAboutView {

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;
    @BindView(R.id.activity_about_appname)
    TextView  mAppNameTv;
    @BindView(R.id.activity_about_versionname)
    TextView  mVerNameTv;

    private VersionPresenter presenter;
    private ProgressDialog   progressDialog;

    private static Handler mHandler = new Handler();


    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("关于信鸽");

        presenter = new VersionPresenter(new AboutModel(this));
        presenter.attachView(this);

        String appName = ApiUtils.getAppName(this);
        String versionName = ApiUtils.getVersionName(this);

        if (appName != null) {
            mAppNameTv.setText(appName);
        }

        if (versionName != null) {
            mVerNameTv.setText("v" + versionName);
        }

    }

    @OnClick(R.id.activity_about_brief)
    void briefOnCli() {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("title_tag","about");
        startActivity(intent);

    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli() {
        this.finish();
    }

    @OnClick(R.id.activity_about_version)
    void verUpdate(){

        if (!ApiUtils.isNetworkConnected(this)){
            ApiUtils.showToast(this,getString(R.string.net_conn_2));
            return;
        }

        presenter.updateVar(getParaMap());
    }

    public static int versionConvert(String source) {
        int sum = 0;
        char[] array = source.toCharArray();
        for (char anArray : array) {
            if ((anArray >= '0') && (anArray <= '9')) {
                sum = sum * 10 + (anArray - '0');
            }
        }
        return sum;
    }

    @Override
    public void toJudgeVer(OurVerBean verBean) {
        //2 查看是否有最新版本，
        int version_app = versionConvert(ApiUtils.getVersionName(this));
        int version_air = versionConvert(verBean.getData().getVersion());

        if (version_app >= version_air){
//                if ("1.5.3".equals(verCode)){
            //一致，没有最新版本
            ApiUtils.showToast(AboutActivity.this,"当前应用已经是最新版本！");

        }else {
            //不一致，有最新版本
            //弹出对话框，提醒用户更新版本
            StringBuilder sb = new StringBuilder();
            sb.append("有新版本,是否更新");

//            if (ver.getApkDesc().contains(";")) {
//                String[] strs = ver.getApkDesc().split(";");
//                for (int i = 0; i < strs.length; i++) {
//                    if (i == strs.length - 1) {
//                        sb.append(strs[i]);
//                    } else {
//                        sb.append(strs[i]).append("\r\n");
//                    }
//                }
//            }
            Log.e("sb", sb.toString());

            showUpdateDialog(verBean.getData().getVersion(), verBean.getData().getUrl_android(), sb.toString());
        }
        }

    @Override
    public void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);//设置对话框不能消失
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }



    @Override
    public void hideProgressDialog() {
        progressDialog.dismiss();

    }

    @Override
    public void setProgressMax(int max) {
        progressDialog.setMax(max);
    }

    @Override
    public void setCuProgress(int progress) {
        progressDialog.setProgress(progress);
    }

    private void showUpdateDialog(String verCode, final String apkUrl, String desc) {

        final CustomDialog dialog = new CustomDialog(AboutActivity.this,"发现新版本","发现新版本:v"+ verCode+"\n是否立即升级？","立即升级","稍后再说");

        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {

                if (!ApiUtils.isNetworkConnected(AboutActivity.this)){
                    ApiUtils.showToast(AboutActivity.this,getString(R.string.net_conn_2));
                    return;
                }

                netTag = true;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (netTag) {
                            hideProgress();
                            ApiUtils.showToast(AboutActivity.this, getString(R.string.net_conn_1));
                        }
                    }
                }, 1000 * 15);

                //升级操作，下载apk
                presenter.downloadApk(apkUrl);
                Log.e("update","下载apk");

                dialog.dismiss();
            }

            @Override
            public void doCancel() {

                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void installApk(String path, int install) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(new File(path)),"application/vnd.android.package-archive");
//        startActivityForResult(intent,INSTALL_REQUESTCODE);
        startActivityForResult(intent,install);
    }

    @Override
    public String getMethod() {
        return "/app/gversion/get_current_version";
    }
    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",mToken);

        Log.e("fadvwasvb",map.toString()+"----+map");

        return map;
    }
}
