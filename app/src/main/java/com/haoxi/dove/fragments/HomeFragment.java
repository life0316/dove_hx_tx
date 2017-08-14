package com.haoxi.dove.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoxi.dove.R;
import com.haoxi.dove.acts.MainActivity;
import com.haoxi.dove.modules.home.MyPigeonActivity;
import com.haoxi.dove.modules.home.MyRingActivity;
import com.haoxi.dove.modules.home.OptimisedActivity;
import com.haoxi.dove.modules.home.PersonalActivity;
import com.haoxi.dove.newin.bean.OurUserInfo;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.widget.GlideCircleImage;

import java.io.ByteArrayInputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by lifei on 2016/12/27.
 */

public class HomeFragment extends Fragment {

    private OurUserInfo userInfo;
    private String userPhone;
    private SharedPreferences preferences;
    public HomeFragment homeFragment;

    private String headPic;

    @BindView(R.id.fragment_home_civ_icon)
    CircleImageView mHomeCiv;

    @BindView(R.id.fragment_home_username)
    TextView mUserNameTv;

    @BindView(R.id.fragment_home_userid)
    TextView mUserCodeTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userInfo = ((MainActivity) getActivity()).getUser();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        ButterKnife.bind(this, view);
        readFromPreference();

        mHomeCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PersonalActivity.class);
                intent.putExtra("userobject",userInfo);
                startActivity(intent);
            }
        });

        if (userInfo != null) {
            userPhone =userInfo.getData().getTelephone();
            headPic = userInfo.getData().getHeadpic();
//            if (!"".equals(headPic)) {
//
//                GlideCircleImage circleImage = new GlideCircleImage(getContext());
//
//                Glide.with(getContext())
//                        .load(headPic)
//                        .transform(circleImage)
//                        .centerCrop()
////                        .fitCenter()
//                        .thumbnail(0.1f)
////                        .placeholder(R.mipmap.btn_img_photo_default)
//                        .error(R.mipmap.btn_img_photo_default)
//                        .crossFade()
//                        .into(mHomeCiv);
//            }
        }
        return view;
    }

    @OnClick(R.id.fragment_home_route)
    void routeOnCli() {

        Intent intent = new Intent(getActivity(), RouteSelectActivity.class);
        startActivity(intent);

    }

    @OnClick(R.id.fragment_home_mypigeons)
    void myPigeonOnCli() {
        Intent intent = new Intent(getActivity(), MyPigeonActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.fragment_home_ring)
    void myRingOnCli() {

            Intent intent = new Intent(getActivity(), MyRingActivity.class);
            startActivity(intent);
    }

    @OnClick(R.id.fragment_home_optimised)
    void settingOnCli() {
        Intent intent = new Intent(getActivity(), OptimisedActivity.class);
        intent.putExtra("userobject", userInfo);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        readFromPreference();
    }

    private void readFromPreference() {

        preferences = this.getActivity().getSharedPreferences(ConstantUtils.USERINFO, Context.MODE_PRIVATE);

        String mNickName = preferences.getString("nick_name","");
        String mUserCode = preferences.getString("user_code","");
        String mUserPVR = preferences.getString("user_headpic", "");

        mUserNameTv.setText(mNickName);
        mUserCodeTv.setText(mUserCode);

        if (mUserPVR.startsWith("http")) {
            GlideCircleImage circleImage = new GlideCircleImage(getContext());

//                Glide.with(getContext())
//                        .load(headPic)
//                        .transform(circleImage)
//                        .centerCrop()
//                        .thumbnail(0.1f)
//                        .error(R.mipmap.btn_img_photo_default)
//                        .crossFade()
//                        .into(mHomeCiv);

            Glide.with(getContext())
                    .load(headPic)
                    .asBitmap()
//                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
//                    .into(mHomeCiv);
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mHomeCiv.setImageBitmap(resource);
                        }
                    });

        }else {
            if (mUserPVR.equals("") == false) {
                byte[] byteArray = Base64.decode(mUserPVR, Base64.DEFAULT);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                //第三步:利用ByteArrayInputStream生成Bitmap
                Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                mHomeCiv.setImageBitmap(bitmap);
            }
        }
    }

    public static HomeFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString("ARGS", content);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
