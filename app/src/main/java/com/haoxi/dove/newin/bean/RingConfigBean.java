package com.haoxi.dove.newin.bean;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class RingConfigBean {

    public int code;
    public String msg;
    public InnerConfig data;

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

    public InnerConfig getData() {
        return data;
    }

    public void setData(InnerConfig data) {
        this.data = data;
    }

    public class InnerConfig{
        public int on_off_status;
        public String off_time;
        public String on_time;
        public String configid;
        public int lfq;
        public int rfq;

        public int getOn_off_status() {
            return on_off_status;
        }

        public void setOn_off_status(int on_off_status) {
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

        public String getConfigid() {
            return configid;
        }

        public void setConfigid(String configid) {
            this.configid = configid;
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
    }

}
