package com.haoxi.dove.newin.bean;

import java.util.List;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class AttentionBean {

    public int code;
    public String msg;
    public List<InnerAttention> data;


    public List<InnerAttention> getData() {
        return data;
    }

    public void setData(List<InnerAttention> data) {
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
