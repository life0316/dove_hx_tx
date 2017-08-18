package com.haoxi.dove.newin.ourcircle.ui;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoxi.dove.R;
import com.haoxi.dove.adapter.CirclePhotoAdapter;
import com.haoxi.dove.holder.CommentHolder;
import com.haoxi.dove.adapter.MyLmAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.base.MyApplication;
import com.haoxi.dove.callback.MyItemClickListener;
import com.haoxi.dove.callback.OnHolder2Listener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerOurCommentComponent;
import com.haoxi.dove.inject.OurCommentMoudle;
import com.haoxi.dove.modules.circle.IEachView;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.ourcircle.presenter.EachCirclePresenter;
import com.haoxi.dove.newin.bean.EachCircleBean;
import com.haoxi.dove.newin.bean.InnerComment;
import com.haoxi.dove.newin.ourcircle.presenter.InnerCommentPresenter;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.OurCommentBean;
import com.haoxi.dove.retrofit.DataLoadType;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomDialog;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.haoxi.dove.widget.MyRecyclerview;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

@ActivityFragmentInject(contentViewId = R.layout.activity_circle_each)
public class EarchCircleActivity extends BaseActivity implements IMyCommentView<OurCommentBean>,IEachView<EachCircleBean>,View.OnClickListener, OnHolder2Listener<InnerComment,CommentHolder>,MyItemClickListener, OnRefreshListener {

    private static final String TAG = "EarchCircleActivity";

    @BindView(R.id.test_rv)
    MyRecyclerview recyclerView;
    @BindView(R.id.refreshLayout)
    RefreshLayout refreshLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.item_circle_ll)
    LinearLayout linearLayout;

    @BindView(R.id.each_share)
    TextView mTransTv;

    @BindView(R.id.each_comment)
    TextView mCommentTv;

    @BindView(R.id.each_fab)
    TextView mLikeFabTv;

    @BindView(R.id.de_icon)
    CircleImageView mUserIconCv;

    @BindView(R.id.friend_name)
    TextView userNameTv;

    @BindView(R.id.friend_text)
    TextView mContentTv;

    @BindView(R.id.tran_circle_name)
    TextView mTranName;

    @BindView(R.id.tran_circle_text)
    TextView mTranContentTv;

    @BindView(R.id.item_tran_fl)
    FrameLayout transpondFl;

    @BindView(R.id.item_isfriend_imageview)
    ImageView mContentImage;

    @BindView(R.id.tran_circle_rv)
    RecyclerView mTranRv;

    @BindView(R.id.item_isfriend_rv)
    RecyclerView mRecyclerView;

    @BindView(R.id.friend_date)
    TextView mCreateTimeTv;

    @BindView(R.id.comment_friend_dislike)
    Button mLikeBtn;

    @BindView(R.id.comment_friend_comments)
    Button mCommentsBtn;

    @BindView(R.id.comment_friend_share)
    Button mShareBtn;

    @Inject
    InnerCommentPresenter innerCommentPresenter;

    @Inject
    EachCirclePresenter eachCirclePresenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    @Inject
    RxBus mRxBus;

    private Dialog mDialogEt;

    private InnerCircleBean innerCircleBean;

    private int likeCount = 0;
    private int transCount = 0;

    private Handler mHandler = new Handler();
    MyLmAdapter myLmAdapter;

    private int methodType = MethodType.METHOD_TYPE_GET_COMMENT;

    private int PAGENUM = 1;

    private String circleid = "";
    private String friendid = "";
    private int lastVisibleItem = 0;

    private int circleTag = 0;

    //自己是否已经点过赞
    private boolean iHasFab = false;

    //是否 在该界面进行了：点赞/取消赞、评论/删除评论、回复，转发等操作
    private boolean isChange = false;

    //是否是自己发的朋友圈
    private String circleUserId = "";

    private String commentContent = "";

    private String commentid = "";

    private int fabSize = 0;

    private List<InnerComment> innerComments = new ArrayList<>();
    private TextView commentSub;


    @Override
    public void toDo() {
        isChange = true;
        changeCount();
    }

    private void changeCount(){
        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_COMMENT:
            case MethodType.METHOD_TYPE_ADD_REPLY:

                methodType = MethodType.METHOD_TYPE_GET_COMMENT;
                PAGENUM = 1;
                innerCommentPresenter.refreshFromNets(getParaMap());
                break;
            case MethodType.METHOD_TYPE_REMOVE_COMMENT:

                methodType = MethodType.METHOD_TYPE_GET_COMMENT;
                PAGENUM = 1;
                innerCommentPresenter.refreshFromNets(getParaMap());
                break;
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                eachCirclePresenter.getDataFromNets(getParaMap());
                break;
        }
    }

    @Override
    protected void initInject() {

        DaggerOurCommentComponent.builder()
                .appComponent(getAppComponent())
                .ourCommentMoudle(new OurCommentMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        linearLayout.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_press);
        }
        Intent intent = getIntent();
        if (intent != null) {
            innerCircleBean = intent.getParcelableExtra("innerCircleBean");
            circleTag = intent.getIntExtra("circle_tag",0);
            if (innerCircleBean != null) {
                if (actionBar != null) {
                    actionBar.setTitle(innerCircleBean.getUsername());
                }
                likeCount = innerCircleBean.getFab_count();
                transCount = innerCircleBean.getShare_count();
                circleid = innerCircleBean.getCircleid();
                circleUserId = innerCircleBean.getUserid();
                iHasFab = innerCircleBean.isHas_fab();

                if (innerCircleBean.isHas_fab()) {
                    Drawable likeLift = getResources().getDrawable(R.mipmap.like);
                    likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                    mLikeBtn.setCompoundDrawables(likeLift, null, null, null);
                }else {
                    Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
                    likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                    mLikeBtn.setCompoundDrawables(likeLift, null, null, null);
                }
            }
        }

        refreshLayout.setOnRefreshListener(this);

        initCircle();

        mTransTv.setOnClickListener(this);
        mLikeFabTv.setOnClickListener(this);
        mCommentTv.setOnClickListener(this);
        mLikeBtn.setOnClickListener(this);
        mCommentsBtn.setOnClickListener(this);
        mShareBtn.setOnClickListener(this);

        initRv();
    }

    private void initRv() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        myLmAdapter = new MyLmAdapter<InnerCircleBean>(this,true,1);

        myLmAdapter.setLayout(R.layout.item_comments_our);

        recyclerView.setAdapter(myLmAdapter);

        myLmAdapter.setOnHolderListener(this);
        myLmAdapter.setMyItemClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if (!myLmAdapter.isFadeTips() && lastVisibleItem + 1 == myLmAdapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {updateRecyclerView();
                            }
                        },500);
                    }

                    if (myLmAdapter.isFadeTips() && lastVisibleItem + 2 == myLmAdapter.getItemCount()){
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {updateRecyclerView();
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
    }

    // 上拉加载时调用的更新RecyclerView的方法
    private void updateRecyclerView() {

        methodType = MethodType.METHOD_TYPE_GET_COMMENT;
        innerCommentPresenter.loadMoreData(getParaMap());
    }
    @Override
    public String getMethod() {
        String method ="";

        switch (methodType){
            case MethodType.METHOD_TYPE_GET_COMMENT:
                method = "/app/circle_comment/get_comment";
                break;
            case MethodType.METHOD_TYPE_REMOVE_COMMENT:
                method = "/app/circle_comment/remove_comment";
                break;
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                method = "/app/circle_comment/add_comment";
                break;
            case MethodType.METHOD_TYPE_ADD_FAB:
                method = "/app/circle_fab/add_fab";
                break;
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                method = "/app/circle_fab/remove_fab";
                break;
            case MethodType.METHOD_TYPE_ADD_REPLY:
                method = "/app/circle_comment/add_reply";
                break;
            case MethodType.METHOD_TYPE_CIRCLE_DETAIL:
                method = "/app/circle/get_circle_detail";
                break;
        }
        return method;
    }


    private void initCircle() {

        if (innerCircleBean.getUsername() != null) {
            userNameTv.setText(innerCircleBean.getUsername());
        }

        if (innerCircleBean.getContent() != null && "".equals(innerCircleBean.getContent())){
            mContentTv.setVisibility(View.GONE);
        }else {
            mContentTv.setVisibility(View.VISIBLE);
        }

        String headpic = ConstantUtils.HEADPIC;
        if (innerCircleBean.getHeadpic() != null && !"".equals(innerCircleBean.getHeadpic()) && !"-1".equals(innerCircleBean.getHeadpic())){
            headpic += innerCircleBean.getHeadpic();

            mUserIconCv.setTag(R.id.imageid,headpic);
            Glide.with(this)
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(mUserIconCv);

        }else {
           mUserIconCv.setImageResource(R.mipmap.btn_img_photo_default);
        }

        if (innerCircleBean.getShare_count() != 0) {
            mTransTv.setText("转发 "+innerCircleBean.getShare_count());
        }

        if (innerCircleBean.getComment_count() != 0) {
            mCommentTv.setText("评论 "+innerCircleBean.getComment_count());
        }
        if (innerCircleBean.getFab_count() != 0){
            mLikeFabTv.setText("赞 "+innerCircleBean.getFab_count());
        }

        mUserIconCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (innerCircleBean.isIs_friend() || innerCircleBean.getUserid().equals(mUserObjId)){
                    Intent intent = new Intent(EarchCircleActivity.this,CircleDetialActivity.class);
                    intent.putExtra("friendid",innerCircleBean.getUserid());
                    intent.putExtra("name",innerCircleBean.getUsername());
                    intent.putExtra("innerCircleBean",innerCircleBean);
                    startActivity(intent);
                }
            }
        });

        if (innerCircleBean.getTrans_userid() != null && !"".equals(innerCircleBean.getTrans_userid()) && !"-1".equals(innerCircleBean.getTrans_userid())) {
            transpondFl.setVisibility(View.VISIBLE);

            mRecyclerView.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){

                String content = innerCircleBean.getContent();

                String[] contents = content.split("#");
                mTranContentTv.setText(contents[contents.length - 1]);

                Log.e("mTranContentTv",content+"--------"+innerCircleBean.getUsername()+"--------"+innerCircleBean.getTrans_name());

                if (content.lastIndexOf("#") != -1) {
                    mContentTv.setText(content.substring(0,content.lastIndexOf("#")));
                }else {
                    mContentTv.setText("转发动态");
                }

            }

            if (!"".equals(innerCircleBean.getTrans_name()) && innerCircleBean.getTrans_name() != null ){
                mTranName.setText("@" + String.valueOf(innerCircleBean.getTrans_name()));
            }

            if (innerCircleBean.getPics() != null && innerCircleBean.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = innerCircleBean.getPics().size();

                selectedPhotos.addAll(innerCircleBean.getPics());

                mTranRv.removeAllViews();

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(EarchCircleActivity.this, selectedPhotos);

                mTranRv.setVisibility(View.VISIBLE);
                mTranRv.setAdapter(photoAdapter);
                mTranRv.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) mTranRv.getItemAnimator()).setSupportsChangeAnimations(false);

                photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        MyPhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .setShowDeleteButton(false)
                                .start(EarchCircleActivity.this);
                    }
                });
            }else {
                mTranRv.removeAllViews();
                mTranRv.setVisibility(View.GONE);
            }

        }else {

            transpondFl.setVisibility(View.GONE);


            if (!TextUtils.isEmpty(innerCircleBean.getContent()) && !"".equals(innerCircleBean.getContent())){
                mContentTv.setText(innerCircleBean.getContent());
            }

            if (innerCircleBean.getPics() != null && innerCircleBean.getPics().size() != 0){

                final ArrayList<String> selectedPhotos = new ArrayList<>();
                selectedPhotos.clear();

                int picCount = innerCircleBean.getPics().size();

                selectedPhotos.addAll(innerCircleBean.getPics());

                mRecyclerView.removeAllViews();

                CirclePhotoAdapter  photoAdapter = new CirclePhotoAdapter(EarchCircleActivity.this, selectedPhotos);

                mRecyclerView.setVisibility(View.VISIBLE);
                mRecyclerView.setAdapter(photoAdapter);
                mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ?2:3, OrientationHelper.VERTICAL));

                ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

                photoAdapter.setMyItemClickListener(new MyItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        MyPhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .setShowDeleteButton(false)
                                .start(EarchCircleActivity.this);
                    }
                });
            }else {
                mRecyclerView.removeAllViews();
                mRecyclerView.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(innerCircleBean.getCreate_time()) && !"".equals(innerCircleBean.getCreate_time())) {
           mCreateTimeTv.setText(innerCircleBean.getCreate_time());
        }
    }

        @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PAGENUM = 1;
        methodType = MethodType.METHOD_TYPE_GET_COMMENT;
        innerCommentPresenter.refreshFromNets(getParaMap());
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        map.put(MethodParams.PARAMS_USER_OBJ,mUserObjId);
        map.put(MethodParams.PARAMS_TOKEN,mToken);

        switch (methodType){
            case MethodType.METHOD_TYPE_CIRCLE_DETAIL:
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                break;
            case MethodType.METHOD_TYPE_GET_COMMENT:
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                map.put(MethodParams.PARAMS_CP,String.valueOf(PAGENUM));
                map.put(MethodParams.PARAMS_PS,String.valueOf(10));
                break;

            case MethodType.METHOD_TYPE_ADD_COMMENT:
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                map.put(MethodParams.PARAMS_CONTENT,commentContent);
                break;
            case MethodType.METHOD_TYPE_REMOVE_COMMENT:
                map.put(MethodParams.PARAMS_COMMENT_ID,commentid);
                break;
            case MethodType.METHOD_TYPE_ADD_REPLY:
                map.put(MethodParams.PARAMS_COMMENT_ID,commentid);
                map.put(MethodParams.PARAMS_CONTENT,commentContent);
                map.put(MethodParams.PARAMS_CIRCLE_ID,circleid);
                break;
        }

        Log.e(TAG,map.toString()+"----"+TAG);
        return map;
    }

    private void changeRxBus(){
//        mRxBus.post("load_circle",0);
//        mRxBus.post("load_circle",1);
//        mRxBus.post("load_circle",2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.comment_friend_comments:

                // 评论 或者 回复

                Log.e("fafafasdf","评论---");

                showMyDialog(false,null);

                break;

            case R.id.comment_friend_dislike:

                //  点赞 或 取消赞
                Log.e("fafafasdf","点赞---");

                if (iHasFab) {

                    methodType = MethodType.METHOD_TYPE_REMOVE_FAB;
                    ourCodePresenter.removeFab(getParaMap());

                }else {
                    methodType = MethodType.METHOD_TYPE_ADD_FAB;
                    ourCodePresenter.addFab(getParaMap());
                }

                break;
            case R.id.comment_friend_share:

                mShareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EarchCircleActivity.this,TransCircleActivity.class);
                        intent.putExtra("innerCircleBean",innerCircleBean);
                        startActivity(intent);
                    }
                });

                break;
            case R.id.each_share:
                Log.e("earch","转发");
                if (transCount != 0) {
                    //跳转 转发列表界面
                }
                break;
            case R.id.each_fab:
                Log.e("earch","点赞");

                //跳转 点赞列表界面

                if (likeCount > 0) {
                    Intent intent = new Intent(EarchCircleActivity.this,FabActivity.class);
                    intent.putExtra("circleid",innerCircleBean.getCircleid());
                    intent.putExtra("innerCircleBean",innerCircleBean);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void toInitHolder(CommentHolder holder, int position, InnerComment data) {

        if (data.getContent() == null || "".equals(data.getContent())){
            holder.mTextContentTv.setVisibility(View.GONE);
        }else {
            holder.mTextContentTv.setVisibility(View.VISIBLE);
        }

        if (!"".equals(data.getUsername()) && data.getUsername() != null ){
            holder.mTextNameTv.setText(String.valueOf(data.getUsername()));
        }

        if (!TextUtils.isEmpty(data.getContent()) && !"".equals(data.getContent())){

            if (data.getReply() != null && "true".equals(data.getReply())){
                holder.mTextContentTv.setText("@"+data.getTo_username()+" : "+data.getContent());
            }else {
                holder.mTextContentTv.setText(data.getContent());
            }
        }

        if (!TextUtils.isEmpty(data.getCreate_time()) && !"".equals(data.getCreate_time())) {
            holder.mTimeTv.setText(data.getCreate_time());
        }

        String headpic = ConstantUtils.HEADPIC;
        if (data.getHeadpic() != null && !"".equals(data.getHeadpic()) && !"-1".equals(data.getHeadpic())){
            if (data.getHeadpic().startsWith("http")){
                headpic = data.getHeadpic();
            }else {
                headpic += data.getHeadpic();
            }

            String tag = (String) holder.civ.getTag(R.id.imageid);

            if (!TextUtils.equals(headpic,tag)){
                holder.civ.setImageResource(R.mipmap.btn_img_photo_default);
            }
            holder.civ.setTag(R.id.imageid,headpic);
            Glide.with(EarchCircleActivity.this)
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(holder.civ);

        }else {
            holder.civ.setTag(R.id.imageid,"");
            holder.civ.setImageResource(R.mipmap.btn_img_photo_default);
        }
    }

    @Override
    public void onItemClick(View view, int position) {

        InnerComment innerComment = innerComments.get(position);

        Log.e("innerComment",innerComment.getContent() +"----"+innerComment.getReply());
        Log.e("innerComment",mUserObjId+"-------"+circleUserId);

        if (mUserObjId.equals(circleUserId)){
            // 如果是自己朋友圈中的评论或者回复，可进行 回复或者删除操作，否则只能进行回复或评论
            showDownDialog(innerComment);

        }else {
            circleid = innerComment.getCircleid();
            showMyDialog(true,innerComment);
        }
    }

    @Override
    public void toUpdateEach(EachCircleBean data) {

        if (data.getData() != null) {
            InnerCircleBean innerCircleBean = data.getData();
            fabSize = innerCircleBean.getFab_count();
            if (fabSize != likeCount){
                likeCount = fabSize;
                mLikeFabTv.setText(fabSize == 0?"赞":"赞 "+fabSize);

                innerCircleBean.setFab_count(fabSize);
                MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBean);

                changeRxBus();
            }

            if (fabSize == 0){
                Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                mLikeBtn.setCompoundDrawables(likeLift, null, null, null);
            }

            iHasFab = innerCircleBean.isHas_fab();

            if (innerCircleBean.isHas_fab()) {
                Drawable likeLift = getResources().getDrawable(R.mipmap.like);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                mLikeBtn.setCompoundDrawables(likeLift, null, null, null);
            }else {
                Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                mLikeBtn.setCompoundDrawables(likeLift, null, null, null);
            }
        }
    }

    public void showMyDialog(final boolean isReply, InnerComment innerComment) {

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

        if (isReply) {
            mEtDialog.setHint("回复 @"+innerComment.getUsername());
        }
        commentSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commentContent = mEtDialog.getText().toString().trim();

                if (!TextUtils.equals("",commentContent)) {

                    Log.e("OurCommentBean",isReply+"------回复");

                    if (isReply) {

                        methodType = MethodType.METHOD_TYPE_ADD_REPLY;
                        ourCodePresenter.addReply(getParaMap());
                    }else {
                        methodType = MethodType.METHOD_TYPE_ADD_COMMENT;
                        ourCodePresenter.addComment(getParaMap());
                    }
                }
                mDialogEt.dismiss();
            }
        });

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
    }
    private void showDownDialog(final InnerComment innerComment){
        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.personal_down_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mRemoveTv = (TextView) view.findViewById(R.id.headciv_dialog_remove_attention);
        TextView mShouTv = (TextView) view.findViewById(R.id.headciv_dialog_shou);
        TextView mDeleteTv = (TextView) view.findViewById(R.id.headciv_dialog_delete);
        TextView mCancle = (TextView) view.findViewById(R.id.headciv_dialog_cancle);
        view.findViewById(R.id.view_line1).setVisibility(View.GONE);

        mRemoveTv.setText("回复@"+innerComment.getUsername());
        mShouTv.setVisibility(View.GONE);

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                methodType = MethodType.METHOD_TYPE_ADD_REPLY;
                circleid = innerComment.getCircleid();
                commentid = innerComment.getCommentid();
                showMyDialog(true,innerComment);

                mDialog.dismiss();
            }
        });
        mDeleteTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                methodType = MethodType.METHOD_TYPE_REMOVE_COMMENT;

                commentid = innerComment.getCommentid();

                deleteComment();

                mDialog.dismiss();
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
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

    private void deleteComment(){
         final CustomDialog dialog = new CustomDialog(this, "删除评论", "确定要删除所选评论?", "确定", "取消");
        dialog.setCancelable(true);
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {

                if (!ApiUtils.isNetworkConnected(EarchCircleActivity.this)) {

                    ApiUtils.showToast(EarchCircleActivity.this, getString(R.string.net_conn_2));
                    return;
                }
                ourCodePresenter.removeComment(getParaMap());
                dialog.dismiss();
            }

            @Override
            public void doCancel() {
                dialog.dismiss();
            }
        });
    }
    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        changeRxBus();
        super.onBackPressed();
    }

    @Override
    public void updateCommentList(OurCommentBean data, String errorMsg, @DataLoadType.DataLoadTypeChecker int type) {

        if (refreshLayout.isRefreshing()){
            refreshLayout.finishRefresh(false);
        }else if (refreshLayout.isLoading()){
            refreshLayout.finishLoadmore(false);
        }

        if (data.getData() != null) {
            for (int i = 0; i < data.getData().size(); i++) {
                Log.e("OurCommentBean",data.getData().get(i).getUsername()+"-------"+data.getData().get(i).getReply());
            }
        }
        switch (type) {
            case DataLoadType.TYPE_LOAD_MORE_FAIL:

                break;
            case DataLoadType.TYPE_REFRESH_FAIL:
                break;
            case DataLoadType.TYPE_LOAD_MORE_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0){

                    PAGENUM += 1;
                    myLmAdapter.addData(data.getData(),true);
                    innerComments.addAll(data.getData());

                }else if (data.getData().size() == 0){
                    myLmAdapter.addData(data.getData(),false);
                }

                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:

                if (data != null && data.getData() != null && data.getData().size() != 0) {

                    PAGENUM += 1;

                    myLmAdapter.updateList(data.getData(),data.getData().size() >= 10 );

                    innerComments.clear();
                    innerComments.addAll(data.getData());
                }else {
                    myLmAdapter.updateList(new ArrayList<InnerComment>(),false );

                    innerComments.clear();
                }
                break;
        }

        innerCircleBean.setComment_count(innerComments.size());

        Log.e("faamap99999","id---2----onItemClick----"+ innerCircleBean.getId());
        MyApplication.getDaoSession().getInnerCircleBeanDao().updateInTx(innerCircleBean);

        changeRxBus();
        mCommentTv.setText(innerComments.size() != 0? "评论 "+innerComments.size():"评论");
    }

    @Override
    public void onRefresh(RefreshLayout refreshLayout) {
        if (!ApiUtils.isNetworkConnected(this)) {
        } else {
            methodType = MethodType.METHOD_TYPE_GET_COMMENT;
            PAGENUM = 1;
            innerCommentPresenter.refreshFromNets(getParaMap());
        }
    }
}
