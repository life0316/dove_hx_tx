package com.haoxi.dove.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseFragment;
import com.haoxi.dove.modules.circle.AdCircleAdapter;
import com.haoxi.dove.modules.home.MyPigeonActivity;
import com.haoxi.dove.modules.home.MyRingActivity;
import com.haoxi.dove.modules.home.OptimisedActivity;
import com.haoxi.dove.modules.home.PersonalActivity;
import com.haoxi.dove.modules.loginregist.model.UserInfoModel;
import com.haoxi.dove.modules.loginregist.presenter.UserInfoPresenter;
import com.haoxi.dove.modules.loginregist.ui.IGetInfo;
import com.haoxi.dove.newin.bean.OurUserInfo;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.SpConstant;
import com.haoxi.dove.utils.SpUtils;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment implements IGetInfo {

    private OurUserInfo userInfo;
    @BindView(R.id.fragment_home_civ_icon)
    CircleImageView mHomeCiv;

    @BindView(R.id.fragment_home_username)
    TextView mUserNameTv;

    @BindView(R.id.fragment_home_userid)
    TextView mUserCodeTv;
    private UserInfoPresenter infoPresenter;
    private boolean isLoad = true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        infoPresenter = new UserInfoPresenter(new UserInfoModel());
        infoPresenter.attachView(this);

        mHomeCiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),PersonalActivity.class);
                intent.putExtra("userobject",userInfo);
                startActivity(intent);
            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_home;
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

        int curNum = SpUtils.getInt(getActivity(),SpConstant.CLICK_NUM,3);
        boolean hasChange = SpUtils.getBoolean(getActivity(),SpConstant.USER_INFO_CHANGE);

        if (isLoad && curNum == 3) {
            infoPresenter.getDataFromNets(getParaMap());
            isLoad = false;
        }else if (hasChange){
            readFromPreference();
        }
    }
    private void readFromPreference() {
        String mNickName = SpUtils.getString(getActivity(),SpConstant.NICK_NAME);
        String mUserCode = SpUtils.getString(getActivity(),SpConstant.USER_CODE);
        String mUserPVR = SpUtils.getString(getActivity(),SpConstant.USER_HEAD_PIC);

        mUserNameTv.setText(mNickName);
        mUserCodeTv.setText(mUserCode);

        if (mUserPVR.startsWith("http")) {

            Log.e("hfaw",mUserPVR+"---mUserPVR");
            Glide.with(getContext())
                    .load(mUserPVR)
                    .asBitmap()
                    .error(R.mipmap.btn_img_photo_default)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            mHomeCiv.setImageBitmap(resource);
                        }
                    });

//            Glide.with(getContext())
//                    .load(mUserPVR)
//                    .dontAnimate()
//                    .placeholder(R.mipmap.btn_img_photo_default)
//                    .error(R.mipmap.btn_img_photo_default)
//                    .into(mHomeCiv);

        }else {
            if (!mUserPVR.equals("")) {
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

    @Override
    public void getUserInfo(OurUserInfo userInfo) {
        SpUtils.putBoolean(getActivity(), SpConstant.USER_INFO_CHANGE,false);

        SpUtils.putString(getActivity(),SpConstant.USER_TELEPHONE, userInfo.getData().getTelephone());
        SpUtils.putString(getActivity(),SpConstant.NICK_NAME,userInfo.getData().getNickname());
        SpUtils.putString(getActivity(),SpConstant.USER_HEAD_PIC, userInfo.getData().getHeadpic());
        SpUtils.putString(getActivity(),SpConstant.USER_DOVECOTE, userInfo.getData().getLoftname());
        SpUtils.putString(getActivity(),SpConstant.USER_CODE, userInfo.getData().getUserid());
        SpUtils.putString(getActivity(),SpConstant.USER_AGE, String.valueOf(userInfo.getData().getAge()));
        SpUtils.putString(getActivity(),SpConstant.USER_BIRTH, String.valueOf(userInfo.getData().getUser_birth()));
        SpUtils.putString(getActivity(),SpConstant.USER_SEX, String.valueOf(userInfo.getData().getGender()));
        SpUtils.putString(getActivity(),SpConstant.USER_YEAR,userInfo.getData().getExperience() == null?"1年":String.valueOf(userInfo.getData().getExperience()));
        readFromPreference();
    }

    @Override
    public String getMethod() {
        return MethodConstant.USER_INFO_DETAIL;
    }

    @Override
    protected Map<String, String> getParaMap() {
        Map<String,String> map = super.getParaMap();
        map.put(MethodParams.PARAMS_TOKEN,getToken());
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        return map;
    }
}
