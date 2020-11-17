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
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Adapter.BankListAdapter
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.GetMemberBankAccountModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityMtUserAddedAcBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MT_UserAddedBankAC : AppCompatActivity() {
    lateinit var mainBinding: ActivityMtUserAddedAcBinding
    var pref = com.codunite.myrecharge.Helper.Local_data(this@MT_UserAddedBankAC)
    var TAG="@@monettransfer"
    var linearLayoutManager: LinearLayoutManager? = null
    var addbankList:ArrayList<com.codunite.myrecharge.Models.GetMemberBankAccountModel> = java.util.ArrayList()
    var array_sort = java.util.ArrayList<com.codunite.myrecharge.Models.GetMemberBankAccountModel>()
    var adapter: BankListAdapter? = null
    var pausingDialog: SweetAlertDialog?=null
    var textlength = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_mt_user_added_ac)
        pref.setMyappContext(this@MT_UserAddedBankAC)

        linearLayoutManager = LinearLayoutManager(this@MT_UserAddedBankAC, LinearLayoutManager.VERTICAL, false)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = BankListAdapter(addbankList,this@MT_UserAddedBankAC)
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
        mainBinding.btnAddBank.setOnClickListener {
            startActivity(Intent(this,AddBankAccount::class.java))
        }
        mainBinding.back.setOnClickListener {
            super.onBackPressed()
        }
        mainBinding.btnViewHistory.setOnClickListener {
            var mIntent=Intent(this@MT_UserAddedBankAC, MoneyTransferReportActivity::class.java)
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(mIntent)
        }
        mainBinding.mainSwiperefresh.setOnRefreshListener {
            addbankList.clear()
            array_sort.clear()
            getBankList()
        }
    }

    fun getBankList()
    {
        pausingDialog?.show()
        mainBinding.imgEmptylist.visibility = View.GONE
        
        var apiInterface: ApiInterface = RetrofitManager(this@MT_UserAddedBankAC).instance!!.create(
            ApiInterface::class.java)

        apiInterface.GetMemberBankAccount(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno)).
        enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@MT_UserAddedBankAC,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
                mainBinding.imgEmptylist.visibility = View.VISIBLE

                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
                pausingDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
                if(mainBinding.mainSwiperefresh.isRefreshing == true) {
                    mainBinding.mainSwiperefresh.setRefreshing(false) } else { }

                mainBinding.imgEmptylist.visibility = View.GONE
                
                pausingDialog?.dismiss()
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
                            addbankList.clear()
                            array_sort.clear()
                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mRechargereportModel: com.codunite.myrecharge.Models.GetMemberBankAccountModel =
                                    com.codunite.myrecharge.Models.GetMemberBankAccountModel()
                                mRechargereportModel.id =
                                    JsonObjectData.getString("ID").toInt()
                                mRechargereportModel.bankName=
                                    JsonObjectData.getString("BankName")
                                mRechargereportModel.customernumber=
                                    JsonObjectData.getString("customernumber")
                                mRechargereportModel.accountnumber=
                                    JsonObjectData.getString("Accountnumber")
                                mRechargereportModel.customerName=
                                    JsonObjectData.getString("CustomerName")
                                mRechargereportModel.ifsccode=
                                    JsonObjectData.getString("ifsccode")
                                mRechargereportModel.bankImage=
                                    JsonObjectData.getString("BankImage")
                                mRechargereportModel.bankID=
                                    JsonObjectData.getString("BankID").toInt()




                                addbankList.add(mRechargereportModel)
                                Log.d("!!" + TAG, "data " + i.toString() + " " + mRechargereportModel.bankName)

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
        val filterdNames: ArrayList<com.codunite.myrecharge.Models.GetMemberBankAccountModel> = ArrayList()

        //looping through existing elements
        for (s in addbankList) {
            //if the existing elements contains the search input
            if (s.bankName.toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                filterdNames.add(s)
            }
        }

        //calling a method of the adapter class and passing the filtered list
        adapter?.filterList(filterdNames)
    }
}