package com.codunite.myrecharge.Activitys.Manus

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Adapter.Pay_Adapter
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.HistoryModel
import com.codunite.myrecharge.Models.OperatorModel
import com.codunite.myrecharge.Models.PayReportDeatilsModel
import com.codunite.myrecharge.Models.RechargePlaneModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityPayBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public class Pay_Activity : AppCompatActivity(), DialogInterface.OnClickListener, OnItemClickListener
{
    lateinit var mainBinding : ActivityPayBinding
    var pref= com.codunite.myrecharge.Helper.Local_data(this@Pay_Activity)
    var TAG="@@pay_activty"
    lateinit var title:ArrayList<String>
    lateinit var operator_image:ArrayList<String>
    var pausingDialog: SweetAlertDialog?=null
    var adapter: Pay_Adapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var rechargeReportHistory:ArrayList<com.codunite.myrecharge.Models.PayReportDeatilsModel> = java.util.ArrayList()
    var viewAll_Status=true
    var list_Status=false
    var Rechargetype="0"
    var last_history_ID=0
    var mOperator_CODE="101"
    var operator_type="1"
    var isLoading=false
    protected var handler: Handler? = null
    var ifFirstTimeCall=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_pay)
        pref.setMyappContext(this@Pay_Activity)
        title= ArrayList()
        operator_image= ArrayList()
        pausingDialog =SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)

        linearLayoutManager = LinearLayoutManager(
            this@Pay_Activity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = Pay_Adapter(this@Pay_Activity, rechargeReportHistory)
        mainBinding.mainRecycler.adapter=adapter

        mainBinding.imgEmptylist.visibility=View.GONE
        getSannedId()
        val asyncTask = AsyncTaskExample()
        asyncTask.execute()

        mainBinding.mainRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
                if (recyclerView.canScrollVertically(1).not()
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    Log.d(">>-----", "end")
                    if(isLoading){

                        Log.d(">>-----", "end3")
                        isLoading=false
                        val asyncTask = AsyncTaskExample()
                        asyncTask.execute()
                        mainBinding.mainProgress.visibility=View.VISIBLE
                    }
                }

            }
        })

        mainBinding.toolbarLayout.back.setOnClickListener { super.onBackPressed() }
        mainBinding.txtViewAll.setOnClickListener { viewAll() }
    mainBinding.btnScan.setOnClickListener { startActivity(
        Intent(this@Pay_Activity, Scanner_Activity::class.java)) }

        mainBinding.btnPay.setOnClickListener {

            mainBinding.edtMemberIdorNumber.setError(null)
            mainBinding.edtAmount.setError(null)
            mainBinding.txtChechDetails.setError(null)

            if(mainBinding.edtMemberIdorNumber.text.toString().isEmpty()&&mainBinding.edtAmount.text.toString().isEmpty()) {
                mainBinding.edtMemberIdorNumber.setError("Enter number or ID")
                mainBinding.edtAmount.setError("Enter Amount")
            }
            else if(mainBinding.txtUserfachedDetails.text.toString().isEmpty()&&!mainBinding.txtUserfachedDetails.text.equals("Invalid Details try Again!"))
            {
                mainBinding.txtChechDetails.setError("Check It")
                Toast.makeText(
                    this@Pay_Activity,
                    "Please Enter User id or number",
                    Toast.LENGTH_LONG
                ).show()
            }
            else if(mainBinding.edtMemberIdorNumber.text.toString().isEmpty())
            {
                mainBinding.edtMemberIdorNumber.setError("Enter number or ID")
                Toast.makeText(
                    this@Pay_Activity,
                    "Please Enter Number",
                    Toast.LENGTH_LONG
                ).show()
            }
            else if(mainBinding.edtAmount.text.toString().isEmpty())
            {
                mainBinding.edtAmount.setError("Enter Amount")
            }  else {
                mainBinding.edtMemberIdorNumber.setError(null)
                mainBinding.edtAmount.setError(null)
                mainBinding.txtChechDetails.setError(null)
                pay()
            }
        }

        mainBinding.mainSwiperefresh.setOnRefreshListener {
//            if(isLoading) {
                adapter?.clear()
                rechargeReportHistory.clear()
                last_history_ID = 0
                val asyncTask = AsyncTaskExample()
                asyncTask.execute()
                pausingDialog?.show()
           /* }else {
                mainBinding.mainSwiperefresh.isRefreshing=false
            }*/
        }
        mainBinding.txtChechDetails.setOnClickListener {
            if(mainBinding.edtMemberIdorNumber.text.toString().length>5) {
                mainBinding.edtMemberIdorNumber.setError(null)

                PayFatchMemberDeatils()
            } else {
                mainBinding.edtMemberIdorNumber.setError("Enter number or ID")
            }

        }
        mainBinding.txtViewAll.setOnClickListener { viewAll() }
        mainBinding.toolbarLayout.btnUser.setOnClickListener {
            showProfile()
        }


    }
    fun showProfile() {
        var mIntent: Intent?=null
        mIntent= Intent(this, DashboardActivity::class.java)
        mIntent.putExtra("from_HF",true)
        startActivity(mIntent)
    }

    fun PayFatchMemberDeatils() {
        mainBinding.progressbarMini.visibility=View.VISIBLE
        mainBinding.txtUserfachedDetails.visibility=View.GONE
        var apiInterface:ApiInterface=RetrofitManager(this).instance!!.create(ApiInterface::class.java)

        apiInterface.PayFatchMemberDeatils(
            mainBinding.edtMemberIdorNumber.text.toString().trim(),
            " "
        ).
        enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@Pay_Activity, " Bad Response....", Toast.LENGTH_LONG).show()
                mainBinding.progressbarMini.visibility = View.GONE
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                mainBinding.progressbarMini.visibility = View.GONE
                if (response.isSuccessful) {
                    mainBinding.txtUserfachedDetails.visibility = View.VISIBLE
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    val jsonObject = JSONObject(response.body().toString())
                    //
                    Toast.makeText(
                        this@Pay_Activity,
                        " " + jsonObject.getString("Message"),
                        Toast.LENGTH_LONG
                    ).show()
                    if (jsonObject.getString("Error").toLowerCase().equals("false")) {

                        if (jsonObject.has("Data") && !jsonObject.isNull("Data")) {
                            val json_Array: JSONArray = jsonObject.getJSONArray("Data")

                            val jsonobject1: JSONObject = json_Array.getJSONObject(0)
                            jsonobject1.getString("MemberID")

                            mainBinding.txtUserfachedDetails.visibility = View.VISIBLE
                            mainBinding.txtUserfachedDetails.text = jsonobject1.getString("MemberName") + "[" + jsonobject1.getString("mobile") + "]"
                            mainBinding.txtUserfachedDetails.setTextColor(Color.BLACK)
                        } else {
                            mainBinding.progressbarMini.visibility = View.GONE
                            Toast.makeText(
                                this@Pay_Activity,
                                "Invalid Number or ID",
                                Toast.LENGTH_LONG
                            ).show()
                            mainBinding.txtUserfachedDetails.text="Invalid Details try Again!"
                            mainBinding.txtUserfachedDetails.setTextColor(Color.RED)
                        }
                    } else {
                        mainBinding.progressbarMini.visibility = View.GONE
                        Toast.makeText(this@Pay_Activity, "Invalid Number or ID", Toast.LENGTH_LONG)
                            .show()
                        mainBinding.txtUserfachedDetails.text="Invalid Details try Again!"
                        mainBinding.txtUserfachedDetails.setTextColor(Color.RED)
                    }
                } else {
                    mainBinding.progressbarMini.visibility = View.GONE
                    Toast.makeText(this@Pay_Activity, "Bad Response ! ", Toast.LENGTH_LONG).show()
                    mainBinding.txtUserfachedDetails.text="Invalid Details try Again!"
                    mainBinding.txtUserfachedDetails.setTextColor(Color.RED)

                }


            }

        })
    }

    fun getSannedId() {

//        val i = intent.extras
        if(intent.extras!=null) {
            if(intent?.getStringExtra(com.codunite.myrecharge.Helper.Constances.PREF_MemberID)!!.toString().trim().length>=5&&!intent?.getStringExtra(
                    com.codunite.myrecharge.Helper.Constances.PREF_MemberID
                )!!.trim().equals("null")) {
                Log.e(">>1", intent.getStringExtra(com.codunite.myrecharge.Helper.Constances.PREF_MemberID).toString())
                mainBinding.edtMemberIdorNumber.setText(intent.getStringExtra(com.codunite.myrecharge.Helper.Constances.PREF_MemberID))
                PayFatchMemberDeatils()
                mainBinding.layoutUserId.visibility=View.GONE
            }

        }
    }

    fun viewAll() {
        if(viewAll_Status) {
            mainBinding.topCardview.visibility=View.GONE
            mainBinding.txtViewAll.text="Re-size"
            viewAll_Status=false
        }
        else {
            mainBinding.topCardview.visibility = View.VISIBLE
            mainBinding.txtViewAll.text = "View All"
            viewAll_Status = true
        }
    }


    override fun onClick(p0: DialogInterface?, p1: Int) {
        Log.d(TAG, "onClick: " + p1.toString())
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d(TAG, "onClick: " + p2.toString())
    }

    fun pay() {
        pausingDialog?.show()
        var apiInterface: ApiInterface = RetrofitManager(this@Pay_Activity).instance!!.create(
            ApiInterface::class.java
        )
        apiInterface.PayFundtransfertomember(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_MemberID),
            mainBinding.edtMemberIdorNumber.text.toString(),
            mainBinding.edtAmount.text.toString()
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@Pay_Activity, t.message.toString() + " ", Toast.LENGTH_LONG)
                        .show()
                    pausingDialog?.dismiss()
                    showDialog("Something wrong", "3")
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pausingDialog?.dismiss()
                    if (response.isSuccessful) {
                        Log.d(TAG, "onResponse: "+response.body().toString())
                        var mJSONObject = JSONObject(response.body().toString())
                        Toast.makeText(
                            this@Pay_Activity,
                            mJSONObject.getString("Message").toString() + " ",
                            Toast.LENGTH_LONG
                        ).show()
                        if (mJSONObject.getString("Error").toLowerCase()
                                .equals("false") && mJSONObject != null
                        ) {
                            var mjsonArray: JSONArray = mJSONObject.getJSONArray("Data")
                            var mjsondata = mjsonArray.getJSONObject(0)
                            if (mjsondata.getString("Error").toLowerCase().equals("false")) {
                                showDialog(mjsondata.getString("Message").toString() + " ", "2")
                            } else {
                                showDialog("Something wrong", "1")
                            }
                        } else {
                            showDialog("Something wrong", "3")
                        }
                    } else {
                        showDialog("Something wrong", "3")
                    }
                }

            })
    }

    fun showDialog(title: String, type: String) {
        val pDialog = SweetAlertDialog(this, type.toInt())
        pDialog.setTitleText(title)
        pDialog.setCancelable(false)
        pDialog.setConfirmText("OK")
        pDialog.setConfirmClickListener { sDialog ->
                sDialog.dismiss()
                mainBinding.edtMemberIdorNumber.setText("")
                mainBinding.edtAmount.setText("")
                mainBinding.edtMemberIdorNumber.setText("")
                mainBinding.txtUserfachedDetails.visibility=View.GONE
                mainBinding.layoutUserId.visibility=View.VISIBLE
            }

        pDialog.show()
    }

    fun getrechargereport()
    {

        mainBinding.imgEmptylist.visibility = View.GONE
        mainBinding.mainProgress.visibility = View.VISIBLE
        var apiInterface: ApiInterface = RetrofitManager(this@Pay_Activity).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.PayReportDeatils(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            last_history_ID.toString(),
            Rechargetype
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@Pay_Activity, t.message.toString() + " ", Toast.LENGTH_LONG)
                    .show()
                pausingDialog?.dismiss()
                mainBinding.imgEmptylist.visibility = View.VISIBLE
                mainBinding.mainProgress.visibility = View.GONE
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false)
                } else {
                }

                mainBinding.imgEmptylist.visibility = View.VISIBLE
                mainBinding.mainProgress.visibility = View.GONE

                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: " + response.toString())
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog: com.codunite.myrecharge.Custom_.CustomDialog

                    if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {

                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mPayReportDeatilsModel: com.codunite.myrecharge.Models.PayReportDeatilsModel =
                                    com.codunite.myrecharge.Models.PayReportDeatilsModel()
                                mPayReportDeatilsModel.eWalletTransactionID =
                                    JsonObjectData.getString("EWalletTransactionID").toInt()
                                mPayReportDeatilsModel.amount = JsonObjectData.getDouble("Amount")
                                mPayReportDeatilsModel.narration =
                                    JsonObjectData.getString("Narration")
                                mPayReportDeatilsModel.adddate = JsonObjectData.getString("Adddate")
                                mPayReportDeatilsModel.transtime =
                                    JsonObjectData.getString("Transtime")

                                rechargeReportHistory.add(mPayReportDeatilsModel)

//                                last_history_ID = JsonObjectData.getString("HistoryID").toInt()
                                last_history_ID = 0
                            }

                            adapter?.notifyDataSetChanged()
                            

                            Log.e("??", rechargeReportHistory.size.toString())
                            if (rechargeReportHistory.size < 9) {
//                                adapter?.removeLoadingFooter()
                            }
                        } else {
                            mainBinding.imgEmptylist.visibility = View.VISIBLE
                            mainBinding.mainProgress.visibility = View.GONE
                            list_Status = false
                        }
                    } else {
                        mainBinding.imgEmptylist.visibility = View.VISIBLE
                        mainBinding.mainProgress.visibility = View.GONE
                        list_Status = false
                    }
                } else {
                    mainBinding.imgEmptylist.visibility = View.VISIBLE
                    mainBinding.mainProgress.visibility = View.GONE
                    list_Status = false
                }
            }
        })
    }


inner class AsyncTaskExample :
    AsyncTask<String?, String?, String>() {
    override fun doInBackground(vararg params: String?): String {

        var apiInterface: ApiInterface = RetrofitManager(this@Pay_Activity).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.PayReportDeatils(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            last_history_ID.toString(),
            Rechargetype
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@Pay_Activity, t.message.toString() + " ", Toast.LENGTH_LONG)
                    .show()
                pausingDialog?.dismiss()
                mainBinding.mainProgress.visibility=View.GONE
                mainBinding.imgEmptylist.visibility=View.VISIBLE

            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {



                if(pausingDialog!!.isShowing) {
                    pausingDialog!!.dismiss()
                }
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: " + response.toString())
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())

                    if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {

                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mPayReportDeatilsModel: com.codunite.myrecharge.Models.PayReportDeatilsModel =
                                    com.codunite.myrecharge.Models.PayReportDeatilsModel()
                                mPayReportDeatilsModel.eWalletTransactionID =
                                    JsonObjectData.getString("EWalletTransactionID").toInt()
                                mPayReportDeatilsModel.amount = JsonObjectData.getDouble("Amount")
                                mPayReportDeatilsModel.narration =
                                    JsonObjectData.getString("Narration")
                                mPayReportDeatilsModel.adddate = JsonObjectData.getString("Adddate")
                                mPayReportDeatilsModel.transtime =
                                    JsonObjectData.getString("Transtime")

                                rechargeReportHistory.add(mPayReportDeatilsModel)

                                last_history_ID = JsonObjectData.getInt("EWalletTransactionID")
                            }
                            isLoading = true
                            adapter?.notifyDataSetChanged()
                            mainBinding.mainProgress.visibility=View.GONE

                        } else {
                            mainBinding.mainProgress.visibility=View.GONE
                        }
                    } else {
                        mainBinding.mainProgress.visibility=View.GONE
                        mainBinding.imgEmptylist.visibility=View.VISIBLE
                        isLoading=false
                    }
                } else {
                    mainBinding.mainProgress.visibility=View.GONE
                }
            }
        })
        return "1"
    }

    override fun onPreExecute() {

        mainBinding.imgEmptylist.visibility=View.GONE
        if(ifFirstTimeCall) {
            pausingDialog!!.show()
            ifFirstTimeCall=false
        }
        super.onPreExecute()
    }

    override fun onPostExecute(result: String?) {


        mainBinding.imgEmptylist.visibility=View.VISIBLE

        if(mainBinding.mainSwiperefresh.isRefreshing) {
            mainBinding.mainSwiperefresh.isRefreshing=false
        }
        super.onPostExecute(result)
        if(rechargeReportHistory.size>9) {
            Log.e("//","execute1")
        }
        else {
            Log.e("//","execute2")
//            adapter?.removeLoadingFooter1()
            mainBinding.imgEmptylist.visibility=View.VISIBLE
        }
    }
}
}