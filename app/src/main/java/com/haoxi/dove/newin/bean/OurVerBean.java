package com.haoxi.dove.newin.bean;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

public class OurVerBean {

    public int code;
    public String msg;
    public InnerVer data;

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

    public InnerVer getData() {
        return data;
    }

    public void setData(InnerVer data) {
        this.data = data;
    }

    public class InnerVer {

        public String url_ios;
        public String url_android;
        public String version;

        public String getUrl_ios() {
            return url_ios;
        }

        public void setUrl_ios(String url_ios) {
            this.url_ios = url_ios;
        }

        public String getUrl_android() {
            return url_android;
        }

        public void setUrl_android(String url_android) {
            this.url_android = url_android;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }

}
