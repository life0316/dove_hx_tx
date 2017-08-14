package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017\6\7 0007.
 */
@Entity
public class InnerDoveData implements Parcelable{

    @Id
    private Long id;

    private String ancestry;
    private String color;
    private int age;
    private String create_time;
    private String gender;
    private String playerid;

    @Unique
    private String doveid;
    private String eye;
    private String foot_ring;

    private String ringid;
    private String ring_code;

    private boolean fly_status;

    private boolean   open;

    private boolean isSetMate;

    private String fly_recordid;

    public String getFly_recordid() {
        return fly_recordid;
    }

    public void setFly_recordid(String fly_recordid) {
        this.fly_recordid = fly_recordid;
    }

    public boolean isSetMate() {
        return isSetMate;
    }

    public void setSetMate(boolean setMate) {
        isSetMate = setMate;
    }

    public boolean isFly_status() {
        return fly_status;
    }

    public String getRingid() {
        return ringid;
    }

    public void setRingid(String ringid) {
        this.ringid = ringid;
    }

    public String getRing_code() {
        return ring_code;
    }

    public void setRing_code(String ring_code) {
        this.ring_code = ring_code;
    }

    public String getFoot_ring() {
        return foot_ring;
    }

    public void setFoot_ring(String foot_ring) {
        this.foot_ring = foot_ring;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


    public String getAncestry() {
        return ancestry;
    }

    public void setAncestry(String ancestry) {
        this.ancestry = ancestry;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getDoveid() {
        return doveid;
    }

    public void setDoveid(String doveid) {
        this.doveid = doveid;
    }

    public String getEye() {
        return eye;
    }

    public void setEye(String eye) {
        this.eye = eye;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeByte((byte)(fly_status?1:0));

        dest.writeInt(age);
        dest.writeString(ancestry);
        dest.writeString(color);
        dest.writeString(create_time);
        dest.writeString(gender);
        dest.writeString(playerid);
        dest.writeString(doveid);
        dest.writeString(eye);
        dest.writeString(foot_ring);
        dest.writeString(ringid);
        dest.writeString(ring_code);
        dest.writeString(fly_recordid);

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean getOpen() {
        return this.open;
    }

    public boolean getFly_status() {
        return this.fly_status;
    }

    public void setFly_status(boolean fly_status) {
        this.fly_status = fly_status;
    }

    public boolean getIsSetMate() {
        return this.isSetMate;
    }

    public void setIsSetMate(boolean isSetMate) {
        this.isSetMate = isSetMate;
    }

    public static Parcelable.Creator<InnerDoveData> CREATOR = new Creator<InnerDoveData>() {
        @Override
        public InnerDoveData createFromParcel(Parcel source) {

            InnerDoveData innerDoveData = new InnerDoveData();

            innerDoveData.fly_status = source.readByte() != 0;

            innerDoveData.age = source.readInt();
            innerDoveData.ancestry = source.readString();
            innerDoveData.color = source.readString();
            innerDoveData.create_time = source.readString();
            innerDoveData.gender = source.readString();
            innerDoveData.playerid = source.readString();
            innerDoveData.doveid = source.readString();
            innerDoveData.eye = source.readString();
            innerDoveData.foot_ring  = source.readString();
            innerDoveData.ringid  = source.readString();
            innerDoveData.ring_code  = source.readString();
            innerDoveData.fly_recordid  = source.readString();

            return innerDoveData;
        }

        @Override
        public InnerDoveData[] newArray(int size) {
            return new InnerDoveData[size];
        }
    };

    @Generated(hash = 685290883)
    public InnerDoveData(Long id, String ancestry, String color, int age, String create_time, String gender,
            String playerid, String doveid, String eye, String foot_ring, String ringid, String ring_code,
            boolean fly_status, boolean open, boolean isSetMate, String fly_recordid) {
        this.id = id;
        this.ancestry = ancestry;
        this.color = color;
        this.age = age;
        this.create_time = create_time;
        this.gender = gender;
        this.playerid = playerid;
        this.doveid = doveid;
        this.eye = eye;
        this.foot_ring = foot_ring;
        this.ringid = ringid;
        this.ring_code = ring_code;
        this.fly_status = fly_status;
        this.open = open;
        this.isSetMate = isSetMate;
        this.fly_recordid = fly_recordid;
    }

    @Generated(hash = 1801748538)
    public InnerDoveData() {
    }
}

