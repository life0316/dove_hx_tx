package com.haoxi.dove.receiver;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.push.MessageReceiver;
import com.alibaba.sdk.android.push.notification.CPushMessage;

import java.util.Map;

/**
 * Created by lifei on 2017/5/5.
 */

public class YunPuchReceiver extends MessageReceiver {

    private static final String TAG = "YunPuchReceiver";

    @Override
    protected void onMessage(Context context, CPushMessage cPushMessage) {
        super.onMessage(context, cPushMessage);

        Log.e(TAG,cPushMessage.getTitle()+"------msg----"+cPushMessage.getContent());

    }

    /**
     * @param context
     * @param title
     * @param summary
     * @param map     context 上下文环境
     *                title 通知标题
     *                summary 通知内容
     *                extraMap 通知额外参数，包括部分系统自带参数：
     *                _ALIYUN_NOTIFICATION_ID_(V2.3.5及以上):创建通知对应id
     *                _ALIYUN_NOTIFICATION_PRIORITY_(V2.3.5及以上):创建通知对应id。默认不带，需要通过OpenApi设置
     */
    @Override
    protected void onNotification(Context context, String title, String summary, Map<String, String> map) {
        super.onNotification(context, title, summary, map);

        Log.e(TAG,title+"------notify----"+summary);

    }

    @Override
    protected void onNotificationOpened(Context context, String title, String summary, String s2) {
        super.onNotificationOpened(context, title, summary, s2);

        Log.e(TAG,title+"------notifyOpened----"+summary);
    }


}
