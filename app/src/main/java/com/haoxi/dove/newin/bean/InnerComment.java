package com.haoxi.dove.newin.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017\6\21 0021.
 */

@Entity
public class InnerComment {

    @Id
    Long id;

    //首次评论
    String content;     //评论内容
    String commentid;   //评论id
    String headpic;     //评论者头像
    String username;
    String create_time;
    String userid;
    String reply;
    String circleid;

    //回复首次评论给 某某
    String to_username;

    @Generated(hash = 1908807812)
    public InnerComment(Long id, String content, String commentid, String headpic,
            String username, String create_time, String userid, String reply,
            String circleid, String to_username) {
        this.id = id;
        this.content = content;
        this.commentid = commentid;
        this.headpic = headpic;
        this.username = username;
        this.create_time = create_time;
        this.userid = userid;
        this.reply = reply;
        this.circleid = circleid;
        this.to_username = to_username;
    }

    @Generated(hash = 1596483738)
    public InnerComment() {
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
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

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getCircleid() {
        return circleid;
    }

    public void setCircleid(String circleid) {
        this.circleid = circleid;
    }

    public String getTo_username() {
        return to_username;
    }

    public void setTo_username(String to_username) {
        this.to_username = to_username;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
