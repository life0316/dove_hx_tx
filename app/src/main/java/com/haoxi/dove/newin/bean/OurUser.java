package com.haoxi.dove.newin.bean;

public class OurUser {
    public int code;
    public String msg;
    public InnerData data;
    public InnerData getData() {
        return data;
    }
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
    public void setCode(int code) {
        this.code = code;
    }
    public void setData(InnerData data) {
        this.data = data;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public class InnerData{
        public String token;
        public String userid;
        public String getToken() {
            return token;
        }
        public String getUserid() {
            return userid;
        }
        public void setToken(String token) {
            this.token = token;
        }
        public void setUserid(String userid) {
            this.userid = userid;
        }
    }
}
