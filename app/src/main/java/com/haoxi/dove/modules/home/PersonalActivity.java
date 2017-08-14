package com.haoxi.dove.modules.home;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.bean.DaoSession;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerPersonalComponent;
import com.haoxi.dove.inject.PersonalMoudle;
import com.haoxi.dove.modules.mvp.views.IPersonalView;
import com.haoxi.dove.modules.mvp.presenters.PersonalPresenter;
import com.haoxi.dove.modules.mvp.views.IUpdateImageView;
import com.haoxi.dove.retrofit.MethodType;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.newin.bean.UploadImageBean;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;
import com.google.zxing.DecodeHintType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.iwf.photopicker.utils.ImageCaptureManager;
import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by lifei on 2017/1/13.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_personal)
public class PersonalActivity extends BaseActivity implements IPersonalView,IUpdateImageView {

    private static final String TAG = "PersonalActivity";
    private int methodType = MethodType.METHOD_TYPE_IMAGE_UPLOAD;

    @BindView(R.id.activity_personal_dovecote_name)
    TextView        mDoveNameTv;
    @BindView(R.id.activity_personal_civ)
    CircleImageView mCiv;
    @BindView(R.id.activity_personal_usernumber)
    TextView        mUserNumTv;
    @BindView(R.id.activity_personal_nick_name)
    TextView        mNickNameTv;
    @BindView(R.id.activity_personal_userphone)
    TextView        mUserPhoneTv;
    @BindView(R.id.activity_personal_age)
    TextView        mUserAgeTv;
    @BindView(R.id.activity_personal_sex)
    TextView        mUserSexTv;
    @BindView(R.id.activity_personal_year)
    TextView        mPigeonYearTv;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;
    @BindView(R.id.custom_toolbar_tv)
    TextView  mTitleTv;

    private String  tagMsg      = "拍照权限";
    private Bitmap mScanBitmap;

    @Inject
    PersonalPresenter presenter;

    @Inject
    RxBus mRxBus;
    @Inject
    DaoSession daoSession;

    @Inject
    OurCodePresenter ourCodePresenter;


    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;

    private ImageCaptureManager captureManager;


    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA

    };
    private String mUserYear="";
    private String userHeadpic;
    private Dialog mDialogEt;
    private String userAge;
    private String mUserBirth;
    private String mNickName;

    @Override
    protected void initInject() {

        DaggerPersonalComponent.builder()
                .appComponent(getAppComponent())
                .personalMoudle(new PersonalMoudle(this,this))
                .build()
                .inject(this);
    }

    @Override
    protected void init() {

        captureManager = new ImageCaptureManager(this);

        mBackIv.setVisibility(View.VISIBLE);
        mTitleTv.setText("帐号管理");
        preferences = getSharedPreferences(ConstantUtils.USERINFO, Context.MODE_PRIVATE);
        editor = preferences.edit();

        userHeadpic = preferences.getString("user_headpic","");
        String mDovecoteName = preferences.getString("user_dovecote", "");
        mNickName = preferences.getString("nick_name","");
        String mUserPhone = preferences.getString("user_phone", "");
        String mUserCode = preferences.getString("user_code", "");
        String mUserAge = preferences.getString("user_age", "");
        mUserYear = preferences.getString("user_year", "1年");

        mUserBirth = preferences.getString("user_birth","");

        Log.e("afsdfe",mUserYear);
        String mUserSex = preferences.getString("user_sex", "");

        //readFromPreference();

        Log.e("headpic",userHeadpic+"-------home");

        mNickNameTv.setText(mNickName);

        if (!"".equals(userHeadpic)) {

//            GlideCircleImage circleImage = new GlideCircleImage(this);
//
//            Glide.with(this)
//                    .load(userHeadpic)
//                    .transform(circleImage)
////                    .fitCenter()
//                    .centerCrop()
//                    .thumbnail(0f)
////                    .placeholder(R.mipmap.btn_img_photo_default)
//                    .error(R.mipmap.btn_img_photo_default)
//                    .crossFade()
//                    .into(mCiv);

            if (userHeadpic.startsWith("http")){
                Glide.with(this)
                        .load(userHeadpic)
                        .asBitmap()
                        .placeholder(R.mipmap.btn_img_photo_default)
                        .error(R.mipmap.btn_img_photo_default)
//                    .into(mCiv);
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                mCiv.setImageBitmap(resource);
                            }
                        });
            }else {
                if (!userHeadpic.equals("")) {
                    byte[] byteArray = Base64.decode(userHeadpic, Base64.DEFAULT);
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);
                    //第三步:利用ByteArrayInputStream生成Bitmap
                    Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
                    mCiv.setImageBitmap(bitmap);
                }
            }
        }

        mDoveNameTv.setText(mDovecoteName);
        if (!"".equals(mUserSex)) {
            switch (mUserSex) {
                case "1":
                case "男":
                    mUserSexTv.setText("男");
                    break;
                case "2":
                case "女":
                    mUserSexTv.setText("女");
                    break;
            }
        }
        mPigeonYearTv.setText(mUserYear);
        mUserNumTv.setText(mUserCode);
        mUserPhoneTv.setText(mUserPhone);
        if (!"".equals(mUserAge)) {
            mUserAgeTv.setText(mUserAge+"岁");
        }
    }


    @OnClick(R.id.custom_toolbar_iv)
    void backOnCli(View v){
        mRxBus.post("isLoad",false);
        this.finish();
    }

    @OnClick(R.id.activity_personal_civ_rv)
    void civOnCli(View v){
        methodType = MethodType.METHOD_TYPE_IMAGE_UPLOAD;

        showHeadDialog();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case ConstantUtils.PERMISSION_REQUESTCODE_1:

                //没有授权

                if (!ApiUtils.verifyPermissions(grantResults)) {

                    isNeedCheck = true;
                }else {
                    takePhoto();
                    isNeedCheck = false;
                }

                break;
            case ConstantUtils.PERMISSION_REQUESTCODE_2:
                //没有授权

                if (!ApiUtils.verifyPermissions(grantResults)) {

                    isNeedCheck = true;
                }else {
                    choosePhoto();
                    isNeedCheck = false;
                }
                break;
        }
    }

    private void takePhoto() {

//        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        //文件夹aaaa
//        String path = Environment.getExternalStorageDirectory().toString()+"/pigeon";
//        File path1 = new File(path);
//        if(!path1.exists()){
//            path1.mkdirs();
//        }
//        File file = new File(path1,System.currentTimeMillis()+".png");
//        Uri mOutPutFileUri = Uri.fromFile(file);
//        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT,mOutPutFileUri);
//
//        startActivityForResult(takeIntent, 100);

        try {
            Intent intent = captureManager.dispatchTakePictureIntent();
            startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            // TODO No Activity Found to handle Intent
            e.printStackTrace();
        }
    }

    private void choosePhoto() {

        Intent innerIntent = new Intent(Intent.ACTION_PICK,null);
        innerIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
        innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(innerIntent, 200);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("requestCode",(resultCode== RESULT_CANCELED)+"-----");

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case 100:
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:

                    captureManager.galleryAddPic();

                    String takePath = captureManager.getCurrentPhotoPath();

                    Log.e("takephoto",takePath+"------path");

                    updatePhotoPath = takePath;
                    updatePamas = UPDATE_HEADPIC;
                    methodType = MethodType.METHOD_TYPE_IMAGE_UPLOAD;
                    presenter.uploadImage(getParaMap2());

//                    if (data != null) {
//
//                        Uri uri = data.getData();
//
//                        if (uri == null) {
//                            Bundle bundle = data.getExtras();
//                            if (bundle != null) {
//                                Bitmap photo = (Bitmap) bundle.get("data");
//                                if (photo != null) {
//
//                                    mCiv.setImageBitmap(photo);
//                                    saveToPreference(photo);
//
//                                }
//                            }
//                        }else {
//                            Log.e("fasdfawf",uri.getPath()+"-----uri");
//                        }
//                    }
                    break;

                case 200:

                    try {
                        if (data != null) {
                            Uri uri1 = data.getData();
                            final String absolutePath = ApiUtils.getAbsolutePath(PersonalActivity.this, uri1);
                            //setPicToView(absolutePath);
                            updatePhotoPath = absolutePath;
                            updatePamas = UPDATE_HEADPIC;
                            methodType = MethodType.METHOD_TYPE_IMAGE_UPLOAD;
                            presenter.uploadImage(getParaMap2());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private String updatePhotoPath;

    private void setPicToView(final String mPhotoPath) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mPhotoPath.isEmpty()){
                    return;
                }

                Hashtable<DecodeHintType,String> hints = new Hashtable<>();
                hints.put(DecodeHintType.CHARACTER_SET,"UTF8");//设置二维码内容的编码

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;//先获取原大小
                mScanBitmap = BitmapFactory.decodeFile(mPhotoPath, options);

                options.inJustDecodeBounds = false;//获取新的大小
                int sampleSize = (int)(options.outHeight/(float)200);
                if (sampleSize <= 0)
                    sampleSize = 1;
                options.inSampleSize = sampleSize;
                mScanBitmap = BitmapFactory.decodeFile(mPhotoPath,options);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCiv.setImageBitmap(mScanBitmap);
                        saveToPreference(mScanBitmap);
                    }
                });
            }
        }).start();

    }

    private void saveToPreference(Bitmap src){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        src.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        //第二步:利用Base64将字节数组输出流中的数据转换成字符串String
        byte[] byteArray=byteArrayOutputStream.toByteArray();
        String imageString=new String(Base64.encodeToString(byteArray, Base64.DEFAULT));

        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("user_headpic", imageString);
        editor.commit();
    }

    private void readFromPreference(){
        String mUserPVR = preferences.getString("user_pvr", "");

        if(mUserPVR.equals("") == false)
        {
            byte[] byteArray= Base64.decode(mUserPVR, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(byteArray);
            //第三步:利用ByteArrayInputStream生成Bitmap
            Bitmap bitmap=BitmapFactory.decodeStream(byteArrayInputStream);
            mCiv.setImageBitmap(bitmap);
        }
    }

    @OnClick(R.id.activity_personal_dovname_rl)
    void dovNameOnCli(View v){
        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        showMyDialog("loftname");
    }
    @OnClick(R.id.activity_personal_nickname_rl)
    void nickNameOnCli(View v){
        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        showMyDialog("nickname");
    }

    @OnClick(R.id.activity_personal_age_rl)
    void ageOnCli(View v){
        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        if (mUserBirth != null && !"".equals(mUserBirth) && !"null".equals(mUserBirth)) {
            showAgeDialog();
        }

//        Log.e("mmsmsmsm",mUserBirth+"-------"+(mUserBirth != null)+"--------"+ ( !"".equals(mUserBirth)));
//        Log.e("mmsmsmsm",mUserBirth+"-------"+("null".equals(mUserBirth)));
    }

    @OnClick(R.id.activity_personal_sex_rl)
    void sexOnCli(View v){
        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        setUserSex();
    }

    @OnClick(R.id.activity_personal_year_rl)
    void yearOnCli(View v){
        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        showYearDialog();
    }


    @Override
    public String getToken() {
        return mToken;
    }

    @Override
    public String getDovecoteName() {

        String mDovName = mDoveNameTv.getText().toString().trim();

        return mDovName;
    }

    @Override
    public String getUserSex() {

        String mUserSex = mUserSexTv.getText().toString().trim();
        return mUserSex;
    }

    @Override
    public int getFeedPigeonYear() {
        String mPigeonYear = mPigeonYearTv.getText().toString().trim();

        return Integer.parseInt(mPigeonYear);
    }

    @Override
    public void setUserHead() {

    }

    @Override
    public void setDovecoteName(String dovecoteName) {

        mDoveNameTv.setText(dovecoteName);
    }

    @Override
    public void setUserAge(String age) {
        mUserAgeTv.setText(age);
    }

    @Override
    public void setUserSex(String sex) {
        mUserSexTv.setText(sex);
    }

    @Override
    public void setFeedPigeonYear(int feedPigeonYear) {

        mPigeonYearTv.setText(feedPigeonYear+"年");
    }

    @Override
    public void toUploadHeadPic(UploadImageBean uploadImageBean) {

        methodType = MethodType.METHOD_TYPE_USER_UPDATE;
        updatePamas = UPDATE_HEADPIC;
        updateHeadpic = uploadImageBean.getData();

        ourCodePresenter.updateUserInfo(getParaMap());
    }


    @Override
    public void toDo() {

        switch (updatePamas){
            case UPDATE_BIRTH:
                break;
            case UPDATE_GENDER:
                mUserSexTv.setText("1".equals(updateGender)?"男":"女");
                editor.putString("user_sex","1".equals(updateGender)?"男":"女");
                break;
            case UPDATE_NAME:
                mNickNameTv.setText(updateName);
                editor.putString("nick_name",updateName);
                break;
            case UPDATE_HEADPIC:
                setPicToView(updatePhotoPath);

//                if (!updateHeadpic.startsWith("http")){
//                    updateHeadpic = "http://118.178.227.194:8087/" + updateHeadpic;
//                }
                editor.putString("user_headpic",updatePhotoPath);
                break;
            case UPDATE_LOFTNAME:
                mDoveNameTv.setText(updateLoftName);
                editor.putString("user_dovecote",updateLoftName);
                break;
            case UPDATE_EXPE:
                mPigeonYearTv.setText(updateExper);
                editor.putString("user_year",updateExper);
                break;
        }
        editor.commit();
    }

    private int updatePamas = 0;
    private final int UPDATE_BIRTH = 0;
    private final int UPDATE_NAME = 1;


    private final int UPDATE_GENDER = 2;
    private final int UPDATE_HEADPIC = 3;
    private final int UPDATE_LOFTNAME = 4;
    private final int UPDATE_EXPE = 5;

    private  String updateBirth = "";
    private String updateName = "";
    private String updateGender = "";
    private String updateLoftName= "";
    private String updateHeadpic = "";
    private String updateExper = "";

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
        Log.e("fasdfsd",updatePhotoPath+"-------path");

        if (file != null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
            map.put("file\"; filename=\""+file.getName()+"", fileBody);
        }
        return map;
    }


    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();
        map.put("method",getMethod());
        map.put("sign",getSign());
        map.put("time",getTime());
        map.put("version",getVersion());
        map.put("userid",mUserObjId);
        map.put("token",getToken());

        switch (methodType){
            case MethodType.METHOD_TYPE_IMAGE_UPLOAD:
                break;
            case MethodType.METHOD_TYPE_USER_UPDATE:
                switch (updatePamas){
                    case UPDATE_BIRTH:
                        map.put("birthday",updateBirth);
                        break;
                    case UPDATE_GENDER:
                        map.put("gender",updateGender);
                        break;
                    case UPDATE_NAME:
                        map.put("nickname",updateName);
                        break;
                    case UPDATE_HEADPIC:
                        map.put("headpic",updateHeadpic);
                        break;
                    case UPDATE_LOFTNAME:
                        map.put("loftname",updateLoftName);
                        break;
                    case UPDATE_EXPE:
                        map.put("experience",updateExper);
                        break;
                }
                break;
        }

        return map;
    }

    @Override
    public String getMethod() {
        String method = "";
        switch (methodType){
            case MethodType.METHOD_TYPE_IMAGE_UPLOAD:
                method = "/app/image/upload";
                break;
            case MethodType.METHOD_TYPE_USER_UPDATE:
                method = "/app/user/update";
                break;
        }

        return method;
    }

    private void showHeadDialog(){

        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.personal_headciv_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mTakePhoto = (TextView) view.findViewById(R.id.headciv_dialog_takephoto);
        TextView mFromPhoto = (TextView) view.findViewById(R.id.headciv_dialog_fromphoto);
        TextView mCancle = (TextView) view.findViewById(R.id.headciv_dialog_cancle);

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagMsg = "拍照权限";

                if (isNeedCheck){
                    if (!ApiUtils.checkPermissions(PersonalActivity.this, ConstantUtils.PERMISSION_REQUESTCODE_1,needPermissions)){
                        takePhoto();
                    }
                }else {
                    takePhoto();
                }

                mDialog.dismiss();
            }
        });
        mFromPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagMsg = "相册权限";

                if (isNeedCheck){
                    if (!ApiUtils.checkPermissions(PersonalActivity.this, ConstantUtils.PERMISSION_REQUESTCODE_2,needPermissions)){
                        choosePhoto();
                    }
                }else {
                    choosePhoto();
                }
                mDialog.dismiss();
            }
        });

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();

    }

    public void showMyDialog(final String tagMsg) {
        mDialogEt = new Dialog(this, R.style.DialogTheme);
        mDialogEt.setCancelable(false);

        View view = getLayoutInflater().inflate(R.layout.pigeon_name_dialog, null);
        final EditText mEtDialog = (EditText) view.findViewById(R.id.pigeon_name_dialog_et);
        final TextView nameTitle = (TextView) view.findViewById(R.id.pigeon_name_dialog_title);
        TextView nameCancle = (TextView) view.findViewById(R.id.name_dialog_cancle);
        TextView nameConfirm = (TextView) view.findViewById(R.id.name_dialog_confirm);
        switch (tagMsg){
            case "nickname":
                nameTitle.setText("昵称");
                mEtDialog.setText(getNickName());
                break;
            case "loftname":
                nameTitle.setText("鸽舍名");
                mEtDialog.setText(getDovecoteName());
                break;
        }
        mEtDialog.setSelection(mEtDialog.getText().length());
        nameCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogEt.dismiss();
            }
        });
        nameConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String str = mEtDialog.getText().toString().trim();

                if (!ApiUtils.isNetworkConnected(PersonalActivity.this)) {
                    ApiUtils.showToast(PersonalActivity.this, PersonalActivity.this.getString(R.string.net_conn_2));
                    return;
                }

                switch (tagMsg){
                    case "nickname":
                        updatePamas = UPDATE_NAME;
                        updateName = str;
                        break;
                    case "loftname":
                        updatePamas = UPDATE_LOFTNAME;
                        updateLoftName = str;
                        break;
                }

                ourCodePresenter.updateUserInfo(getParaMap());

                mDialogEt.dismiss();
            }
        });

        mDialogEt.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialogEt.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        mDialogEt.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ApiUtils.setDialogWindow(mDialogEt);
        mDialogEt.show();

    }

    private String getNickName() {
        return mNickName;
    }

    public void setUserSex() {

        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);

        View view = getLayoutInflater().inflate(R.layout.personal_sex_dialog, null);
        mDialog.setCancelable(false);

        final TextView mMale = (TextView) view.findViewById(R.id.dialog_sex_male);
        final TextView mFemale = (TextView) view.findViewById(R.id.dialog_sex_female);
        LinearLayout llMale = (LinearLayout) view.findViewById(R.id.dialog_sex_ll_male);
        LinearLayout llFemale = (LinearLayout) view.findViewById(R.id.dialog_sex_ll_female);

        mMale.setText("男");
        mFemale.setText("女");

        llMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(PersonalActivity.this)) {
                    ApiUtils.showToast(PersonalActivity.this, PersonalActivity.this.getString(R.string.net_conn_2));
                    return;
                }
                updatePamas = UPDATE_GENDER;
                updateGender = "1";
                ourCodePresenter.updateUserInfo(getParaMap());

                mDialog.dismiss();
            }
        });
        llFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(PersonalActivity.this)) {
                    ApiUtils.showToast(PersonalActivity.this, PersonalActivity.this.getString(R.string.net_conn_2));
                    return;
                }

                updatePamas = UPDATE_GENDER;
                updateGender = "2";
                ourCodePresenter.updateUserInfo(getParaMap());
                mDialog.dismiss();
            }
        });

        mDialog.setContentView(view, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    private int pigeonYear, newAgeYear, newAgeMonth, newAgeDay, age, maxFeedAge;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private NumberPicker dayPicker;
    private String mUserAge;

    public void showYearDialog() {

        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);

        View view = this.getLayoutInflater().inflate(R.layout.personal_year_dialog, null);

        mDialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_year);

        TextView birthCancle = (TextView) view.findViewById(R.id.year_dialog_cancle);
        TextView birthConfirm = (TextView) view.findViewById(R.id.year_dialog_confirm);

        birthCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        birthConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ApiUtils.isNetworkConnected(PersonalActivity.this)) {
                    ApiUtils.showToast(PersonalActivity.this, PersonalActivity.this.getString(R.string.net_conn_2));
                    return;
                }


                if (userAge != null && !"".equals(userAge)) {
                }

//                if (pigeonYear > age && pigeonYear > userAge) {
//
//                    ApiUtils.showToast(context, "年限大于年龄");
//                }
//
                updatePamas = UPDATE_EXPE;
                updateExper = String.valueOf(pigeonYear)+"年";
                ourCodePresenter.updateUserInfo(getParaMap());
                mDialog.dismiss();
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);

        String age = mPigeonYearTv.getText().toString().trim();
        String userAge = mUserAgeTv.getText().toString().trim();

//        if (age >= userAge) {
            yearPicker.setMaxValue(Integer.parseInt((userAge.split("岁"))[0]));
//        } else {
//            yearPicker.setMaxValue(userAge);
//        }
        yearPicker.setMinValue(1);

//        String kkk = mYearTv.getText().toString().trim();

//        yearPicker.setValue(Integer.parseInt(kkk.substring(0, kkk.length() - 1)));

        yearPicker.setValue(Integer.parseInt((age.split("年"))[0]));

        pigeonYear = yearPicker.getValue();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                pigeonYear = newVal;
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();
    }

    public void showAgeDialog() {

        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.personal_birth_dialog, null);

        mDialog.setContentView(view, new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        NumberPicker yearPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_year);
        NumberPicker monthPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_month);
        dayPicker = (NumberPicker) view.findViewById(R.id.birth_dialog_np_day);


        TextView birthCancle = (TextView) view.findViewById(R.id.birth_dialog_cancle);
        TextView birthConfirm = (TextView) view.findViewById(R.id.birth_dialog_confirm);

        birthCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        birthConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newAgeYearTemp = String.valueOf(newAgeYear);
                String newAgeMonthTemp = String.valueOf(newAgeMonth);
                String newAgeDayTemp = String.valueOf(newAgeDay);

                if (newAgeYear < 10) {
                    newAgeYearTemp = "0" + newAgeYear;
                }
                if (newAgeMonth < 10) {
                    newAgeMonthTemp = "0" + newAgeMonth;
                }
                if (newAgeDay < 10) {
                    newAgeDayTemp = "0" + newAgeDay;
                }

                final Calendar calendar = Calendar.getInstance();

                if (calendar.get(Calendar.YEAR) - newAgeYear <= 0) {
                    age = 1;
                } else {

                    if (calendar.get(Calendar.MONTH) + 1 - newAgeMonth <= -6) {
                        age = calendar.get(Calendar.YEAR) - newAgeYear - 1;
                    } else {
                        age = calendar.get(Calendar.YEAR) - newAgeYear;
                    }
                }

                mDialog.dismiss();
            }
        });

        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);


        mUserBirth = preferences.getString("user_birth", "");

        yearPicker.setMaxValue(year);
        yearPicker.setMinValue(year - 100);
        yearPicker.setValue(Integer.parseInt(mUserBirth.substring(0, 4)));

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(01);
        monthPicker.setValue(Integer.parseInt(mUserBirth.substring(5, 7)));

        newAgeYear = yearPicker.getValue();
        newAgeMonth = monthPicker.getValue();

        initDay((mUserBirth.substring(8, 10)));

        newAgeDay = dayPicker.getValue();

        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                newAgeYear = newVal;
                initDay("");
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                newAgeMonth = newVal;
                initDay("");
            }
        });

        dayPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                newAgeDay = newVal;
            }
        });

        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();

        if (month + 12 - newAgeMonth == 12) {
            maxFeedAge = year - newAgeYear + 1;
        } else if (month + 12 - newAgeMonth < 12) {
            maxFeedAge = year - newAgeYear;
        }
    }

    private void initDay(String dayValue) {

        if (newAgeMonth == 1 || newAgeMonth == 3 || newAgeMonth == 5 || newAgeMonth == 7 || newAgeMonth == 8 || newAgeMonth == 10 || newAgeMonth == 12) {
            dayPicker.setMaxValue(31);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        } else if (newAgeMonth == 4 || newAgeMonth == 6 || newAgeMonth == 9 || newAgeMonth == 11) {
            dayPicker.setMaxValue(30);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        } else if (newAgeMonth == 2 && newAgeYear % 4 == 0) {
            dayPicker.setMaxValue(29);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        } else if (newAgeMonth == 2 && newAgeYear % 4 != 0) {
            dayPicker.setMaxValue(28);
            dayPicker.setMinValue(01);
            if (!"".equals(dayValue)) {
                dayPicker.setValue(Integer.parseInt(dayValue));
            }
        }
    }


    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
