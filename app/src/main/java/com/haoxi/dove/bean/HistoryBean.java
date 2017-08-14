package com.haoxi.dove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by lifei on 2017/4/10.
 */

@Entity
public class HistoryBean implements Parcelable {

    @Id
    Long id;

    String PIGEON_OBJ_ID;
    String FLYING_START_TIME;
    String FLYING_START_ADDRESS;
    String FLYING_END_TIME;
    String FLYING_END_ADDRESS;
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
    String FLYING_MODE;

    String ROUTE_OBJ_ID;

    public String getOBJ_ID() {
        return OBJ_ID;
    }

    public void setOBJ_ID(String OBJ_ID) {
        this.OBJ_ID = OBJ_ID;
    }

    String OBJ_ID;


    public String getROUTE_OBJ_ID() {
        return ROUTE_OBJ_ID;
    }

    public void setROUTE_OBJ_ID(String ROUTE_OBJ_ID) {
        this.ROUTE_OBJ_ID = ROUTE_OBJ_ID;
    }

    public String getPIGEON_OBJ_ID() {
        return PIGEON_OBJ_ID;
    }

    public void setPIGEON_OBJ_ID(String PIGEON_OBJ_ID) {
        this.PIGEON_OBJ_ID = PIGEON_OBJ_ID;
    }

    public String getFLYING_START_TIME() {
        return FLYING_START_TIME;
    }

    public void setFLYING_START_TIME(String FLYING_START_TIME) {
        this.FLYING_START_TIME = FLYING_START_TIME;
    }

    public String getFLYING_START_ADDRESS() {
        return FLYING_START_ADDRESS;
    }

    public void setFLYING_START_ADDRESS(String FLYING_START_ADDRESS) {
        this.FLYING_START_ADDRESS = FLYING_START_ADDRESS;
    }

    public String getFLYING_END_TIME() {
        return FLYING_END_TIME;
    }

    public void setFLYING_END_TIME(String FLYING_END_TIME) {
        this.FLYING_END_TIME = FLYING_END_TIME;
    }

    public String getFLYING_END_ADDRESS() {
        return FLYING_END_ADDRESS;
    }

    public void setFLYING_END_ADDRESS(String FLYING_END_ADDRESS) {
        this.FLYING_END_ADDRESS = FLYING_END_ADDRESS;
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

    public String getLONGTUDE() {
        return LONGITUDE;
    }

    public void setLONGTUDE(String LONGTUDE) {
        this.LONGITUDE = LONGTUDE;
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

    public String getFLYING_MODE() {
        return FLYING_MODE;
    }

    public void setFLYING_MODE(String FLYING_MODE) {
        this.FLYING_MODE = FLYING_MODE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(PIGEON_OBJ_ID);
        dest.writeString(FLYING_START_ADDRESS);
        dest.writeString(FLYING_START_TIME);
        dest.writeString(FLYING_END_TIME);
        dest.writeString(FLYING_END_ADDRESS);
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
        dest.writeString(FLYING_MODE);
        dest.writeString(ROUTE_OBJ_ID);
        dest.writeString(OBJ_ID);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLONGITUDE() {
        return this.LONGITUDE;
    }

    public void setLONGITUDE(String LONGITUDE) {
        this.LONGITUDE = LONGITUDE;
    }

    public static final Creator<HistoryBean> CREATOR = new Creator<HistoryBean>() {
        @Override
        public HistoryBean createFromParcel(Parcel source) {

            HistoryBean realFlyBean = new HistoryBean();

            realFlyBean.PIGEON_OBJ_ID = source.readString();
            realFlyBean.FLYING_START_TIME = source.readString();
            realFlyBean.FLYING_START_ADDRESS = source.readString();
            realFlyBean.FLYING_END_TIME = source.readString();
            realFlyBean.FLYING_END_ADDRESS = source.readString();
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
            realFlyBean.FLYING_MODE = source.readString();
            realFlyBean.ROUTE_OBJ_ID = source.readString();
            realFlyBean.OBJ_ID = source.readString();


            return realFlyBean;
        }

        @Override
        public HistoryBean[] newArray(int size) {
            return new HistoryBean[size];
        }
    };

    @Generated(hash = 2068190135)
    public HistoryBean(Long id, String PIGEON_OBJ_ID, String FLYING_START_TIME,
            String FLYING_START_ADDRESS, String FLYING_END_TIME,
            String FLYING_END_ADDRESS, String GENERATE_TIME, String FLYING_SPEED,
            String FLYING_HEIGHT, String FLYING_DIRECTION, String LONGITUDE,
            String LATITUDE, String SURPLUS_POWER, String FLYING_NATION,
            String FLYING_PROVINCE, String FLYING_CITY, String FLYING_COUNTY,
            String FLYING_ADDRESS, String FLYING_MODE, String ROUTE_OBJ_ID,
            String OBJ_ID) {
        this.id = id;
        this.PIGEON_OBJ_ID = PIGEON_OBJ_ID;
        this.FLYING_START_TIME = FLYING_START_TIME;
        this.FLYING_START_ADDRESS = FLYING_START_ADDRESS;
        this.FLYING_END_TIME = FLYING_END_TIME;
        this.FLYING_END_ADDRESS = FLYING_END_ADDRESS;
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
        this.FLYING_MODE = FLYING_MODE;
        this.ROUTE_OBJ_ID = ROUTE_OBJ_ID;
        this.OBJ_ID = OBJ_ID;
    }

    @Generated(hash = 48590348)
    public HistoryBean() {
    }

}
