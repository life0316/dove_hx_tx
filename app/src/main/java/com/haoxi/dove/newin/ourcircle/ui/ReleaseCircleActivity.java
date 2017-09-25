package com.haoxi.dove.newin.ourcircle.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.adapter.PhotoAdapter;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.callback.RecyclerItemClickListener;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.AddOurCircleMoudle;
import com.haoxi.dove.inject.DaggerAddOurCircleComponent;
import com.haoxi.dove.modules.mvp.presenters.PersonalPresenter;
import com.haoxi.dove.modules.mvp.views.IAddDynamicView;
import com.haoxi.dove.modules.mvp.views.IUpdateImageView;
import com.haoxi.dove.newin.bean.UploadImageBean;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.widget.CustomDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by lifei on 2017/4/25.
 */
@ActivityFragmentInject(contentViewId = R.layout.activity_release)
public class ReleaseCircleActivity extends BaseActivity implements IAddDynamicView,IUpdateImageView {

    @BindView(R.id.custom_toolbar_keep)
    TextView mRigthTv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_left_tv)
    TextView mLeftTv;

    @BindView(R.id.edit_content)
    EditText mContentEt;

    @BindView(R.id.act_release_rv)
    RecyclerView mRv;

    @Inject
    PersonalPresenter presenter;

    @Inject
    OurCodePresenter ourCodePresenter;

    private int methodType;
    private int type = 1;//  1 代表纯文字，  2  代表图片 + 文字  3 代表 视频 + 文字


    private String updatePhotoPath = "";

    private String addParams = "";

    private StringBuffer addPicSb = new StringBuffer();

    @Inject
    RxBus mRxBus;

    PhotoAdapter photoAdapter;

    ArrayList<String> selectedPhotos = new ArrayList<>();

    private static Handler mHandler = new Handler();
    private int tranUserCode;
    private String tranObjId;
    private String dynamicId;

    @Override
    protected void initInject() {
        DaggerAddOurCircleComponent.builder()
                .appComponent(getAppComponent())
                .addOurCircleMoudle(new AddOurCircleMoudle(this,this))
                .build()
                .inject(this);
    }


    private String editPwd;

    private Runnable delayRun = new Runnable() {
        @Override
        public void run() {

            if (editPwd == null || editPwd.length() == 0 || "".equals(editPwd) ) {

                Log.e("selectedPhotos",selectedPhotos.size()+"-------1");
                if (selectedPhotos.size() != 0) {
                    mRigthTv.setEnabled(true);
                    mRigthTv.setTextColor(Color.WHITE);
                }else {
                    mRigthTv.setTextColor(Color.GRAY);
                    mRigthTv.setEnabled(false);
                }
            } else {
                    mRigthTv.setEnabled(true);
                    mRigthTv.setTextColor(Color.WHITE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();
            Log.e("selectedPhotos",selectedPhotos.size()+"-------2");
            Log.e("selectedPhotos",(photos != null)+"-------3");

            if (photos != null) {
                selectedPhotos.addAll(photos);

                if (selectedPhotos.size() == 0) {
                    mHandler.postDelayed(delayRun, 500);
                }else {
                    mRigthTv.setEnabled(true);
                    mRigthTv.setTextColor(Color.WHITE);
                }

                for (int i = 0; i < selectedPhotos.size(); i++) {
                    Log.e("selectedphote", selectedPhotos.get(i));
                    ///storage/emulated/0/com.kingroot.master/notifyPic/km_28.jpg.png
                    ///storage/emulated/0/com.kingroot.master/notifyPic/km_28.jpg.png
                }
            }else {
                mHandler.postDelayed(delayRun, 300);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    private boolean tranTag = false;

    @Override
    protected void init() {
        mLeftTv.setVisibility(View.VISIBLE);
        mRigthTv.setVisibility(View.VISIBLE);
        mRigthTv.setTextColor(Color.GRAY);

        mTitleTv.setVisibility(View.GONE);

        Intent intent = getIntent();
        if ("dynamic".equals(intent.getAction())){

            tranTag = true;

//            InterMyDynamic interMyDynamic = intent.getParcelableExtra("interMyDynamic");
//
//            tranUserCode = interMyDynamic.getUSER_CODE();
//
//            dynamicId = interMyDynamic.getDYNAMIC_ID();
//
//            tranObjId = interMyDynamic.getUSER_OBJ_ID();
//
//            mContentEt.setText("@"+tranUserCode+"//"+interMyDynamic.getCONTENT());

            mContentEt.setSelection(0);

            mRigthTv.setEnabled(true);
            mRigthTv.setTextColor(Color.WHITE);

        }

        mRigthTv.setText(tranTag? "转发":"发布");


        photoAdapter = new PhotoAdapter(this, selectedPhotos);

        mRv.setLayoutManager(new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL));

        mRv.setAdapter(photoAdapter);

        mRv.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (photoAdapter.getItemViewType(position) == PhotoAdapter.TYPE_ADD) {
                    PhotoPicker.builder()
                            .setPhotoCount(PhotoAdapter.MAX)
//                            .setPhotoCount(5)
                            .setShowCamera(true)
                            .setShowGif(false)
                            .setPreviewEnabled(true)
                            .setSelected(selectedPhotos)
                            .start(ReleaseCircleActivity.this);
                } else {
                    PhotoPreview.builder()
                            .setPhotos(selectedPhotos)
                            .setCurrentItem(position)
                            .start(ReleaseCircleActivity.this);
                }
            }
        }));


        mContentEt.addTextChangedListener(new TextWatcher() {
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

    @Override
    public void toDo() {
        tranTag = false;
        mRxBus.post(ConstantUtils.OBSER_LOAD_CIRCLE,4);
        ReleaseCircleActivity.this.finish();

    }

    @Override
    public String getMethod() {
        String method = "" ;

        switch (methodType){
            case MethodType.METHOD_TYPE_ADD_CIRCLE:
                method = MethodConstant.CIRCLE_ADD;
                break;
            case MethodType.METHOD_TYPE_IMAGE_UPLOAD:
                method = MethodConstant.IMAGE_UPLOAD;
                break;
        }
        return method;
    }

    @OnClick(R.id.custom_toolbar_keep)
    void releaseMsg() {
        if (TextUtils.equals("",getContent()) && selectedPhotos.size() == 0){
            return;
        }

       type = selectedPhotos.size() > 0 ? 2:1;

        switch (type){
            case 1:
                methodType = MethodType.METHOD_TYPE_ADD_CIRCLE;
                ourCodePresenter.addCircle(getParaMap());
                break;
            case 2:
                addPicSb.setLength(0);
                picLists.clear();
                picCount = 0;

                methodType = MethodType.METHOD_TYPE_IMAGE_UPLOAD;

                for (int i = 0; i < selectedPhotos.size(); i++) {
                    updatePhotoPath = selectedPhotos.get(i);
                    presenter.uploadImage(getParaMap2());
                }

                break;
            case 3:
                break;
        }
    }

    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",getToken());
        map.put("type",String.valueOf(type));

        switch (type){
            case 1:
                map.put("content",getContent());
                break;
            case 2:
                map.put("content",getContent());
                map.put("pics",getPicsStr());
                break;
            case 3:
                map.put("content",getContent());
                break;
        }
        return map;
    }

    public Map<String, RequestBody> getParaMap2(){

        RequestBody methodBody = RequestBody.create(MediaType.parse("text/plain"),getMethod());
        RequestBody signBody = RequestBody.create(MediaType.parse("text/plain"),getSign());
        RequestBody timeBody = RequestBody.create(MediaType.parse("text/plain"),getTime());
        RequestBody versionBody = RequestBody.create(MediaType.parse("text/plain"),getVersion());
        RequestBody objIdBody = RequestBody.create(MediaType.parse("text/plain"),mUserObjId);
        RequestBody tokenBody = RequestBody.create(MediaType.parse("text/plain"),getToken());

        Map<String,RequestBody> map = new HashMap<>();
        map.put("method",methodBody);
        map.put("sign",signBody);
        map.put("time",timeBody);
        map.put("version",versionBody);
        map.put("userid",objIdBody);
        map.put("token",tokenBody);
        File file = new File(updatePhotoPath);
        if (file != null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
            map.put("file\"; filename=\""+file.getName()+"", fileBody);
        }
        return map;
    }

    @OnClick(R.id.custom_toolbar_left_tv)
    void cancle(View v) {
        final CustomDialog dialog = new CustomDialog(this, "删除鸽环", "退出本次编辑?", "确定", "取消");
        dialog.setCancelable(true);
        dialog.show();
        dialog.setClickListenerInterface(new CustomDialog.ClickListenerInterface() {
            @Override
            public void doConfirm() {
                dialog.dismiss();
                ReleaseCircleActivity.this.finish();
            }

            @Override
            public void doCancel() {
                dialog.dismiss();
            }
        });
    }

    @Override
    public String getUserObjIds() {
        return mUserObjId;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public String getContent() {

        String mContent = mContentEt.getText().toString();

        return mContent==null?"":mContent;
    }

    private List<String> picLists = new ArrayList<>();
    private int picCount = 0;

    @Override
    public void toUploadHeadPic(UploadImageBean uploadImageBean) {

     String  updateHeadpic = uploadImageBean.getData();
        if ((picCount < (selectedPhotos.size()))) {
            picCount ++;
            picLists.add(updateHeadpic);
        }

        if (picCount == selectedPhotos.size()){
            methodType = MethodType.METHOD_TYPE_ADD_CIRCLE;
            ourCodePresenter.addCircle(getParaMap());
        }
    }
    public String getPicsStr(){
        for (int i = 0; i < picLists.size(); i++) {
            String picStr = picLists.get(i);
            if (i == (picLists.size() - 1)) {
                addPicSb.append(picStr);
            }else {
                addPicSb.append(picStr);
                addPicSb.append(",");
            }
        }
        return addPicSb.toString();
    }
}
