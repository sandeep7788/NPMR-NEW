package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankListModel {

    @SerializedName("BankerMasterID")
    @Expose
    private int BankerMasterID;
    @SerializedName("BankerMasterName")
    @Expose
    private String BankerMasterName;

    public String getBankImage() {
        return BankImage;
    }

    public void setBankImage(String bankImage) {
        BankImage = bankImage;
    }

    @SerializedName("BankImage")
    @Expose
    private String BankImage;

    public int getBankerMasterID() {
        return BankerMasterID;
    }

    public void setBankerMasterID(int bankerMasterID) {
        BankerMasterID = bankerMasterID;
    }

    public String getBankerMasterName() {
        return BankerMasterName;
    }

    public void setBankerMasterName(String bankerMasterName) {
        BankerMasterName = bankerMasterName;
    }
}