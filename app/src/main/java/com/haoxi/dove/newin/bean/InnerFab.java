package com.haoxi.dove.newin.bean;

/**
 * Created by Administrator on 2017\6\21 0021.
 */

public class InnerFab {
    private String headpic;
    private String username;
    private String fabulousid;
    private String create_time;
    private String userid;
    private String circleid;
    private boolean is_friend;  //是否是关注好友


    public boolean isIs_friend() {
        return is_friend;
    }

    public void setIs_friend(boolean is_friend) {
        this.is_friend = is_friend;
    }

    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFabulousid() {
        return fabulousid;
    }

    public void setFabulousid(String fabulousid) {
        this.fabulousid = fabulousid;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }
}
