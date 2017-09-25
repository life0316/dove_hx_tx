package com.haoxi.dove.newin.ourcircle.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haoxi.dove.R;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.newin.bean.InnerCircleBean;
import com.haoxi.dove.newin.trail.presenter.OurCodePresenter;
import com.haoxi.dove.retrofit.MethodConstant;
import com.haoxi.dove.retrofit.MethodParams;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.utils.RxBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by Administrator on 2017\7\3 0003.
 */

@ActivityFragmentInject(contentViewId = R.layout.activity_circle_trans)
public class TransCircleActivity  extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tran_et)
    EditText editText;

    @BindView(R.id.custom_toolbar_keep)
    TextView mRigthTv;
    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTv;
    @BindView(R.id.custom_toolbar_left_tv)
    TextView mLeftTv;

    @BindView(R.id.tran_other_iv)
    ImageView mPlayerIv;
    @BindView(R.id.tran_other_name)
    TextView mNameTv;
    @BindView(R.id.tran_other_content)
    TextView mContentTv;

    OurCodePresenter ourCodePresenter;
    private RxBus mRxBus;

    private String circleId = "";
    private String content = "转发动态";

    private String playerName = "";
    private String playerDes = "";
    private String playerHead = "";

    private InnerCircleBean innerCircleBean;
    private int circleTag;

    @Override
    public void toDo() {
        mRxBus.post(ConstantUtils.OBSER_LOAD_CIRCLE,circleTag);
        finish();
    }

    @Override
    public String getMethod() {
        return MethodConstant.SHARE_CIRCLE;
    }

    @Override
    protected void initInject() {

        ourCodePresenter = new OurCodePresenter(this);
        mRxBus = RxBus.getInstance();
    }

    @Override
    protected void init() {

        mLeftTv.setVisibility(View.VISIBLE);
        mRigthTv.setVisibility(View.VISIBLE);
        mRigthTv.setTextColor(Color.GRAY);

        mTitleTv.setText("转发动态");

        mRigthTv.setText("发布");
        mRigthTv.setOnClickListener(this);
        mLeftTv.setOnClickListener(this);
        mRigthTv.setEnabled(true);
        mRigthTv.setTextColor(Color.WHITE);

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        Timer timer = new Timer();
        timer.schedule(new TimerTask()
                       {
                           public void run()
                           {
                               InputMethodManager inputManager =
                                       (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       },
                500);

        Intent intent = getIntent();
        if (intent != null) {
            innerCircleBean = intent.getParcelableExtra("innerCircleBean");
            circleTag = intent.getIntExtra("circle_tag",0);
            if (innerCircleBean != null) {
                circleId = innerCircleBean.getCircleid();
                playerName = innerCircleBean.getUsername();
                playerDes = "";
                playerHead = innerCircleBean.getHeadpic();
            }
        }

        initCircle();
    }

    private void initCircle() {

        String headpic = ConstantUtils.HEADPIC;
        if (playerHead != null && !"".equals(playerHead) && !"-1".equals(playerHead)){

            if (playerHead.startsWith("http")) {
                headpic = playerHead;
            }else {
                headpic += playerHead;
            }

            Glide.with(this)
                    .load(headpic)
                    .dontAnimate()
                    .placeholder(R.mipmap.btn_img_photo_default)
                    .error(R.mipmap.btn_img_photo_default)
                    .into(mPlayerIv);

        }else {
            mPlayerIv.setImageResource(R.mipmap.btn_img_photo_default);
        }

        mNameTv.setText("@"+ playerName);
        mContentTv.setText(playerDes);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.custom_toolbar_left_tv:
                finish();
                break;
            case R.id.custom_toolbar_keep:
                ourCodePresenter.shareCircle(getParaMap());
                break;
        }
    }
    public Map<String,String> getParaMap(){

        Map<String,String> map = new HashMap<>();

        map.put(MethodParams.PARAMS_METHOD,getMethod());
        map.put(MethodParams.PARAMS_SIGEN,getSign());
        map.put(MethodParams.PARAMS_TIME,getTime());
        map.put(MethodParams.PARAMS_VERSION,getVersion());
        map.put(MethodParams.PARAMS_USER_OBJ,mUserObjId);
        map.put(MethodParams.PARAMS_TOKEN,mToken);

        map.put(MethodParams.PARAMS_CIRCLE_ID,circleId);
        map.put(MethodParams.PARAMS_CONTENT,getContent());
        return map;
    }
    private String getContent(){
        content = editText.getText().toString().trim();
        if (content == null || "".equals(content)) {
            content = "转发动态";
        }
        return content;
    }
}
