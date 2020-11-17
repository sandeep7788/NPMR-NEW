package com.codunite.myrecharge.Activitys.Manus

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Adapter.TopServicesAdapter
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.*
import com.codunite.myrecharge.Models.OperatorModel
import com.codunite.myrecharge.Models.RechargePlaneModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityPrepaidBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopServicesActivity : AppCompatActivity() ,
    com.codunite.myrecharge.Helper.PaginationAdapterCallback {
    lateinit var mainBinding: ActivityPrepaidBinding
    var pref =
        com.codunite.myrecharge.Helper.Local_data(this@TopServicesActivity)
    var TAG="@@prepaid"
    var mOption:Int=0
    var pausingDialog: SweetAlertDialog?=null
    var adapter: TopServicesAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var rechargeReportHistory:ArrayList<com.codunite.myrecharge.Models.RechargePlaneModel> = java.util.ArrayList()
    var viewAll_Status=true
    var Rechargetype="0"
    var last_history_ID=0
    var mOperator_CODE="101"
    var operator_type="1"
    var isLoading=false
    var ifFirstTimeCall=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_prepaid)
        pref.setMyappContext(this@TopServicesActivity)
        pausingDialog =SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)
        linearLayoutManager = LinearLayoutManager(this@TopServicesActivity,LinearLayoutManager.VERTICAL,false)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = TopServicesAdapter(this@TopServicesActivity,rechargeReportHistory)
        mainBinding.mainRecycler.adapter=adapter
        var MyReceiver: BroadcastReceiver?= null;
        MyReceiver = MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        mOption=pref.ReadIntPreferences("opt")
        setView()
        monClick()

        mainBinding.mainSwiperefresh.setOnRefreshListener {
            if(isLoading) {
                adapter?.clear()
                rechargeReportHistory.clear()
                last_history_ID = 0
                val asyncTask = AsyncTaskExample()
                asyncTask.execute()
                pausingDialog?.show()
            }else {
                mainBinding.mainSwiperefresh.isRefreshing=false
            }
        }

        mainBinding.mainRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
//                                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                if (recyclerView.canScrollVertically(1)
                        .not() && newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    Log.d(">>-----", "end")
                    if (isLoading) {
                        Log.d(">>-----", "end3")
                        isLoading = false
                        mainBinding.mainProgress.visibility=View.VISIBLE
                        val asyncTask = AsyncTaskExample()
                        asyncTask.execute()
                    }
                }
            }
        })
        val asyncTask = AsyncTaskExample()
        asyncTask.execute()
    }

    fun setView() {
        Log.d(TAG, "setview1 " + pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_amt))
        Log.d(TAG, "setview2 " + pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code))
        Log.d(TAG, "setview3 " + pref.ReadIntPreferences("opt"))

        if(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_amt).length>2) {
            var mSelectedplane = pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_amt).replace("₹", "");
            mainBinding.txtSelectedRechargePlane.setText(mSelectedplane)
            mainBinding.txtSelectedRechargePlane.setTextColor(Color.BLACK)
            mainBinding.txtOperator.text=pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code)
            mainBinding.edtNumber.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_temp_number))
            mainBinding.txtOperator.setTextColor(Color.BLACK)
        }
        
        when(mOption) {
            1 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.prepaid)
                Rechargetype = "Prepaidreport"
                operator_type = "1"
            }
            2 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.prepaid)
                Rechargetype = "Postpaidreport"
                operator_type = "2"
            }
            3 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.dth)
                Rechargetype = "Dthreport"
                operator_type = "5"
            }
            4 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.electricity)
                Rechargetype = "Datacardreport"
            }
            5 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.landline)
                Rechargetype = "Prepaidreport"
            }
            9 -> {
                mainBinding.imgNumber.setImageResource(R.drawable.landline)
                Rechargetype = "Leadline"
            }

            else -> Toast.makeText(
                this@TopServicesActivity,
                "Please go to back.... \n try again.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun monClick()
    {
        mainBinding.toolbarLayout.back.setOnClickListener {
             super.onBackPressed()
             }
        mainBinding.txtOperator.setOnClickListener { gerrateOperatorList(operator_type) }
        mainBinding.txtViewAll.setOnClickListener { viewAll() }
        mainBinding.txtBrowesPlane.setOnClickListener {
            if(!mainBinding.txtOperator.text.toString().isEmpty()) {
                pref.writeStringPreference(
                    com.codunite.myrecharge.Helper.Constances.PREF_temp_number,
                    mainBinding.edtNumber.text.toString()
                )
                val intent1 = Intent(
                    this@TopServicesActivity,
                    SelectRecharge_Palne_Activity::class.java
                )
                startActivity(intent1)
            } else {
                Toast.makeText(this, "Please First Select Operator.", Toast.LENGTH_LONG).show()
            }
        }
        mainBinding.btnProcess.setOnClickListener {
            if(mainBinding.edtNumber.text.toString().length<8&&mainBinding.txtOperator.text.toString().isEmpty()&&mainBinding.txtSelectedRechargePlane.text.toString().isEmpty()) {
                mainBinding.edtNumber.setError("Please Enter Valid Number")
                mainBinding.txtOperator.setError("Please select Operator")
                mainBinding.txtSelectedRechargePlane.setError("Please Enter Amount")
            }  else if(mainBinding.edtNumber.text.toString().length<8) {
                mainBinding.edtNumber.setError("Please Enter Valid Number")
            } else if(mainBinding.txtOperator.text.toString().isEmpty()) {
                mainBinding.txtOperator.setError("Please select Operator")
            } else if(mainBinding.txtSelectedRechargePlane.text.toString().isEmpty()) {
                mainBinding.txtSelectedRechargePlane.setError("Please Enter Amount")
            } else {

                mainBinding.edtNumber.setError(null)
                mainBinding.txtOperator.setError(null)
                mainBinding.txtSelectedRechargePlane.setError(null)
                rechrgeProcess()
            }
        }
    }

    fun showEmptyBox() {
        mainBinding.imgEmptylist.visibility=View.VISIBLE
    }

    fun rechrgeProcess() {

        pausingDialog?.show()
        var apiInterface: ApiInterface = RetrofitManager(this@TopServicesActivity).instance!!.create(
            ApiInterface::class.java
        )

        Log.e(
            ">>", pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile) + " _ " +
                    pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password) + " _ " +
                    mainBinding.txtSelectedRechargePlane.text.toString() + " _ " +
                    pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code) + " _ " + mainBinding.edtNumber.text.toString()
        )
        apiInterface.getRechargeprcess(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password),
            mainBinding.txtSelectedRechargePlane.text.toString(),
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code),
            mainBinding.edtNumber.text.toString()
        )
            .enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@TopServicesActivity,
                        t.message.toString() + " ",
                        Toast.LENGTH_LONG
                    ).show()
                    pausingDialog?.dismiss()
                    if (!isFinishing()) {
                        showDialog("Something wrong", "3")
                    }


                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pausingDialog?.dismiss()
                    if (response.isSuccessful) {
                        Log.e(TAG, "onResponse: " + response.body().toString())
                        var mJSONObject = JSONObject(response.body().toString())
                        Toast.makeText(
                            this@TopServicesActivity,
                            mJSONObject.getString("Message").toString() + " ",
                            Toast.LENGTH_LONG
                        ).show()
                        if (mJSONObject.getString("Error").toLowerCase()
                                .equals("false") && mJSONObject != null
                        ) {
                            if (mJSONObject.has("Data") && !mJSONObject.isNull("Data")) {
                                var mjsonArray: JSONArray = mJSONObject.getJSONArray("Data")
                                var mjsondata = mjsonArray.getJSONObject(0)
                                if (mjsondata.getString("Status").equals("Success")) {
                                    showDialog(mjsondata.getString("Status"), "2")
                                } else {
                                    showDialog(mjsondata.getString("Status"), "1")
                                }
                            } else {
                                showDialog(mJSONObject.getString("Message").toString(), "3")
                            }
                        } else {
                            showDialog(mJSONObject.getString("Message").toString(), "3")
                        }
                    } else {
                        showDialog("Something wrong", "3")
                    }
                }
            })
    }

    fun gerrateOperatorList(operator_type: String)
    {
        pausingDialog?.show()
        var operatorList:ArrayList<com.codunite.myrecharge.Models.OperatorModel> = java.util.ArrayList()
        var apiInterface: ApiInterface = RetrofitManager(this@TopServicesActivity).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.getoperatordetails(operator_type).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(
                    this@TopServicesActivity,
                    t.message.toString() + " ",
                    Toast.LENGTH_LONG
                ).show()
                pausingDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog: com.codunite.myrecharge.Custom_.CustomDialog
                    Toast.makeText(
                        this@TopServicesActivity,
                        mJsonObject.getString("Message") + " ",
                        Toast.LENGTH_LONG
                    ).show()
                    if (mJsonObject.getString("Error").toLowerCase().equals("false")) {
                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        operatorList.clear()

                        for (i in 0 until mJsonArray.length()) {
                            var JsonObjectData = mJsonArray.getJSONObject(i)
                            var mOperatorModel: com.codunite.myrecharge.Models.OperatorModel =
                                com.codunite.myrecharge.Models.OperatorModel()
                            mOperatorModel.operatorID =
                                JsonObjectData.getString("OperatorID").toInt()
                            mOperatorModel.operatorName = JsonObjectData.getString("OperatorName")
                            mOperatorModel.operatorCode = JsonObjectData.getString("OperatorCode")
                            mOperatorModel.operaotimageurl =
                                JsonObjectData.getString("operaotimageurl")
                            operatorList.add(mOperatorModel)
                        }

                        operatorDialog =
                            com.codunite.myrecharge.Custom_.CustomDialog(
                                this@TopServicesActivity,
                                operatorList,
                                mainBinding.txtOperator, mOperator_CODE
                            )
                        operatorDialog.getWindow()!!.setFlags(
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN
                        );
                        operatorDialog.show()
                    }
                }
                pausingDialog?.dismiss()
            }
        })
    }

    override fun retryPageLoad() {
//
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

    fun cln() {
        Log.d(TAG, "setview1 " + pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_amt))
        Log.d(TAG, "setview2 " + pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code))
        Log.d(TAG, "setview3 " + pref.ReadIntPreferences("opt"))
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_amt, "")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_operator_code, "")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_temp_number, "")
        pref.writeIntPreference("opt", 0)
    }

    fun showDialog(title: String, type: String) {
        try {
            SweetAlertDialog(this, type.toInt())
                .setTitleText(title)
//            .setContentText(title)
                .setConfirmText("OK")
                .setConfirmClickListener { sDialog ->
                    sDialog.dismiss()
                    cln()
                }
                .show()
        } catch (e: Exception) { }
    }

    override fun onBackPressed() {
//        super.onBackPressed()/
        cln()
        startActivity(Intent(this@TopServicesActivity, DashboardActivity::class.java))
    }
    inner class AsyncTaskExample :
        AsyncTask<String?, String?, String>() {
        override fun doInBackground(vararg params: String?): String {

            var apiInterface: ApiInterface = RetrofitManager(this@TopServicesActivity).instance!!.create(
                ApiInterface::class.java
            )

            apiInterface.getrechargereport(
                pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
                last_history_ID.toString(),
                Rechargetype
            ).
            enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@TopServicesActivity,
                        t.message.toString() + " ",
                        Toast.LENGTH_LONG
                    ).show()
                    pausingDialog?.dismiss()
                    mainBinding.mainProgress.visibility=View.GONE
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
                                    var mRechargereportModel: com.codunite.myrecharge.Models.RechargePlaneModel =
                                        com.codunite.myrecharge.Models.RechargePlaneModel()
                                    mRechargereportModel.historyID =
                                        JsonObjectData.getString("HistoryID").toInt()
                                    mRechargereportModel.mobileno =
                                        JsonObjectData.getString("Mobileno")
                                    mRechargereportModel.rechargeAmount =
                                        JsonObjectData.getString("RechargeAmount").toDouble()
                                    mRechargereportModel.transID =
                                        JsonObjectData.getString("TransID")
                                    mRechargereportModel.status = JsonObjectData.getString("Status")
                                    mRechargereportModel.addDate =
                                        JsonObjectData.getString("AddDate")
                                    mRechargereportModel.operatorName =
                                        JsonObjectData.getString("OperatorName")
                                    mRechargereportModel.operaotimageurl =
                                        JsonObjectData.getString("operaotimageurl")
                                    mRechargereportModel.rechargetime =
                                        JsonObjectData.getString("rechargetime")

                                    last_history_ID =
                                        JsonObjectData.getInt("HistoryID")
                                    Log.e("@@ID",JsonObjectData.getInt("HistoryID").toString()+" _")
                                    if (!JsonObjectData.getString("TransID").isEmpty() && JsonObjectData.getString("TransID") != null) {
                                        rechargeReportHistory.add(mRechargereportModel)
                                    }
                                }

                                
                                adapter?.notifyDataSetChanged()
                                isLoading = true
                                mainBinding.mainProgress.visibility=View.GONE
                            } else {

                                showEmptyBox()
                                mainBinding.mainProgress.visibility=View.GONE
                            }
                        } else {

                            mainBinding.mainProgress.visibility=View.GONE
                        }
                    } else {

                        showEmptyBox()
                        mainBinding.mainProgress.visibility=View.GONE
                    }
                }
            })
            return "1"
        }

        override fun onPreExecute() {

            if(ifFirstTimeCall) {
                pausingDialog!!.show()
                ifFirstTimeCall=false
            }


            mainBinding.imgEmptylist.visibility = View.GONE
            
            
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {



            if(  mainBinding.mainSwiperefresh.isRefreshing) {
                mainBinding.mainSwiperefresh.isRefreshing=false
            }
            mainBinding.imgEmptylist.visibility=View.GONE
            
            super.onPostExecute(result)
        }
    }
}