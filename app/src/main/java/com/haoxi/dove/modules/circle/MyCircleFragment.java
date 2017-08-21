package com.haoxi.dove.modules.circle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.MyPhotoPreview;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ytb.inner.widget.G;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;

public class MyCircleFragment extends BaseSrFragment implements IEachView<EachCircleBean>, IMyCircleView<CircleBean>,OnRefreshListener, OnLoadmoreListener, OnHolder2Listener<InnerCircleBean,CircleAdapter.MyRefrashHolder>, MyItemClickListener {

    private int methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;

    private int PAGENUM = 1;  //查询起始下标，默认为0

    private boolean isFriend = false;
    private boolean isLoad = true;
    private int tag = 2;
    private String headpic;

    private String circleid;
    private String friendId;
    private String commentContent;
    private int currentPosition;//当前操作的 position
    private TextView commentSub;
    private Handler mHandler = new Handler();
    private Dialog mDialogEt;
    private Observable<Integer> netObservale;


    private List<InnerCircleBean> innerCircleBeans = new ArrayList<>();

    @BindView(R.id.refreshLayout) SmartRefreshLayout refreshLayout;
    @BindView(R.id.bsr_rv) RecyclerView recyclerView;

    @Inject
    InnerCirclePresenter innerCirclePresenter;
    @Inject
    OurCodePresenter ourCodePresenter;

    EachCirclePresenter eachCirclePresenter;
    @Inject
    RxBus mRxBus;

    CircleAdapter circleAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        netObservale = mRxBus.register("load_circle", Integer.class);
        netObservale.subscribe(new Action1<Integer>() {
            @Override
            public void call(Integer integer) {
                if (integer == 2 ){
                    circleAdapter.getDatas().remove(currentPosition);
                    methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                    eachCirclePresenter.getDataFromNets(getParaMap());
                }
            }
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        eachCirclePresenter = new EachCirclePresenter(this);

        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setOnLoadmoreListener(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        circleAdapter = new CircleAdapter<InnerCircleBean>(getActivity(),0);
        recyclerView.setAdapter(circleAdapter);
        circleAdapter.setOnHolderListener(this);
        circleAdapter.setMyItemClickListener(this);
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
                    PAGENUM += 1;
                    circleAdapter.addData(data.getData());
                    innerCircleBeans.addAll(data.getData());
                }
                break;
            case DataLoadType.TYPE_REFRESH_SUCCESS:
                if (data != null && data.getData() != null && data.getData().size() != 0) {
                    PAGENUM += 1;
                    circleAdapter.updateList(data.getData());
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

        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
            case MethodType.METHOD_TYPE_ADD_COMMENT:
                circleAdapter.getDatas().remove(currentPosition);
                methodType = MethodType.METHOD_TYPE_CIRCLE_DETAIL;
                eachCirclePresenter.getDataFromNets(getParaMap());
                break;
            case MethodType.METHOD_TYPE_DELETE_SINGEL:
                circleAdapter.getDatas().remove(currentPosition);
                circleAdapter.notifyDataSetChanged();
                innerCircleBeans.remove(currentPosition);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isLoad) {
            PAGENUM = 1;
            methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
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
        methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
        innerCirclePresenter.loadMoreData(getParaMap(),0);
    }

    public void getDatas() {
        if (ApiUtils.isNetworkConnected(getActivity())) {
            innerCirclePresenter.refreshFromNets(getParaMap(),tag);
        }
    }

    @Override
    public void onRefresh(RefreshLayout refreshlayout) {
        if (ApiUtils.isNetworkConnected(getActivity())) {
            PAGENUM = 1;
            methodType = MethodType.METHOD_TYPE_SINGLE_CIRCLES;
            innerCirclePresenter.refreshFromNets(getParaMap(),0);
        }
    }

    @Override
    public String getMethod() {
        String method ="";
        switch (methodType){
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                method = MethodConstant.GET_SINGLE_FRIEND_CIRCLES;
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
            case MethodType.METHOD_TYPE_DELETE_SINGEL:
                method = MethodConstant.DELETE_SINGLE_CIRCLE;
                break;
            case MethodType.METHOD_TYPE_CIRCLE_DETAIL:
                method = MethodConstant.GET_CIRCLE_DETAIL;
                break;
        }
        return method;
    }

    protected Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();
        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        map.put(MethodParams.PARAMS_USER_OBJ,getUserObjId());
        map.put(MethodParams.PARAMS_TOKEN,getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_SINGLE_CIRCLES:
                map.put(MethodParams.PARAMS_FRIEND_ID,getUserObJId());
                map.put(MethodParams.PARAMS_CP,String.valueOf(PAGENUM));
                map.put(MethodParams.PARAMS_PS,String.valueOf(10));
                break;
            case MethodType.METHOD_TYPE_SHARE_CIRCLE:
            case MethodType.METHOD_TYPE_ADD_FAB:
            case MethodType.METHOD_TYPE_REMOVE_FAB:
            case  MethodType.METHOD_TYPE_CIRCLE_DETAIL:
            case  MethodType.METHOD_TYPE_DELETE_SINGEL:
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
    public void toInitHolder(final CircleAdapter.MyRefrashHolder holder, final int position, final InnerCircleBean data) {
            if (data.getTrans_userid() != null && !"".equals(data.getTrans_userid()) && !"-1".equals(data.getTrans_userid())) {
                 holder.transpondFl.setVisibility(View.VISIBLE);

                holder.mRecyclerView.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(data.getContent()) && !"".equals(data.getContent())) {
                    String content = data.getContent();
                    String[] contents = content.split("#");
                    holder.mTranContentTv.setText(contents[contents.length - 1]);
                    if (content.lastIndexOf("#") != -1) {
                        holder.mContentTv.setText(content.substring(0, content.lastIndexOf("#")));
                    } else {
                        holder.mContentTv.setText("转发动态");
                    }
                }
                if (!"".equals(data.getTrans_name()) && data.getTrans_name() != null) {
                    holder.mTranName.setText("@" + String.valueOf(data.getTrans_name()));
                }
                if (data.getPics() != null && data.getPics().size() != 0) {
                    final ArrayList<String> selectedPhotos = new ArrayList<>();
                    selectedPhotos.clear();
                    int picCount = data.getPics().size();
                    selectedPhotos.addAll(data.getPics());
                    holder.mTranRv.removeAllViews();
                    CirclePhotoAdapter photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);
                    holder.mTranRv.setVisibility(View.VISIBLE);
                    holder.mTranRv.setAdapter(photoAdapter);
                    holder.mTranRv.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ? 2 : 3, OrientationHelper.VERTICAL));
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
                } else {
                    holder.mTranRv.removeAllViews();
                    holder.mTranRv.setVisibility(View.GONE);
                }

                    holder.mAddFriendBtn.setVisibility(View.GONE);
                    holder.mDownIv.setVisibility(View.GONE);

            } else {

               holder.transpondFl.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(data.getContent()) && !"".equals(data.getContent())) {
                   holder.mContentTv.setText(data.getContent());
                }
                if (data.getPics() != null && data.getPics().size() != 0) {
                    final ArrayList<String> selectedPhotos = new ArrayList<>();
                    selectedPhotos.clear();

                    int picCount = data.getPics().size();

                    selectedPhotos.addAll(data.getPics());

                    holder.mRecyclerView.removeAllViews();

                    CirclePhotoAdapter photoAdapter = new CirclePhotoAdapter(getContext(), selectedPhotos);

                     holder.mRecyclerView.setVisibility(View.VISIBLE);
                     holder.mRecyclerView.setAdapter(photoAdapter);
                     holder.mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(picCount < 3 ? 2 : 3, OrientationHelper.VERTICAL));

                    ((SimpleItemAnimator)holder.mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

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
                } else {
                    holder.mRecyclerView.removeAllViews();
                    holder.mRecyclerView.setVisibility(View.GONE);
                }
            }

            if (data.isIs_friend() || tag == 1 || tag == 2) {
                holder.mAddFriendBtn.setVisibility(View.GONE);
                holder.mDownIv.setVisibility(View.VISIBLE);
            } else {
                holder.mAddFriendBtn.setVisibility(View.VISIBLE);
                holder.mDownIv.setVisibility(View.GONE);
            }
            if (data.isHas_fab()) {
                Drawable likeLift = getResources().getDrawable(R.mipmap.like);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
            } else {
                Drawable likeLift = getResources().getDrawable(R.mipmap.dislike);
                likeLift.setBounds(0, 0, likeLift.getMinimumWidth(), likeLift.getMinimumHeight());
                holder.mPraiseBtn.setCompoundDrawables(likeLift, null, null, null);
            }

            if (data.getShare_count() != 0) {
                holder.mTranspondBtn.setText(getString(R.string.share_circle) + data.getShare_count());
            } else {
                holder.mTranspondBtn.setText(getString(R.string.share_circle));
            }

            if (data.getComment_count() != 0) {
                holder.mCommentBtn.setText(getString(R.string.comment_circle) + data.getComment_count());
            } else {
               holder.mCommentBtn.setText(getString(R.string.comment_circle));
            }
            if (data.getFab_count() != 0) {
                holder.mPraiseBtn.setText(getString(R.string.fab_circle) + data.getFab_count());
            } else {
                holder.mPraiseBtn.setText(getString(R.string.fab_circle));
            }

            if (data.getContent() == null || "".equals(data.getContent())) {
               holder.mContentTv.setVisibility(View.GONE);
            } else {
                holder.mContentTv.setVisibility(View.VISIBLE);
            }

            if (!"".equals(data.getUsername()) && data.getUsername() != null) {
                holder.mUserName.setText(String.valueOf(data.getUsername()));
            }

            headpic = ConstantUtils.HEADPIC;
            if (data.getHeadpic() != null && !"".equals(data.getHeadpic()) && !"-1".equals(data.getHeadpic())) {
                headpic += data.getHeadpic();

                String tag = (String) holder.mUserIcon.getTag(R.id.imageid);

                if (!TextUtils.equals(headpic, tag)) {
                    holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
                }

                holder.mUserIcon.setTag(R.id.imageid, headpic);
                Glide.with(getContext())
                        .load(headpic)
                        .dontAnimate()
                        .placeholder(R.mipmap.btn_img_photo_default)
                        .error(R.mipmap.btn_img_photo_default)
                        .into(holder.mUserIcon);

            } else {
                holder.mUserIcon.setTag(R.id.imageid, "");
                holder.mUserIcon.setImageResource(R.mipmap.btn_img_photo_default);
            }

           holder.mUserIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;
                    if (data.isIs_friend() || data.getUserid().equals(getUserObJId())) {
                        Intent intent = new Intent(getActivity(), CircleDetialActivity.class);
                        intent.putExtra("friendid", data.getUserid());
                        intent.putExtra("name", data.getUsername());
                        intent.putExtra("innerCircleBean", data);
                        intent.putExtra("current_position", data);
                        intent.putExtra("circle_tag", tag);
                        startActivity(intent);
                    }
                }
            });

            if (!TextUtils.isEmpty(data.getCreate_time()) && !"".equals(data.getCreate_time())) {
                holder.mCreateTimeTv.setText(data.getCreate_time());
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


           holder.mAddFriendBtn.setVisibility(View.GONE);

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
                    } else {
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

                    if (data.getComment_count() != 0) {
                        Intent intent = new Intent(getActivity(), EarchCircleActivity.class);
                        intent.putExtra("innerCircleBean", innerCircleBeans.get(currentPosition));
                        intent.putExtra("circle_tag", tag);
                        startActivity(intent);
                    } else {
                        showMyDialog();
                    }
                }
            });

            holder.mTranspondBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentPosition = position;

                    circleid = data.getCircleid();
                    Intent intent = new Intent(getActivity(), TransCircleActivity.class);
                    intent.putExtra("innerCircleBean", data);
                    intent.putExtra("circle_tag", tag);
                    startActivity(intent);
                }
            });
        }
    @Override
    public void onItemClick(View view, int position) {
        currentPosition = position;
        if (innerCircleBeans.get(position) != null) {

            circleid = innerCircleBeans.get(position).getCircleid();

            Intent intent = new Intent(getActivity(),EarchCircleActivity.class);
            intent.putExtra("innerCircleBean",innerCircleBeans.get(position));
            intent.putExtra("circle_tag",tag);
            startActivity(intent);
        }
    }

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
            mDialogEt.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ApiUtils.setDialogWindow(mDialogEt);
            mDialogEt.show();
        }

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

        circleAdapter.getDatas().add(currentPosition,circleBean);
        circleAdapter.notifyDataSetChanged();

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
}
