package com.haoxi.dove.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by lifei on 2017/4/6.
 */

@Entity
public class RouteBean implements Parcelable {
    @Id
    Long id;

    @Unique
    String obj_id;

    String user_obj_id;

    String route_name;
    String route_start_address;
    String route_start_time;
    String route_end_address;
    String route_end_time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoute_end_time() {

        return route_end_time;
    }

    public void setRoute_end_time(String route_end_time) {
        this.route_end_time = route_end_time;
    }

    public String getRoute_end_address() {
        return route_end_address;
    }

    public void setRoute_end_address(String route_end_address) {
        this.route_end_address = route_end_address;
    }

    public String getRoute_start_time() {
        return route_start_time;
    }

    public void setRoute_start_time(String route_start_time) {
        this.route_start_time = route_start_time;
    }

    public String getRoute_start_address() {
        return route_start_address;
    }

    public void setRoute_start_address(String route_start_address) {
        this.route_start_address = route_start_address;
    }

    public String getRoute_name() {
        return route_name;
    }

    public void setRoute_name(String route_name) {
        this.route_name = route_name;
    }

    public String getUser_obj_id() {
        return user_obj_id;
    }

    public void setUser_obj_id(String user_obj_id) {
        this.user_obj_id = user_obj_id;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(user_obj_id);
        dest.writeString(route_name);
        dest.writeString(route_start_address);
        dest.writeString(route_start_time);
        dest.writeString(route_end_address);
        dest.writeString(route_end_time);
        dest.writeString(obj_id);
    }

    public static final Parcelable.Creator<RouteBean> CREATOR = new Creator<RouteBean>() {
        @Override
        public RouteBean createFromParcel(Parcel source) {

            RouteBean routeBean = new RouteBean();
            routeBean.obj_id = source.readString();
            routeBean.user_obj_id = source.readString();
            routeBean.route_start_address = source.readString();
            routeBean.route_start_time = source.readString();
            routeBean.route_name = source.readString();
            routeBean.route_end_address = source.readString();
            routeBean.route_end_time = source.readString();

            return routeBean;
        }

        @Override
        public RouteBean[] newArray(int size) {
            return new RouteBean[size];
        }
    };

    @Generated(hash = 585232330)
    public RouteBean(Long id, String obj_id, String user_obj_id, String route_name,
            String route_start_address, String route_start_time, String route_end_address,
            String route_end_time) {
        this.id = id;
        this.obj_id = obj_id;
        this.user_obj_id = user_obj_id;
        this.route_name = route_name;
        this.route_start_address = route_start_address;
        this.route_start_time = route_start_time;
        this.route_end_address = route_end_address;
        this.route_end_time = route_end_time;
    }

    @Generated(hash = 1264139803)
    public RouteBean() {
    }

}
