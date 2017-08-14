package com.haoxi.dove.utils;

import android.app.PendingIntent;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import java.util.Stack;

/**
 * @创建者 Administrator
 * @创建时间 2017/2/13 11:08
 * @描述
 */
public class AppManager {

    private static Stack<AppCompatActivity> activityStack;
    private static AppManager appManager;
    private PendingIntent restartIntent;

    private AppManager(){};

    public static AppManager getAppManager(){
        if (appManager == null) {
            appManager = new AppManager();
        }
        return appManager;
    }

    /**
     * 添加activity到堆栈中
     * @param appCompatActivity
     */
    public void addActivity(AppCompatActivity appCompatActivity){
        if (activityStack == null){
            activityStack = new Stack<>();
        }
        activityStack.add(appCompatActivity);
    }

    /**
     * 获取当前 activity（堆栈中最后一个压入的）
     * @return
     */
    public AppCompatActivity currentActivity(){
        AppCompatActivity appCompatActivity = activityStack.lastElement();
        return appCompatActivity;
    }

    /**
     * 结束当前 activity（堆栈中最后一个压入的）
     * @return
     */
    public void finishActivity(){
        AppCompatActivity appCompatActivity = activityStack.lastElement();
        finishActivity(appCompatActivity);
    }

    /**
     * 结束指定 activity
     * @return
     */
    public void finishActivity(AppCompatActivity appCompatActivity){

        if (appCompatActivity != null) {
            activityStack.remove(appCompatActivity);
            appCompatActivity.finish();
            appCompatActivity = null;
        }
    }

    /**
     * 结束指定类名 activity
     * @return
     */
    public void finishActivity(Class<?> cls){

        for (AppCompatActivity appCompatActivity: activityStack){
            if (appCompatActivity.getClass().equals(cls)) {
                finishActivity(appCompatActivity);
            }
        }
    }


    /**
     * 结束所有 activity
     * @return
     */
    public void finishAllActivity(){

        for (int i = 0; i < activityStack.size(); i++) {
            if (activityStack.get(i) != null) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }


    /**
     * 退出应用
     * @param context
     */
    public void exitApp(Context context){
        finishAllActivity();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
