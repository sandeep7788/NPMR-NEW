package com.codunite.myrecharge.Activitys.Manus

import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
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
import com.codunite.myrecharge.Adapter.MoneyTransferAdapter
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.MoneyTransferReportModel
import com.codunite.myrecharge.Models.OperatorModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityHistoryBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

public class MoneyTransferReportActivity : AppCompatActivity(), DialogInterface.OnClickListener, OnItemClickListener
{
    lateinit var mainBinding : ActivityHistoryBinding
    var pref=
        com.codunite.myrecharge.Helper.Local_data(this@MoneyTransferReportActivity)
    var TAG="@@pay_activty"
    lateinit var title:ArrayList<String>
    lateinit var operator_image:ArrayList<String>
    var pausingDialog: SweetAlertDialog?=null
    var adapter: MoneyTransferAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null
    var rechargeReportHistory:ArrayList<com.codunite.myrecharge.Models.MoneyTransferReportModel> = java.util.ArrayList()
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
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_)
        pref.setMyappContext(this@MoneyTransferReportActivity)
        title= ArrayList()
        operator_image= ArrayList()
        pausingDialog =SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)

        linearLayoutManager = LinearLayoutManager(
            this@MoneyTransferReportActivity,
            LinearLayoutManager.VERTICAL,
            false
        )
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = MoneyTransferAdapter(this@MoneyTransferReportActivity, rechargeReportHistory)
        mainBinding.mainRecycler.adapter=adapter

        val asyncTask = AsyncTaskExample()
        asyncTask.execute()
        mainBinding.mainProgress.visibility = View.VISIBLE

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
                        mainBinding.mainProgress.visibility = View.VISIBLE
                        val asyncTask = AsyncTaskExample()
                        asyncTask.execute()
                    }
                }

            }
        })

        mainBinding.toolbarLayout.back.setOnClickListener { super.onBackPressed() }
        
        mainBinding.mainSwiperefresh.setOnRefreshListener {
            if(isLoading) {
                adapter?.clear()
                rechargeReportHistory.clear()
                last_history_ID = 0
                val asyncTask = AsyncTaskExample()
                asyncTask.execute()
            }else {
                mainBinding.mainSwiperefresh.isRefreshing=false
            }
        }

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





    override fun onClick(p0: DialogInterface?, p1: Int) {
        Log.d(TAG, "onClick: " + p1.toString())
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.d(TAG, "onClick: " + p2.toString())
    }


    fun showDialog(title: String, type: String) {
        Log.d("@@" + TAG, "showDialog: ")
        SweetAlertDialog(this, type.toInt())
            .setTitleText(title)
//            .setContentText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
            }
            .show()
    }

    fun getrechargereport()
    {

        mainBinding.imgEmptylist.visibility = View.GONE
        mainBinding.mainProgress.visibility = View.VISIBLE
        var apiInterface: ApiInterface = RetrofitManager(this@MoneyTransferReportActivity).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.PayReportDeatils(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            last_history_ID.toString(),
            Rechargetype
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@MoneyTransferReportActivity, t.message.toString() + " ", Toast.LENGTH_LONG)
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
                                var mMoneyTransferReportModel: com.codunite.myrecharge.Models.MoneyTransferReportModel =
                                    com.codunite.myrecharge.Models.MoneyTransferReportModel()
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("ID")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("AccountNumber")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("AccountIFSC")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Amount")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("CustomerName")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Customermobile")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Surcharge")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Status")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Adddate")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("Transtime")
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("transID")


                                rechargeReportHistory.add(mMoneyTransferReportModel)

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
    fun showEmptyBox() {
        mainBinding.imgEmptylist.visibility=View.VISIBLE
    }

inner class AsyncTaskExample :
    AsyncTask<String?, String?, String>() {
    override fun doInBackground(vararg params: String?): String {

        Log.d(TAG, "onResponse: " + "1")

        var apiInterface: ApiInterface = RetrofitManager(this@MoneyTransferReportActivity).instance!!.create(ApiInterface::class.java)

        apiInterface.MoneyTransferReport(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            last_history_ID.toString()
        ).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d(TAG, "onResponse: " + "2")
                Toast.makeText(this@MoneyTransferReportActivity, t.message.toString() + " ", Toast.LENGTH_LONG)
                    .show()
                pausingDialog?.dismiss()
                mainBinding.mainProgress.visibility = View.GONE
                showEmptyBox()

            }
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(TAG, "onResponse: " + response.toString())
                Log.d(TAG, "onResponse: " + response.body().toString())

                if(pausingDialog!!.isShowing) {
                    pausingDialog!!.dismiss()
                }

                if (response.isSuccessful) {

                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog: com.codunite.myrecharge.Custom_.CustomDialog

                    if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {

                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mMoneyTransferReportModel: com.codunite.myrecharge.Models.MoneyTransferReportModel =
                                    com.codunite.myrecharge.Models.MoneyTransferReportModel()
                                mMoneyTransferReportModel.id = JsonObjectData.getInt("ID")
                                mMoneyTransferReportModel.accountNumber = JsonObjectData.getString("AccountNumber")
                                mMoneyTransferReportModel.accountIFSC = JsonObjectData.getString("AccountIFSC")
                                mMoneyTransferReportModel.amount = JsonObjectData.getDouble("Amount")
                                mMoneyTransferReportModel.customerName = JsonObjectData.getString("CustomerName")
                                mMoneyTransferReportModel.customermobile = JsonObjectData.getString("Customermobile")
                                mMoneyTransferReportModel.surcharge = JsonObjectData.getDouble("Surcharge")
                                mMoneyTransferReportModel.status = JsonObjectData.getString("Status")
                                mMoneyTransferReportModel.adddate = JsonObjectData.getString("Adddate")
                                mMoneyTransferReportModel.transtime = JsonObjectData.getString("Transtime")
                                mMoneyTransferReportModel.transID = JsonObjectData.getString("transID")

                                rechargeReportHistory.add(mMoneyTransferReportModel)

                                last_history_ID = JsonObjectData.getInt("ID")
//                                last_history_ID = 0
                            }

                            adapter?.notifyDataSetChanged()
                            mainBinding.mainProgress.visibility = View.GONE


                        } else {
                            mainBinding.mainProgress.visibility = View.GONE
                        }
                    } else {
                        mainBinding.mainProgress.visibility = View.GONE

                    }
                } else {
                    mainBinding.mainProgress.visibility = View.GONE
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


        isLoading = true
        mainBinding.imgEmptylist.visibility=View.GONE
        mainBinding.mainSwiperefresh.isRefreshing=false
        if(rechargeReportHistory.size>9) {
            Log.e("//","execute1")
        }
        else {
            Log.e("//","execute2")
            adapter?.removeLoadingFooter1()
            adapter?.notifyDataSetChanged()
//            showEmptyBox()
        }
        super.onPostExecute(result)

    }
}

    fun getOperatorList()
    {
        var operatorList:ArrayList<com.codunite.myrecharge.Models.OperatorModel> = java.util.ArrayList()
        var apiInterface: ApiInterface = RetrofitManager(this@MoneyTransferReportActivity).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.getoperatordetails("1").enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@MoneyTransferReportActivity, t.message.toString() + " ", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())

                    var operatorDialog: com.codunite.myrecharge.Custom_.CustomDialog

                    Toast.makeText(
                        this@MoneyTransferReportActivity,
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
                    }
                }
            }
        })
    }
}