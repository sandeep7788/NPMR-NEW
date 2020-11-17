package com.codunite.myrecharge.Activitys

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentTransaction
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityLoginBinding
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login_Activity: AppCompatActivity() {

    lateinit var mainBinding :ActivityLoginBinding
    lateinit var transaction: FragmentTransaction
    lateinit var mLocal_data: com.codunite.myrecharge.Helper.Local_data
    var pref= com.codunite.myrecharge.Helper.Local_data(this@Login_Activity)
    var TAG="@@login"
    var pDialog:SweetAlertDialog ?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        mLocal_data= com.codunite.myrecharge.Helper.Local_data(this)

        pDialog=SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#132752")
        pDialog!!.titleText = "Loading ..."
        pDialog!!.setCancelable(false)

        var MyReceiver: BroadcastReceiver? = null;
        MyReceiver = com.codunite.myrecharge.Helper.MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))



        mainBinding.btnSignIn.setOnClickListener {

            if(mainBinding.edtLoginID.text.isEmpty())
            {
                mainBinding.edtLoginID.setError("Please Enter Login-ID")
            }
            else if(mainBinding.edtPassword.text.isEmpty())
            {
                mainBinding.edtPassword.setError("Please Enter Password")
            }
            else
            {
                getUrl(true)

            }
        }
        mainBinding.tvForgotPassword.setOnClickListener {
            getUrl(false)
        }
    }

    fun getUrl(status: Boolean) {
        var url_Interface: ApiInterface = RetrofitManager(this@Login_Activity).getUrl_instance!!.create(
            ApiInterface::class.java
        )
        url_Interface.getUrl("Paymyrecharge").enqueue(object : Callback<JsonArray> {
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d(TAG, "onFailure: " + t.message.toString() + " ")
                Toast.makeText(this@Login_Activity, " " + t.message, Toast.LENGTH_LONG).show()

                showDialog("Something wrong", "3")
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.d(TAG, "onResponse: " + response.toString())
                if (response.isSuccessful) {
                    var mjsonArray: JSONArray = JSONArray(response.body().toString())
                    var mjsonObject: JSONObject = mjsonArray.getJSONObject(0)

                    Log.d(TAG, "onResponse: url" + mjsonObject.getString("AppUrl"))
                    //Toast.makeText(this@Login_Activity," "+mjsonObject.getString("UrlName").toString(),Toast.LENGTH_LONG).show()

                    mLocal_data.writeStringPreference(
                        com.codunite.myrecharge.Helper.Constances.PREF_base_url, mjsonObject.getString("AppUrl"))

                    Log.e("##", status.toString())
                    if (status) {
                        login()
                    } else {
                        showforgetpasswordDialog()
                        Log.e("##", status.toString() + " 1")
                    }
                } else {
                    showDialog("Something wrong", "3")
                }
            }
        })
    }

    fun login()
    {
        pDialog!!.show()
        var apiInterface:ApiInterface=RetrofitManager(this).instance!!.create(ApiInterface::class.java)

        apiInterface.getLogin(
            mainBinding.edtLoginID.text.toString(),
            mainBinding.edtPassword.text.toString()
        ).
    enqueue(object : Callback<JsonObject> {
        override fun onFailure(call: Call<JsonObject>, t: Throwable) {
            Toast.makeText(this@Login_Activity, " " + t.message.toString(), Toast.LENGTH_LONG)
                .show()
            pDialog!!.dismiss()
        }

        override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
            pDialog!!.dismiss()
            if (response.isSuccessful) {

                Log.d(TAG, "onResponse: " + response.body().toString())
                val jsonObject = JSONObject(response.body().toString())

                if (jsonObject.getString("Error").toLowerCase().equals("false")) {
//                    Toast.makeText(this@Login_Activity," "+jsonObject.getString("Message"),Toast.LENGTH_LONG).show()

                    val json_Array: JSONArray = jsonObject.getJSONArray("Data")
                    for (i in 0 until json_Array.length()) {

                        val jsonobject1: JSONObject = json_Array.getJSONObject(i)
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Login_password,
                            mainBinding.edtPassword.text.toString()
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Msrno, jsonobject1.getString(
                                "Msrno"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Membertype, jsonobject1.getString(
                                "Membertype"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_MemberID, jsonobject1.getString(
                                "MemberID"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_MemberName, jsonobject1.getString(
                                "MemberName"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Mobile, jsonobject1.getString(
                                "Mobile"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_TransPass, jsonobject1.getString(
                                "TransPass"
                            )
                        )
                        //
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_email, jsonobject1.getString(
                                "Email"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Address, jsonobject1.getString(
                                "Address"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Landmark, jsonobject1.getString(
                                "landmark"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_CountryCode, jsonobject1.getString(
                                "CountryID"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_StateId, jsonobject1.getString(
                                "stateID"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_Zip, jsonobject1.getString(
                                "ZIP"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_GST_no, jsonobject1.getString(
                                "GSTno"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_F_name, jsonobject1.getString(
                                "FirstName"
                            )
                        )
                        mLocal_data.writeStringPreference(
                            com.codunite.myrecharge.Helper.Constances.PREF_L_name, jsonobject1.getString(
                                "LastName"
                            )
                        )

                        startActivity(Intent(this@Login_Activity, DashboardActivity::class.java))
                    }
                } else {
                    showDialog(jsonObject.getString("Message") + " ", "3")

                    Toast.makeText(this@Login_Activity, "Bad Response ! ", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this@Login_Activity, "Bad Response ! ", Toast.LENGTH_LONG).show()
            }

        }

    })
    }
    fun showDialog(title: String, type: String){

        val pDialog = SweetAlertDialog(this, type.toInt())
        pDialog.setTitleText(title)
        pDialog.confirmText="OK"
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
        }
        pDialog.show()
        pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundColor(getResources().getColor(R.color.c_blue_dark))

    }

    fun showforgetpasswordDialog() {

        val pDialog = SweetAlertDialog(this, 1)
        pDialog.setTitleText("Forget Password")
        pDialog.setContentText("Do you want to Send Request! \n")
        pDialog.confirmText="Continue"
        pDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        pDialog.setCancelable(false)
        pDialog.setConfirmClickListener {
            pDialog.dismiss()
            forgetPasswordMain()
        }
        pDialog.show()
        pDialog.getButton(SweetAlertDialog.BUTTON_CONFIRM).setBackgroundColor(getResources().getColor(R.color.c_blue_dark))
    }

    fun forgetPassword(username: String) {
        pDialog?.show()
        var apiInterface: ApiInterface = RetrofitManager(this).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.ForgotPassword(username, " ").enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pDialog?.dismiss()
                Log.d(TAG, "onResponse: " + response.toString())
                if (response.isSuccessful) {
                    var mjsonObject: JSONObject = JSONObject(response.body().toString())

//                    Log.d(TAG, "onResponse: url"+mjsonObject.getString("Error"))

                    if (mjsonObject.getString("Error").toLowerCase().equals("false")) {
                        Toast.makeText(
                            this@Login_Activity,
                            " " + mjsonObject.getString("Message"),
                            Toast.LENGTH_LONG
                        ).show()
                        showDialog(mjsonObject.getString("Message") + " ", "2")

                    } else {
                        showDialog(mjsonObject.getString("Message") + " ", "3")
                    }
                } else {
                    showDialog("Something wrong", "3")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                pDialog?.dismiss()
                showDialog("Something wrong", "3")
                Toast.makeText(this@Login_Activity, " " + t.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    protected fun forgetPasswordMain() {
        val dialogBuilder: android.app.AlertDialog? = android.app.AlertDialog.Builder(this).create()
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_forget_password_main, null)

        val edt_comment = dialogView.findViewById(R.id.edt_comment) as EditText
        val buttonSubmit = dialogView.findViewById(R.id.buttonSubmit) as Button
        val buttonCancel = dialogView.findViewById(R.id.buttonCancel) as Button

        buttonCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialogBuilder?.dismiss()
            }


        })
        buttonSubmit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                if(edt_comment.text.isEmpty()) {
                    Toast.makeText(this@Login_Activity, "Please Enter UserName!", Toast.LENGTH_LONG).show()
                } else {
                    dialogBuilder?.dismiss()
                    forgetPassword(edt_comment.text.toString())
                }
            }


        })

        dialogBuilder?.setView(dialogView)
        dialogBuilder?.show()
    }

}