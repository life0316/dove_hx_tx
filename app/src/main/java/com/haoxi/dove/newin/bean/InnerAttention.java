package com.haoxi.dove.newin.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017\6\12 0012.
 */

@Entity
public class InnerAttention{

    @Id
    private Long id;

    private String headpic;
    private String nickname;
    private int age;

    @Unique
    private String userid;
    private String gender;
    private String experience;
    private String loftname;
    private String telephone;


    @Generated(hash = 887262941)
    public InnerAttention(Long id, String headpic, String nickname, int age,
            String userid, String gender, String experience, String loftname,
            String telephone) {
        this.id = id;
        this.headpic = headpic;
        this.nickname = nickname;
        this.age = age;
        this.userid = userid;
        this.gender = gender;
        this.experience = experience;
        this.loftname = loftname;
        this.telephone = telephone;
    }

    @Generated(hash = 1034930050)
    public InnerAttention() {
    }


    public String getHeadpic() {
        return headpic;
    }

    public void setHeadpic(String headpic) {
        this.headpic = headpic;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getLoftname() {
        return loftname;
    }

    public void setLoftname(String loftname) {
        this.loftname = loftname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}

