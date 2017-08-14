package com.haoxi.dove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lifei on 2017/4/13.
 */


@Entity
public class UniqueBean implements Parcelable {

    @Id
    Long id;

    String userObjId;
    String pigeonObjId;

    String time;


    public String getUserObjId() {
        return userObjId;
    }

    public void setUserObjId(String userObjId) {
        this.userObjId = userObjId;
    }

    public String getPigeonObjId() {
        return pigeonObjId;
    }

    public void setPigeonObjId(String pigeonObjId) {
        this.pigeonObjId = pigeonObjId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pigeonObjId);
        dest.writeString(userObjId);
        dest.writeString(time);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static final Parcelable.Creator<UniqueBean> CREATOR = new Creator<UniqueBean>() {
        @Override
        public UniqueBean createFromParcel(Parcel source) {

            UniqueBean uniqueBean = new UniqueBean();
            uniqueBean.pigeonObjId= source.readString();
            uniqueBean.time = source.readString();
            uniqueBean.userObjId = source.readString();

            return uniqueBean;
        }

        @Override
        public UniqueBean[] newArray(int size) {
            return new UniqueBean[size];
        }
    };


    @Generated(hash = 530097966)
    public UniqueBean(Long id, String userObjId, String pigeonObjId, String time) {
        this.id = id;
        this.userObjId = userObjId;
        this.pigeonObjId = pigeonObjId;
        this.time = time;
    }

    @Generated(hash = 1790070257)
    public UniqueBean() {
    }
}
