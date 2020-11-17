package com.codunite.myrecharge.Activitys.Manus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityMoneyTransferBinding
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MoneyTransferActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMoneyTransferBinding
    var pref =
        com.codunite.myrecharge.Helper.Local_data(this@MoneyTransferActivity)
    var ID=""
    var bank_id=""
    var pausingDialog: SweetAlertDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_money_transfer)
        pref.setMyappContext(this@MoneyTransferActivity)
        pausingDialog =SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)
        setView()
        clickListeners()
    }

    fun clickListeners() {
        mainBinding.toolbarLayout.back.setOnClickListener {
            super.onBackPressed()
        }
        mainBinding.btnPay.setOnClickListener {
            if(mainBinding.edtAmount.text.toString().isEmpty()) {
                mainBinding.edtAmount.setError("Please Enter Amount")

            } else {
                mainBinding.edtAmount.setError(null)
                MoneyTransfer()
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

    fun MoneyTransfer() {
        pausingDialog?.show()

        var apiInterface: ApiInterface = RetrofitManager(this@MoneyTransferActivity).instance!!.create(ApiInterface::class.java)

        apiInterface.MoneyTransfer(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password),
            mainBinding.edtAmount.text.toString(),
            ID," ").enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pausingDialog?.dismiss()
                if(response.isSuccessful) {
                    Log.e("@@",response.body().toString())

                    var mJSONObject = JSONObject(response.body().toString())
                    Toast.makeText(
                        this@MoneyTransferActivity, mJSONObject.getString("Message").toString() + " ",
                        Toast.LENGTH_LONG
                    ).show()
                    if (mJSONObject.getString("Error").toLowerCase().equals("false")) {
                        if (mJSONObject.has("Data") && !mJSONObject.isNull("Data")) {
                            var mjsonArray: JSONArray = mJSONObject.getJSONArray("Data")
                            Log.e("@@",mjsonArray.getJSONObject(0).getString("Status").toLowerCase())
                            if(mjsonArray.getJSONObject(0).getString("Status").toLowerCase().equals("success")) {
                                showDialog(mjsonArray.getJSONObject(0).getString("Status"),"2")
                                mainBinding.edtAmount.setText("")
                            } else {
                                showDialog(mjsonArray.getJSONObject(0).getString("Status").toString()+" ","3")
                            }
                        }
                        else {
                            showDialog(mJSONObject.getString("Message").toString()+" ","3")
                        }
                    } else {
                        showDialog(mJSONObject.getString("Message").toString()+" ","1")
                    }
                } else {
                    showDialog("Something Wrong!","1")
                }
                        }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pausingDialog?.dismiss()
                showDialog("Something wrong try later!","3")
            }

        })
    }

    fun showDialog(title:String,type:String){
        Log.e(">>","1")
        SweetAlertDialog(this@MoneyTransferActivity, type.toInt())
            .setTitleText(title)
//            .setContentText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
                var mIntent: Intent?=null
                mIntent= Intent(this@MoneyTransferActivity, DashboardActivity::class.java)
                startActivity(mIntent)
                finish()
            }
            .show()
    }

    fun setView() {
        if (intent!=null) {

            ID= intent.getStringExtra("id").toString()
            bank_id= intent.getStringExtra("bankID").toString()
            mainBinding.txtAccountNo.setText(intent.getStringExtra("ifsccode"))
            mainBinding.txtName.setText(intent.getStringExtra("customerName"))
            mainBinding.txtBankname.setText(intent.getStringExtra("bankName"))

            Glide.with(this)
                .load(intent.getStringExtra("bankImage"))
                .thumbnail(0.5f)
                .into(mainBinding.ingBank)
        }
    }
}