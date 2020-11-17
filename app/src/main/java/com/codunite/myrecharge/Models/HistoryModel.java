package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HistoryModel {

    @SerializedName("EWalletTransactionID")
    @Expose
    private Integer eWalletTransactionID;
    @SerializedName("Amount")
    @Expose
    private Double amount;
    @SerializedName("Balance")
    @Expose
    private Double balance;
    @SerializedName("Narration")
    @Expose
    private String narration;
    @SerializedName("Adddate")
    @Expose
    private String adddate;
    @SerializedName("Factor")
    @Expose
    private String factor;
    @SerializedName("Wallettime")
    @Expose
    private String wallettime;

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
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

    public String getFactor() {
        return factor;
    }

    public void setFactor(String factor) {
        this.factor = factor;
    }

    public String getWallettime() {
        return wallettime;
    }

    public void setWallettime(String wallettime) {
        this.wallettime = wallettime;
    }

}