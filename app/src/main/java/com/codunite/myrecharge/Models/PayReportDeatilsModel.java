package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PayReportDeatilsModel {

    @SerializedName("EWalletTransactionID")
    @Expose
    private Integer eWalletTransactionID;
    @SerializedName("Amount")
    @Expose
    private Double amount;
    @SerializedName("Narration")
    @Expose
    private String narration;
    @SerializedName("Adddate")
    @Expose
    private String adddate;
    @SerializedName("Transtime")
    @Expose
    private String transtime;

    public Integer getEWalletTransactionID() {
        return eWalletTransactionID;
    }

    public void setEWalletTransactionID(Integer eWalletTransactionID) {
        this.eWalletTransactionID = eWalletTransactionID;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
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

}
