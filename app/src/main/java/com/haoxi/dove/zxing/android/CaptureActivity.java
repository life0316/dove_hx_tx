package com.haoxi.dove.zxing.android;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.haoxi.dove.R;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.ConstantUtils;
import com.haoxi.dove.zxing.camera.CameraManager;
import com.haoxi.dove.zxing.decode.RGBLuminanceSource;
import com.haoxi.dove.zxing.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;


/**
 * 这个activity打开相机，在后台线程做常规的扫描；它绘制了一个结果view来帮助正确地显示条形码，在扫描的时候显示反馈信息，
 * 然后在扫描成功的时候覆盖扫描结果
 */
public final class CaptureActivity extends Activity implements
        SurfaceHolder.Callback {

    private static final int REQUEST_CODE = 100;
    private static final int PARSE_BARCODE_SUC = 300;
    private static final int PARSE_BARCODE_FAIL = 303;

    private static final String TAG = CaptureActivity.class.getSimpleName();

    // 相机控制
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private IntentSource source;
    private Collection<BarcodeFormat> decodeFormats;
    private Map<DecodeHintType, ?> decodeHints;
    private String characterSet;
    // 电量控制
    private InactivityTimer inactivityTimer;
    // 声音、震动控制
    private BeepManager beepManager;

    private ImageButton imageButton_back;
    private TextView imageButton_function;
    private String mPhotoPath;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            mProgress.dismiss();
            switch (msg.what) {
                case PARSE_BARCODE_SUC:
                    onResultHandler((String) msg.obj, mScanBitmap);
                    break;
                case PARSE_BARCODE_FAIL:
                    Toast.makeText(CaptureActivity.this, (String) msg.obj, Toast.LENGTH_LONG).show();
                    break;

            }
        }

    };
    private ProgressDialog mProgress;
    private Bitmap mScanBitmap;

    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {

            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA

    };

    /**
     * 判断是否需要检测，防止不停的弹框
     */
    private boolean isNeedCheck = true;
    private SurfaceHolder surfaceHolder;


    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * OnCreate中初始化一些辅助类，如InactivityTimer（休眠）、Beep（声音）以及AmbientLight（闪光灯）
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        // 保持Activity处于唤醒状态
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.capture);

        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        imageButton_back = (ImageButton) findViewById(R.id.capture_imageview_back);
        imageButton_function = (TextView) findViewById(R.id.button_function);
        imageButton_back.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        imageButton_function.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
                innerIntent.setType("image/*");
                Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
                startActivityForResult(wrapperIntent, REQUEST_CODE);
            }
        });

        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    //获取图片路径
//					Cursor cursor = getContentResolver().query(data.getData(), null, null, null, null);

//					String[] projection = {MediaStore.Images.Media._ID,MediaStore.Images.Media.DATA};
//					Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.Media._ID);


                    if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(CaptureActivity.this, data.getData())) {
                        String wholeID = DocumentsContract.getDocumentId(data.getData());
                        String id = wholeID.split(":")[1];
                        String[] column = {MediaStore.Images.Media.DATA};
                        String sel = MediaStore.Images.Media._ID + "=?";
                        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                        if (cursor.moveToFirst()) {
                            int columnIndex = cursor.getColumnIndex(column[0]);
                            mPhotoPath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    }


                    mProgress = new ProgressDialog(CaptureActivity.this);
                    mProgress.setMessage("正在扫描...");
                    mProgress.setCancelable(false);
                    mProgress.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(mPhotoPath);
                            Log.e("dddddd", result + "---------6");
                            if (result != null) {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_SUC;
                                m.obj = result.getText();
                                Log.e("dddddd", result.getText() + "---------5");
                                mHandler.sendMessage(m);
                            } else {
                                Message m = mHandler.obtainMessage();
                                m.what = PARSE_BARCODE_FAIL;
                                m.obj = "Scan failed!";
                                mHandler.sendMessage(m);
                            }

                        }
                    }).start();
            }
        }
    }


    /**
     * 跳转到上一个页面
     *
     * @param resultString
     * @param bitmap
     */
    private void onResultHandler(String resultString, Bitmap bitmap) {
        if (resultString.isEmpty()) {
            Toast.makeText(CaptureActivity.this, "未发现二维码,请继续扫描", Toast.LENGTH_SHORT).show();
            return;
        }

        if ("Scan failed!".equals(resultString)) {

            Toast.makeText(CaptureActivity.this, "未发现二维码,请继续扫描", Toast.LENGTH_SHORT).show();

            return;
        }


        Intent resultIntent = new Intent();
//		Bundle bundle = new Bundle();
//		bundle.putString("result", resultString);
//		bundle.putParcelable("bitmap", bitmap);
//		resultIntent.putExtras(bundle);
        resultIntent.putExtra("codedContent", resultString);
        //resultIntent.putExtra("codedBitmap", bitmap);


        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }

    /**
     * 扫描二维码图片的方法
     *
     * @param path
     * @return
     */

    private Result scanningImage(String path) {

        if (path.isEmpty()) {
            return null;
        }

        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8");//设置二维码内容的编码

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//先获取原大小
        mScanBitmap = BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false;//获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        mScanBitmap = BitmapFactory.decodeFile(path, options);
        RGBLuminanceSource source = new RGBLuminanceSource(mScanBitmap);
//			RGBLuminanceSource source = new RGBLuminanceSource(mScanBitmap.getWidth(), mScanBitmap.getHeight(),new int[mScanBitmap.getWidth() * mScanBitmap.getHeight()]);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();


        try {
            return reader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager必须在这里初始化，而不是在onCreate()中。
        // 这是必须的，因为当我们第一次进入时需要显示帮助页，我们并不想打开Camera,测量屏幕大小
        // 当扫描框的尺寸不正确时会出现bug
        cameraManager = new CameraManager(getApplication());

        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        viewfinderView.setCameraManager(cameraManager);

        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

        if (hasSurface) {
//            // activity在paused时但不会stopped,因此surface仍旧存在；
//            // surfaceCreated()不会调用，因此在这里初始化camera
            initCamera(surfaceHolder);
        } else {
//            // 重置callback，等待surfaceCreated()来初始化camera
//
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

        source = IntentSource.NONE;
        decodeFormats = null;
        characterSet = null;
    }

    private void openCamera1() {



        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();

        if (surfaceHolder != null) {
            initCamera(surfaceHolder);
        }
    }

    private void openCamera2() {

//        cameraManager = new CameraManager(getApplication());
//
//        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
//        viewfinderView.setCameraManager(cameraManager);
//
//        handler = null;

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        surfaceHolder = surfaceView.getHolder();


        if (surfaceHolder != null) {
            surfaceHolder.addCallback(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case ConstantUtils.PERMISSION_REQUESTCODE_1:

                //没有授权

                if (!ApiUtils.verifyPermissions(grantResults)) {

                    //showMissingPermissionDialog();

                    isNeedCheck = false;
                } else {
                    //takePhoto();
                    isNeedCheck = false;

                    Log.e("hasSurface",hasSurface+"-----------1");

                    if (hasSurface) {
                        openCamera1();
                    } else {

                        openCamera2();
                    }
                }

                break;
            case ConstantUtils.PERMISSION_REQUESTCODE_2:
                //没有授权

                if (!ApiUtils.verifyPermissions(grantResults)) {

                    //showMissingPermissionDialog();

                    isNeedCheck = false;
                } else {
                    //choosePhoto();
                    isNeedCheck = false;
                }
                break;
        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!hasSurface) {
            SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    /**
     * 扫描成功，处理反馈信息
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */

    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {
        inactivityTimer.onActivity();

        boolean fromLiveScan = barcode != null;
        //这里处理解码完成后的结果，此处将参数回传到Activity处理
//        if (fromLiveScan) {
            beepManager.playBeepSoundAndVibrate();

            //Toast.makeText(this, "扫描成功", Toast.LENGTH_SHORT).show();

            Intent intent = getIntent();
            intent.putExtra("codedContent", rawResult.getText());
            intent.putExtra("codedBitmap", barcode);
            setResult(RESULT_OK, intent);
            finish();
//        }

    }

    /**
     * 初始化Camera
     *
     * @param surfaceHolder
     */
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats,
                        decodeHints, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 显示底层错误信息并退出应用
     */
    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

}
