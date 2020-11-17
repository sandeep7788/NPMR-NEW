package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CompanybankdetailsModel {

    @SerializedName("BankerID")
    @Expose
    private Integer bankerID;
    @SerializedName("accountnumber")
    @Expose
    private String accountnumber;
    @SerializedName("Accountholdername")
    @Expose
    private String accountholdername;
    @SerializedName("ifsccode")
    @Expose
    private String ifsccode;
    @SerializedName("AccountType")
    @Expose
    private String accountType;
    @SerializedName("BankBranch")
    @Expose
    private String bankBranch;
    @SerializedName("BankName")
    @Expose
    private String bankName;
    @SerializedName("BankImage")
    @Expose
    private String bankImage;

    public Integer getBankerID() {
        return bankerID;
    }

    public void setBankerID(Integer bankerID) {
        this.bankerID = bankerID;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getAccountholdername() {
        return accountholdername;
    }

    public void setAccountholdername(String accountholdername) {
        this.accountholdername = accountholdername;
    }

    public String getIfsccode() {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode) {
        this.ifsccode = ifsccode;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getBankBranch() {
        return bankBranch;
    }

    public void setBankBranch(String bankBranch) {
        this.bankBranch = bankBranch;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankImage() {
        return bankImage;
    }

    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }

}