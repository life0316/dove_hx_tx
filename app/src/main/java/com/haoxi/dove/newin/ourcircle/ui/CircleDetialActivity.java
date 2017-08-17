package com.haoxi.dove.newin.ourcircle.ui;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.adapter.MyLmAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.CircleDetialMoudle;
import com.haoxi.dove.inject.DaggerCircleDetialComponent;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCirclePresenter;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.CircleBean;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2017\6\20 0020.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_circle_detial)
public class CircleDetialActivity extends BaseActivity implements IMyCircleView<CircleBean>,AppBarLayout.OnOffsetChangedListener, OnHolder2Listener<InnerCircleBean,MyLmAdapter.MyRefrashHolder>, MyItemClickListener {

    private static final String TAG = "CircleDetialActivity";

    private int methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.backdrop)
    ImageView mBackdropIv;

    @BindView(R.id.image)
    CircleImageView mciv;

    @BindView(R.id.user_code_tv)
    TextView mUserCodeIv;
    @BindView(R.id.user_name_tv)
    TextView mUserNameIv;


    @BindView(R.id.act_circle_rv)
    RecyclerView recyclerView;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @Inject
    InnerCirclePresenter innerCirclePresenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;

    private int PAGENUM = 1;  //查询起始下标，默认为0
    private int PAGESIZE = 10;//每页返回的数据，默认10

    private String friendId = "";
    private String circleid = "";
    private String commentContent = "";
    private int circleTag = 0;

    private String headpic = ConstantUtils.HEADPIC;

    private ActionBar actionBar;

    private Handler mHandler = new Handler();

    MyLmAdapter myLmAdapter;

    CirclePhotoAdapter photoAdapter;
    private InnerCircleBean innerCircleBean;
    private String attenHeadpic;

    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();

    private int lastVisibleItem = 0;
    private int currentPosition;//当前操作的 position

    private String cirUserid = "";
    private String name;


    @Override
    public void toDo() {

        InnerCircleBean innerCircleBean = (InnerCircleBean) myLmAdapter.getItem(currentPosition);

        switch (methodType){
            case MethodType.METHOD_TYPE_DELETE_ALL:

                innerCirclePresenter.deleteCircleBy(getUserObJId(),getUserObJId());

                methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
                innerCirclePresenter.getDatasFromNets(getParaMap(),2);
                loadCircle();
                break;

            case MethodType.METHOD_TYPE_ADD_FAB:

                innerCircleBean.setFab_count(innerCircleBean.getFab_count() + 1);
                innerCircleBean.setHas_fab(true);
                innerCirclePresenter.updateCircle(innerCircleBean);

                loadCircle();

                innerCirclePresenter.getDatasFromDao(getUserObJId(),friendId,isFriend,2);

                break;
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                innerCircleBean.setFab_count(innerCircleBean.getFab_count() - 1);
                innerCircleBean.setHas_fab(false);
                innerCirclePresenter.updateCircle(innerCircleBean);
                loadCircle();

                innerCirclePresenter.getDatasFromDao(getUserObJId(),friendId,isFriend,2);
                break;

            case MethodType.METHOD_TYPE_ADD_COMMENT:
                innerCircleBean.setComment_count(innerCircleBean.getComment_count() + 1);

                innerCirclePresenter.updateCircle(innerCircleBean);

                loadCircle();

                innerCirclePresenter.getDatasFromDao(getUserObJId(),friendId,isFriend,2);

                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:

                myLmAdapter.getDatas().remove(currentPosition);
                myLmAdapter.notifyItemRemoved(currentPosition);
                myLmAdapter.notifyDataSetChanged();

                innerCirclePresenter.deleteCircle(innerCircleBean);

                mRxBus.post("load_circle",0);
                mRxBus.post("load_circle",2);

                break;
        }
    }

    private void loadCircle(){
        mRxBus.post("load_circle",0);
        mRxBus.post("load_circle",1);
        mRxBus.post("load_circle",2);
    }

    @Override
    public String getMethod() {
        String method ="";

        switch (methodType){
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                method = "/app/circle/get_single_friend_circles";
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
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                method = "/app/circle_comment/add_comment";
                break;
        }
        return method;
    }

    @Override
    protected void initInject() {

        DaggerCircleDetialComponent.builder()
                .appComponent(getAppComponent())
                .circleDetialMoudle(new CircleDetialMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        setSupportActionBar(mToolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_press);
        }

        final Intent intent = getIntent();
        if (intent != null) {
            friendId = intent.getStringExtra("friendid");
            name = intent.getStringExtra("name");

            circleTag = intent.getIntExtra("circle_tag",0);
            innerCircleBean = intent.getParcelableExtra("innerCircleBean");
            attenHeadpic = intent.getStringExtra("attenHeadpic");
            if (name != null && !"".equals(name) ) {
                actionBar.setTitle("");
                mUserNameIv.setText("昵称:"+name);
                if (innerCircleBean != null) {
                    cirUserid = innerCircleBean.getUserid();
                }
            }
        }

        initCircle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        myLmAdapter = new MyLmAdapter<InnerCircleBean>(this,true,0);

        recyclerView.setAdapter(myLmAdapter);

        myLmAdapter.setOnHolderListener(this);
        myLmAdapter.setMyItemClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //在 newState为滑到底部时
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    //如果没有隐藏 footview ，那么最后一个条目的位置就比我们的getItemCount 少 1，

                    if (myLmAdapter.isFadeTips() == false && lastVisibleItem + 1 == myLmAdapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //然后调用 加载更多更新recyclerview
                                updateRecyclerView();
                            }
                        },500);
                    }

                    //如果隐藏了提示条，但是又是上拉加载时，那么最后一个条目就要比 getItemCount  少 2
                    if (myLmAdapter.isFadeTips() == true && lastVisibleItem + 2 == myLmAdapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //然后调用 加载更多更新recyclerview
                                updateRecyclerView();
                            }
                        },500);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition();
            }
        });

        mciv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentUrl = new Intent(CircleDetialActivity.this,ImageDetailActivity.class);

                intentUrl.putExtra(ImageDetailActivity.EXTRA_IMAGE_URL,headpic);

                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                  CircleDetialActivity.this,new Pair<View,String>(mciv,ImageDetailActivity.VIEW_NAME_HEADER_IMAGE));

                ActivityCompat.startActivity(CircleDetialActivity.this,intentUrl,activityOptionsCompat.toBundle());
            }
        });
    }

    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView() {

        methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
        innerCirclePresenter.loadMoreData(getParaMap(),2);
    }

    private void initCircle() {

        if (innerCircleBean != null){

            mUserCodeIv.setText("用户编号:"+innerCircleBean.getUserid());

            headpic = ConstantUtils.HEADPIC;
            if (innerCircleBean.getHeadpic() != null && !"".equals(innerCircleBean.getHeadpic()) && !"-1".equals(innerCircleBean.getHeadpic())){

                if (innerCircleBean.getHeadpic().startsWith("http")) {
                    headpic = innerCircleBean.getHeadpic();
                }else {
                    headpic += innerCircleBean.getHeadpic();
                }

                mBackdropIv.setTag(R.id.imageid,headpic);
                Glide.with(this)
                        .load(headpic)
                        .dontAnimate()
                        .placeholder(R.mipmap.btn_img_photo_default)
                        .error(R.mipmap.btn_img_photo_default)
                        .into(mciv);

            }else if (attenHeadpic != null){
                if (attenHeadpic.startsWith("http")) {
                    headpic = attenHeadpic;
                }else {
                    headpic += attenHeadpic;
                }

                mBackdropIv.setTag(R.id.imageid,headpic);
                Glide.with(this)
                        .load(headpic)
                        .dontAnimate()
                        .placeholder(R.mipmap.btn_img_photo_default)
                        .error(R.mipmap.btn_img_photo_default)
                        .into(mciv);
            }else {
                mBackdropIv.setImageResource(R.mipmap.btn_img_photo_default);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        appBarLayout.addOnOffsetChangedListener(this);
        PAGENUM = 1;
        methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
        innerCirclePresenter.refreshFromNets(getParaMap(),2);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (friendId.equals(getUserObJId())){
            getMenuInflater().inflate(R.menu.circle, menu);
        }else {
            getMenuInflater().inflate(R.menu.circle2, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            mRxBus.post("isLoad",false);
            finish();
        }

        if (item.getItemId() == R.id.circle_clear){

            Log.e("clear","清空");

            methodType = MethodType.METHOD_TYPE_DELETE_ALL;
            ourCodePresenter.deleteAllCircle(getParaMap());

        }
        if (item.getItemId() == R.id.circle_share){

            Log.e("clear","分享");
        }
        return super.onOptionsItemSelected(item);
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
            case MethodType.METHOD_TYPE_REMOVE_ATTENTION:
                map.put("friendid",getFriendId());
                break;
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                map.put("friendid",getFriendId());
                map.put("cp",String.valueOf(PAGENUM));
                map.put("ps",String.valueOf(PAGESIZE));
                break;
            case MethodType.METHOD_TYPE_ADD_ATTENTION:
                map.put("friendid",getFriendId());
                break;
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
            case MethodType.METHOD_TYPE_DELETE_SINGEL:
                map.put("circleid",circleid);
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                map.put("circleid",circleid);
                map.put("content",commentContent);
                break;
        }

        Log.e(TAG,map.toString()+"-----map---"+TAG);
        return map;
    }

    private String getFriendId() {
        return friendId;
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
    public void updateCircleList(CircleBean data, String errorMsg, int type) {

        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_FAIL:

                break;
            case DataLoadType.TYPE_REFRESH_FAIL:

                break;
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0){
                    PAGENUM += 1;

                    myLmAdapter.addData(data.getData(),false);
                    innerCircleBeans.addAll(data.getData());

                }else if (data.getData().size() == 0){
                    myLmAdapter.addData(data.getData(),false);
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    PAGENUM += 1;

                    myLmAdapter.updateList(data.getData(),data.getData().size() >= 10 );

                    innerCircleBeans.clear();
                    innerCircleBeans.addAll(data.getData());
                }
                break;
        }
    }

    @Override
    public void setRefrash(boolean refrash) {

    }

    @Override
    public void onItemClick(View view, int position) {
        if (innerCircleBeans.get(position) != null) {
            Intent intent = new Intent(this,EarchCircleActivity.class);
            intent.putExtra("innerCircleBean",innerCircleBeans.get(position));
            intent.putExtra("circle_tag",4);
            startActivity(intent);
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i){

    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }


    @Override
    public void toInitHolder(final MyLmAdapter.MyRefrashHolder holder,final int position,final InnerCircleBean innerCircleBean) {

        this.innerCircleBean = innerCircleBean;

        //转发的

        if (innerCircleBean.getTrans_userid() != null && !"".equals(innerCircleBean.getTrans_userid()) && !"-1".equals(innerCircleBean.getTrans_userid())) {
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
                //
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

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(this, selectedPhotos);

                holder.mTranRv.setVisibility(View.VISIBLE);
                holder.mTranRv.setAdapter(photoAdapter);
                holder.mTranRv.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) holder.mTranRv.getItemAnimator()).setSupportsChangeAnimations(false);

                photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        MyPhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .setShowDeleteButton(false)
                                .start(CircleDetialActivity.this);
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

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(this, selectedPhotos);

                holder.mRecyclerView.setVisibility(View.VISIBLE);
                holder.mRecyclerView.setAdapter(photoAdapter);
                holder.mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) holder.mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        MyPhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .setShowDeleteButton(false)
                                .start(CircleDetialActivity.this);
                    }
                });
            }else {
                holder.mRecyclerView.removeAllViews();
                holder.mRecyclerView.setVisibility(View.GONE);
            }
        }

        holder.mAddFriendBtn.setVisibility(View.GONE);
        holder.mDownIv.setVisibility(View.GONE);

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
            Glide.with(CircleDetialActivity.this)
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(holder.mUserIcon);

        }else {
            holder.mUserIcon.setTag(R.id.imageid,"");
            holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
        }

//        holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                currentPosition = position;
//
//                if (innerCircleBean.isIs_friend() || innerCircleBean.getUserid().equals(getUserObJId())){
//                    Intent intent = new Intent(Ci,CircleDetialActivity.class);
//                    intent.putExtra("friendid",innerCircleBean.getUserid());
//                    intent.putExtra("name",innerCircleBean.getUsername());
//                    intent.putExtra("innerCircleBean",innerCircleBean);
////                    intent.putExtra("current_position",currentPosition);
//                    intent.putExtra("circle_tag",tag);
//                    startActivity(intent);
//                }
//            }
//        });

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
                    Intent intent = new Intent(CircleDetialActivity.this,EarchCircleActivity.class);
                    intent.putExtra("innerCircleBean",innerCircleBeans.get(currentPosition));
                    intent.putExtra("circle_tag",4);
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
                Intent intent = new Intent(CircleDetialActivity.this,TransCircleActivity.class);
                intent.putExtra("innerCircleBean",innerCircleBean);
                startActivity(intent);
            }
        });
    }
    private Dialog mDialogEt;
    private TextView commentSub;
    private boolean isFriend;

    private void showDownDialog(){
        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.personal_down_dialog,null);

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

//        if (isFriend){
//            mRemoveTv.setText("取消关注");
//        }else {
//            mRemoveTv.setText("添加关注");
//        }

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void showMyDialog() {

        Log.e("fafafasdf","评论---showMyDialog");

        mDialogEt = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.comment_dialog, null);
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

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
