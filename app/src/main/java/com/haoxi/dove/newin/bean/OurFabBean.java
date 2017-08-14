package com.haoxi.dove.newin.bean;

import java.util.List;

/**
 * Created by Administrator on 2017\6\21 0021.
 */

public class OurFabBean {
    List<InnerFab> data;
    int code;
    String msg;

    public List<InnerFab> getData() {
        return data;
    }

    public void setData(List<InnerFab> data) {
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
