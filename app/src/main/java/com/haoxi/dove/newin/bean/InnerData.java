package com.haoxi.dove.newin.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2017\6\6 0006.
 */

@Entity
public class  InnerData implements Parcelable {

    @Id
    private Long id;

    @Unique
    private String userid;

    private String headpic;
    private String nickname;
    private int age;

    private String gender;
    private String experience;
    private String loftname;
    private String telephone;
    private String user_birth;

    public String getUser_birth() {
        return user_birth;
    }

    public void setUser_birth(String user_birth) {
        this.user_birth = user_birth;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(age);
        dest.writeString(headpic);
        dest.writeString(nickname);
        dest.writeString(userid);
        dest.writeString(gender);
        dest.writeString(experience);
        dest.writeString(loftname);
        dest.writeString(telephone);
        dest.writeString(user_birth);

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static Parcelable.Creator<InnerData> CREATOR = new Creator<InnerData>() {
        @Override
        public InnerData createFromParcel(Parcel source) {

            InnerData innerData = new InnerData();
            innerData.age = source.readInt();
            innerData.headpic = source.readString();
            innerData.nickname = source.readString();
            innerData.userid = source.readString();
            innerData.gender = source.readString();
            innerData.experience = source.readString();
            innerData.loftname = source.readString();
            innerData.telephone = source.readString();
            innerData.user_birth = source.readString();

            return innerData;
        }

        @Override
        public InnerData[] newArray(int size) {
            return new InnerData[size];
        }
    };

    @Generated(hash = 114319933)
    public InnerData(Long id, String userid, String headpic, String nickname,
            int age, String gender, String experience, String loftname,
            String telephone, String user_birth) {
        this.id = id;
        this.userid = userid;
        this.headpic = headpic;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.experience = experience;
        this.loftname = loftname;
        this.telephone = telephone;
        this.user_birth = user_birth;
    }
    @Generated(hash = 1355322361)
    public InnerData() {
    }
}
