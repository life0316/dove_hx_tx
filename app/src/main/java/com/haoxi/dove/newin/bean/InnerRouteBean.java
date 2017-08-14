package com.haoxi.dove.newin.bean;

import com.haoxi.dove.utils.InnerRbsConverter;
import com.haoxi.dove.utils.PointConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.util.ArrayList;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lifei on 2017/6/27.
 */

@Entity
public class InnerRouteBean {

    @Id
    private Long id;

    private String stop_time;
    private String status;
    private String fly_recordid;
    private String create_time;
    private String doveid;

    @Convert(columnType = String.class, converter = PointConverter.class)
    private PointBean curloc;

    @Convert(columnType = String.class, converter = InnerRbsConverter.class)
    private ArrayList<PointBean> points;

    boolean   open;

    @Generated(hash = 142091516)
    public InnerRouteBean(Long id, String stop_time, String status,
            String fly_recordid, String create_time, String doveid,
            PointBean curloc, ArrayList<PointBean> points, boolean open) {
        this.id = id;
        this.stop_time = stop_time;
        this.status = status;
        this.fly_recordid = fly_recordid;
        this.create_time = create_time;
        this.doveid = doveid;
        this.curloc = curloc;
        this.points = points;
        this.open = open;
    }

    @Generated(hash = 635163612)
    public InnerRouteBean() {
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getStop_time() {
        return stop_time;
    }

    public void setStop_time(String stop_time) {
        this.stop_time = stop_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFly_recordid() {
        return fly_recordid;
    }

    public void setFly_recordid(String fly_recordid) {
        this.fly_recordid = fly_recordid;
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

    public PointBean getCurloc() {
        return curloc;
    }

    public void setCurloc(PointBean curloc) {
        this.curloc = curloc;
    }

    public ArrayList<PointBean> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<PointBean> points) {
        this.points = points;
    }

    @Override
    public String toString() {

        return super.toString();
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
}
