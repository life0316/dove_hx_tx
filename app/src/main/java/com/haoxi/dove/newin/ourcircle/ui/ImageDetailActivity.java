package com.haoxi.dove.newin.ourcircle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.haoxi.dove.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\8\4 0004.
 */

public class ImageDetailActivity extends AppCompatActivity{

    public static final String EXTRA_IMAGE_URL = "detail_image_url";
    public static final String VIEW_NAME_HEADER_IMAGE = "view_header_image";

    @BindView(R.id.image_detail)
    ImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_iamge);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String headerUrl = intent.getStringExtra(EXTRA_IMAGE_URL);

        Glide.with(this)
                .load(headerUrl)
                .dontAnimate()
                .placeholder(R.mipmap.btn_img_photo_default)
                .error(R.mipmap.btn_img_photo_default)
                .into(image);


        ViewCompat.setTransitionName(image,VIEW_NAME_HEADER_IMAGE);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
