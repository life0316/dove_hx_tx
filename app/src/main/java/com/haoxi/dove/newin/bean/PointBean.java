package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lifei on 2017/6/27.
 */
public class PointBean implements Parcelable {

    private String time;
    private double lat;
    private double lng;

    private float speed;
    private float height;
    private float dir;

    private double battery;

    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDir() {
        return dir;
    }

    public void setDir(float dir) {
        this.dir = dir;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(time);
        dest.writeDouble(lat);
        dest.writeDouble(lng);

        dest.writeFloat(speed);
        dest.writeFloat(height);
        dest.writeFloat(dir);

    }

    public static Parcelable.Creator<PointBean> CREATOR = new Creator<PointBean>() {
        @Override
        public PointBean createFromParcel(Parcel source) {

            PointBean pointBean = new PointBean();
            pointBean.time = source.readString();
            pointBean.lat = source.readDouble();
            pointBean.lng = source.readDouble();
            pointBean.speed = source.readFloat();
            pointBean.height = source.readFloat();
            pointBean.dir = source.readFloat();

            return pointBean;
        }

        @Override
        public PointBean[] newArray(int size) {
            return new PointBean[size];
        }
    };

    @Override
    public String toString() {

        String str = time +"-" + speed + "-" + dir + "-" + height + "-" + lat + "-" + lng;

        return str;
    }
}
