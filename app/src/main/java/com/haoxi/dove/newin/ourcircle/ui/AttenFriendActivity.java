package com.haoxi.dove.newin.ourcircle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.AttentionRvAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AttentionMoudle;
import com.haoxi.dove.inject.DaggerAttentionComponent;
import com.haoxi.dove.newin.bean.InnerAttention;
import com.haoxi.dove.newin.ourcircle.presenter.AttentionPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.zly.www.easyrecyclerview.EasyDefRecyclerView;
import com.zly.www.easyrecyclerview.listener.OnLoadListener;
import com.zly.www.easyrecyclerview.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017\6\16 0016.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_refrash_rv)
public class AttenFriendActivity extends BaseActivity implements IMyCircleView<List<InnerAttention>>, OnRefreshListener, OnLoadListener, OnHolderListener<InnerAttention,AttentionRvAdapter.MyRefrashHolder>, MyItemClickListener {

    private int methodType = MethodType.METHOD_TYPE_SEARCH_ATTENTION;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;

    @BindView(R.id.base_erv)
    EasyDefRecyclerView easyRecyclerView;

    @Inject
    AttentionPresenter presenter;

    @Inject
    OurCodePresenter codePresenter;

    AttentionRvAdapter refrashRvAdapter;

    private List<InnerAttention> innerAttenBeans = new ArrayList<>();

    @Inject
    RxBus mRxBus;

    private String friendId = "";
    private Dialog popDialog;

    @Override
    public void toDo() {

        if (popDialog != null && popDialog.isShowing()) {
            popDialog.dismiss();
        }

        methodType = MethodType.METHOD_TYPE_SEARCH_ATTENTION;
        getAttenData(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getAttenData(false);
    }

    @OnClick(R.id.custom_toolbar_iv)
    void onBackOnli() {

        mRxBus.post("isLoad", false);
        finish();
    }

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad", false);
        super.onBackPressed();
    }

    @Override
    public String getMethod() {

        String method = "";

        switch (methodType){
            case MethodType.METHOD_TYPE_SEARCH_ATTENTION:
                method = MethodConstant.ATTENTION_SEARCH;
                break;
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                method = MethodConstant.ATTENTTION_REMOVE;
                break;
        }

        return method;
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",mToken);

        switch (methodType){
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                map.put("friendid",friendId);
                break;
        }

        return map;
    }

    @Override
    protected void initInject() {

        DaggerAttentionComponent.builder()
                .appComponent(getAppComponent())
                .attentionMoudle(new AttentionMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {
        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("关注好友");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        easyRecyclerView.setLayoutManager(linearLayoutManager);

        easyRecyclerView.setLastUpdateTimeRelateObject(this);
        easyRecyclerView.setOnRefreshListener(this);
        easyRecyclerView.setOnLoadListener(this);

        refrashRvAdapter = new AttentionRvAdapter();

        easyRecyclerView.setAdapter(refrashRvAdapter);

        refrashRvAdapter.setOnHolderListener(this);
        refrashRvAdapter.setMyItemClickListener(this);
    }

    private void getAttenData(boolean isRefresh){

        if (!ApiUtils.isNetworkConnected(this)) {
            presenter.getAttentionFromDao(mUserObjId);
        } else {
            presenter.getDataFromNets(getParaMap());
        }
    }

    @Override
    public void onRefreshListener() {
        getAttenData(true);
    }

    @Override
    public void onLoadListener() {

    }

    @Override
    public void onItemClick(View view, int position) {

        InnerAttention innerAttention = innerAttenBeans.get(position);

        Intent intent = new Intent(AttenFriendActivity.this,CircleDetialActivity.class);
        intent.putExtra("friendid",innerAttention.getUserid());
        intent.putExtra("name",innerAttention.getNickname());
        intent.putExtra("attenHeadpic",innerAttention.getHeadpic());
        intent.putExtra("circle_tag",1);
        startActivity(intent);
    }

    @Override
    public void toInitHolder(final AttentionRvAdapter.MyRefrashHolder holder, int position, final InnerAttention innerAttention) {
        if (!"".equals(innerAttention.getNickname()) && innerAttention.getNickname() != null ){
            holder.mUserName.setText(String.valueOf(innerAttention.getNickname()));
        }
        if (!"".equals(innerAttention.getTelephone()) && innerAttention.getTelephone() != null ){
            holder.mUserPhone.setText(String.valueOf(innerAttention.getTelephone()));
        }

        //String headpic = "http://118.178.227.194:8087/";
        if (innerAttention.getHeadpic() != null && !"".equals(innerAttention.getHeadpic()) && !"-1".equals(innerAttention.getHeadpic())){
            String headpic = innerAttention.getHeadpic();

            Log.e("attent",headpic+"-----position");

            Glide.with(this)
                    .load(headpic)
                    .asBitmap()
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            holder.mUserIcon.setImageBitmap(resource);
                        }
                    });
        }else {
            holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
        }
        holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop(innerAttention);
            }
        });
    }

    private void showPop(final InnerAttention innerAttention) {

        popDialog = new Dialog(this, R.style.DialogTheme);

        View view = View.inflate(this, R.layout.layout_show_friend, null);
        popDialog.setCancelable(false);
        popDialog.setContentView(view);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.show_marker_ll);
        final CircleImageView civ = (CircleImageView) view.findViewById(R.id.de_icon);
        TextView mUserNameTv = (TextView) view.findViewById(R.id.show_nickname_tv);
        TextView mAgeTv = (TextView) view.findViewById(R.id.age_tv);
        TextView mSexTv = (TextView) view.findViewById(R.id.sex_tv);
        TextView mUserCodeTv = (TextView) view.findViewById(R.id.user_code_tv);
        TextView mExperTv = (TextView) view.findViewById(R.id.user_exper_tv);
        TextView mLoftNameTv = (TextView) view.findViewById(R.id.loftname_tv);
        Button mRemoveBtn = (Button) view.findViewById(R.id.remove_attention);

        int width = getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        params.width = (width * 62) / 72;
        params.height = (int) ((width * 52) / 72);

        layout.setLayoutParams(params);

        ImageView mDismissIv = (ImageView) view.findViewById(R.id.show_marker_dismiss);


        if (!"".equals(innerAttention.getNickname()) && innerAttention.getNickname() != null ){
            mUserNameTv.setText(String.valueOf(innerAttention.getNickname()));
        }
        if (!"".equals(innerAttention.getAge())){
            mAgeTv.setText(String.valueOf(innerAttention.getAge())+"岁");
        }

        if (!"".equals(innerAttention.getGender())&& innerAttention.getGender() != null){
            mSexTv.setText("1".equals(innerAttention.getGender()) || "男".equals(innerAttention.getGender())?"男":"女");
        }

        if (!"".equals(innerAttention.getExperience())&& innerAttention.getExperience() != null ){
            mExperTv.setText("养鸽年限:"+innerAttention.getExperience());
        }

        if (!"".equals(innerAttention.getTelephone())&& innerAttention.getTelephone() != null ){
            mUserCodeTv.setText("联系方式:"+innerAttention.getTelephone());
        }

        if (!"".equals(innerAttention.getLoftname())&& innerAttention.getLoftname() != null ){
            mLoftNameTv.setText(innerAttention.getLoftname());
        }

        if (innerAttention.getHeadpic() != null && !"".equals(innerAttention.getHeadpic()) && !"-1".equals(innerAttention.getHeadpic())){
            String headpic = innerAttention.getHeadpic();


            Glide.with(this)
                    .load(headpic)
                    .asBitmap()
                    .error(R.mipmap.btn_img_photo_default)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            civ.setImageBitmap(resource);
                        }
                    });
        }

        mRemoveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                friendId = innerAttention.getUserid();
                methodType = MethodType.METHOD_TYPE_REMOVE_ATTENTION;
                codePresenter.removeAttention(getParaMap());
            }
        });

        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.add(innerAttention.getHeadpic());
                MyPhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(0)
                        .setShowDeleteButton(false)
                        .start(AttenFriendActivity.this);

            }
        });

        mDismissIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });

        popDialog.show();
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public String getUserObJId() {
        return mUserObjId;
    }

    @Override
    public void updateCircleList(List<InnerAttention> attentionList, String errorMsg, int type) {
        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_FAIL:

                easyRecyclerView.noMore();

                break;
            case DataLoadType.TYPE_REFRESH_FAIL:

                easyRecyclerView.refreshComplete();

                break;
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:

                easyRecyclerView.loadComplete();

                if (attentionList != null && attentionList != null && attentionList.size() != 0) {
                    refrashRvAdapter.addAll(attentionList);
                    innerAttenBeans.addAll(attentionList);
                } else if (attentionList.size() == 0) {
                    easyRecyclerView.noMore();
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                easyRecyclerView.refreshComplete();
                easyRecyclerView.loading();

                if (attentionList != null && attentionList != null && attentionList.size() != 0) {

                    if (attentionList.size() < 10) {
                        easyRecyclerView.removeFooter();
                    }
                    refrashRvAdapter.update(attentionList);
                    innerAttenBeans.clear();
                    innerAttenBeans.addAll(attentionList);
                } else if (attentionList.size() == 0) {
                    easyRecyclerView.removeFooter();
                    refrashRvAdapter.update(attentionList);
                    innerAttenBeans.clear();
                }

                break;
        }
    }

    @Override
    public void setRefrash(boolean refrash) {

    }
}
