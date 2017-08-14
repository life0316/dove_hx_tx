package com.haoxi.dove.modules.loginregist;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;

import butterknife.BindView;

/**
 * Created by lifei on 2017/5/15.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_user_pro)
public class UserProtocolActivity extends BaseActivity {

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;

    @Override
    protected void initInject() {

    }

    @Override
    protected void init() {

        mTitleTv.setText("用户协议");
        mBackIv.setVisibility(View.VISIBLE);
        mBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        return null;
    }
}
