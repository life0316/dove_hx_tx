package com.haoxi.dove.newin.bean;

public class StartFlyBean {

    private int code;
    private String msg;
    private InnerStart data;

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

    public InnerStart getData() {
        return data;
    }

    public void setData(InnerStart data) {
        this.data = data;
    }

    public class InnerStart{
        private String fly_recordid;

        public String getFly_recordid() {
            return fly_recordid;
        }

        public void setFly_recordid(String fly_recordid) {
            this.fly_recordid = fly_recordid;
        }

    }
}
