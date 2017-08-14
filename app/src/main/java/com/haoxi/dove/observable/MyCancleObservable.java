package com.haoxi.dove.observable;

import java.util.Observable;

/**
 * @创建者 Administrator
 * @创建时间 2017/2/16 9:22
 * @描述
 */
public class MyCancleObservable extends Observable{

    private static MyCancleObservable cancleObs;

    private MyCancleObservable(){};

    public static MyCancleObservable getInstance(){

        if (cancleObs == null) {
            cancleObs = new MyCancleObservable();
        }
        return cancleObs;
    }

    public void setCancled(int i){
        setChanged();
        notifyObservers(i);
    }

    public void setRefrash(int i){
        setChanged();
        notifyObservers(i);
    }

    public void setExit(int i){
        setChanged();
        notifyObservers(i);
    }

}
