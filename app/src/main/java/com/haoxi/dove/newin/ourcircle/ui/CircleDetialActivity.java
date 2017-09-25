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
import com.haoxi.dove.modules.circle.CircleAdapter;
import com.haoxi.dove.modules.circle.IEachView;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.EachCirclePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
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

/**
 * Created by Administrator on 2017\6\20 0020.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_circle_detial)
public class CircleDetialActivity extends BaseActivity implements IEachView<EachCircleBean>, IMyCircleView<CircleBean>,AppBarLayout.OnOffsetChangedListener, OnHolder2Listener<InnerCircleBean,CircleAdapter.MyRefrashHolder>, MyItemClickListener, OnRefreshListener, OnLoadmoreListener {

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

    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @Inject
    InnerCirclePresenter innerCirclePresenter;

    @Inject
    OurCodePresenter ourCodePresenter;
    EachCirclePresenter eachCirclePresenter;

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

    CircleAdapter myLmAdapter;

    CirclePhotoAdapter photoAdapter;
    private InnerCircleBean innerCircleBean;
    private String attenHeadpic;

    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();

    private int lastVisibleItem = 0;
    private int currentPosition;//当前操作的 position

    private String cirUserid = "";
    private String name;
    private Observable<Integer> netObservale;
    private boolean isLoad = true;


    @Override
    public void toDo() {

        switch (methodType){
            case MethodType.METHOD_TYPE_DELETE_ALL:

//                innerCirclePresenter.deleteCircleBy(getUserObJId(),getUserObJId());
//
//                methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
//                innerCirclePresenter.getDatasFromNets(getParaMap(),2);
//                loadCircle();
                break;

            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                myLmAdapter.getDatas().remove(currentPosition);
                methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                eachCirclePresenter.getDataFromNets(getParaMap());
                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:

                myLmAdapter.getDatas().remove(currentPosition);
                myLmAdapter.notifyItemRemoved(currentPosition);
                myLmAdapter.notifyDataSetChanged();

                break;
        }
    }

    @Override
    public String getMethod() {
        String method ="";

        switch (methodType){
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                method = MethodConstant.GET_SINGLE_FRIEND_CIRCLES;
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
            case MethodType.METHOD_TYPE_CIRCLE_DETAIL:
                method = MethodConstant.GET_CIRCLE_DETAIL;
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
        netObservale = mRxBus.register(ConstantUtils.OBSER_LOAD_CIRCLE, Integer.class);
        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 4 ){
                    myLmAdapter.getDatas().remove(currentPosition);
                    methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                    eachCirclePresenter.getDataFromNets(getParaMap());
                }
            }
        });

        eachCirclePresenter = new EachCirclePresenter(this);

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
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);

        myLmAdapter = new CircleAdapter(this,0);
        recyclerView.setAdapter(myLmAdapter);

        myLmAdapter.setOnHolderListener(this);
        myLmAdapter.setMyItemClickListener(this);
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

//        methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
//        innerCirclePresenter.loadMoreData(getParaMap(),2);
        if (innerCircleBeans.size() % 10 != 0) {
            PAGENUM = innerCircleBeans.size() / 10 + 2;
        }else {
            PAGENUM = innerCircleBeans.size() / 10 + 1;
        }
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

        if (isLoad){
            PAGENUM = 1;
            methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
            innerCirclePresenter.refreshFromNets(getParaMap(),2);
            isLoad = false;
        }
        appBarLayout.addOnOffsetChangedListener(this);

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
            case  MethodType.METHOD_TYPE_CIRCLE_DETAIL:
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
                    PAGENUM += 1;

                    myLmAdapter.addData(data.getData());
                    innerCircleBeans.addAll(data.getData());

                }else if (data.getData().size() == 0){
                    myLmAdapter.addData(data.getData());
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    PAGENUM += 1;

                    myLmAdapter.updateList(data.getData());

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
        currentPosition = position;
        if (innerCircleBeans.get(position) != null) {

            circleid = innerCircleBeans.get(position).getCircleid();

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
    public void toInitHolder(final CircleAdapter.MyRefrashHolder holder,final int position,final InnerCircleBean data) {

        this.innerCircleBean = data;

        //转发的

        if (data.getTrans_userid() != null && !"".equals(data.getTrans_userid()) && !"-1".equals(data.getTrans_userid())) {
            holder.transpondFl.setVisibility(View.VISIBLE);

            holder.mRecyclerView.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(data.getContent()) && !"".equals(data.getContent())){

                String content = data.getContent();

                String[] contents = content.split("#");
                holder.mTranContentTv.setText(contents[contents.length - 1]);

                Log.e("mTranContentTv",content+"--------"+data.getUsername()+"--------"+data.getTrans_name());

                if (content.lastIndexOf("#") != -1) {
                    holder.mContentTv.setText(content.substring(0,content.lastIndexOf("#")));
                }else {
                    holder.mContentTv.setText("转发动态");
                }
                //
            }

            if (!"".equals(data.getTrans_name()) && data.getTrans_name() != null ){
                holder.mTranName.setText("@" + String.valueOf(data.getTrans_name()));
            }

            if (data.getPics() != null && data.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = data.getPics().size();

                for (String pics:data.getPics()) {
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

            if (!TextUtils.isEmpty(data.getContent()) && !"".equals(data.getContent())){
                holder.mContentTv.setText(data.getContent());
            }

            if (data.getPics() != null && data.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = data.getPics().size();

                for (String pics:data.getPics()) {
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

        if (data.isHas_fab()) {
            Drawable likeLift = getResources().getDrawable(R.mipmap.like);
            likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
            holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
        }else {
            Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
            likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
            holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
        }

        if (data.getShare_count() != 0) {
            holder.mTranspondBtn.setText("转发 "+data.getShare_count());
        }else {
            holder.mTranspondBtn.setText("转发");
        }

        if (data.getComment_count() != 0) {
            holder.mCommentBtn.setText("评论 "+data.getComment_count());
        }else {
            holder.mCommentBtn.setText("评论");
        }
        if (data.getFab_count() != 0){
            holder.mPraiseBtn.setText("赞 "+data.getFab_count());
        }else {
            holder.mPraiseBtn.setText("赞");
        }

        if (data.getContent() == null || "".equals(data.getContent())){
            holder.mContentTv.setVisibility(View.GONE);
        }else {
            holder.mContentTv.setVisibility(View.VISIBLE);
        }

        if (!"".equals(data.getUsername()) && data.getUsername() != null ){
            holder.mUserName.setText(String.valueOf(data.getUsername()));
        }

        headpic = ConstantUtils.HEADPIC;
        if (data.getHeadpic() != null && !"".equals(data.getHeadpic()) && !"-1".equals(data.getHeadpic())){
            headpic += data.getHeadpic();

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

        if (!TextUtils.isEmpty(data.getCreate_time()) && !"".equals(data.getCreate_time())) {
            holder.mCreateTimeTv.setText(innerCircleBean.getCreate_time());
        }
        holder.mDownIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = position;

                circleid = data.getCircleid();
                friendId = data.getUserid();
                isFriend = data.isIs_friend();
                showDownDialog();
            }
        });

        holder.mPraiseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentPosition = position;

                circleid = data.getCircleid();
                friendId = data.getUserid();
                isFriend = data.isIs_friend();

                if (data.isHas_fab()) {
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

                circleid = data.getCircleid();
                methodType = MethodType.METHOD_TYPE_ADD_COMMENT;

                if (data.getComment_count() != 0){
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

                circleid = data.getCircleid();
                Intent intent = new Intent(CircleDetialActivity.this,TransCircleActivity.class);
                intent.putExtra("innerCircleBean",data);
                intent.putExtra("circle_tag",4);
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
    public void toUpdateEach(EachCircleBean data) {
        InnerCircleBean circleBean = innerCircleBeans.get(currentPosition);
        circleBean.setComment_count(data.getData().getComment_count());
        circleBean.setFab_count(data.getData().getFab_count());
        circleBean.setShare_count(data.getData().getShare_count());
        circleBean.setHas_fab(data.getData().getHas_fab());

        myLmAdapter.getDatas().add(currentPosition,circleBean);
        myLmAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (ApiUtils.isNetworkConnected(this)) {
            PAGENUM = 1;
            methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
            innerCirclePresenter.refreshFromNets(getParaMap(),2);
        }
    }

    @Override
    public void onLoadmore(RefreshLayout refreshlayout) {
        updateRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRxBus.unregister(ConstantUtils.OBSER_LOAD_CIRCLE, netObservale);
    }
}
