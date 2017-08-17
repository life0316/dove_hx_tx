package com.haoxi.dove.modules.home;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.acts.OrbitActivity;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;

import butterknife.BindView;
import butterknife.OnClick;

@ActivityFragmentInject(contentViewId = R.layout.activity_currency)
public class CurrencyActivity extends BaseActivity {

    @BindView(R.id.custom_toolbar_iv) ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv) TextView mTitleTv;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("通用设置");
    }

    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli(View view){
        this.finish();
    }

    @OnClick(R.id.activity_currency_orbit)
    void orbitOnCli(){

        Intent intent = new Intent(this,OrbitActivity.class);
        startActivity(intent);
    }

    @Override
    public String getMethod() {
        return "";
    }
}
