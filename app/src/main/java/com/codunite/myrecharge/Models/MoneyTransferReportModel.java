package com.codunite.myrecharge.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MoneyTransferReportModel {

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("AccountNumber")
    @Expose
    private String accountNumber;
    @SerializedName("AccountIFSC")
    @Expose
    private String accountIFSC;
    @SerializedName("Amount")
    @Expose
    private Double amount;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("Customermobile")
    @Expose
    private String customermobile;
    @SerializedName("Surcharge")
    @Expose
    private Double surcharge;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Adddate")
    @Expose
    private String adddate;
    @SerializedName("Transtime")
    @Expose
    private String transtime;
    @SerializedName("transID")
    @Expose
    private String transID;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountIFSC() {
        return accountIFSC;
    }

    public void setAccountIFSC(String accountIFSC) {
        this.accountIFSC = accountIFSC;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomermobile() {
        return customermobile;
    }

    public void setCustomermobile(String customermobile) {
        this.customermobile = customermobile;
    }

    public Double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Double surcharge) {
        this.surcharge = surcharge;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAdddate() {
        return adddate;
    }

    public void setAdddate(String adddate) {
        this.adddate = adddate;
    }

    public String getTranstime() {
        return transtime;
    }

    public void setTranstime(String transtime) {
        this.transtime = transtime;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

}
