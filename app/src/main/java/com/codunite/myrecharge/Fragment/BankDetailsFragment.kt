package com.codunite.myrecharge.Fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Adapter.BankDitailsAdapter
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.CompanybankdetailsModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.FragmentBankdetailsBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BankDetailsFragment: Fragment() {
    lateinit var thiscontext: Context
    lateinit var mainBinding : FragmentBankdetailsBinding
    var pausingDialog: SweetAlertDialog?=null
    var list:ArrayList<com.codunite.myrecharge.Models.CompanybankdetailsModel> = java.util.ArrayList()
    var adapter: BankDitailsAdapter? = null
    var linearLayoutManager: LinearLayoutManager? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bankdetails, container, false
        )
        thiscontext=container!!.context
        pausingDialog =SweetAlertDialog(thiscontext,SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Application waiting for internet connection..."
        pausingDialog!!.setCancelable(false)

        linearLayoutManager = LinearLayoutManager(thiscontext, LinearLayoutManager.VERTICAL, false)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = BankDitailsAdapter(list,thiscontext)
        mainBinding.mainRecycler.adapter=adapter
        getBankList()
        mainBinding.mainSwiperefresh.setOnRefreshListener {
            getBankList()
        }

        return mainBinding.root
    }

    private fun browseContacts() {
        val pickContactIntent =
            Intent("android.intent.action.PICK", Uri.parse("content://contacts"))
        pickContactIntent.type = "vnd.android.cursor.dir/phone_v2"
        startActivityForResult(pickContactIntent, 1)
    }

    fun getBankList()
    {
        pausingDialog?.show()
        mainBinding.mainProgress.visibility=View.VISIBLE
        var apiInterface: ApiInterface = RetrofitManager(thiscontext).instance!!.create(
            ApiInterface::class.java)

        apiInterface.Companybankdetails().enqueue(object :
            Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(thiscontext,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
                mainBinding.imgEmptylist.visibility= View.VISIBLE

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                stop_progress()

                if(response.isSuccessful) {

                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog : com.codunite.myrecharge.Custom_.CustomDialog

                    if(mJsonObject.has("Data") && !mJsonObject.isNull("Data") ) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")
                        mainBinding.imgEmptylist.visibility= View.GONE
                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {
                            list.clear()
                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mHistoryModel: com.codunite.myrecharge.Models.CompanybankdetailsModel =
                                    com.codunite.myrecharge.Models.CompanybankdetailsModel()
                                mHistoryModel.bankerID =
                                    JsonObjectData.getString("BankerID").toInt()
                                mHistoryModel.accountnumber = JsonObjectData.getString("accountnumber")
                                mHistoryModel.accountholdername =
                                    JsonObjectData.getString("Accountholdername")
                                mHistoryModel.ifsccode = JsonObjectData.getString("ifsccode")
                                mHistoryModel.accountType = JsonObjectData.getString("AccountType")
                                mHistoryModel.bankBranch = JsonObjectData.getString("BankBranch")
                                mHistoryModel.bankName = JsonObjectData.getString("BankName")
                                mHistoryModel.bankImage = JsonObjectData.getString("BankImage")
                                list.add(mHistoryModel)
                            }
                            stop_progress()
                            adapter?.notifyDataSetChanged()
                        }
                    }
                    else {
                        stop_progress()
                    }
                }
                else {
                    stop_progress()
                }
            }
        })
    }
    fun stop_progress() {

        mainBinding.mainProgress.visibility= View.GONE
        if(mainBinding.mainSwiperefresh.isRefreshing == true) {
            mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
        if(pausingDialog?.isShowing!!) {
            pausingDialog?.dismiss()
        }
    }

    override fun onDestroy() {
        if(pausingDialog?.isShowing!!) {
            pausingDialog?.dismiss()
        }
        super.onDestroy()
    }
}