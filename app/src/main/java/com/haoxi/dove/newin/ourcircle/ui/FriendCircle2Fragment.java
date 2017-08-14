package com.haoxi.dove.newin.ourcircle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.adapter.RefrashRvAdapter;
import com.haoxi.dove.base.BaseReFrashRvFragment;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolderListener;
import com.haoxi.dove.inject.DaggerFriendCircle2Component;
import com.haoxi.dove.inject.FriendCircle2Moudle;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.InnerAttention;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.zly.www.easyrecyclerview.EasyDefRecyclerView;
import com.zly.www.easyrecyclerview.footer.ErvDefaultFooter;
import com.zly.www.easyrecyclerview.listener.OnLoadListener;
import com.zly.www.easyrecyclerview.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lifei on 2017/1/14.
 */

public class FriendCircle2Fragment extends BaseReFrashRvFragment implements IMyCircleView<CircleBean>, OnRefreshListener, OnLoadListener, OnHolderListener<InnerCircleBean,RefrashRvAdapter.MyRefrashHolder>, MyItemClickListener {

    private static final String TAG = "FriendCircleFragment";

    private int methodType = MethodType.METHOD_TYPE_FRIENDS_CIRCLES;

    private static final int REQUSET_CODE_ICON = 2000;
    private static final int REQUSET_CODE_DETIAL = 2001;
    private static final int REQUSET_CODE_COMMENT = 2003;
    private static final int REQUSET_CODE_TRANSPONA = 2002;
    private static final int REQUSET_CODE_PRAISE = 2004;


    private int PAGENUM = 0;  //查询起始下标，默认为0
    private int PAGESIZE = 10;//每页返回的数据，默认10

    private boolean isFriend = false;

    private Dialog popDialog;

    private InnerCircleBean innerCircleBean;


    @BindView(R.id.base_erv)
    EasyDefRecyclerView easyRecyclerView;

    @Inject
    InnerCirclePresenter innerCirclePresenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    RefrashRvAdapter refrashRvAdapter;

    CirclePhotoAdapter photoAdapter;

    @Inject
    RxBus mRxBus;

    private int currentPosition;//当前操作的 position

    private int changeTag;

    private Handler mHandler = new Handler();

    private Observable<Integer> changeObservale;
    private Observable<Integer> netObservale;

    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();

    private Map<String,Integer> pageNumMap = new HashMap<>();

    private boolean isLoad = true; // 是否显示就加载数据
    private Observable<Boolean> isLoadObervable;
    private String friendId;

    private String fragmentBundle = "朋友圈";
    private String headpic;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isLoadObervable = mRxBus.register("isLoad", Boolean.class);
        changeObservale = mRxBus.register("change_position", Integer.class);
        netObservale = mRxBus.register("load_circle", Integer.class);

        Bundle bundle = getArguments();
        String bundleKey = (String)bundle.get("key");

        if (bundleKey != null) {
            Log.e("bundlekey",bundleKey+"-------bundleKey");
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

        changeObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                changeTag = integer;
            }
        });

        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {

                if (integer == 0 && "鸽圈".equals(fragmentBundle)){
                    PAGENUM = 0;
                    pageNumMap.put(fragmentBundle,0);
                    tag = 0;
                    methodType = MethodType.METHOD_TYPE_ALL_CIRCLES;
                    Log.e("fragmentBundle",tag+"-------fragmentBundle");
                    innerCirclePresenter.getDatasFromNets(getParaMap(),tag);
                }else if (integer == 1 && "好友圈".equals(fragmentBundle)){
                    PAGENUM = 0;
                    pageNumMap.put(fragmentBundle,0);
                    tag = 1;
                    methodType = MethodType.METHOD_TYPE_FRIENDS_CIRCLES;
                    Log.e("fragmentBundle",tag+"-------fragmentBundle");
                    innerCirclePresenter.getDatasFromNets(getParaMap(),tag);
                }else if (integer == 2 && "我的鸽圈".equals(fragmentBundle)){
                    PAGENUM = 0;
                    pageNumMap.put(fragmentBundle,0);
                    tag = 2;
                    methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
                    Log.e("fragmentBundle",tag+"-------fragmentBundle");
                    innerCirclePresenter.getDatasFromNets(getParaMap(),tag);
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        easyRecyclerView.setLayoutManager(linearLayoutManager);

        easyRecyclerView.setLastUpdateTimeRelateObject(this);
        easyRecyclerView.setOnRefreshListener(this);
        easyRecyclerView.setOnLoadListener(this);

        refrashRvAdapter = new RefrashRvAdapter();

        easyRecyclerView.setAdapter(refrashRvAdapter);

        refrashRvAdapter.setOnHolderListener(this);
        refrashRvAdapter.setMyItemClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (isLoad) {
            PAGENUM = 0;
            pageNumMap.put(fragmentBundle,0);
            changeMethodType();
            innerCirclePresenter.getDatasFromNets(getParaMap(),tag);
        }
    }

    @Override
    protected void inject() {

        DaggerFriendCircle2Component.builder()
                .appComponent(getAppComponent())
                .friendCircle2Moudle(new FriendCircle2Moudle(getActivity(), this))
                .build()
                .inject(this);

    }

    @Override
    public void toDo() {
        InnerCircleBean innerCircleBean = (InnerCircleBean) refrashRvAdapter.getItem(currentPosition);
        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                mRxBus.post("load_circle",1);
                innerCircleBean.setIs_friend(true);
                refrashRvAdapter.notifyItemChanged(currentPosition);
                break;
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                mRxBus.post("load_circle",1);
                innerCircleBean.setIs_friend(false);
                refrashRvAdapter.notifyItemChanged(currentPosition);
                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:

                switch (fragmentBundle){
                    case "鸽圈":

                        if (isFriend) {
                            if (friendId.equals(getUserObJId())){
                                mRxBus.post("load_circle",2);
                            }else {
                                mRxBus.post("load_circle",1);
                            }
                        }

                        break;
                    case "好友圈":
                    case "我的鸽圈":
                        mRxBus.post("load_circle",0);
                        break;
                }

                refrashRvAdapter.getData().remove(currentPosition);
                refrashRvAdapter.notifyItemRemoved(currentPosition);
                refrashRvAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void changeMethodType(){
        PAGENUM = pageNumMap.get(fragmentBundle);

        Log.e("pagenm",PAGENUM +"-----------"+fragmentBundle);
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
                method = "/app/circle/get_friends_circles";
                break;
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                method = "/app/circle/get_single_friend_circles";
                break;
            case MethodType.METHOD_TYPE_ALL_CIRCLES:
                method = "/app/circle/get_all_circles";
                break;
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                //取消关注好友
                method = "/app/attention/remove";
                break;
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                //关注好友
                method = "/app/attention/add";
                break;

            case MethodType.METHOD_TYPE_DELETE_SINGEL:
                //删除指定朋友圈所有消息
                method = "/app/circle/delete_single";
                break;

            case MethodType.METHOD_TYPE_DELETE_ALL:
                //删除自己朋友圈所有消息
                method = "/app/circle/delete_all";
                break;


            case MethodType.METHOD_TYPE_ADD_FAB:
                method = "/app/circle_fab/add_fab";
                break;
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                method = "/app/circle_fab/remove_fab";
                break;
        }
        return method;
    }

    @Override
    public void updateCircleList(final CircleBean data, String errorMsg, @DataLoadType.DataLoadTypeChecker int type) {

        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_FAIL:

                easyRecyclerView.noMore();

                break;
            case DataLoadType.TYPE_REFRESH_FAIL:

                easyRecyclerView.refreshComplete();

                break;
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0){
                    PAGENUM += data.getData().size() - 1 < PAGESIZE? data.getData().size()-1:PAGESIZE;

                    Log.e("pagenm",PAGENUM +"------1-----"+fragmentBundle);

                    pageNumMap.put(fragmentBundle,PAGENUM);
                }

                easyRecyclerView.loadComplete();

                if (data != null && data.getData() != null && data.getData().size() != 0) {
                    refrashRvAdapter.addAll(data.getData());
                    innerCircleBeans.addAll(data.getData());
                }else if (data.getData().size() == 0){
                    easyRecyclerView.noMore();
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    PAGENUM += ((data.getData().size() - 1) < PAGESIZE ? data.getData().size() - 1 : PAGESIZE);
                    pageNumMap.put(fragmentBundle,PAGENUM);
                    easyRecyclerView.refreshComplete();

                    if (data.getData().size() < 10 && refrashRvAdapter.getFooter() != null) {

                        easyRecyclerView.removeFooter();

                    } else if (data.getData().size() == 10 && refrashRvAdapter.getFooter() == null) {
                        easyRecyclerView.setFooterView(new ErvDefaultFooter(getActivity()));
                        easyRecyclerView.loading();
                    }else if (data.getData().size() == 10){
                        easyRecyclerView.loading();
                    }

                    refrashRvAdapter.update(data.getData());
                    innerCircleBeans.clear();
                    innerCircleBeans.addAll(data.getData());
                }else if (data.getData().size() == 0){
                    if (refrashRvAdapter.getItemCount() != 0 && refrashRvAdapter.getData().size() == 0) {
                        easyRecyclerView.removeFooter();
                    }

                    easyRecyclerView.refreshComplete();

                    refrashRvAdapter.update(data.getData());
                    innerCircleBeans.clear();
                }
                break;
        }
    }

    @Override
    public void setRefrash(boolean refrash) {

    }

    @Override
    public String getUserObJId() {
        return userObjId;
    }

    @Override
    public void onRefreshListener() {

        PAGENUM = 0;
        pageNumMap.put(fragmentBundle,PAGENUM);
        //下拉刷新
        changeMethodType();
        innerCirclePresenter.refreshFromNets(getParaMap(),tag);
    }

    @Override
    public void onLoadListener() {
        //加载更多监听
        changeMethodType();
        innerCirclePresenter.loadMoreData(getParaMap(),tag);
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
                map.put("circleid",circleid);
                break;

            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:

                map.put("circleid",circleid);
                break;
        }

        return map;
    }

    private int tag = 0;
    private String circleid;

    private String getFriendId() {
        return friendId;
    }

    @Override
    public void toInitHolder(final RefrashRvAdapter.MyRefrashHolder holder, final int position, final InnerCircleBean innerCircleBean) {

        this.innerCircleBean = innerCircleBean;

        Log.e("zan",position+"--------position-----"+fragmentBundle);
        Log.e("zan",holder.getPosition()+"-------------------getPosition-------------"+innerCircleBean.getContent());


        if (innerCircleBean.getShare_count() != 0) {
            holder.mTranspondBtn.setText("转发 "+innerCircleBean.getShare_count());
        }

        if (innerCircleBean.getComment_count() != 0) {
            holder.mCommentBtn.setText("评论 "+innerCircleBean.getComment_count());
        }
        if (innerCircleBean.getFab_count() != 0){
            holder.mPraiseBtn.setText("赞 "+innerCircleBean.getFab_count());
        }

        if (innerCircleBean.getContent() == null && "".equals(innerCircleBean.getContent())){
            holder.mContentTv.setVisibility(View.GONE);
        }else {
            holder.mContentTv.setVisibility(View.VISIBLE);
        }

        if (!"".equals(innerCircleBean.getUsername()) && innerCircleBean.getUsername() != null ){
            holder.mUserName.setText(String.valueOf(innerCircleBean.getUsername()));
        }

        headpic = "http://118.178.227.194:8087/";
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

                if (innerCircleBean.isIs_friend() || innerCircleBean.getUserid().equals(getUserObJId())){
                    Intent intent = new Intent(getActivity(),CircleDetialActivity.class);
                    intent.putExtra("friendid",innerCircleBean.getUserid());
                    intent.putExtra("name",innerCircleBean.getUsername());
                    intent.putExtra("innerCircleBean",innerCircleBean);
//                    startActivity(intent);
                }
            }
        });

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


//            holder.mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                @Override
//                public void onItemClick(View view, int position) {
//
//                    isLoad = false;

            //这就是个坑
//
//                    Log.e("tagfuyong",innerCircleBean.getCircleid() + "----"+holder.mRecyclerView.getTag() );
//                    if (innerCircleBean.getCircleid().equals(holder.mRecyclerView.getTag())){
//                        MyPhotoPreview.builder()
//                                .setPhotos(selectedPhotos)
//                                .setCurrentItem(position)
//                                .setShowDeleteButton(false)
//                                .start(getActivity());
//                    }
//                }
//            }));
        }else {
            holder.mRecyclerView.removeAllViews();
            holder.mRecyclerView.setVisibility(View.GONE);
        }

        if (innerCircleBean.isIs_friend()) {
            holder.mAddFriendBtn.setVisibility(View.GONE);
            holder.mDownIv.setVisibility(View.VISIBLE);
        }else {
            holder.mAddFriendBtn.setVisibility(View.VISIBLE);
            holder.mDownIv.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){
            holder.mContentTv.setText(innerCircleBean.getContent());
        }

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
                circleid = innerCircleBean.getCircleid();
                methodType = MethodType.METHOD_TYPE_ADD_FAB;
                ourCodePresenter.addFab(getParaMap());
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.e("faf",(innerCircleBean == null) + "-----c--");
        Log.e("zan",position+"----onItemClick----position-----"+fragmentBundle);

        if (innerCircleBeans.get(position) != null) {
            Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
            intent.putExtra("innerCircleBean",innerCircleBeans.get(position));
//            startActivity(intent);
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
        mRxBus.unregister("change_position", changeObservale);
        mRxBus.unregister("isLoad", isLoadObervable);
        mRxBus.unregister("load_circle", netObservale);
    }

}
