package com.haoxi.dove.modules.circle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.base.BaseSrFragment;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.inject.CircleMoudle;
import com.haoxi.dove.inject.DaggerCircleComponent;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.EachCirclePresenter;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;
import com.haoxi.dove.newin.ourcircle.ui.CircleDetialActivity;
import com.haoxi.dove.newin.ourcircle.ui.EarchCircleActivity;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.newin.ourcircle.ui.TransCircleActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.retrofit.ad.AdUtils;
import com.haoxi.dove.retrofit.ad.AdviewResObj;
import com.haoxi.dove.retrofit.ad.Config;
import com.haoxi.dove.retrofit.ad.Http;
import com.haoxi.dove.retrofit.ad.IAdView;
import com.haoxi.dove.retrofit.ad.OpenAdPresenter;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.MD5Tools;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ytb.logic.external.NativeResource;
import com.ytb.logic.interfaces.AdNativeLoadListener;
import com.ytb.logic.view.HmNativeAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class AllCircleFragment extends BaseSrFragment implements IAdView, IMyCircleView<CircleBean>,OnRefreshListener, OnLoadmoreListener, OnHolder2Listener, MyItemClickListener, IEachView<EachCircleBean> {

    private int methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;

    private int PAGENUM = 1;  //查询起始下标，默认为0
    private boolean isFriend = false;
    private boolean isDao = false;
    private boolean isLoad = true;
    private int tag = 0;
    private String headpic;

    private String circleid;
    private String friendId;
    private String commentContent;
    private InnerCircleBean innerCircleBean;
    private int currentPosition;//当前操作的 position

    HmNativeAd nativeAd;
    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();
    private Map<String,Integer> pageNumMap = new HashMap<>();

    @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
    @BindView(R.id.bsr_rv) RecyclerView recyclerView;
    @BindView(R.id.bannerIv)
    ImageView bannerIv;

    @Inject
    InnerCirclePresenter innerCirclePresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    RxBus mRxBus;

    EachCirclePresenter eachCirclePresenter;
    private OpenAdPresenter adPresenter;
    AdCircleAdapter adCircleAdapter;
    private Observable<Integer> netObservale;
    private Observable<Boolean> isLoadObervable;
    private NativeResource nativeResource;
    private boolean isLoadMore = false;

    private boolean isLoadBanner = true;
    private int width;
    private int height;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netObservale = mRxBus.register(ConstantUtils.OBSER_LOAD_CIRCLE, Integer.class);
        isLoadObervable = mRxBus.register("isLoadA", Boolean.class);
        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 0 ){
                    adCircleAdapter.getDatas().remove(currentPosition);
                    methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                    eachCirclePresenter.getDataFromNets(getParaMap());
                }
            }
        });
        isLoadObervable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    PAGENUM = 1;
                    getDatas();
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        eachCirclePresenter = new EachCirclePresenter(this);
        adPresenter = new OpenAdPresenter(this);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adCircleAdapter = new AdCircleAdapter(getActivity(),0);
        recyclerView.setAdapter(adCircleAdapter);
        adCircleAdapter.setOnHolderListener(this);
        adCircleAdapter.setMyItemClickListener(this);
        getAdDatas();
    }

    @Override
    public String getUserObJId() {
        return userObjId;
    }

    @Override
    public void updateCircleList(CircleBean data, String errorMsg, int type) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.finishRefresh(false);
        }else if (refreshLayout.isLoading()){
            refreshLayout.finishLoadmore(false);
        }

        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:
                if (data != null && data.getData() != null && data.getData().size() != 0){
                    if (!isDao) {
                        PAGENUM += 1;
                        pageNumMap.put("all",PAGENUM);
                    }

                    isLoadMore = true;
                    nativeAd.loadAd("429");
                    adCircleAdapter.addData(data.getData());
                    innerCircleBeans.addAll(data.getData());
                }
                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:
                if (data != null && data.getData() != null && data.getData().size() != 0) {
                    if (!isDao) {
                        PAGENUM += 1;
                        pageNumMap.put("all",PAGENUM);
                    }
                    adCircleAdapter.updateList(data.getData());
                    innerCircleBeans.clear();
                    innerCircleBeans.addAll(data.getData());
                }
                break;
        }
    }

    @Override
    public void setRefrash(boolean refrash) {
        refreshLayout.finishRefresh(refrash);
        refreshLayout.finishLoadmore(refrash);
    }

    @Override
    public void toDo() {

        if (adCircleAdapter.getItem(currentPosition) instanceof InnerCircleBean){
            switch (methodType){
                case MethodType.METHOD_TYPE_ADD_ATTENTION:
                case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                    mRxBus.post("isLoadF",true);
                case MethodType.METHOD_TYPE_ADD_FAB:
                case MethodType.METHOD_TYPE_REMOVE_FAB:
                case MethodType.METHOD_TYPE_ADD_COMMENT:
                    adCircleAdapter.getDatas().remove(currentPosition);
                    methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                    eachCirclePresenter.getDataFromNets(getParaMap());
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoad) {
            PAGENUM = 1;
            pageNumMap.put("all",1);
            methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
            refreshLayout.autoRefresh();
            nativeAd.loadAd("429");
        }
        isLoad = false;

        if (isLoadBanner) {
            adPresenter.getOpenAd(AdUtils.getParamsMap(getActivity(),width,height,Config.BANNER_AD));
            isLoadBanner = false;
        }
    }

    private  List<NativeResource> list = new ArrayList<>();

    private void getAdDatas() {
        nativeAd = new HmNativeAd(getActivity(), new AdNativeLoadListener() {
            @Override
            public boolean onAdLoaded(NativeResource nativeResource) {
                AllCircleFragment.this.nativeResource = nativeResource;
                methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
                if (isLoadMore){
                    list.add(nativeResource);
                }else {
                    list.clear();
                    list.add(nativeResource);
                }
                adCircleAdapter.updateAdList(list);
                adCircleAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onFailed(int i, String s) {
                return false;
            }
            @Override
            public void onAdClosed() {

            }
        });
    }

    @Override
    protected void inject() {
        DaggerCircleComponent.builder()
                .appComponent(getAppComponent())
                .circleMoudle(new CircleMoudle(getActivity(), this))
                .build()
                .inject(this);
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        updateRecyclerView();
    }
    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView() {
        if (innerCircleBeans.size() % 10 != 0) {
            PAGENUM = innerCircleBeans.size() / 10 + 2;
        }else {
            PAGENUM = innerCircleBeans.size() / 10 + 1;
        }
        pageNumMap.put("all",PAGENUM);
        methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
        innerCirclePresenter.loadMoreData(getParaMap(),0);
    }

    public void getDatas() {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
        } else {
            isDao = false;
            innerCirclePresenter.refreshFromNets(getParaMap(),tag);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (!ApiUtils.isNetworkConnected(getActivity())) {
            isDao = true;
            innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,0);
        } else {
            PAGENUM = 1;
            pageNumMap.put("all",PAGENUM);
            isDao = false;
            isLoadMore = false;
            //下拉刷新
            methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
            nativeAd.loadAd("429");
            innerCirclePresenter.refreshFromNets(getParaMap(),0);
        }
    }

    @Override
    public String getMethod() {
        String method ="";
        switch (methodType){
            case MethodType.METHOD_TYPE_ALL_CIRCLES:
                method = MethodConstant.GET_ALL_CIRCLES;
                break;
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                //取消关注好友
                method = MethodConstant.ATTENTTION_REMOVE;
                break;
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                //关注好友
                method = MethodConstant.ATTENTION_ADD;
                break;
            case MethodType.METHOD_TYPE_ADD_FAB:
                method = MethodConstant.ADD_FAB;
                break;
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                method = MethodConstant.REMOVE_FAB;
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                method = MethodConstant.ADD_COMMENT;
                break;
            case MethodType.METHOD_TYPE_SHARE_CIRCLE:
                method = MethodConstant.SHARE_CIRCLE;
                break;
            case MethodType.METHOD_TYPE_CIRCLE_DETAIL:
                method = MethodConstant.GET_CIRCLE_DETAIL;
                break;
        }
        return method;
    }

    public Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                map.put(MethodParams.PARAMS_FRIEND_ID,friendId);
                break;
            case MethodType.METHOD_TYPE_ALL_CIRCLES:
                map.put(MethodParams.PARAMS_CP,String.valueOf(PAGENUM));
                break;
            case MethodType.METHOD_TYPE_SHARE_CIRCLE:
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
            case  MethodType.METHOD_TYPE_CIRCLE_DETAIL:
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                map.put(MethodParams.PARAMS_CONTENT,commentContent);
                break;
        }

        return map;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void toInitHolder(final RecyclerView.ViewHolder holder, final int position, final Object data) {
        if (data instanceof InnerCircleBean){
            this.innerCircleBean = (InnerCircleBean) data;
            final InnerCircleBean curData = (InnerCircleBean) data;

            if (curData.getTrans_userid() != null && !"".equals(curData.getTrans_userid()) && !"-1".equals(curData.getTrans_userid())) {
                ((AdCircleAdapter.MyRefrashHolder)holder).transpondFl.setVisibility(View.VISIBLE);
                ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(curData.getContent()) && !"".equals(curData.getContent())){
                    String content = curData.getContent();
                    String[] contents = content.split("#");
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranContentTv.setText(contents[contents.length - 1]);
                    Log.e("mTranContentTv",content+"--------"+curData.getUsername()+"--------"+curData.getTrans_name());

                    if (content.lastIndexOf("#") != -1) {
                        ((AdCircleAdapter.MyRefrashHolder)holder).mContentTv.setText(content.substring(0,content.lastIndexOf("#")));
                    }else {
                        ((AdCircleAdapter.MyRefrashHolder)holder).mContentTv.setText("转发动态");
                    }
                }
                if (!"".equals(curData.getTrans_name()) && curData.getTrans_name() != null ){
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranName.setText("@" + String.valueOf(curData.getTrans_name()));
                }

                if (curData.getPics() != null && curData.getPics().size() != 0){
                    final ArrayList<String> selectedPhotos = new ArrayList<>();
                    selectedPhotos.clear();
                    int picCount = curData.getPics().size();
                    selectedPhotos.addAll(curData.getPics());
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.removeAllViews();
                    CirclePhotoAdapter photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.setVisibility(View.VISIBLE);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.setAdapter(photoAdapter);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));
                    ((SimpleItemAnimator) ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.getItemAnimator()).setSupportsChangeAnimations(false);
                    photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            isLoad = false;
                            MyPhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .setShowDeleteButton(false)
                                    .start(getActivity());
                        }
                    });
                }else {
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.removeAllViews();
                    ((AdCircleAdapter.MyRefrashHolder)holder).mTranRv.setVisibility(View.GONE);
                }
                ((AdCircleAdapter.MyRefrashHolder)holder).mAddFriendBtn.setVisibility(View.GONE);
                ((AdCircleAdapter.MyRefrashHolder)holder).mDownIv.setVisibility(View.GONE);
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).transpondFl.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){
                    ((AdCircleAdapter.MyRefrashHolder)holder).mContentTv.setText(innerCircleBean.getContent());
                }
                if (curData.getPics() != null && curData.getPics().size() != 0){
                    final ArrayList<String> selectedPhotos = new ArrayList<>();
                    selectedPhotos.clear();
                    int picCount = curData.getPics().size();
                    selectedPhotos.addAll(curData.getPics());
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.removeAllViews();
                    CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.setVisibility(View.VISIBLE);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.setAdapter(photoAdapter);
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));
                    ((SimpleItemAnimator) ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
                    photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            isLoad = false;
                            MyPhotoPreview.builder()
                                    .setPhotos(selectedPhotos)
                                    .setCurrentItem(position)
                                    .setShowDeleteButton(false)
                                    .start(getActivity());
                        }
                    });
                }else {
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.removeAllViews();
                    ((AdCircleAdapter.MyRefrashHolder)holder).mRecyclerView.setVisibility(View.GONE);
                }
            }
            if (curData.isIs_friend() || tag == 1 || tag == 2) {
                ((AdCircleAdapter.MyRefrashHolder)holder).mAddFriendBtn.setVisibility(View.GONE);
                ((AdCircleAdapter.MyRefrashHolder)holder).mDownIv.setVisibility(View.VISIBLE);
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mAddFriendBtn.setVisibility(View.VISIBLE);
                ((AdCircleAdapter.MyRefrashHolder)holder).mDownIv.setVisibility(View.GONE);
            }
            if (curData.isHas_fab()) {
                Drawable likeLift = getResources().getDrawable(R.mipmap.like);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                ((AdCircleAdapter.MyRefrashHolder)holder).mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
            }else {
                Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                ((AdCircleAdapter.MyRefrashHolder)holder).mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
            }
            if (curData.getShare_count() != 0) {
                ((AdCircleAdapter.MyRefrashHolder)holder).mTranspondBtn.setText(getString(R.string.share_circle) + curData.getShare_count());
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mTranspondBtn.setText(getString(R.string.share_circle));
            }
            if (curData.getComment_count() != 0) {
                ((AdCircleAdapter.MyRefrashHolder)holder).mCommentBtn.setText(getString(R.string.comment_circle)+curData.getComment_count());
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mCommentBtn.setText(getString(R.string.comment_circle));
            }
            if (curData.getFab_count() != 0){
                ((AdCircleAdapter.MyRefrashHolder)holder).mPraiseBtn.setText(getString(R.string.fab_circle)+curData.getFab_count());
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mPraiseBtn.setText(getString(R.string.fab_circle));
            }
            if (curData.getContent() == null || "".equals(curData.getContent())){
                ((AdCircleAdapter.MyRefrashHolder)holder).mContentTv.setVisibility(View.GONE);
            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mContentTv.setVisibility(View.VISIBLE);
            }
            if (!"".equals(curData.getUsername()) && curData.getUsername() != null ){
                ((AdCircleAdapter.MyRefrashHolder)holder).mUserName.setText(String.valueOf(curData.getUsername()));
            }

            headpic = ConstantUtils.HEADPIC;
            if (curData.getHeadpic() != null && !"".equals(curData.getHeadpic()) && !"-1".equals(curData.getHeadpic())){
                headpic += curData.getHeadpic();
                String tag = (String) ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.getTag(R.id.imageid);
                if (!TextUtils.equals(headpic,tag)){
                    ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
                }

                ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.setTag(R.id.imageid,headpic);
                Glide.with(getContext())
                        .load(headpic)
                        .dontAnimate()
                        .placeholder(R.mipmap.btn_img_photo_default)
                        .error(R.mipmap.btn_img_photo_default)
                        .into(((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon);

            }else {
                ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.setTag(R.id.imageid,"");
                ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
            }

            ((AdCircleAdapter.MyRefrashHolder)holder).mUserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    if (curData.isIs_friend() || curData.getUserid().equals(getUserObJId())){
                        Intent intent = new Intent(getActivity(),CircleDetialActivity.class);
                        intent.putExtra("friendid",curData.getUserid());
                        intent.putExtra("name",curData.getUsername());
                        intent.putExtra("innerCircleBean",curData);
                        intent.putExtra("current_position",curData);
                        intent.putExtra("circle_tag",tag);
                        startActivity(intent);
                    }
                }
            });
            if (!TextUtils.isEmpty(curData.getCreate_time()) && !"".equals(curData.getCreate_time())) {
                ((AdCircleAdapter.MyRefrashHolder)holder).mCreateTimeTv.setText(curData.getCreate_time());
            }
            ((AdCircleAdapter.MyRefrashHolder)holder).mDownIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    circleid = curData.getCircleid();
                    friendId = curData.getUserid();
                    isFriend = curData.isIs_friend();
                    showDownDialog();
                }
            });

            ((AdCircleAdapter.MyRefrashHolder)holder).mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    circleid = curData.getCircleid();
                    methodType = MethodType.METHOD_TYPE_ADD_ATTENTION;
                    friendId = curData.getUserid();
                    ourCodePresenter.addAttention(getParaMap());
                }
            });

            ((AdCircleAdapter.MyRefrashHolder)holder).mPraiseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    circleid = curData.getCircleid();
                    friendId = curData.getUserid();
                    isFriend = curData.isIs_friend();

                    if (curData.isHas_fab()) {
                        methodType = MethodType.METHOD_TYPE_REMOVE_FAB;
                        ourCodePresenter.removeFab(getParaMap());
                    }else {
                        methodType = MethodType.METHOD_TYPE_ADD_FAB;
                        ourCodePresenter.addFab(getParaMap());
                    }

                }
            });

            ((AdCircleAdapter.MyRefrashHolder)holder).mCommentBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    circleid = curData.getCircleid();
                    methodType = MethodType.METHOD_TYPE_ADD_COMMENT;
                    if (curData.getComment_count() != 0){
                        Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
                        intent.putExtra("innerCircleBean",innerCircleBeans.get(currentPosition));
                        intent.putExtra("circle_tag",tag);
                        startActivity(intent);
                    }else {
                        showMyDialog();
                    }
                }
            });

            ((AdCircleAdapter.MyRefrashHolder)holder).mTranspondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;

                    circleid = curData.getCircleid();
                    Intent intent = new Intent(getActivity(),TransCircleActivity.class);
                    intent.putExtra("innerCircleBean",curData);
                    startActivity(intent);
                }
            });
        }if (data instanceof NativeResource){

            Log.e("ad","原生广告");
            ((AdCircleAdapter.AdHolder)holder).content.setText( ((NativeResource)data).getTextForLabel("title"));
                NativeResource.Img img =  ((NativeResource)data).getImgForLabel("ad");
            ((NativeResource)data).onExposured(((AdCircleAdapter.AdHolder)holder).adImage);
            ((AdCircleAdapter.AdHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((NativeResource)data).onClick(((AdCircleAdapter.AdHolder)holder).adImage);
                    }
                });
                if(img != null){

                    Glide.with(getActivity())
                            .load(img.getUrl())
                            .error(R.mipmap.default_pic)
                            .into(((AdCircleAdapter.AdHolder)holder).adImage);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRxBus.unregister(ConstantUtils.OBSER_LOAD_CIRCLE, netObservale);
        mRxBus.unregister("isLoadA", isLoadObervable);
    }

    @Override
    public void onItemClick(View view, int position) {
        int curPost = position >= 1? position - (adCircleAdapter.getAdDatas().size() / 10 + adCircleAdapter.getAdDatas().size()):position;
        currentPosition = curPost;
        if (innerCircleBeans.get(curPost) != null) {
            circleid = innerCircleBeans.get(curPost).getCircleid();
            Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
            intent.putExtra("innerCircleBean",innerCircleBeans.get(curPost));
            intent.putExtra("circle_tag",tag);
            intent.putExtra("circle_cur", currentPosition);
            startActivity(intent);
        }
    }
    private TextView commentSub;
    private Handler mHandler = new Handler();
    private Dialog mDialogEt;
    public void showMyDialog() {
        mDialogEt = new Dialog(getActivity(), R.style.DialogTheme);

        View view = getActivity().getLayoutInflater().inflate(R.layout.comment_dialog, null);
        final EditText mEtDialog = (EditText) view.findViewById(R.id.pigeon_name_dialog_et);
        commentSub = (TextView) view.findViewById(R.id.comment_submit);

        mEtDialog.setSelection(mEtDialog.getText().length());

        Window window = mDialogEt.getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
        mDialogEt.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ApiUtils.setDialogWindow(mDialogEt);
        mDialogEt.show();

        mEtDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (delayRun != null) {
                    mHandler.removeCallbacks(delayRun);
                }
                editPwd = s.toString();
                mHandler.postDelayed(delayRun, 500);
            }
        });

        commentSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commentContent = mEtDialog.getText().toString().trim();
                if (!TextUtils.equals("",commentContent)) {
                    ourCodePresenter.addComment(getParaMap());
                }
                mDialogEt.dismiss();
            }
        });
    }
    private String editPwd;

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {
            if (editPwd.length() == 0 || "".equals(editPwd) || editPwd == null) {
                commentSub.setEnabled(false);
                commentSub.setTextColor(getResources().getColor(R.color.darkgray));
            } else {
                commentSub.setEnabled(true);
                commentSub.setTextColor(getResources().getColor(R.color.colorBlue));
            }
        }
    };

    @Override
    public void toUpdateEach(EachCircleBean data) {
        InnerCircleBean circleBean = innerCircleBeans.get(currentPosition);
        circleBean.setComment_count(data.getData().getComment_count());
        circleBean.setFab_count(data.getData().getFab_count());
        circleBean.setShare_count(data.getData().getShare_count());
        circleBean.setHas_fab(data.getData().getHas_fab());
        circleBean.setIs_friend(data.getData().getIs_friend());
        adCircleAdapter.getDatas().add(currentPosition,circleBean);
        adCircleAdapter.notifyDataSetChanged();
    }
    private void showDownDialog(){
        final Dialog mDialog = new Dialog(getActivity(), R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.personal_down_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mRemoveTv = (TextView) view.findViewById(R.id.headciv_dialog_remove_attention);
        TextView mShouTv = (TextView) view.findViewById(R.id.headciv_dialog_shou);
        TextView mDeleteTv = (TextView) view.findViewById(R.id.headciv_dialog_delete);
        TextView mCancle = (TextView) view.findViewById(R.id.headciv_dialog_cancle);

        mDeleteTv.setVisibility(View.GONE);
        mShouTv.setVisibility(View.GONE);
        if (getUserObJId().equals(friendId)){
            mRemoveTv.setVisibility(View.GONE);
            mShouTv.setVisibility(View.GONE);
        }else {
            mRemoveTv.setVisibility(View.VISIBLE);
            mShouTv.setVisibility(View.VISIBLE);
        }

        if (isFriend){
            mRemoveTv.setText("取消关注");
        }else {
            mRemoveTv.setText("添加关注");
        }

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                methodType = MethodType.METHOD_TYPE_REMOVE_ATTENTION;
                ourCodePresenter.removeAttention(getParaMap());
                mDialog.dismiss();
            }
        });
        mShouTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodType = MethodType.METHOD_TYPE_DELETE_SINGEL;
                ourCodePresenter.deleteSingleCircle(getParaMap());
                mDialog.dismiss();
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    @Override
    public void setOpenAd(AdviewResObj resObj) {
        adviewResObj = resObj;

        List<AdviewResObj.AdBody> adBodyList = resObj.getAd();
        if (adBodyList != null) {
            if (adBodyList.size() != 0 && adBodyList.get(0).getApi().size() != 0) {
                String imgUrl = adBodyList.get(0).getApi().get(0);
                String al = adBodyList.get(0).getAl();

                if (!TextUtils.isEmpty(al)) {
                    bannerIv.setVisibility(View.VISIBLE);
                    clickAdImg = al;
                }
                final Map<String, List<String>> map = resObj.getAd().get(0).getEs();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (map != null && map.size() > 0) {
                            Set<Map.Entry<String, List<String>>> set = map.entrySet();
                            for (Map.Entry<String, List<String>> entry : set) {
                                List<String> arr = entry.getValue();
                                for (String url : arr) {
                                    String getEc = Http.get(url, null);
                                }
                            }
                        }
                    }
                }).start();

                Glide.with(getActivity())
                        .load(imgUrl)
                        .into(bannerIv);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        bannerIv.setVisibility(View.GONE);
                    }
                },5 * 1000);
            }
        }
    }

    private String clickAdImg = "";
    private AdviewResObj adviewResObj;

    @OnClick(R.id.bannerIv)
    void banner(){
                if (!TextUtils.isEmpty(clickAdImg) && clickAdImg.startsWith("http")){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (adviewResObj != null) {
                                List<String> ec =  adviewResObj.getAd().get(0).getEc();
                                if (ec != null && ec.size() > 0) {
                                    for (String url:ec) {
                                        String getEc = Http.get(url,null);
                                    }
                                }
                            }
                        }
                    }).start();

                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri contentUri = Uri.parse(clickAdImg);
                    intent.setData(contentUri);
                    startActivity(intent);
        }
    }
}
