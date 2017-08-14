package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class OurRingBean  implements Parcelable {
    public int code;
    public String msg;
    List<InnerRing> data;

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

    public List<InnerRing> getData() {
        return data;
    }

    public void setData(List<InnerRing> data) {
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

    public Parcelable.Creator<OurRingBean> CREATOR = new Creator<OurRingBean>() {
        @Override
        public OurRingBean createFromParcel(Parcel source) {

            OurRingBean ourRingBean = new OurRingBean();
            ourRingBean.msg = source.readString();
            ourRingBean.code = source.readInt();

            if (data == null) {
                data = new ArrayList<InnerRing>();
            }

            source.readTypedList(data,InnerRing.CREATOR);

            return ourRingBean;
        }

        @Override
        public OurRingBean[] newArray(int size) {
            return new OurRingBean[size];
        }
    };


}
