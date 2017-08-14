package com.haoxi.dove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by lifei on 2017/4/10.
 */

@Entity
public class RealFlyBean implements Parcelable {

//    {"GENERATE_TIME":"2017-04-12 11:44:54",
//            "FLYING_NATION":"",
//            "FLYING_CITY":"",
//            "SURPLUS_POWER":"75",
//            "FLYING_ADDRESS":"",
//            "LATITUDE":"39.955635",
//            "FLYING_HEIGHT":"63",
//            "FLYING_SPEED":"0",
//            "FLYING_COUNTY":"",
//            "LONGITUDE":"116.151512",
//            "FLYING_PROVINCE":"",
//            "FLYING_DIRECTION":"54",
//            "OBJ_ID":"0b60a9c35b4defc1015b60439ab907f4",
//            "PIGEON_OBJ_ID":"0b60a9c35a63c2c3015b18a2ed29066b"}


    @Id
    Long id;

    String PIGEON_OBJ_ID;

    @Unique
    String OBJ_ID;
    //String FLYING_START_TIME;
    //String FLYING_START_ADDRESS;
    //String FLYING_END_TIME;
    //String FLYING_END_ADDRESS;

    String GENERATE_TIME;

    String FLYING_SPEED;
    String FLYING_HEIGHT;
    String FLYING_DIRECTION;
    String LONGITUDE;
    String LATITUDE;
    String SURPLUS_POWER;
    String FLYING_NATION;
    String FLYING_PROVINCE;
    String FLYING_CITY;
    String FLYING_COUNTY;
    String FLYING_ADDRESS;
//    String FLYING_MODE;

//    String ROUTE_OBJ_ID;

    String USER_OBJ_ID;
    String RING_OBJ_ID;

    public String getUSER_OBJ_ID() {
        return USER_OBJ_ID;
    }

    public void setUSER_OBJ_ID(String USER_OBJ_ID) {
        this.USER_OBJ_ID = USER_OBJ_ID;
    }

    public String getRING_OBJ_ID() {
        return RING_OBJ_ID;
    }

    public void setRING_OBJ_ID(String RING_OBJ_ID) {
        this.RING_OBJ_ID = RING_OBJ_ID;
    }

    public String getPIGEON_OBJ_ID() {
        return PIGEON_OBJ_ID;
    }

    public void setPIGEON_OBJ_ID(String PIGEON_OBJ_ID) {
        this.PIGEON_OBJ_ID = PIGEON_OBJ_ID;
    }


    public String getGENERATE_TIME() {
        return GENERATE_TIME;
    }

    public void setGENERATE_TIME(String GENERATE_TIME) {
        this.GENERATE_TIME = GENERATE_TIME;
    }

    public String getFLYING_SPEED() {
        return FLYING_SPEED;
    }

    public void setFLYING_SPEED(String FLYING_SPEED) {
        this.FLYING_SPEED = FLYING_SPEED;
    }

    public String getFLYING_HEIGHT() {
        return FLYING_HEIGHT;
    }

    public void setFLYING_HEIGHT(String FLYING_HEIGHT) {
        this.FLYING_HEIGHT = FLYING_HEIGHT;
    }

    public String getFLYING_DIRECTION() {
        return FLYING_DIRECTION;
    }

    public void setFLYING_DIRECTION(String FLYING_DIRECTION) {
        this.FLYING_DIRECTION = FLYING_DIRECTION;
    }

    public String getLATITUDE() {
        return LATITUDE;
    }

    public void setLATITUDE(String LATITUDE) {
        this.LATITUDE = LATITUDE;
    }

    public String getSURPLUS_POWER() {
        return SURPLUS_POWER;
    }

    public void setSURPLUS_POWER(String SURPLUS_POWER) {
        this.SURPLUS_POWER = SURPLUS_POWER;
    }

    public String getFLYING_NATION() {
        return FLYING_NATION;
    }

    public void setFLYING_NATION(String FLYING_NATION) {
        this.FLYING_NATION = FLYING_NATION;
    }

    public String getFLYING_PROVINCE() {
        return FLYING_PROVINCE;
    }

    public void setFLYING_PROVINCE(String FLYING_PROVINCE) {
        this.FLYING_PROVINCE = FLYING_PROVINCE;
    }

    public String getFLYING_CITY() {
        return FLYING_CITY;
    }

    public void setFLYING_CITY(String FLYING_CITY) {
        this.FLYING_CITY = FLYING_CITY;
    }

    public String getFLYING_COUNTY() {
        return FLYING_COUNTY;
    }

    public void setFLYING_COUNTY(String FLYING_COUNTY) {
        this.FLYING_COUNTY = FLYING_COUNTY;
    }

    public String getFLYING_ADDRESS() {
        return FLYING_ADDRESS;
    }

    public void setFLYING_ADDRESS(String FLYING_ADDRESS) {
        this.FLYING_ADDRESS = FLYING_ADDRESS;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PIGEON_OBJ_ID);
//        dest.writeString(FLYING_START_ADDRESS);
//        dest.writeString(FLYING_START_TIME);
//        dest.writeString(FLYING_END_TIME);
//        dest.writeString(FLYING_END_ADDRESS);
        dest.writeString(GENERATE_TIME);
        dest.writeString(SURPLUS_POWER);
        dest.writeString(FLYING_SPEED);
        dest.writeString(FLYING_HEIGHT);
        dest.writeString(FLYING_DIRECTION);
        dest.writeString(LONGITUDE);
        dest.writeString(LATITUDE);
        dest.writeString(FLYING_NATION);
        dest.writeString(FLYING_PROVINCE);
        dest.writeString(FLYING_CITY);
        dest.writeString(FLYING_COUNTY);
        dest.writeString(FLYING_ADDRESS);
        dest.writeString(OBJ_ID);
        dest.writeString(USER_OBJ_ID);
        dest.writeString(RING_OBJ_ID);
//        dest.writeString(FLYING_MODE);
//        dest.writeString(ROUTE_OBJ_ID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOBJ_ID() {
        return this.OBJ_ID;
    }

    public void setOBJ_ID(String OBJ_ID) {
        this.OBJ_ID = OBJ_ID;
    }

    public String getLONGITUDE() {
        return this.LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public static final Parcelable.Creator<RealFlyBean> CREATOR = new Creator<RealFlyBean>() {
        @Override
        public RealFlyBean createFromParcel(Parcel source) {

            RealFlyBean realFlyBean = new RealFlyBean();

            realFlyBean.PIGEON_OBJ_ID = source.readString();
//            realFlyBean.FLYING_START_TIME = source.readString();
//            realFlyBean.FLYING_START_ADDRESS = source.readString();
//            realFlyBean.FLYING_END_TIME = source.readString();
//            realFlyBean.FLYING_END_ADDRESS = source.readString();
            realFlyBean.FLYING_SPEED= source.readString();
            realFlyBean.FLYING_HEIGHT = source.readString();
            realFlyBean.FLYING_DIRECTION = source.readString();
            realFlyBean.GENERATE_TIME = source.readString();
            realFlyBean.LATITUDE = source.readString();
            realFlyBean.LONGITUDE = source.readString();
            realFlyBean.SURPLUS_POWER = source.readString();
            realFlyBean.FLYING_NATION = source.readString();
            realFlyBean.FLYING_PROVINCE = source.readString();
            realFlyBean.FLYING_CITY = source.readString();
            realFlyBean.FLYING_COUNTY = source.readString();
            realFlyBean.FLYING_ADDRESS = source.readString();
            realFlyBean.RING_OBJ_ID = source.readString();
            realFlyBean.USER_OBJ_ID = source.readString();
//            realFlyBean.FLYING_MODE = source.readString();
//            realFlyBean.ROUTE_OBJ_ID = source.readString();
            realFlyBean.OBJ_ID = source.readString();


            return realFlyBean;
        }

        @Override
        public RealFlyBean[] newArray(int size) {
            return new RealFlyBean[size];
        }
    };

    @Generated(hash = 608091329)
    public RealFlyBean(Long id, String PIGEON_OBJ_ID, String OBJ_ID, String GENERATE_TIME,
            String FLYING_SPEED, String FLYING_HEIGHT, String FLYING_DIRECTION,
            String LONGITUDE, String LATITUDE, String SURPLUS_POWER, String FLYING_NATION,
            String FLYING_PROVINCE, String FLYING_CITY, String FLYING_COUNTY,
            String FLYING_ADDRESS, String USER_OBJ_ID, String RING_OBJ_ID) {
        this.id = id;
        this.PIGEON_OBJ_ID = PIGEON_OBJ_ID;
        this.OBJ_ID = OBJ_ID;
        this.GENERATE_TIME = GENERATE_TIME;
        this.FLYING_SPEED = FLYING_SPEED;
        this.FLYING_HEIGHT = FLYING_HEIGHT;
        this.FLYING_DIRECTION = FLYING_DIRECTION;
        this.LONGITUDE = LONGITUDE;
        this.LATITUDE = LATITUDE;
        this.SURPLUS_POWER = SURPLUS_POWER;
        this.FLYING_NATION = FLYING_NATION;
        this.FLYING_PROVINCE = FLYING_PROVINCE;
        this.FLYING_CITY = FLYING_CITY;
        this.FLYING_COUNTY = FLYING_COUNTY;
        this.FLYING_ADDRESS = FLYING_ADDRESS;
        this.USER_OBJ_ID = USER_OBJ_ID;
        this.RING_OBJ_ID = RING_OBJ_ID;
    }

    @Generated(hash = 1309089543)
    public RealFlyBean() {
    }

}
