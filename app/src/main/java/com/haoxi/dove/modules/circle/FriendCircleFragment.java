package com.haoxi.dove.modules.circle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.base.BaseSrFragment;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.inject.CircleMoudle;
import com.haoxi.dove.inject.DaggerCircleComponent;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;
import com.haoxi.dove.newin.ourcircle.ui.CircleDetialActivity;
import com.haoxi.dove.newin.ourcircle.ui.EarchCircleActivity;
import com.haoxi.dove.newin.ourcircle.ui.IMyCircleView;
import com.haoxi.dove.newin.ourcircle.ui.TransCircleActivity;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
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

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;

public class FriendCircleFragment extends BaseSrFragment implements IMyCircleView<CircleBean>,OnRefreshListener, OnLoadmoreListener, OnHolder2Listener, MyItemClickListener {

    private int methodType = MethodType.METHOD_TYPE_FRIENDS_CIRCLES;

    private int PAGENUM = 1;  //查询起始下标，默认为0
    private int PAGESIZE = 10;//每页返回的数据，默认10

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

    @Inject
    InnerCirclePresenter innerCirclePresenter;
    @Inject
    OurCodePresenter ourCodePresenter;
    @Inject
    RxBus mRxBus;

    AdCircleAdapter adCircleAdapter;
    private Observable<Integer> netObservale;
    private Observable<Boolean> isLoadObervable;
    private Observable<Boolean> isUpdateObervable;
    private NativeResource nativeResource;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoadObervable = mRxBus.register("isLoad", Boolean.class);
        isUpdateObervable = mRxBus.register("update", Boolean.class);
        netObservale = mRxBus.register("load_circle", Integer.class);

        initObservale();
    }

    private void initObservale() {

        isLoadObervable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                isLoad = aBoolean;
            }
        });
        isUpdateObervable.subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    mRxBus.post("load_circle",0);
                    mRxBus.post("load_circle",1);
                }
            }
        });

        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 0) {
                    isDao = true;
                    innerCirclePresenter.getDatasFromDao(getUserObJId(), getUserObJId(), true, 0);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
    }

    @Override
    public void toDo() {

        if (adCircleAdapter.getItem(currentPosition) instanceof InnerCircleBean){

            InnerCircleBean innerCircleBean = (InnerCircleBean) adCircleAdapter.getItem(currentPosition);
            switch (methodType){
                case MethodType.METHOD_TYPE_ADD_ATTENTION:
                    innerCirclePresenter.updateCircleBy(getUserObJId(),innerCircleBean.getUserid(),true);
                    break;
                case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                    innerCirclePresenter.updateCircleBy(getUserObJId(),innerCircleBean.getUserid(),false);
                    break;
                case MethodType.METHOD_TYPE_ADD_FAB:

                    Log.e("faamap99999","点赞-------"+ innerCircleBean.getCircleid());
                    Log.e("faamap99999","id---1----"+ innerCircleBean.getId());

//                    innerCircleBean.setHas_fab(true);
                    innerCircleBean.setFab_count(innerCircleBean.getFab_count() + 10);

                    innerCirclePresenter.updateCircle(innerCircleBean);
                    adCircleAdapter.getDatas().remove(currentPosition);
                    adCircleAdapter.getDatas().add(currentPosition,innerCircleBean);
                    adCircleAdapter.notifyDataSetChanged();

                    //loadCircle();

                    break;
                case MethodType.METHOD_TYPE_REMOVE_FAB:

                    innerCircleBean.setHas_fab(false);
                    innerCircleBean.setFab_count(innerCircleBean.getFab_count() - 1);
                    innerCirclePresenter.updateCircle(innerCircleBean);
                    loadCircle();
                    break;

                case MethodType.METHOD_TYPE_ADD_COMMENT:
                    innerCircleBean.setComment_count(innerCircleBean.getComment_count() + 1);
                    innerCirclePresenter.updateCircle(innerCircleBean);
                    loadCircle();
                    break;
            }
        }
    }

    private void loadCircle(){
        mRxBus.post("load_circle",0);
        mRxBus.post("load_circle",1);
        mRxBus.post("load_circle",2);
    }


    @Override
    public void onResume() {
        super.onResume();
        if (isLoad) {
            PAGENUM = 1;
            pageNumMap.put("all",1);
            methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
            getDatas();

            nativeAd.loadAd("429");
        }
        isLoad = false;
    }

    private void getAdDatas() {

        nativeAd = new HmNativeAd(getActivity(), new AdNativeLoadListener() {
            @Override
            public boolean onAdLoaded(NativeResource nativeResource) {
                Log.d("Inapp","onAdLoaded " + nativeResource);
                FriendCircleFragment.this.nativeResource = nativeResource;
                List<NativeResource> list = new ArrayList<>();
                list.add(nativeResource);
                adCircleAdapter.updateAdList(list);

                adCircleAdapter.notifyDataSetChanged();

                return false;
            }

            @Override
            public boolean onFailed(int i, String s) {
                Log.d("Inapp","onFailed " + i+"-------"+s);
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
            isDao = true;
            innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
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
            //下拉刷新
            methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
            innerCirclePresenter.refreshFromNets(getParaMap(),0);
        }
    }

    @Override
    public String getMethod() {
        String method ="";
        switch (methodType){
            case MethodType.METHOD_TYPE_FRIENDS_CIRCLES:
                method = MethodConstant.GET_FRIENDS_CIRCLES;
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
        }
        return method;
    }

    public Map<String,String> getParaMap(){
        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",getUserObjId());
        map.put("token",getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                map.put("friendid",friendId);
                break;
            case MethodType.METHOD_TYPE_FRIENDS_CIRCLES:
                map.put("cp",String.valueOf(PAGENUM));
                map.put("ps",String.valueOf(PAGESIZE));
                break;
            case MethodType.METHOD_TYPE_SHARE_CIRCLE:
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                map.put("circleid",circleid);
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                map.put("circleid",circleid);
                map.put("content",commentContent);
                break;
        }
        return map;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void toInitHolder(final RecyclerView.ViewHolder holder, final int position, Object data) {
        if (data instanceof InnerCircleBean){
            this.innerCircleBean = (InnerCircleBean) data;
            final InnerCircleBean curData = (InnerCircleBean) data;

            //转发的

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

            Log.e("afsdfdewf",curData.getPlayerid()+"------"+curData.getCircleid()+"-----"+curData.getIs_friend());

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
//                    showDownDialog();
                }
            });


            ((AdCircleAdapter.MyRefrashHolder)holder).mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    currentPosition = position;
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

            ((AdCircleAdapter.AdHolder)holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nativeResource.onClick(((AdCircleAdapter.AdHolder)holder).adImage);
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
        mRxBus.unregister("isLoad", isLoadObervable);
        mRxBus.unregister("load_circle", netObservale);
        mRxBus.unregister("update", isUpdateObervable);
    }

    @Override
    public void onItemClick(View view, int position) {
        currentPosition = position;
        if (innerCircleBeans.get(position) != null) {
            Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
            intent.putExtra("innerCircleBean",innerCircleBeans.get(position));
            intent.putExtra("circle_tag",tag);
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

        mDialogEt.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialogEt.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialogEt.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ApiUtils.setDialogWindow(mDialogEt);
        mDialogEt.show();

        mEtDialog.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

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
}
