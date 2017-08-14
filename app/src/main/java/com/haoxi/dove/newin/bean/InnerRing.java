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
public class InnerRing implements Parcelable{

    @Id
    Long id;

    public String on_off_status;
    public String off_time;
    public String on_time;

    @Unique
    public String ringid;
    public int lfq;
    public int rfq;
    public String ring_code;
    public String dove_code;

    public String create_time;
    public String doveid;

    public String playerid;

    boolean   open;


    public String getPlayerid() {
        return playerid;
    }

    public void setPlayerid(String playerid) {
        this.playerid = playerid;
    }

    public String getDove_code() {
        return dove_code;
    }

    public void setDove_code(String dove_code) {
        this.dove_code = dove_code;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getRing_code() {
        return ring_code;
    }

    public void setRing_code(String ring_code) {
        this.ring_code = ring_code;
    }

    public String getRingid() {
        return ringid;
    }

    public void setRingid(String ringid) {
        this.ringid = ringid;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getDoveid() {
        return doveid;
    }

    public void setDoveid(String doveid) {
        this.doveid = doveid;
    }

    public String getOn_off_status() {
        return on_off_status;
    }

    public void setOn_off_status(String on_off_status) {
        this.on_off_status = on_off_status;
    }

    public String getOff_time() {
        return off_time;
    }

    public void setOff_time(String off_time) {
        this.off_time = off_time;
    }

    public String getOn_time() {
        return on_time;
    }

    public void setOn_time(String on_time) {
        this.on_time = on_time;
    }

    public int getLfq() {
        return lfq;
    }

    public void setLfq(int lfq) {
        this.lfq = lfq;
    }

    public int getRfq() {
        return rfq;
    }

    public void setRfq(int rfq) {
        this.rfq = rfq;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(lfq);
        dest.writeInt(rfq);
        dest.writeString(on_off_status);
        dest.writeString(off_time);
        dest.writeString(on_time);
        dest.writeString(ringid);
        dest.writeString(ring_code);
        dest.writeString(create_time);
        dest.writeString(doveid);
        dest.writeString(dove_code);


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
    public static Parcelable.Creator<InnerRing> CREATOR = new Creator<InnerRing>() {
        @Override
        public InnerRing createFromParcel(Parcel source) {

            InnerRing innerRing = new InnerRing();
            innerRing.lfq = source.readInt();
            innerRing.rfq = source.readInt();
            innerRing.on_off_status = source.readString();
            innerRing.off_time = source.readString();
            innerRing.on_time = source.readString();
            innerRing.ringid = source.readString();
            innerRing.ring_code = source.readString();
            innerRing.create_time = source.readString();
            innerRing.doveid = source.readString();
            innerRing.dove_code = source.readString();

            return innerRing;
        }

        @Override
        public InnerRing[] newArray(int size) {
            return new InnerRing[size];
        }
    };

    @Generated(hash = 1160379541)
    public InnerRing(Long id, String on_off_status, String off_time, String on_time,
            String ringid, int lfq, int rfq, String ring_code, String dove_code,
            String create_time, String doveid, String playerid, boolean open) {
        this.id = id;
        this.on_off_status = on_off_status;
        this.off_time = off_time;
        this.on_time = on_time;
        this.ringid = ringid;
        this.lfq = lfq;
        this.rfq = rfq;
        this.ring_code = ring_code;
        this.dove_code = dove_code;
        this.create_time = create_time;
        this.doveid = doveid;
        this.playerid = playerid;
        this.open = open;
    }

    @Generated(hash = 1469222011)
    public InnerRing() {
    }
}

