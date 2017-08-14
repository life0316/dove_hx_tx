package com.haoxi.dove.bean;

import java.io.Serializable;

/**
 * Created by lifei on 2016/12/22.
 */

public class User implements Serializable {


    /**
     * CLUB : null
     * USER_PWD : q88aCj2f6xRx5LQDCCLoIa/WzVYX3e3yf87GRojnSEyKn7Vw27TH16dnbt6kAptNAzOJlyhYoOolXZMfVv+AckQdTrQVtWqCHeByHsxP7EQt7Fhi0I2TrLYHZ4ndkgdqHD6EKabVvFNXaK0f3V6qX+2ASuHJhi6Ifm5HzzbxEHY=
     * DOVECOTE_NAME : 测试
     * PIGENO_UNION : null
     * OBJ_ID : 0b60a9c358f6906b0158f70277b0000a
     * USER_PHONE : 13761510351
     * CODES : 200
     * USER_CODE : 950744409
     * USER_AGE : 2011-12-30 00:00:00
     * ENROLLMENT_TIME : 2016-12-13 15:08:03
     * FEED_PIGEON_YEAR : 1
     * USER_SEX : 1
     * TOKEN : 024AA88EB8B8366F7F5F0B2CB1FC6BB1
     *
     *
     *
     String CODES;
     String TOKEN;
     String OBJ_ID;
     String USER_CODE;
     String USER_PHONE;
     String DOVECOTE_NAME;
     String CLUB;
     String PIGENO_UNION;
     String USER_AGE;
     String USER_SEX;
     String FEED_PIGEON_YEAR;

     String USER_EMAIL;
     String expired_time;
     String USER_PWD;
     String ENROLLMENT_TIME;
     String secret_key;
     */

    private Object CLUB;
    private String USER_PWD;
    private String USER_EMAIL;
    private String DOVECOTE_NAME;
    private Object PIGENO_UNION;
    private String OBJ_ID;
    private String USER_PHONE;
    private String CODES;
    private String USER_CODE;
    private String USER_AGE;
    private String ENROLLMENT_TIME;
    private int FEED_PIGEON_YEAR;
    private String USER_SEX;
    private String TOKEN;
    private String expired_time;
    private String secret_key;

    private String USER_OBJ_ID;

    public String getUSER_OBJ_ID() {
        return USER_OBJ_ID;
    }

    public void setUSER_OBJ_ID(String USER_OBJ_ID) {
        this.USER_OBJ_ID = USER_OBJ_ID;
    }

    public String getUSER_EMAIL() {
        return USER_EMAIL;
    }

    public void setUSER_EMAIL(String USER_EMAIL) {
        this.USER_EMAIL = USER_EMAIL;
    }

    public String getExpired_time() {
        return expired_time;
    }

    public void setExpired_time(String expired_time) {
        this.expired_time = expired_time;
    }

    public String getSecret_key() {
        return secret_key;
    }

    public void setSecret_key(String secret_key) {
        this.secret_key = secret_key;
    }

    public Object getCLUB() {
        return CLUB;
    }

    public void setCLUB(Object CLUB) {
        this.CLUB = CLUB;
    }

    public String getUSER_PWD() {
        return USER_PWD;
    }

    public void setUSER_PWD(String USER_PWD) {
        this.USER_PWD = USER_PWD;
    }

    public String getDOVECOTE_NAME() {
        return DOVECOTE_NAME;
    }

    public void setDOVECOTE_NAME(String DOVECOTE_NAME) {
        this.DOVECOTE_NAME = DOVECOTE_NAME;
    }

    public Object getPIGENO_UNION() {
        return PIGENO_UNION;
    }

    public void setPIGENO_UNION(Object PIGENO_UNION) {
        this.PIGENO_UNION = PIGENO_UNION;
    }

    public String getOBJ_ID() {
        return OBJ_ID;
    }

    public void setOBJ_ID(String OBJ_ID) {
        this.OBJ_ID = OBJ_ID;
    }

    public String getUSER_PHONE() {
        return USER_PHONE;
    }

    public void setUSER_PHONE(String USER_PHONE) {
        this.USER_PHONE = USER_PHONE;
    }

    public String getCODES() {
        return CODES;
    }

    public void setCODES(String CODES) {
        this.CODES = CODES;
    }

    public String getUSER_CODE() {
        return USER_CODE;
    }

    public void setUSER_CODE(String USER_CODE) {
        this.USER_CODE = USER_CODE;
    }

    public String getUSER_AGE() {
        return USER_AGE;
    }

    public void setUSER_AGE(String USER_AGE) {
        this.USER_AGE = USER_AGE;
    }

    public String getENROLLMENT_TIME() {
        return ENROLLMENT_TIME;
    }

    public void setENROLLMENT_TIME(String ENROLLMENT_TIME) {
        this.ENROLLMENT_TIME = ENROLLMENT_TIME;
    }

    public int getFEED_PIGEON_YEAR() {
        return FEED_PIGEON_YEAR;
    }

    public void setFEED_PIGEON_YEAR(int FEED_PIGEON_YEAR) {
        this.FEED_PIGEON_YEAR = FEED_PIGEON_YEAR;
    }

    public String getUSER_SEX() {
        return USER_SEX;
    }

    public void setUSER_SEX(String USER_SEX) {
        this.USER_SEX = USER_SEX;
    }

    public String getTOKEN() {
        return TOKEN;
    }

    public void setTOKEN(String TOKEN) {
        this.TOKEN = TOKEN;
    }
}
