package com.haoxi.dove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

@Entity
public class SetTriBean implements Parcelable {

    @Id
    Long id;

    private String USER_OBJ_ID;

    @Unique
    private String OBJ_ID;        //信鸽基本信息主键

    private String RING_OBJ_ID;   //绑定的鸽环信息主键

    private int trilWidth;
    private int trilPic;
    private String trilColor;

    private boolean startFly;
    private int isFlying = 0;

    private int isShowMark ;         //默认显示，不为0时隐藏


    public int isShowMark() {
        return isShowMark;
    }

    public void setShowMark(int showMark) {
        isShowMark = showMark;
    }

    public String getRING_OBJ_ID() {
        return RING_OBJ_ID;
    }

    public void setRING_OBJ_ID(String RING_OBJ_ID) {
        this.RING_OBJ_ID = RING_OBJ_ID;
    }

    public String getUSER_OBJ_ID() {
        return USER_OBJ_ID;
    }

    public void setUSER_OBJ_ID(String USER_OBJ_ID) {
        this.USER_OBJ_ID = USER_OBJ_ID;
    }


    public String getOBJ_ID() {
        return OBJ_ID;
    }

    public void setOBJ_ID(String OBJ_ID) {
        this.OBJ_ID = OBJ_ID;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {


        dest.writeString(OBJ_ID);
        dest.writeString(USER_OBJ_ID);
        dest.writeInt(isFlying);
        dest.writeString(trilColor);
        dest.writeInt(trilPic);
        dest.writeInt(trilWidth);
        dest.writeString(RING_OBJ_ID);
        dest.writeInt(isShowMark);
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTrilWidth() {
        return this.trilWidth;
    }

    public void setTrilWidth(int trilWidth) {
        this.trilWidth = trilWidth;
    }

    public int getTrilPic() {
        return this.trilPic;
    }

    public void setTrilPic(int trilPic) {
        this.trilPic = trilPic;
    }

    public String getTrilColor() {
        return this.trilColor;
    }

    public void setTrilColor(String trilColor) {
        this.trilColor = trilColor;
    }

    public boolean getStartFly() {
        return this.startFly;
    }

    public void setStartFly(boolean startFly) {
        this.startFly = startFly;
    }

    public int getIsFlying() {
        return this.isFlying;
    }

    public void setIsFlying(int isFlying) {
        this.isFlying = isFlying;
    }

    public int getIsShowMark() {
        return this.isShowMark;
    }

    public void setIsShowMark(int isShowMark) {
        this.isShowMark = isShowMark;
    }

    public static final Creator<SetTriBean> CREATOR = new Creator<SetTriBean>() {
        @Override
        public SetTriBean createFromParcel(Parcel source) {

            SetTriBean pigeonBean = new SetTriBean();
            //ringBean.id = source.readLong();
            pigeonBean.OBJ_ID = source.readString();
            pigeonBean.USER_OBJ_ID = source.readString();
            pigeonBean.isFlying = source.readInt();
            pigeonBean.trilColor = source.readString();
            pigeonBean.trilPic = source.readInt();
            pigeonBean.trilWidth = source.readInt();
            pigeonBean.RING_OBJ_ID = source.readString();
            pigeonBean.isShowMark = source.readInt();

            return pigeonBean;
        }

        @Override
        public SetTriBean[] newArray(int size) {
            return new SetTriBean[size];
        }
    };


    @Generated(hash = 435514868)
    public SetTriBean(Long id, String USER_OBJ_ID, String OBJ_ID,
            String RING_OBJ_ID, int trilWidth, int trilPic, String trilColor,
            boolean startFly, int isFlying, int isShowMark) {
        this.id = id;
        this.USER_OBJ_ID = USER_OBJ_ID;
        this.OBJ_ID = OBJ_ID;
        this.RING_OBJ_ID = RING_OBJ_ID;
        this.trilWidth = trilWidth;
        this.trilPic = trilPic;
        this.trilColor = trilColor;
        this.startFly = startFly;
        this.isFlying = isFlying;
        this.isShowMark = isShowMark;
    }

    @Generated(hash = 392594895)
    public SetTriBean() {
    }


}
