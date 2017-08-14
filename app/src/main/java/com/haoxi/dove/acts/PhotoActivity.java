package com.haoxi.dove.acts;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haoxi.dove.R;
import com.haoxi.dove.callback.SaveImgCallback;
import com.haoxi.dove.retrofit.RegistService.IOurNewService;
import com.haoxi.dove.retrofit.RetrofitManager;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;
import com.haoxi.dove.utils.SavePicByUrlUtils;

import java.util.List;

import me.iwf.photopicker.PhotoPagerActivity;

import static me.iwf.photopicker.PhotoPreview.EXTRA_CURRENT_ITEM;
import static me.iwf.photopicker.PhotoPreview.EXTRA_PHOTOS;

public class PhotoActivity extends PhotoPagerActivity {

    private ActionBar actionBar;

    private RxBus mRxBus;
    private int currentItem;
    private List<String> paths;
    private IOurNewService ourNewService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRxBus = RxBus.getInstance();

        actionBar = getSupportActionBar();

        RetrofitManager retrofitManager = RetrofitManager.builder();

        ourNewService = retrofitManager.getOurNewService();

        currentItem = getIntent().getIntExtra(EXTRA_CURRENT_ITEM, 0);
        paths = getIntent().getStringArrayListExtra(EXTRA_PHOTOS);

        //actionBar.setTitle("");
        actionBar.setHomeAsUpIndicator(R.mipmap.back_press);
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            mRxBus.post("isLoad",false);
            finish();
        }

        if (item.getItemId() == R.id.ab_search){
            showDownDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDownDialog(){
        final Dialog mDialog = new Dialog(this, R.style.DialogTheme);
        mDialog.setCancelable(false);
        View view = getLayoutInflater().inflate(R.layout.personal_down_dialog,null);

        mDialog.setContentView(view,new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView mRemoveTv = (TextView) view.findViewById(R.id.headciv_dialog_remove_attention);
        TextView mShouTv = (TextView) view.findViewById(R.id.headciv_dialog_shou);
        TextView mDeleteTv = (TextView) view.findViewById(R.id.headciv_dialog_delete);
        TextView mCancle = (TextView) view.findViewById(R.id.headciv_dialog_cancle);

        mRemoveTv.setText("保存图片");
        mShouTv.setText("转发");
        mDeleteTv.setText("赞");

        mCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mRemoveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (paths != null) {

                    String item = actionBar.getTitle().toString().split("/")[0];
                    if (item != null && !"0".equals(item)) {
                        currentItem = Integer.parseInt(item) - 1;
                        Log.e("baocun",paths.get(currentItem)+"-----------path--------");

                        SavePicByUrlUtils.downloadImg(PhotoActivity.this, ourNewService, paths.get(currentItem), new SaveImgCallback() {
                            @Override
                            public void saveImgSuccess(String success) {
                                //ApiUtils.showToast(PhotoActivity.this,success+"----------保存成功");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ApiUtils.showToast(PhotoActivity.this,"图片存储成功");
                                    }
                                });
                                Log.e("baocun",success+"-----------保存成功");
                            }

                            @Override
                            public void saveImaFailure(String fail) {
                                Log.e("baocun",fail+"-----------fail");
                            }
                        });
                    }
                }

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
                mDialog.dismiss();
            }
        });
        ApiUtils.setDialogWindow(mDialog);
        mDialog.show();

    }

    @Override
    public void updateActionBarTitle() {
        super.updateActionBarTitle();
    }

    @Override
    public void onBackPressed() {
        mRxBus.post("isLoad",false);
        super.onBackPressed();
    }
}
