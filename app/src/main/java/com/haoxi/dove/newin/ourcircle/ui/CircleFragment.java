package com.haoxi.dove.newin.ourcircle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.adapter.MyLmAdapter;
import com.haoxi.dove.base.BaseSrFragment;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.inject.CircleMoudle;
import com.haoxi.dove.inject.DaggerCircleComponent;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.InnerAttention;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.functions.Action1;

public class CircleFragment extends BaseSrFragment implements IMyCircleView<CircleBean>,MyItemClickListener, OnHolder2Listener<InnerCircleBean,MyLmAdapter.MyRefrashHolder>, OnRefreshListener, OnLoadmoreListener {

    private static final String TAG = "CircleFragment";

    private int methodType = MethodType.METHOD_TYPE_FRIENDS_CIRCLES;

    private int PAGENUM = 1;  //查询起始下标，默认为0
    private int PAGESIZE = 10;//每页返回的数据，默认10

    private boolean isFriend = false;

    private Dialog popDialog;

    private InnerCircleBean innerCircleBean;

    private Dialog mDialogEt;

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.bsr_rv)
    RecyclerView recyclerView;
    @Inject
    InnerCirclePresenter innerCirclePresenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    MyLmAdapter myLmAdapter;

    @Inject
    RxBus mRxBus;

    private boolean isDao = false;

    private int currentPosition;//当前操作的 position

    private Handler mHandler = new Handler();

    private Observable<Integer> netObservale;

    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();

    private Map<String,Integer> pageNumMap = new HashMap<>();

    private boolean isLoad = true; // 是否显示就加载数据
    private Observable<Boolean> isLoadObervable;
    private Observable<Boolean> isUpdateObervable;
    private String friendId;

    private String fragmentBundle = "朋友圈";
    private String headpic;
    private LinearLayoutManager linearLayoutManager;

    private int lastVisibleItem = 0;
    private TextView commentSub;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoadObervable = mRxBus.register("isLoad", Boolean.class);
        isUpdateObervable = mRxBus.register("update", Boolean.class);
        netObservale = mRxBus.register("load_circle", Integer.class);

        Bundle bundle = getArguments();
        String bundleKey = (String)bundle.get("key");

        if (bundleKey != null) {
            fragmentBundle = bundleKey;
        }
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
                update = aBoolean;
                if (update) {
                    mRxBus.post("load_circle",0);
                    mRxBus.post("load_circle",1);
                    update = false;
                }
            }
        });


        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

                if (integer == 0 && "鸽圈".equals(fragmentBundle)){
                    tag = 0;
                    isDao = true;
                    innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
                }else if (integer == 1 && "好友圈".equals(fragmentBundle)){
                    tag = 1;
                    isDao = true;
                    innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
                }else if (integer == 2 && "我的鸽圈".equals(fragmentBundle)){
                    tag = 2;
                    isDao = true;
                    innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
                }else if (integer == 4 && "我的鸽圈".equals(fragmentBundle)){
                    tag = 2;

                    isDao = false;
                    PAGENUM = 1;
                    pageNumMap.put(fragmentBundle,PAGENUM);

                    methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;

                    innerCirclePresenter.getDatasFromNets(getParaMap(),tag);
                    innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
                }
            }
        });
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        myLmAdapter = new MyLmAdapter<InnerCircleBean>(getActivity(),true,0);

        recyclerView.setAdapter(myLmAdapter);

        myLmAdapter.setOnHolderListener(this);
        myLmAdapter.setMyItemClickListener(this);
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//
//                //在 newState为滑到底部时
//                if (newState == RecyclerView.SCROLL_STATE_IDLE){
//                    //如果没有隐藏 footview ，那么最后一个条目的位置就比我们的getItemCount 少 1，
//
//                    if (myLmAdapter.isFadeTips() == false && lastVisibleItem + 1 == myLmAdapter.getItemCount()){
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //然后调用 加载更多更新recyclerview
//                                updateRecyclerView();
//                            }
//                        },500);
//                    }
//
//                    //如果隐藏了提示条，但是又是上拉加载时，那么最后一个条目就要比 getItemCount  少 2
//                    if (myLmAdapter.isFadeTips() == true && lastVisibleItem + 2 == myLmAdapter.getItemCount()){
//                        mHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                //然后调用 加载更多更新recyclerview
//                                updateRecyclerView();
//                            }
//                        },500);
//                    }
//                }
//            }
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
//            }
//        });
    }

    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView() {

        if (innerCircleBeans.size() % 10 != 0) {
            PAGENUM = innerCircleBeans.size() / 10 + 2;
        }else {
            PAGENUM = innerCircleBeans.size() / 10 + 1;
        }

        pageNumMap.put(fragmentBundle,PAGENUM);

        changeMethodType();
        innerCirclePresenter.loadMoreData(getParaMap(),tag);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLoad) {
            PAGENUM = 1;
            pageNumMap.put(fragmentBundle,1);
            changeMethodType();
            getDatas();
        }

        isLoad = false;
    }

    @Override
    protected void inject() {

        DaggerCircleComponent.builder()
                .appComponent(getAppComponent())
                .circleMoudle(new CircleMoudle(getActivity(), this))
                .build()
                .inject(this);
    }

    private boolean update = false;

    @Override
    public void toDo() {
        InnerCircleBean innerCircleBean = (InnerCircleBean) myLmAdapter.getItem(currentPosition);
        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                innerCirclePresenter.updateCircleBy(getUserObJId(),innerCircleBean.getUserid(),true);
                break;
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                innerCirclePresenter.updateCircleBy(getUserObJId(),innerCircleBean.getUserid(),false);
                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:

                innerCirclePresenter.deleteCircleById(innerCircleBean.getId());

                loadCircle();

                break;

            case MethodType.METHOD_TYPE_ADD_FAB:

                Log.e("faamap99999","点赞-------"+ innerCircleBean.getCircleid());
                Log.e("faamap99999","id---1----"+ innerCircleBean.getId());

                innerCircleBean.setHas_fab(true);
                innerCircleBean.setFab_count(innerCircleBean.getFab_count() + 1);

                innerCirclePresenter.updateCircle(innerCircleBean);

                loadCircle();

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

    private void loadCircle(){
        mRxBus.post("load_circle",0);
        mRxBus.post("load_circle",1);
        mRxBus.post("load_circle",2);
    }


    private void changeMethodType(){
        PAGENUM = pageNumMap.get(fragmentBundle);

        switch (fragmentBundle){
            case "鸽圈":
                tag = 0;
                methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
                break;
            case "好友圈":
                tag = 1;
                methodType = MethodType.METHOD_TYPE_FRIENDS_CIRCLES;
                break;
            case "我的鸽圈":
                tag = 2;
                methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
                break;
        }
    }

    @Override
    public String getMethod() {
        String method ="";

        switch (methodType){
            case MethodType.METHOD_TYPE_FRIENDS_CIRCLES:
                method = MethodConstant.GET_FRIENDS_CIRCLES;
                break;
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                method = MethodConstant.GET_SINGLE_FRIEND_CIRCLES;
                break;
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

            case MethodType.METHOD_TYPE_DELETE_SINGEL:
                //删除指定朋友圈所有消息
                method = MethodConstant.DELETE_SINGLE_CIRCLE;
                break;

            case MethodType.METHOD_TYPE_DELETE_ALL:
                //删除自己朋友圈所有消息
                method = MethodConstant.DELETE_ALL_CIRCLE;
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

    @Override
    public void updateCircleList(final CircleBean data, String errorMsg, @DataLoadType.DataLoadTypeChecker int type) {

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

                if (data != null && data.getData() != null && data.getData().size() != 0){
                    if (!isDao) {
                        PAGENUM += 1;
                        pageNumMap.put(fragmentBundle,PAGENUM);
                    }

                    myLmAdapter.addData(data.getData(),true);
                    innerCircleBeans.addAll(data.getData());

                }else if (data.getData().size() == 0){
                    myLmAdapter.addData(data.getData(),false);
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    if (!isDao) {
                        PAGENUM += 1;
                        pageNumMap.put(fragmentBundle,PAGENUM);
                    }

                    myLmAdapter.updateList(data.getData(),data.getData().size() >= 10);

                    innerCircleBeans.clear();
                    innerCircleBeans.addAll(data.getData());
                }else if ( data.getData().size() == 0){

                    myLmAdapter.updateList(data.getData(),data.getData().size() >= 10);

                    innerCircleBeans.clear();
                }
                break;
        }
    }

    @Override
    public void setRefrash(boolean refrash) {
//        swipeRefreshLayout.setRefreshing(refrash);
        refreshLayout.finishRefresh(refrash);
    }

    @Override
    public String getUserObJId() {
        return userObjId;
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
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                map.put("friendid",getFriendId());
                break;
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                map.put("friendid",getUserObJId());
                map.put("cp",String.valueOf(PAGENUM));
                map.put("ps",String.valueOf(PAGESIZE));
                break;
            case MethodType.METHOD_TYPE_FRIENDS_CIRCLES:
                map.put("cp",String.valueOf(PAGENUM));
                map.put("ps",String.valueOf(PAGESIZE));
                break;
            case MethodType.METHOD_TYPE_ALL_CIRCLES:
                map.put("cp",String.valueOf(PAGENUM));
                map.put("ps",String.valueOf(PAGESIZE));
                break;
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                map.put("friendid",getFriendId());
                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:
            case MethodType.METHOD_TYPE_SHARE_CIRCLE:
                map.put("circleid",circleid);
                break;

            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:

                map.put("circleid",circleid);
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:

                map.put("circleid",circleid);
                map.put("content",commentContent);
                break;
        }

        Log.e("faamap",map.toString()+"----"+fragmentBundle+"------"+TAG);

        return map;
    }

    private int tag = 0;
    private String circleid;

    private String getFriendId() {
        return friendId;
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

    private void showDownDialog(){
        final Dialog mDialog = new Dialog(getActivity(), R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.personal_down_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mRemoveTv = (TextView) view.findViewById(R.id.headciv_dialog_remove_attention);
        TextView mShouTv = (TextView) view.findViewById(R.id.headciv_dialog_shou);
        TextView mDeleteTv = (TextView) view.findViewById(R.id.headciv_dialog_delete);
        TextView mCancle = (TextView) view.findViewById(R.id.headciv_dialog_cancle);

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

    private void showPop(final InnerAttention innerAttention) {

        popDialog = new Dialog(getActivity(), R.style.DialogTheme);

        View view = View.inflate(getActivity(), R.layout.layout_show_friend, null);
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

        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
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
            mSexTv.setText("1".equals(innerAttention.getGender())?"男":"女");
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
//                ourCodePresenter.removeAttention(getParaMap());
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
                        .start(getActivity());

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
    public void onDestroy() {
        super.onDestroy();
        mRxBus.unregister("isLoad", isLoadObervable);
        mRxBus.unregister("load_circle", netObservale);
        mRxBus.unregister("update", isUpdateObervable);
    }

//    @Override
//    public void onRefresh() {
//
//        if (!ApiUtils.isNetworkConnected(getActivity())) {
//            isDao = true;
//            innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
//        } else {
//            PAGENUM = 1;
//            pageNumMap.put(fragmentBundle,PAGENUM);
//            isDao = false;
//            //下拉刷新
//            changeMethodType();
//            innerCirclePresenter.refreshFromNets(getParaMap(),tag);
//        }
//    }

    @Override
    public void toInitHolder(final MyLmAdapter.MyRefrashHolder holder,final int position,final InnerCircleBean innerCircleBean) {

        this.innerCircleBean = innerCircleBean;

        //转发的

        if (innerCircleBean.getTrans_userid() != null && !"".equals(innerCircleBean.getTrans_userid()) && !"-1".equals(innerCircleBean.getTrans_userid())) {
            tranHolder(holder,position,innerCircleBean);
            holder.transpondFl.setVisibility(View.VISIBLE);

            holder.mRecyclerView.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){

                String content = innerCircleBean.getContent();

                String[] contents = content.split("#");
                holder.mTranContentTv.setText(contents[contents.length - 1]);

                Log.e("mTranContentTv",content+"--------"+innerCircleBean.getUsername()+"--------"+innerCircleBean.getTrans_name());

                if (content.lastIndexOf("#") != -1) {
                    holder.mContentTv.setText(content.substring(0,content.lastIndexOf("#")));
                }else {
                    holder.mContentTv.setText("转发动态");
                }
            }

            if (!"".equals(innerCircleBean.getTrans_name()) && innerCircleBean.getTrans_name() != null ){
                holder.mTranName.setText("@" + String.valueOf(innerCircleBean.getTrans_name()));
            }

            if (innerCircleBean.getPics() != null && innerCircleBean.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = innerCircleBean.getPics().size();

                for (String pics:innerCircleBean.getPics()) {
                    selectedPhotos.add(pics);
                }

                holder.mTranRv.removeAllViews();

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);

                holder.mTranRv.setVisibility(View.VISIBLE);
                holder.mTranRv.setAdapter(photoAdapter);
                holder.mTranRv.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) holder.mTranRv.getItemAnimator()).setSupportsChangeAnimations(false);

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
                holder.mTranRv.removeAllViews();
                holder.mTranRv.setVisibility(View.GONE);
            }

            holder.mAddFriendBtn.setVisibility(View.GONE);
            holder.mDownIv.setVisibility(View.GONE);

        }else {

            holder.transpondFl.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){
                holder.mContentTv.setText(innerCircleBean.getContent());
            }

            if (innerCircleBean.getPics() != null && innerCircleBean.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = innerCircleBean.getPics().size();

                for (String pics:innerCircleBean.getPics()) {
                    selectedPhotos.add(pics);
                }

                holder.mRecyclerView.removeAllViews();

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);

                holder.mRecyclerView.setVisibility(View.VISIBLE);
                holder.mRecyclerView.setAdapter(photoAdapter);
                holder.mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) holder.mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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
                holder.mRecyclerView.removeAllViews();
                holder.mRecyclerView.setVisibility(View.GONE);
            }
        }

        Log.e("afsdfdewf",innerCircleBean.getPlayerid()+"------"+innerCircleBean.getCircleid()+"-----"+innerCircleBean.getIs_friend());

        if (innerCircleBean.isIs_friend() || tag == 1 || tag == 2) {
            holder.mAddFriendBtn.setVisibility(View.GONE);
            holder.mDownIv.setVisibility(View.VISIBLE);
        }else {
            holder.mAddFriendBtn.setVisibility(View.VISIBLE);
            holder.mDownIv.setVisibility(View.GONE);
        }
        if (innerCircleBean.isHas_fab()) {
            Drawable likeLift = getResources().getDrawable(R.mipmap.like);
            likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
            holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
        }else {
            Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
            likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
            holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
        }

        if (innerCircleBean.getShare_count() != 0) {
            holder.mTranspondBtn.setText("转发 "+innerCircleBean.getShare_count());
        }else {
            holder.mTranspondBtn.setText("转发");
        }

        if (innerCircleBean.getComment_count() != 0) {
            holder.mCommentBtn.setText("评论 "+innerCircleBean.getComment_count());
        }else {
            holder.mCommentBtn.setText("评论");
        }
        if (innerCircleBean.getFab_count() != 0){
            holder.mPraiseBtn.setText("赞 "+innerCircleBean.getFab_count());
        }else {
            holder.mPraiseBtn.setText("赞");
        }

        if (innerCircleBean.getContent() == null || "".equals(innerCircleBean.getContent())){
            holder.mContentTv.setVisibility(View.GONE);
        }else {
            holder.mContentTv.setVisibility(View.VISIBLE);
        }

        if (!"".equals(innerCircleBean.getUsername()) && innerCircleBean.getUsername() != null ){
            holder.mUserName.setText(String.valueOf(innerCircleBean.getUsername()));
        }

        headpic = ConstantUtils.HEADPIC;
        if (innerCircleBean.getHeadpic() != null && !"".equals(innerCircleBean.getHeadpic()) && !"-1".equals(innerCircleBean.getHeadpic())){
            headpic += innerCircleBean.getHeadpic();

            String tag = (String) holder.mUserIcon.getTag(R.id.imageid);

            if (!TextUtils.equals(headpic,tag)){
                holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
            }

            holder.mUserIcon.setTag(R.id.imageid,headpic);
            Glide.with(getContext())
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(holder.mUserIcon);

        }else {
            holder.mUserIcon.setTag(R.id.imageid,"");
            holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
        }

        holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e("zan",position+"----mUserIcon----position-----"+fragmentBundle);

                currentPosition = position;

                if (innerCircleBean.isIs_friend() || innerCircleBean.getUserid().equals(getUserObJId())){
                    Intent intent = new Intent(getActivity(),CircleDetialActivity.class);
                    intent.putExtra("friendid",innerCircleBean.getUserid());
                    intent.putExtra("name",innerCircleBean.getUsername());
                    intent.putExtra("innerCircleBean",innerCircleBean);
//                    intent.putExtra("current_position",currentPosition);
                    intent.putExtra("circle_tag",tag);
                    startActivity(intent);
                }
            }
        });

        if (!TextUtils.isEmpty(innerCircleBean.getCreate_time()) && !"".equals(innerCircleBean.getCreate_time())) {
            holder.mCreateTimeTv.setText(innerCircleBean.getCreate_time());
        }
        holder.mDownIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = position;

                circleid = innerCircleBean.getCircleid();
                friendId = innerCircleBean.getUserid();
                isFriend = innerCircleBean.isIs_friend();
                showDownDialog();
            }
        });


        holder.mAddFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = position;
                methodType = MethodType.METHOD_TYPE_ADD_ATTENTION;

                friendId = innerCircleBean.getUserid();

                ourCodePresenter.addAttention(getParaMap());
            }
        });

        holder.mPraiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = position;

                circleid = innerCircleBean.getCircleid();
                friendId = innerCircleBean.getUserid();
                isFriend = innerCircleBean.isIs_friend();

                if (innerCircleBean.isHas_fab()) {
                    methodType = MethodType.METHOD_TYPE_REMOVE_FAB;
                    ourCodePresenter.removeFab(getParaMap());
                }else {
                    methodType = MethodType.METHOD_TYPE_ADD_FAB;
                    ourCodePresenter.addFab(getParaMap());
                }

            }
        });

        holder.mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = position;

                circleid = innerCircleBean.getCircleid();
                methodType = MethodType.METHOD_TYPE_ADD_COMMENT;

                if (innerCircleBean.getComment_count() != 0){
                    Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
                    intent.putExtra("innerCircleBean",innerCircleBeans.get(currentPosition));
                    intent.putExtra("circle_tag",tag);
                    intent.putExtra("circle_cur", currentPosition);
                    startActivity(intent);
                }else {
                    showMyDialog();
                }
            }
        });

        holder.mTranspondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPosition = position;

                circleid = innerCircleBean.getCircleid();
                Intent intent = new Intent(getActivity(),TransCircleActivity.class);
                intent.putExtra("innerCircleBean",innerCircleBean);
                startActivity(intent);
            }
        });
    }

    private void tranHolder(MyLmAdapter.MyRefrashHolder holder, int position, InnerCircleBean innerCircleBean) {



    }

    public void showMyDialog() {

        Log.e("fafafasdf","评论---showMyDialog");

        mDialogEt = new Dialog(getActivity(), R.style.DialogTheme);
//        mDialogEt.setCancelable(false);

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

    private String commentContent = "";

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
    public void onRefresh(RefreshLayout refreshLayout) {

        if (!ApiUtils.isNetworkConnected(getActivity())) {
            isDao = true;
            innerCirclePresenter.getDatasFromDao(getUserObJId(),getUserObJId(),true,tag);
        } else {
            PAGENUM = 1;
            pageNumMap.put(fragmentBundle,PAGENUM);
            isDao = false;
            //下拉刷新
            changeMethodType();
            innerCirclePresenter.refreshFromNets(getParaMap(),tag);
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshLayout) {
        updateRecyclerView();
    }
}
