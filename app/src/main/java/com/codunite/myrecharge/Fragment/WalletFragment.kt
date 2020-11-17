package com.codunite.myrecharge.Fragment

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Adapter.HistoryAdapter

import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.HistoryModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.FragmentWalletBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletFragment : Fragment() {
    lateinit var mContext: Context
    lateinit var mainBinding : FragmentWalletBinding
    var pref = com.codunite.myrecharge.Helper.Local_data(context)
    var TAG="@@prepaid"
    var pausingDialog: SweetAlertDialog?=null
    var adapter: HistoryAdapter? = null
    var history_list:ArrayList<com.codunite.myrecharge.Models.HistoryModel> = java.util.ArrayList()
    var Last_History_ID=0
    var linearLayoutManager: LinearLayoutManager? = null
    var isLoading=false
    protected var handler: Handler? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View? {
        mainBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_wallet, container, false)
        mContext=container!!.context
        pref.setMyappContext(mContext)
        pausingDialog =SweetAlertDialog(mContext,SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Application waiting for internet connection..."
        pausingDialog!!.setCancelable(false)

        linearLayoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        mainBinding.mainRecycler!!.layoutManager = linearLayoutManager
        mainBinding.mainRecycler!!.itemAnimator = DefaultItemAnimator()
        adapter = HistoryAdapter(mContext,history_list)
        mainBinding.mainRecycler.adapter=adapter

        getHistory()
        monClick()
        initView()
        mainBinding.mainSwiperefresh.setOnRefreshListener {
            adapter?.clear()
            history_list.clear()
            Last_History_ID=0
            getHistory()
        }

        mainBinding.mainRecycler.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                super.onScrollStateChanged(recyclerView, newState)
//                                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                if (recyclerView.canScrollVertically(1).not()
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                ) {
                    Log.d(">>-----", "end")
                    if(!isLoading){
                        Handler(Looper.getMainLooper()).postDelayed({
                            Log.d(">>-----", "end3")
                            isLoading=true
                            getHistory()
                            mainBinding.mainProgress.visibility=View.VISIBLE
                        }, 400)
                    }
                }

            }
        })




        return mainBinding.root
    }

    fun initView()
    {
        var pref= com.codunite.myrecharge.Helper.Local_data(mContext)
        pref.setMyappContext(mContext)

        mainBinding.txtBalance.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Balance))
    }
    fun monClick()
    {

    }
    fun getHistory()
    {
        var apiInterface: ApiInterface = RetrofitManager(mContext).instance!!.create(
            ApiInterface::class.java)

        apiInterface.getHistory(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),Last_History_ID.toString()," ").enqueue(object :
            Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(mContext,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
                mainBinding.imgEmptylist.visibility=View.VISIBLE
                mainBinding.mainProgress.visibility=View.GONE
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                mainBinding.imgEmptylist.visibility=View.GONE
                mainBinding.mainProgress.visibility=View.GONE
                stop_progress()
                if(response.isSuccessful) {
                    Log.d(TAG, "onResponse: "+response.toString())
                    Log.d(TAG, "onResponse: "+response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog : com.codunite.myrecharge.Custom_.CustomDialog
                   /* Toast.makeText(mContext,mJsonObject.getString("Message")+" ",
                        Toast.LENGTH_LONG).show()*/

                    if(mJsonObject.has("Data") && !mJsonObject.isNull("Data") ) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {

                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mHistoryModel: com.codunite.myrecharge.Models.HistoryModel =
                                    com.codunite.myrecharge.Models.HistoryModel()
                                mHistoryModel.eWalletTransactionID =
                                    JsonObjectData.getString("EWalletTransactionID").toInt()
                                mHistoryModel.amount = JsonObjectData.getString("Amount").toDouble()
                                mHistoryModel.balance =
                                    JsonObjectData.getString("Balance").toDouble()
                                mHistoryModel.narration = JsonObjectData.getString("Narration")
                                mHistoryModel.adddate = JsonObjectData.getString("Adddate")
                                mHistoryModel.factor = JsonObjectData.getString("Factor")
                                mHistoryModel.wallettime = JsonObjectData.getString("Wallettime")
                                history_list.add(mHistoryModel)
                                Last_History_ID = JsonObjectData.getString("EWalletTransactionID").toInt()
                            }
                            mainBinding.imgEmptylist.visibility= View.GONE
                            stop_progress()
                            adapter?.notifyDataSetChanged()
                            isLoading=false

                        }
                    }
                    else {
                        stop_progress()
                        mainBinding.imgEmptylist.visibility= View.GONE
                    }
                }
                else {
                    stop_progress()
                    mainBinding.imgEmptylist.visibility= View.GONE
                }
            }
        })
    }
    fun stop_progress() {
        if(mainBinding.mainSwiperefresh.isRefreshing == true) {
            mainBinding.mainSwiperefresh.setRefreshing(false) } else { }
    }
}