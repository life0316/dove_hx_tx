package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class OurUserInfo implements Parcelable {

    public int code;
    public String msg;
    public InnerData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public InnerData getData() {
        return data;
    }

    public void setData(InnerData data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(code);

        dest.writeString(msg);

        dest.writeParcelable(this.data,flags);
    }

    public static Parcelable.Creator<OurUserInfo> CREATOR = new Creator<OurUserInfo>() {
        @Override
        public OurUserInfo createFromParcel(Parcel source) {

            OurUserInfo ourUserInfo = new OurUserInfo();
            ourUserInfo.code = source.readInt();
            ourUserInfo.msg = source.readString();
            ourUserInfo.data = source.readParcelable(InnerData.class.getClassLoader());

            return ourUserInfo;
        }

        @Override
        public OurUserInfo[] newArray(int size) {
            return new OurUserInfo[size];
        }
    };

}
