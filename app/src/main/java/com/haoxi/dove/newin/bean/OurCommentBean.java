package com.haoxi.dove.newin.bean;

import java.util.List;

/**
 * Created by Administrator on 2017\6\21 0021.
 */

public class OurCommentBean {

    List<InnerComment> data;

    int code;

    String msg;

    public List<InnerComment> getData() {
        return data;
    }

    public void setData(List<InnerComment> data) {
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
