package com.haoxi.dove.modules.home;

import android.content.Intent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.utils.ConstantUtils;

import butterknife.BindView;
import butterknife.OnClick;
@ActivityFragmentInject(contentViewId = R.layout.activity_webview)
public class WebViewActivity extends BaseActivity {

    private String urlTag = ConstantUtils.APP_INTRO_URL;
    private String titleTag = "about";

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.activity_webview_pb)
    ProgressBar webViewPb;
    @BindView(R.id.activity_webview_wv)
    WebView mWebView;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        mTitleTv.setText("加载中...");

        mBackIv.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        titleTag = intent.getStringExtra("title_tag");

        switch (titleTag){
            case "about":
                urlTag = ConstantUtils.APP_INTRO_URL;
                break;
        }

        webViewPb.setMax(100);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(false);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                webViewPb.setProgress(newProgress);
                if (newProgress >= 100){
                    webViewPb.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (titleTag != null) {
                                mTitleTv.setText(titleTag);
                            }
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mTitleTv.setText("加载中...");
                        }
                    });
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                view.loadUrl(url);

                return true;
            }
        });

        mWebView.loadUrl(urlTag);

    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnlic(){
        this.finish();
    }

    @Override
    public String getMethod() {
        return "";
    }
}
