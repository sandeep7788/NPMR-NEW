package com.codunite.myrecharge.Activitys.Manus

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Adapter.AddBankAdapter
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.BankListModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityAddBankAccountBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBankAccount : AppCompatActivity() {
    lateinit var mainBinding: ActivityAddBankAccountBinding
    var pref = com.codunite.myrecharge.Helper.Local_data(this@AddBankAccount)
    var TAG="@@monettransfer"
    var linearLayoutManager: GridLayoutManager? = null
    var rechargeReportHistory:ArrayList<com.codunite.myrecharge.Models.BankListModel> = java.util.ArrayList()
    var array_sort = java.util.ArrayList<com.codunite.myrecharge.Models.BankListModel>()
    var adapter: AddBankAdapter? = null
    var pausingDialog: SweetAlertDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_add_bank_account)
        pref.setMyappContext(this@AddBankAccount)

        linearLayoutManager = GridLayoutManager(this@AddBankAccount,2)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = AddBankAdapter(rechargeReportHistory,this@AddBankAccount)
        mainBinding.mainRecycler.adapter=adapter
        var MyReceiver: BroadcastReceiver?= null;
        MyReceiver = com.codunite.myrecharge.Helper.MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        pausingDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)
        getBankList()
        list_filter()

        mainBinding.mainSwiperefresh.setOnRefreshListener {
            getBankList()
        }
        mainBinding.toolbarLayout.back.setOnClickListener {
            super.onBackPressed()
        }
        mainBinding.toolbarLayout.btnUser.setOnClickListener {
            showProfile()
        }

    }
    fun showProfile() {
        var mIntent: Intent?=null
        mIntent= Intent(this@AddBankAccount, DashboardActivity::class.java)
        mIntent.putExtra("from_HF",true)
        startActivity(mIntent)
    }
    fun getBankList()
    {
        mainBinding.imgEmptylist.visibility = View.GONE
        
        var apiInterface: ApiInterface = RetrofitManager(this@AddBankAccount).instance!!.create(
            ApiInterface::class.java)

        apiInterface.getAllbanknamedetails().
        enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@AddBankAccount,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
                mainBinding.imgEmptylist.visibility = View.VISIBLE
                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }

                mainBinding.imgEmptylist.visibility = View.GONE

                if(response.isSuccessful) {
                    Log.d(TAG, "onResponse: "+response.toString())
                    Log.d(TAG, "onResponse: "+response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
//                    Toast.makeText(this@Prepaid_Activity,mJsonObject.getString("Message")+" ",Toast.LENGTH_LONG).show()

                    if(mJsonObject.has("Data") && !mJsonObject.isNull("Data") ) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {
                            rechargeReportHistory.clear()
                            array_sort.clear()
                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mRechargereportModel: com.codunite.myrecharge.Models.BankListModel =
                                    com.codunite.myrecharge.Models.BankListModel()
                                mRechargereportModel.bankerMasterID =
                                    JsonObjectData.getString("BankerMasterID").toInt()
                                mRechargereportModel.bankerMasterName =
                                    JsonObjectData.getString("BankerMasterName")
                                    mRechargereportModel.bankImage =
                                    JsonObjectData.getString("BankImage")

                                rechargeReportHistory.add(mRechargereportModel)
                                Log.d("!!" + TAG, "data " + i.toString() + " " + mRechargereportModel.bankerMasterName)

                            }
                            adapter?.notifyDataSetChanged()

                        } else {
                            mainBinding.imgEmptylist.visibility = View.VISIBLE
                            

                        }
                    } else {
                        mainBinding.imgEmptylist.visibility = View.VISIBLE
                        

                    }
                } else {
                    mainBinding.imgEmptylist.visibility = View.VISIBLE
                    

                }
            }
        })
    }
    fun list_filter() {
        mainBinding.edtSearche.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                filter(mainBinding.edtSearche.text.toString())
            }
        })
    }
    private fun filter(text: String) {
        //new array list that will hold the filtered data
        val filterdNames: ArrayList<com.codunite.myrecharge.Models.BankListModel> = ArrayList()

        //looping through existing elements
        for (s in rechargeReportHistory) {
            //if the existing elements contains the search input
            if (s.bankerMasterName.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter?.filterList(filterdNames)
    }

}