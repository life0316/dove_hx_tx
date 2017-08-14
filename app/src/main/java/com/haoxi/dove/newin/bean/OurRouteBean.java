package com.haoxi.dove.newin.bean;

import java.util.List;

/**
 * Created by lifei on 2017/6/27.
 */

public class OurRouteBean {


    private int code;
    private String msg;

    private List<InnerRouteBean> data;

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

    public List<InnerRouteBean> getData() {
        return data;
    }

    public void setData(List<InnerRouteBean> data) {
        this.data = data;
    }
}
