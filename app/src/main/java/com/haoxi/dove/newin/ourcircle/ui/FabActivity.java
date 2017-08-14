package com.haoxi.dove.newin.ourcircle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.haoxi.dove.adapter.RefrashFabRvAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerFabComponent;
import com.haoxi.dove.inject.FabMoudle;
import com.haoxi.dove.newin.bean.InnerAttention;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.bean.InnerFab;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.PlayerBean;
import com.haoxi.dove.newin.ourcircle.presenter.FabPresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.OurFabBean;
import com.haoxi.dove.newin.ourcircle.presenter.PlayerPresenter;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017\6\22 0022.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_fab)
public class FabActivity extends BaseActivity implements IPlayerInfoView<PlayerBean>,View.OnClickListener,IFabView<OurFabBean>,OnHolderListener<InnerFab,RefrashFabRvAdapter.MyRefrashHolder>,MyItemClickListener, com.scwang.smartrefresh.layout.listener.OnRefreshListener {

    private int methodType = MethodType.METHOD_TYPE_GET_FAB;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;

    @BindView(R.id.bsr_rv)
    RecyclerView recyclerView;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @Inject
    FabPresenter fabPresenter;

    @Inject
    PlayerPresenter playerPresenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;


    private int PAGENUM = 0;  //查询起始下标，默认为0
    private int PAGESIZE = 10;//每页返回的数据，默认10


    private String circleid = "";

    private String playerId = "";

    private String friendId = "";

    private boolean isFriend = false;

    RefrashFabRvAdapter refrashRvAdapter;

    private List<InnerFab> innerFabList = new ArrayList<>();

    private InnerCircleBean innerCircleBean;

    private boolean isLoad  = true;

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {

        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_GET_FAB:
                method =  "/app/circle_fab/get_fab";
                break;
            case MethodType.METHOD_TYPE_PLAYER_INFO:
                method =  "/app/user/get_player_info";
                break;
        }

        return method;
    }

    @Override
    protected void initInject() {

        DaggerFabComponent.builder()
                .appComponent(getAppComponent())
                .fabMoudle(new FabMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("点赞的人");

        mBackIv.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            circleid = intent.getStringExtra("circleid");

        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout.setOnRefreshListener(this);
//        refreshLayout.setOnLoadmoreListener(this);

        refrashRvAdapter = new RefrashFabRvAdapter();

        recyclerView.setAdapter(refrashRvAdapter);

        refrashRvAdapter.setOnHolderListener(this);
        refrashRvAdapter.setMyItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoad) {
            methodType = MethodType.METHOD_TYPE_GET_FAB;
            fabPresenter.refreshFromNets(getParaMap());
            isLoad = false;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.custom_toolbar_iv:
                finish();
                break;
        }
    }

    @Override
    public String getUserObJId() {
        return mUserObjId;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public void setRefrash(boolean refrash) {
        refreshLayout.finishRefresh(refrash);
    }

    @Override
    public void updateFabList(OurFabBean data, String errorMsg, int type) {

        if (refreshLayout.isRefreshing()){
            refreshLayout.finishRefresh(false);
        }else if (refreshLayout.isLoading()){
            refreshLayout.finishLoadmore(false);
        }

        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_FAIL:

                break;
            case DataLoadType.TYPE_REFRESH_FAIL:

                break;
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:
                Log.e("fabbbb","TYPE_LOAD_MORE_SUCCESS");



                if (data != null && data.getData() != null && data.getData().size() != 0) {
                    PAGENUM += 1;
                }

                if (data != null && data.getData() != null && data.getData().size() != 0) {
                    refrashRvAdapter.addAll(data.getData());
                    innerFabList.addAll(data.getData());
                } else if (data.getData().size() == 0) {
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                Log.e("fabbbb","TYPE_REFRESH_SUCCESS");

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    PAGENUM += 1;
                    if (data.getData().size() < 10 && refrashRvAdapter.getFooter() != null) {

                    } else if (data.getData().size() == 10 && refrashRvAdapter.getFooter() == null) {
//                        recyclerView.setFooterView(new ErvDefaultFooter(this));
//                        recyclerView.loading();
                    }else if (data.getData().size() == 10){
//                        recyclerView.loading();
                    }

                    refrashRvAdapter.update(data.getData());
                    innerFabList.clear();
                    innerFabList.addAll(data.getData());
                } else if (data.getData().size() == 0) {
                    if (refrashRvAdapter.getItemCount() != 0 && refrashRvAdapter.getData().size() == 0) {
//                        recyclerView.removeFooter();
                    }
//                    recyclerView.refreshComplete();
                    refrashRvAdapter.update(data.getData());
                    innerFabList.clear();
                }
                break;
        }
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",getUserObJId());
        map.put("token",getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_GET_FAB:
                map.put("circleid",circleid);
                break;
            case MethodType.METHOD_TYPE_PLAYER_INFO:
                map.put("playerid",playerId);
                break;
        }
        return map;
    }

    @Override
    public void toInitHolder(RefrashFabRvAdapter.MyRefrashHolder holder, int position, InnerFab data) {

        if (data.getUsername() != null) {
            holder.fabName.setText(data.getUsername());
        }

        Log.e("innerfab",data.getHeadpic()+"-----fab");

        String headpic = "http://118.178.227.194:8087/";
        if (data.getHeadpic() != null && !"".equals(data.getHeadpic()) && !"-1".equals(data.getHeadpic())){

            if (data.getHeadpic().startsWith("http")){
                headpic = data.getHeadpic();
            }else {
                headpic += data.getHeadpic();
            }

            holder.civ.setTag(R.id.imageid,headpic);
            Glide.with(this)
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(holder.civ);

        }else {
            holder.civ.setImageResource(R.mipmap.btn_img_photo_default);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        InnerFab innerFab = innerFabList.get(position);
        if (innerFab != null) {

            isFriend = innerFab.isIs_friend();

            if (isFriend) {
                Intent intent = new Intent(FabActivity.this,CircleDetialActivity.class);
                intent.putExtra("headPicStr",innerFab.getHeadpic());
                intent.putExtra("friendid",innerFab.getUserid());
                intent.putExtra("name",innerFab.getUsername());
                intent.putExtra("name",innerFab.getUsername());
                intent.putExtra("innerCircleBean",innerCircleBean);
                startActivity(intent);
            }else {
                playerId = innerFab.getUserid() == null?"":innerFab.getUserid();
                methodType = MethodType.METHOD_TYPE_PLAYER_INFO;
                playerPresenter.getDataFromNets(getParaMap());
            }
        }
    }

    @Override
    public String getPlayerId() {
        return playerId;
    }

    @Override
    public void updatePlayerInfo(PlayerBean playerBean) {
        showPop(playerBean.getData());
    }

    private Dialog popDialog;

    private void showPop(final InnerAttention playerBean) {

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

        if (!"".equals(playerBean.getNickname()) && playerBean.getNickname() != null ){
            mUserNameTv.setText(String.valueOf(playerBean.getNickname()));
        }
        if (!"".equals(playerBean.getAge())){
            mAgeTv.setText(String.valueOf(playerBean.getAge())+"岁");
        }

        if (!"".equals(playerBean.getGender())&& playerBean.getGender() != null){
            mSexTv.setText("1".equals(playerBean.getGender()) || "男".equals(playerBean.getGender())?"男":"女");
        }

        if (!"".equals(playerBean.getExperience())&& playerBean.getExperience() != null ){
            mExperTv.setText("养鸽年限:"+playerBean.getExperience());
        }

        if (!"".equals(playerBean.getTelephone())&& playerBean.getTelephone() != null ){
            mUserCodeTv.setText("联系方式:"+playerBean.getTelephone());
        }

        if (!"".equals(playerBean.getLoftname())&& playerBean.getLoftname() != null ){
            mLoftNameTv.setText("鸽舍: "+playerBean.getLoftname());
        }

        if (playerBean.getHeadpic() != null && !"".equals(playerBean.getHeadpic()) && !"-1".equals(playerBean.getHeadpic())){
            String headpic = playerBean.getHeadpic();


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

                friendId = playerBean.getUserid();
                methodType = MethodType.METHOD_TYPE_REMOVE_ATTENTION;
                ourCodePresenter.removeAttention(getParaMap());
            }
        });

        civ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.add(playerBean.getHeadpic());
                MyPhotoPreview.builder()
                        .setPhotos(selectedPhotos)
                        .setCurrentItem(0)
                        .setShowDeleteButton(false)
                        .start(FabActivity.this);
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
    public void onRefresh(RefreshLayout refreshLayout) {
//        PAGENUM = 1;
        methodType = MethodType.METHOD_TYPE_GET_FAB;
        fabPresenter.refreshFromNets(getParaMap());
    }
}
