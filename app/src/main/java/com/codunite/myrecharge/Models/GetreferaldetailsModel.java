package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetreferaldetailsModel  {

    @SerializedName("MemberID")
    @Expose
    private String memberID;
    @SerializedName("MemberName")
    @Expose
    private String memberName;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("adddate")
    @Expose
    private String adddate;
    @SerializedName("password")
    @Expose
    private String password;

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
