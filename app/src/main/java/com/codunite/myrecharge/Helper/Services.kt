package com.codunite.myrecharge.Helper

import com.google.gson.JsonArray
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

class   Services
{

    var getOperator: OperatorList? = null
    fun getOperator1(): OperatorList? {
        if (getOperator == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://paymyrecharge.in/APIMANAGER/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            getOperator = retrofit.create(OperatorList::class.java)
        }
        return getOperator
    }

    //    https://paymyrecharge.in/APIMANAGER/oparetor.aspx?Servertype=1
    interface OperatorList {
        @FormUrlEncoded
        @POST("oparetor.aspx?")
        fun getOperatorList(
            @Field("Servertype") Servertype: Int?
        ): Call<JsonArray>
    }
}