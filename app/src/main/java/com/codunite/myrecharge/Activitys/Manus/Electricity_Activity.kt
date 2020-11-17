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
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Custom_.CustomDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.HistoryModel
import com.codunite.myrecharge.Models.OperatorModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityElectricityBinding
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_electricity_.view.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Electricity_Activity : AppCompatActivity() {
    lateinit var mainBinding: ActivityElectricityBinding
    var pref =
        com.codunite.myrecharge.Helper.Local_data(this@Electricity_Activity)
    var TAG="@@electricity"
    var pausingDialog: SweetAlertDialog?=null
    var history_list:ArrayList<com.codunite.myrecharge.Models.HistoryModel> = java.util.ArrayList()
    var Last_History_ID=0
    var linearLayoutManager: LinearLayoutManager? = null
    var mOperator_CODE="101"
    var OperatorName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_electricity_)
        var MyReceiver: BroadcastReceiver?= null;
        MyReceiver = com.codunite.myrecharge.Helper.MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        pausingDialog =SweetAlertDialog(this,SweetAlertDialog.PROGRESS_TYPE)
        pausingDialog!!.titleText = "Please wait...."
        pausingDialog!!.setCancelable(false)

        mOnClick()

        mainBinding.txtBoard.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.e(TAG, "afterTextChanged: 1"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile)+"_\n"+pref.ReadStringPreferences(
                    com.codunite.myrecharge.Helper.Constances.PREF_Login_password)
                        +"_\n"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code) )
                mainBinding.layoutShoewFachDetails.visibility = View.GONE
                cln_edt()
                get_billGetFatchbilleroperatordetails()

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.e(TAG, "afterTextChanged: 2"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile)+"_\n"+pref.ReadStringPreferences(
                    com.codunite.myrecharge.Helper.Constances.PREF_Login_password)
                        +"_\n"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code) )
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.e(TAG, "afterTextChanged: 3"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile)+"_\n"+pref.ReadStringPreferences(
                    com.codunite.myrecharge.Helper.Constances.PREF_Login_password)
                        +"_\n"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code) )

            }
        })
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

    fun mOnClick()
    {
        mainBinding.toolbarLayout.back.setOnClickListener {
            super.onBackPressed()
        }
        mainBinding.txtBoard.setOnClickListener {
            getoperatorlist("7")
            mainBinding.edtBillNumber.setError(null)
            mainBinding.txtBoard.setError(null)
        }
        mainBinding.btnFatchBill.setOnClickListener {
            if (mainBinding.txtBoard.text.isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity, "Please Select Board", Toast.LENGTH_LONG
                ).show()
                mainBinding.txtBoard.setError("Please Select Board")
            } else if (mainBinding.edtBillNumber.text.isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity, "Please Enter Bill number", Toast.LENGTH_LONG
                ).show()
                mainBinding.edtBillNumber.setError("Please Enter Bill number")
            } else {
                mainBinding.txtBoard.setError(null)
                mainBinding.edtBillNumber.setError(null)
                fatch_Bill()
            }
        }

        mainBinding.btnProcess.setOnClickListener {
            mainBinding.edtBillNumber.setError(null)
            mainBinding.edtNumber.setError(null)
            mainBinding.edtAmount.setError(null)
            if(mainBinding.txtBoard.text.isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity,"Please Select Board",Toast.LENGTH_LONG).show()
                mainBinding.txtBoard.setError("Please Select Board")
            } else if(mainBinding.edtBillNumber.text.isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity,"Please Enter Bill number",Toast.LENGTH_LONG).show()
                mainBinding.edtBillNumber.setError("Please Enter Bill number")
            } else if(mainBinding.edtNumber.text!!.isEmpty()&&mainBinding.edtNumber.text!!.length<9) {
                Toast.makeText(
                    this@Electricity_Activity,"Please Enter Mobile Number",Toast.LENGTH_LONG).show()
                mainBinding.edtNumber.setError("Please Enter Mobile Number")
            } else if(mainBinding.edtAmount.text!!.isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity,"Please Enter Amount",Toast.LENGTH_LONG).show()
                mainBinding.edtAmount.setError("Please Enter Amount")
            } else if(mainBinding.cName.text.toString().isEmpty()) {
                Toast.makeText(
                    this@Electricity_Activity,"Please Fetch Bill Details!",Toast.LENGTH_LONG).show()
                mainBinding.btnFatchBill.setError("Please Fetch Details!")
            }
            else {
                mainBinding.edtBillNumber.setError(null)
                mainBinding.edtNumber.setError(null)
                mainBinding.edtAmount.setError(null)
                Log.d(TAG, "mOnClick: ")
                BillElectricitypayment()
                }
        }
    }



    fun fatch_Bill() {
        pausingDialog?.show()
        Log.e(TAG,"caled1"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code)+"_");
        var apiInterface: ApiInterface = RetrofitManager(this@Electricity_Activity).instance!!.create(
            ApiInterface::class.java)
        apiInterface.billGetFatchdetails(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code),mainBinding.edtBillNumber.text.toString()).enqueue(object : Callback<JsonObject> {
                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(this@Electricity_Activity,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                    pausingDialog?.dismiss()
                    mainBinding.txtInvalid.visibility = View.VISIBLE
                }

                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    pausingDialog?.dismiss()
                    if(response.isSuccessful) {
                        Log.d(TAG, "billGetFatchdetails" + response.body().toString())
                        var mJsonObject = JSONObject(response.body().toString())
                     /*   Toast.makeText(
                            this@Electricity_Activity, mJsonObject.getString("Message") + " ",Toast.LENGTH_LONG).show()
                        */
                        if (mJsonObject.getString("Error").toLowerCase().equals("false")) {

                                var mJsonobject = mJsonObject.getJSONObject("Data")


                                if(mJsonobject.has("records") && !mJsonobject.isNull("records") ) {

                                    var mRecords = mJsonobject.getJSONArray("records")
                                    var mRecordsObject = mRecords.getJSONObject(0)

                                    Log.e("??","2"+mRecordsObject.getString("CustomerName"))
                                    if(mRecordsObject.getString("CustomerName")!=null&&mRecordsObject.getString("Billamount")!=null&&
                                        mRecordsObject.getString("Billdate")!=null&&mRecordsObject.getString("Duedate")!=null && !mRecordsObject.getString("CustomerName").toString().equals("null") &&  !mRecordsObject.getString("CustomerName").toString().isEmpty()) {
                            
                                        mainBinding.cName.text =
                                            mRecordsObject.getString("CustomerName")
                                        mainBinding.cAmount.text =
                                            mRecordsObject.getString("Billamount")
                                        mainBinding.cBillDate.text =
                                            mRecordsObject.getString("Billdate")
                                        mainBinding.cDueDate.text =
                                            mRecordsObject.getString("Duedate")
                                        mainBinding.imgFachbill.visibility = View.GONE
                                        mainBinding.txtInvalid.visibility = View.GONE

                                        mainBinding.layoutShoewFachDetails.visibility = View.VISIBLE

                                    } else {
                                        mainBinding.imgFachbill.visibility=View.VISIBLE
                                        mainBinding.layoutShoewFachDetails.visibility=View.GONE
                                        mainBinding.txtInvalid.visibility = View.VISIBLE
                                    }
                                }
                                else {
                                    mainBinding.imgFachbill.visibility=View.VISIBLE
                                    mainBinding.layoutShoewFachDetails.visibility=View.GONE
                                    mainBinding.txtInvalid.visibility = View.VISIBLE
                                }
                        } else { mainBinding.txtInvalid.visibility = View.VISIBLE }
                    } else { mainBinding.txtInvalid.visibility = View.VISIBLE }
                }
            })
    }

    fun get_billGetFatchbilleroperatordetails() {

        pausingDialog?.show()
        var operatorList:ArrayList<com.codunite.myrecharge.Models.OperatorModel> = java.util.ArrayList()
        var apiInterface: ApiInterface = RetrofitManager(this@Electricity_Activity).instance!!.create(
            ApiInterface::class.java)


        apiInterface.get_billGetFatchbilleroperatordetails(pref.ReadStringPreferences(
            com.codunite.myrecharge.Helper.Constances.PREF_Mobile),pref.ReadStringPreferences(
            com.codunite.myrecharge.Helper.Constances.PREF_Login_password),pref.ReadStringPreferences(
            com.codunite.myrecharge.Helper.Constances.PREF_operator_code),mainBinding.edtBillNumber.text.toString()).enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@Electricity_Activity,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pausingDialog?.dismiss()
                if(response.isSuccessful) {
                    Log.d(TAG, "onResponse: 1" + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                  /*  Toast.makeText(
                        this@Electricity_Activity, mJsonObject.getString("Message") + " ",
                        Toast.LENGTH_LONG
                    ).show()*/
                    if (mJsonObject.getString("Error").toLowerCase().equals("false")) {
                        var mJsonArray = mJsonObject.getJSONArray("Data")
                        Log.e(TAG, "onResponse: size "+mJsonArray.length().toString())
                        if(mJsonArray.length()>=0) {
                            var JsonObjectData = mJsonArray.getJSONObject(0)

                            /*JsonObjectData.getString("OperatorName")
                        JsonObjectData.getString("operatorcode")
                        JsonObjectData.getString("Numbertype")
                        JsonObjectData.getString("Extranumber")
                        JsonObjectData.getString("digit")*/
                            mainBinding.edtBillNumber.setHint(JsonObjectData.getString("Numbertype").toString())
                            OperatorName=JsonObjectData.getString("operatorcode").toString()
//                            operatorcode = JsonObjectData.getString("operatorcode");
                            show_fachbill_layout(JsonObjectData.getString("Numbertype") + " ")
                        }
                    }
                }
            }

        })
    }

    fun getoperatorlist(operator_type:String)
    {
        pausingDialog?.show()
        var operatorList:ArrayList<com.codunite.myrecharge.Models.OperatorModel> = java.util.ArrayList()
        var apiInterface: ApiInterface = RetrofitManager(this@Electricity_Activity).instance!!.create(
            ApiInterface::class.java)

        apiInterface.getoperatordetails(operator_type).enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@Electricity_Activity,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pausingDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pausingDialog?.dismiss()
                if(response.isSuccessful) {
                    Log.d(TAG, "onResponse: "+response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog : com.codunite.myrecharge.Custom_.CustomDialog
            /*        Toast.makeText(this@Electricity_Activity,mJsonObject.getString("Message")+" ",
                        Toast.LENGTH_LONG).show()*/
                    if (mJsonObject.getString("Error").toLowerCase().equals("false")){
                        var mJsonArray=mJsonObject.getJSONArray("Data")

                        operatorList.clear()

                        for (i in 0 until mJsonArray.length()) {
                            var JsonObjectData=mJsonArray.getJSONObject(i)
                            var mOperatorModel: com.codunite.myrecharge.Models.OperatorModel =
                                com.codunite.myrecharge.Models.OperatorModel()
                            mOperatorModel.operatorID=JsonObjectData.getString("OperatorID").toInt()
                            mOperatorModel.operatorName=JsonObjectData.getString("OperatorName")
                            mOperatorModel.operatorCode=JsonObjectData.getString("OperatorCode")
                            mOperatorModel.operaotimageurl=JsonObjectData.getString("operaotimageurl")
                            operatorList.add(mOperatorModel) }

                        operatorDialog=
                            com.codunite.myrecharge.Custom_.CustomDialog(
                                this@Electricity_Activity,
                                operatorList,
                                mainBinding.txtBoard,
                                mOperator_CODE
                            )
                        operatorDialog.getWindow()!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        operatorDialog.show()
                    }
                }

            }
        })
    }

    fun show_fachbill_layout(hint:String) {
        mainBinding.layoutFachBill.visibility=View.VISIBLE
        mainBinding.layoutFachBill.edt_bill_number.setHint(hint)
    }
    override fun onResume() {
        Log.d(TAG, "onResume: "+mOperator_CODE)
        super.onResume()

    }

    override fun onBackPressed() {
        cln_data()
        super.onBackPressed()
    }

    fun cln_data() {
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_amt,"")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_operator_code,"")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_temp_number,"")
        pref.writeIntPreference("opt",0)
    }
    fun cln_edt() {
        mainBinding.edtBillNumber.setText("")
        mainBinding.edtAmount.setText("")
        mainBinding.edtNumber.setText("")
        mainBinding.cName.setText("")
        mainBinding.cAmount.setText("")
        mainBinding.cBillDate.setText("")
        mainBinding.cDueDate.setText("")
    }

    fun BillElectricitypayment() {

        pausingDialog!!.show()
        var apiInterface: ApiInterface =
            RetrofitManager(this).instance!!.create(ApiInterface::class.java)

        Log.e(
            "@@data", pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile) + "\n" +
                    pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password) + "\n" +
                    mainBinding.edtAmount.text.toString() + "\n" +
                    OperatorName + "\n" +
                    mainBinding.edtBillNumber.text.toString() + "\n" +
                    mainBinding.edtNumber.text.toString())

        if (OperatorName.isEmpty()) {
            Toast.makeText(this@Electricity_Activity, "Please Select Operator!", Toast.LENGTH_LONG)
                .show()
        } else {
            apiInterface.BillElectricitypayment(
                pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),
                pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password),
                mainBinding.edtAmount.text.toString(),
                OperatorName,
                mainBinding.edtBillNumber.text.toString(),
                mainBinding.edtNumber.text.toString()
            ).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    Log.d(TAG, "onResponse: " + response.body().toString())
                    Log.d(TAG, "onResponse: " + response.toString())
                    pausingDialog!!.dismiss()

                    if (response.isSuccessful) {
                        Log.d(TAG, "onResponse: " + response.toString())
                        Log.d(TAG, "onResponse: " + response.body().toString())
                        var mJsonObject = JSONObject(response.body().toString())

                        if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                            var mJsonArray = mJsonObject.getJSONArray("Data")

                            if (mJsonObject.getString("Error").toLowerCase()
                                    .equals("false") && mJsonArray != null
                            ) {

                                showDialog(mJsonArray.getJSONObject(0).getString("Status"), "0")
                            } else {
                                showDialog("Something wrong", "3")
                            }
                        } else {
                            showDialog("Something wrong", "3")
                        }
                    } else {
                        showDialog("Something wrong", "3")
                    }
                }


                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    Toast.makeText(
                        this@Electricity_Activity,
                        " " + t.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    pausingDialog!!.dismiss()
                    showDialog("Something wrong", "3")
                }
            })
        }
    }
    fun showDialog(title: String, type: String) {
        Log.d("@@" + TAG, "showDialog: ")
        SweetAlertDialog(this, type.toInt())
            .setTitleText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
                startActivity(Intent(this@Electricity_Activity,DashboardActivity::class.java))
                finish()
            }
            .show()

    }

}