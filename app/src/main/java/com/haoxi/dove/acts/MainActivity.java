package com.haoxi.dove.acts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.haoxi.dove.R;
import com.haoxi.dove.fragments.HomeFragment;
import com.haoxi.dove.fragments.PigeonCircleFragment;
import com.haoxi.dove.fragments.PigeonFragment;
import com.haoxi.dove.inject.ActivityFragmentInject;
import com.haoxi.dove.inject.DaggerMainComponent;
import com.haoxi.dove.inject.MainMoudle;
import com.haoxi.dove.base.BaseActivity;
import com.haoxi.dove.modules.traject.OurTrailFragment;
import com.haoxi.dove.newin.bean.OurUserInfo;
import com.haoxi.dove.observable.MyCancleObservable;
import com.haoxi.dove.utils.ApiUtils;
import com.haoxi.dove.utils.RxBus;

import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

import butterknife.BindView;
import rx.Subscriber;

@ActivityFragmentInject(contentViewId = R.layout.act_main)
public class MainActivity extends BaseActivity implements Observer {

    private static final String TAG = "MainActivity";

//    private LayoutInflater layoutInflater;

    private OurUserInfo userInfo;

    @BindView(R.id.act_main_cvp)
    FrameLayout mCvp;

    @BindView(R.id.act_main_rg)
    RadioGroup mRg;
    @BindView(R.id.act_main_rb_tab1)
    RadioButton mRb1;
    @BindView(R.id.act_main_rb_tab2)
    RadioButton mRb2;
    @BindView(R.id.act_main_rb_tab3)
    RadioButton mRb3;
    @BindView(R.id.act_main_rb_tab4)
    RadioButton mRb4;

    @Inject
    RxBus mRxBus;
    private rx.Observable<Integer> mExitObserver;

    public OurUserInfo getUser() {
        return userInfo;
    }


    PigeonFragment mTab1;
    OurTrailFragment mTab2;
    PigeonCircleFragment mTab3;
    HomeFragment mTab4;

    private BroadcastReceiver mHomeKeyEventReceiver = new BroadcastReceiver() {
        String SYSTEM_REASON = "reason";
        String SYSTEM_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case Intent.ACTION_CLOSE_SYSTEM_DIALOGS:
                    String reason = intent.getStringExtra(SYSTEM_REASON);
                    if (TextUtils.equals(reason, SYSTEM_HOME_KEY)) {
                        //表示按了home键,程序到了后台
                        mRxBus.post("isLoad", false);
                        mRxBus.post("load_dynamic", false);
                    }
                    break;
                case Intent.ACTION_SCREEN_OFF:
                    //表示按了home键,程序到了后台

                    mRxBus.post("isLoad", false);
                    mRxBus.post("isScreen", false);
                    mRxBus.post("load_dynamic", false);
                    break;
                case Intent.ACTION_SCREEN_ON:
                    //表示按了home键,程序到了后台

                    mRxBus.post("isScreen", true);
                    mRxBus.post("load_dynamic", false);
                    break;
            }
        }
    };


    @Override
    protected void initInject() {

        //noinspection deprecation
        DaggerMainComponent.builder().appComponent(getAppComponent())
                .mainMoudle(new MainMoudle(this))
                .build()
                .inject(this);

    }

    @Override
    protected void init() {

//        layoutInflater = LayoutInflater.from(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);


        registerReceiver(mHomeKeyEventReceiver, filter);


        userInfo = getIntent().getParcelableExtra("user_info");

        MyCancleObservable myCancleObservable = MyCancleObservable.getInstance();
        myCancleObservable.addObserver(this);

        mAppManager.addActivity(this);

        initView();

        setSelection(0);

    }

    private void initView() {

        mRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.act_main_rb_tab1:
                        setSelection(0);
                        break;
                    case R.id.act_main_rb_tab2:
                        setSelection(1);
                        break;
                    case R.id.act_main_rb_tab3:
                        setSelection(2);
                        break;
                    case R.id.act_main_rb_tab4:
                        setSelection(3);

                        break;
                }
            }
        });
    }

    private int currentPos = 0;

    @Override
    protected void onResume() {
        super.onResume();

        mExitObserver = mRxBus.register("exit", Integer.class);

        mExitObserver.subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }
            @Override
            public void onError(Throwable e) {

                Log.e(TAG,e.toString()+"-----退出异常");
            }

            @Override
            public void onNext(Integer integer) {
                isCanExit = integer;
            }
        });

        setSelection(currentPos);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setSelection(int position) {

        mRxBus.post("clickRadio", position);

        currentPos = position;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        hideAllFragments(ft);

        switch (position) {
            case 0:
                if (mTab1 == null) {
                    mTab1 = PigeonFragment.newInstance("信鸽");
                    ft.add(R.id.act_main_cvp, mTab1);
                } else {
                    ft.show(mTab1);
                }
                break;
            case 1:
                if (mTab2 == null) {
                    mTab2 = OurTrailFragment.newInstance("轨迹");
                    ft.add(R.id.act_main_cvp, mTab2);
                } else {
                    ft.show(mTab2);
                }
                break;
            case 2:
                if (mTab3 == null) {
                    mTab3 = PigeonCircleFragment.newInstance("鸽圈");
                    ft.add(R.id.act_main_cvp, mTab3);
                } else {
                    ft.show(mTab3);
                }
                break;
            case 3:
                if (mTab4 == null) {
                    mTab4 = HomeFragment.newInstance("我的");
                    ft.add(R.id.act_main_cvp, mTab4);
                } else {
                    ft.show(mTab4);
                }
                break;
        }

        //ft.commit();
        ft.commitAllowingStateLoss();
    }

    private void hideAllFragments(FragmentTransaction ft) {

        if (mTab1 != null) {
            ft.hide(mTab1);
        }
        if (mTab2 != null) {
            ft.hide(mTab2);
        }
        if (mTab3 != null) {
            ft.hide(mTab3);
        }
        if (mTab4 != null) {
            ft.hide(mTab4);
        }


    }

    @Override
    public void update(Observable o, Object arg) {

        switch ((int) arg) {
            case 10:
                isCanExit = 10;
                break;
            case 20:
                isCanExit = 20;
                break;
            case 30:
                isCanExit = 30;
                break;
            case 40:
                isCanExit = 40;
                break;
        }

    }

    private int isCanExit = 30;

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {


        switch (isCanExit) {
            case 30:

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((System.currentTimeMillis() - exitTime) > 2000) {
                        ApiUtils.showToast(this, "再按一次退出信鸽");
                        exitTime = System.currentTimeMillis();
                    } else {
                        finish();
                        System.exit(0);
                    }
                    return true;
                }
                break;
            case 10:
                mRxBus.post("tagnum", 0);
                return false;
            case 20:
                mRxBus.post("tagnum", 1);
                return false;
            case 40:
                mRxBus.post("showTrilPop",false);
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHomeKeyEventReceiver);
        mRxBus.unregister("exit", mExitObserver);

    }

    @Override
    public void toDo() {

    }

    @Override
    public String getMethod() {
        return null;
    }
}