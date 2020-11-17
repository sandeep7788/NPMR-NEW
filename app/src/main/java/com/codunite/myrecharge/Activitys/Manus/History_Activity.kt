package com.codunite.myrecharge.Activitys.Manus

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Adapter.HistoryAdapter
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.HistoryModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityHistoryBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class History_Activity : AppCompatActivity() {
    lateinit var mainBinding: ActivityHistoryBinding
    var pref = com.codunite.myrecharge.Helper.Local_data(this@History_Activity)
    var TAG="@@prepaid"
    var pausingDialog: SweetAlertDialog?=null
    var adapter: HistoryAdapter? = null
    var history_list:ArrayList<com.codunite.myrecharge.Models.HistoryModel> = java.util.ArrayList()
    var Last_History_ID=0
    var linearLayoutManager: LinearLayoutManager? = null
    var isLoading=false
    var ifFirstTimeCall=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_history_)
        pref.setMyappContext(this@History_Activity)

        linearLayoutManager = LinearLayoutManager(this@History_Activity, LinearLayoutManager.VERTICAL, false)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = HistoryAdapter(this, history_list)
        mainBinding.mainRecycler.adapter=adapter
        pausingDialog =SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)
        mainBinding.mainRecycler.performClick()
        monClick()



        mainBinding.mainProgress.visibility=View.VISIBLE
        val asyncTask = AsyncTaskExample()
        asyncTask.execute()

        mainBinding.mainSwiperefresh.setOnRefreshListener {
            if(isLoading) {
                adapter?.clear()
                history_list.clear()
                Last_History_ID = 0
                val asyncTask = AsyncTaskExample()
                asyncTask.execute()
                pausingDialog?.show()
                mainBinding.mainProgress.visibility=View.VISIBLE
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
    }

    fun monClick()
    {
        mainBinding.toolbarLayout.back.setOnClickListener {
            super.onBackPressed()
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

    fun stop_progress() {


        if(mainBinding.mainSwiperefresh.isRefreshing == true) {
            mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
    }

    inner class AsyncTaskExample :
        AsyncTask<String?, String?, String>() {



        override fun doInBackground(vararg params: String?): String? {

            var apiInterface: ApiInterface =
                RetrofitManager(this@History_Activity).instance!!.create(
                    ApiInterface::class.java
                )

            apiInterface.getHistory(
                pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
                Last_History_ID.toString(),
                " "
            ).enqueue(object :
                Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@History_Activity,
                        t.message.toString() + " ",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    showEmptyBox()

                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    stop_progress()

//                    Handler(Looper.getMainLooper()).postDelayed({
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
                                        var mHistoryModel: com.codunite.myrecharge.Models.HistoryModel =
                                            com.codunite.myrecharge.Models.HistoryModel()
                                        mHistoryModel.eWalletTransactionID =
                                            JsonObjectData.getInt("EWalletTransactionID")
                                        mHistoryModel.amount =
                                            JsonObjectData.getDouble("Amount")
                                        mHistoryModel.balance =
                                            JsonObjectData.getDouble("Balance")
                                        mHistoryModel.narration =
                                            JsonObjectData.getString("Narration")
                                        mHistoryModel.adddate = JsonObjectData.getString("Adddate")
                                        mHistoryModel.factor = JsonObjectData.getString("Factor")
                                        mHistoryModel.wallettime =
                                            JsonObjectData.getString("Wallettime")
                                        Last_History_ID =
                                            JsonObjectData.getInt("EWalletTransactionID")

                                        if (JsonObjectData.getInt("EWalletTransactionID").toString().isNotEmpty()) {
                                            history_list.add(mHistoryModel)
                                        }
                                    }
                                    mainBinding.mainProgress.visibility=View.GONE
                                    adapter?.notifyDataSetChanged()

                                    stop_progress()
                                    isLoading = true
                                }
                            } else {


                                isLoading = false
                                mainBinding.mainProgress.visibility=View.GONE
                            }
                        } else {
                            stop_progress()
                            showEmptyBox()
                            mainBinding.mainProgress.visibility=View.GONE
                        }

//                    }, 1000)
                }

            })

            return "1"
        }

        override fun onPostExecute(result: String?) {



            mainBinding.mainSwiperefresh.isRefreshing=false
            mainBinding.imgEmptylist.visibility=View.GONE
            mainBinding.mainRecycler.performClick()
            mainBinding.linearLayout3.performClick()
            if(ifFirstTimeCall) {
                pausingDialog!!.show()
                ifFirstTimeCall=false
            }

            super.onPostExecute(result)
        }

        override fun onPreExecute() {


            super.onPreExecute()



        }
    }
    

    fun showEmptyBox() {
        mainBinding.imgEmptylist.visibility=View.VISIBLE
    }
    fun hideEmptyBox() {
        mainBinding.imgEmptylist.visibility=View.GONE    }
    fun showLoader() {
        runOnUiThread(object : Runnable {
            override fun run() {
                pausingDialog = SweetAlertDialog(this@History_Activity,SweetAlertDialog.PROGRESS_TYPE)
                pausingDialog?.titleText="Please wait...."
                pausingDialog!!.show()
            }
        })
    }
}