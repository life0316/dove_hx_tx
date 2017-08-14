package com.haoxi.dove.newin.bean;

import java.util.List;

/**
 * Created by Administrator on 2017\6\12 0012.
 */

public class CircleBean {

    public List<InnerCircleBean> data;
    public int code;
    public String msg;

    public List<InnerCircleBean> getData() {
        return data;
    }

    public void setData(List<InnerCircleBean> data) {
        this.data = data;
    }

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
}
