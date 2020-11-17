package com.codunite.myrecharge.Helper

import com.codunite.myrecharge.Models.ModelRequestdetails
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*

interface ApiInterface {
//https://paymyrecharge.in/APIMANAGER/oparetor.aspx?Servertype=2

//https://therippco.com/api/wallet.ashx?f=t1&loginid=5002081557&
// FromDate=11/11/1111&ToDate=&ttype=&start=10&length=20

    @POST("loginprocess")
    fun getLogin(
        @Header("username") username: String?,
        @Header("Password") Password: String?
    ): Call<JsonObject>

    @POST("loginprocess")
    fun getbalance(
        @Header("Membermsrno") Membermsrno: String?): Call<JsonObject>

    @POST("getoperatordetails")
    fun getoperatordetails(
        @Header("operatortype") operatortype: String?
    ): Call<JsonObject>

    @GET("apimanager/APIUSE.aspx")
    fun getUrl(
        @Query("UrlName") UrlName: String?): Call<JsonArray>


    @FormUrlEncoded
    @POST("getrechargereport")
    fun getrechargereport(
        @Header("Membermsrno") Membermsrno: String?,
        @Header("recordhistoryID") recordhistoryID: String?,
        @Field("Rechargetype") Rechargetype:String
    ): Call<JsonObject>
//http://recharge.codunite.com/Mobileapi.asmx/getrechargereport
    @FormUrlEncoded
    @POST("Rechargeprcess")
    fun getRechargeprcess(
        @Header("username") username: String?,
        @Header("password") password: String?,
        @Field("RechargeAmount") RechargeAmount:String,
        @Field("Operator") Opreator:String,
        @Field("Number") Number:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("getWalletreport")
    fun getHistory(
        @Header("Membermsrno") Membermsrno: String?,
        @Header("recordWalletID") recordhistoryID: String?,
        @Field(".") Rechargetype:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("getbalance")
    fun getUserBalance(
        @Header("Membermsrno") Membermsrno: String?,
        @Field(".") Rechargetype:String
    ): Call<JsonObject>





    @FormUrlEncoded
    @POST("GetPlandetails")
    fun GetPlandetails(
        @Field("Operator") Rechargetype:String
    ): Call<com.codunite.myrecharge.Models.ModelRequestdetails>

    @FormUrlEncoded
    @POST("billGetFatchbilleroperatordetails")
    fun get_billGetFatchbilleroperatordetails(
        @Header("username") username:String,
        @Header("password") password:String,
        @Field("Operator") Operator:String,
        @Field("servicenum") servicenum:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("MoneyTransfer")
    fun MoneyTransfer(
        @Header("username") username:String,
        @Header("password") password:String,
        @Header("amount") amount:String,
        @Header("MemberAccountID") MemberAccountID:String,
        @Field(".") Operator:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("billGetFatchdetails")
    fun billGetFatchdetails(
        @Field("Operator") Operator:String,
        @Field("servicenum") servicenum:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("PayFatchMemberDeatils")
    fun PayFatchMemberDeatils(
        @Header("MemberID") MemberID:String,
        @Field(".") a:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("PayFundtransfertomember")
    fun PayFundtransfertomember(
        @Header("FromMember") FromMember:String,
        @Header("Tomember") Tomember:String,
        @Field("Amount") Amount:String
    ): Call<JsonObject>

    @FormUrlEncoded
        @POST("PayReportDeatils")
    fun PayReportDeatils(
        @Header("Membermsrno") Membermsrno:String,
            @Header("recordPaymentID") recordPaymentID:String,
        @Field(".") Amount:String
    ): Call<JsonObject>

    @FormUrlEncoded
        @POST("MoneyTransferReport")
    fun MoneyTransferReport(
        @Field("Msrno") Membermsrno:String,
            @Field("RecordID") recordPaymentID:String
    ): Call<JsonObject>

    @POST("getAllbanknamedetails")
    fun getAllbanknamedetails(): Call<JsonObject>

    @FormUrlEncoded
    @POST("GetMemberBankAccount")
    fun GetMemberBankAccount(@Field("Msrno")Msrno:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("AddMemberBankAccount")
    fun AddMemberBankAccount(@Field("Msrno")Msrno:String,
                             @Field("BankID")BankID:String,
                             @Field("customernumber")customernumber:String,
                             @Field("Accountnumber")Accountnumber:String,
                             @Field("CustomerName")CustomerName:String,
                             @Field("ifsccode")ifsccode:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("EditMemberBankAccount")
        fun EditMemberBankAccount(
        @Header("username") username:String,
        @Header("password") password:String,
        @Field("Msrno")Msrno:String,
        @Field("AccountID")AccountID:String,
        @Field("BankID")BankID:String,
        @Field("customernumber")customernumber:String,
        @Field("Accountnumber")Accountnumber:String,
        @Field("CustomerName")CustomerName:String,
        @Field("ifsccode")ifsccode:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("BillElectricitypayment")
        fun BillElectricitypayment(
                            @Header("username") username:String,
                            @Header("password") password:String,
                            @Field("RechargeAmount")RechargeAmount:String,
                            @Field("Operator")Operator:String,
                             @Field("Number")Number:String,
                             @Field("CustomNumber")CustomNumber:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("UpdateMemberProfile")
        fun UpdateMemberProfile(
                            @Header("Msrno") Msrno:String,
                            @Field("FirstName") FirstName:String,
                            @Field("LastName")LastName:String,
                            @Field("Mobile")Mobile:String,
                             @Field("Email")Email:String,
                             @Field("Address")Address:String,
                             @Field("landmark")landmark:String,
                             @Field("CountryID")CountryID:String,
                             @Field("ZIP")ZIP:String,
                             @Field("GSTno")GSTno:String,
                             @Field("stateID")stateID:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("Addnewmember")
    fun Addnewmember(
        @Header("Msrno") Msrno:String,
        @Field("FirstName") FirstName:String,
        @Field("LastName")LastName:String,
        @Field("Mobile")Mobile:String,
        @Field("Email")Email:String,
        @Field("Address")Address:String,
        @Field("landmark")landmark:String,
        @Field("CountryID")CountryID:String,
        @Field("ZIP")ZIP:String,
        @Field("GSTno")GSTno:String,
        @Field("stateID")stateID:String
    ): Call<JsonObject>

    @FormUrlEncoded
    @POST("RemoveMemberBankAccount")
    fun RemoveMemberBankAccount(@Header("username") username:String,
                                @Header("password") password:String,
                                @Field("AccountID")AccountID:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("Getreferaldetails")
    fun Getreferaldetails(@Header("Membermsrno") Membermsrno:String,
                                @Field("AccountID")AccountID:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("ChangePassword")
    fun ChangePassword(     @Header("Msrno") username:String,
                                     @Header("oldpassword") oldpassword:String,
                             @Header("newpassword")newpassword:String
    ,@Field(".") mField:String): Call<JsonObject>

    @FormUrlEncoded
    @POST("ForgotPassword")
    fun ForgotPassword(     @Header("username") username:String
    ,@Field(".") mField:String): Call<JsonObject>


    @POST("getAllState")
    fun getAllState(): Call<JsonObject>

    @POST("Companybankdetails")
    fun Companybankdetails(): Call<JsonObject>
}