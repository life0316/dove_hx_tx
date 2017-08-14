package com.haoxi.dove.modules.pigeon;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haoxi.dove.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.QRCodeDecoder;

/**
 * Created by lifei on 2017/5/25.
 */

public class MyScanActivity extends AppCompatActivity implements QRCodeView.Delegate{

    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    @BindView(R.id.zxingview)
    QRCodeView qrCodeView;

    @BindView(R.id.custom_toolbar_tv)
    TextView mTitleTV;

    @BindView(R.id.custom_toolbar_iv)
    ImageView mBackIv;

    @BindView(R.id.custom_toolbar_keep)
    TextView mPicTv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.act_my_scan);

        ButterKnife.bind(this);

        mTitleTV.setText("扫一扫");
        mBackIv.setVisibility(View.VISIBLE);
        mPicTv.setVisibility(View.VISIBLE);
        mPicTv.setText("相册");

        qrCodeView.setDelegate(this);

    }

    @OnClick(R.id.custom_toolbar_iv)
    void onBack(View view){
        this.finish();
    }

    @OnClick(R.id.custom_toolbar_keep)
    void onKeep(View view){

        startActivityForResult(BGAPhotoPickerActivity.newIntent(this,null,1,null,false),REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY);

    }

    @Override
    protected void onResume() {
        super.onResume();

        qrCodeView.startCamera();

        qrCodeView.showScanRect();

        qrCodeView.startSpot();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        qrCodeView.stopCamera();
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        qrCodeView.onDestroy();
        super.onDestroy();
    }

    private void vibrate(){
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        //Toast.makeText(this,result,Toast.LENGTH_SHORT).show();


        Log.e("content","result:-----"+result);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("codedContent", result);


        this.setResult(RESULT_OK, resultIntent);
        MyScanActivity.this.finish();

//        vibrate();
//        qrCodeView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {

        Toast.makeText(this,"错误",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        qrCodeView.showScanRect();
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY) {
            final String picPath = BGAPhotoPickerActivity.getSelectedImages(data).get(0);

            new AsyncTask<Void,Void,String>(){

                @Override
                protected String doInBackground(Void... params) {
                    return QRCodeDecoder.syncDecodeQRCode(picPath);
                }

                @Override
                protected void onPostExecute(String result) {

                    if (TextUtils.isEmpty(result)) {

                        result = "未发现二维码";

                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("codedContent", result);

                    MyScanActivity.this.setResult(RESULT_OK, resultIntent);
                    MyScanActivity.this.finish();

                }
            }.execute();
        }
    }
}
