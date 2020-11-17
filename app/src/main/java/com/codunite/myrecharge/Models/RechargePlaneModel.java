package com.codunite.myrecharge.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RechargePlaneModel {

    @SerializedName("HistoryID")
    @Expose
    private Integer historyID;
    @SerializedName("Mobileno")
    @Expose
    private String mobileno;
    @SerializedName("RechargeAmount")
    @Expose
    private Double rechargeAmount;
    @SerializedName("TransID")
    @Expose
    private String transID;
    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("AddDate")
    @Expose
    private String addDate;
    @SerializedName("OperatorName")
    @Expose
    private String operatorName;
    @SerializedName("operaotimageurl")
    @Expose
    private String operaotimageurl;
    @SerializedName("rechargetime")
    @Expose
    private String rechargetime;

    public Integer getHistoryID() {
        return historyID;
    }

    public void setHistoryID(Integer historyID) {
        this.historyID = historyID;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public Double getRechargeAmount() {
        return rechargeAmount;
    }

    public void setRechargeAmount(Double rechargeAmount) {
        this.rechargeAmount = rechargeAmount;
    }

    public String getTransID() {
        return transID;
    }

    public void setTransID(String transID) {
        this.transID = transID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperaotimageurl() {
        return operaotimageurl;
    }

    public void setOperaotimageurl(String operaotimageurl) {
        this.operaotimageurl = operaotimageurl;
    }

    public String getRechargetime() {
        return rechargetime;
    }

    public void setRechargetime(String rechargetime) {
        this.rechargetime = rechargetime;
    }

}
