package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class OurDoveBean implements Parcelable {

    public int code;
    public String msg;
    public List<InnerDoveData> data;

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

    public List<InnerDoveData> getData() {
        return data;
    }

    public void setData(List<InnerDoveData> data) {
        this.data = data;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(msg);
        dest.writeInt(code);
        dest.writeTypedList(data);

    }
    public Parcelable.Creator<OurDoveBean> CREATOR = new Creator<OurDoveBean>() {
        @Override
        public OurDoveBean createFromParcel(Parcel source) {

            OurDoveBean ourDoveBean = new OurDoveBean();
            ourDoveBean.msg = source.readString();
            ourDoveBean.code = source.readInt();

            if (data == null) {
                data = new ArrayList<InnerDoveData>();
            }

            source.readTypedList(data,InnerDoveData.CREATOR);

            return ourDoveBean;
        }

        @Override
        public OurDoveBean[] newArray(int size) {
            return new OurDoveBean[size];
        }
    };

}
