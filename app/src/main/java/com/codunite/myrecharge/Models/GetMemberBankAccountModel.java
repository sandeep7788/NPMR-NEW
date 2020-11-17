package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetMemberBankAccountModel {
    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("BankID")
    @Expose
    private Integer bankID;
    @SerializedName("BankName")
    @Expose
    private String bankName;
    @SerializedName("customernumber")
    @Expose
    private String customernumber;
    @SerializedName("Accountnumber")
    @Expose
    private String accountnumber;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("ifsccode")
    @Expose
    private String ifsccode;
    @SerializedName("BankImage")
    @Expose
    private String bankImage;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public Integer getBankID() {
        return bankID;
    }

    public void setBankID(Integer bankID) {
        this.bankID = bankID;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCustomernumber() {
        return customernumber;
    }

    public void setCustomernumber(String customernumber) {
        this.customernumber = customernumber;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getIfsccode() {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode) {
        this.ifsccode = ifsccode;
    }

    public String getBankImage() {
        return bankImage;
    }

    public void setBankImage(String bankImage) {
        this.bankImage = bankImage;
    }
}