package com.haoxi.dove.retrofit.ad;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017\8\18 0018.
 */

public class AdviewResObj {

    int res;                //广告回应情况标识
    int co;                 //返回的广告条数
    String mg;              //广告回应失败的原因
    List<AdBody> ad;        //广告信息，正确返回 0 或多个广告



    public int getCo() {
        return co;
    }

    public int getRes() {
        return res;
    }

    public String getMg() {
        return mg;
    }

    public List<AdBody> getAd() {
        return ad;
    }

    public void setRes(int res) {
        this.res = res;
    }

    public void setCo(int co) {
        this.co = co;
    }

    public void setMg(String mg) {
        this.mg = mg;
    }

    public void setAd(List<AdBody> ad) {
        this.ad = ad;
    }

    class AdBody{

        String posId;       //广告位 id
        int act;            //广告动作类型 1广告页
        int at;             //返回的广告素材类型
        String as;          //广告尺寸
        String aic;         //广告图标url
        String ate;         //广告内容文字
        String ast;         //广告副标题
        String ati;         //广告标题
        List<String> api;   //图片广告时，图片的url地址
        String adIcon;      //广告字样
        String al;          //点击跳转地址
        List<String> ec;    // 点击监控url

        Map<String,List<String>> es;

        public Map<String, List<String>> getEs() {
            return es;
        }

        public void setEs(Map<String, List<String>> es) {
            this.es = es;
        }

        public String getPosId() {
            return posId;
        }

        public void setPosId(String posId) {
            this.posId = posId;
        }

        public int getAct() {
            return act;
        }

        public void setAct(int act) {
            this.act = act;
        }

        public int getAt() {
            return at;
        }

        public void setAt(int at) {
            this.at = at;
        }

        public String getAs() {
            return as;
        }

        public void setAs(String as) {
            this.as = as;
        }

        public String getAic() {
            return aic;
        }

        public void setAic(String aic) {
            this.aic = aic;
        }

        public String getAte() {
            return ate;
        }

        public void setAte(String ate) {
            this.ate = ate;
        }

        public String getAst() {
            return ast;
        }

        public void setAst(String ast) {
            this.ast = ast;
        }

        public String getAti() {
            return ati;
        }

        public void setAti(String ati) {
            this.ati = ati;
        }

        public List<String> getApi() {
            return api;
        }

        public void setApi(List<String> api) {
            this.api = api;
        }

        public String getAdIcon() {
            return adIcon;
        }

        public void setAdIcon(String adIcon) {
            this.adIcon = adIcon;
        }

        public String getAl() {
            return al;
        }

        public void setAl(String al) {
            this.al = al;
        }

        public List<String> getEc() {
            return ec;
        }

        public void setEc(List<String> ec) {
            this.ec = ec;
        }
    }

}
