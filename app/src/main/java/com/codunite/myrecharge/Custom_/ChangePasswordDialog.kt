package com.codunite.myrecharge.Custom_

import android.app.Activity
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityLoginBinding
import com.codunite.myrecharge.databinding.DialogChangePasswordBinding
import com.google.gson.JsonObject
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordDialog(var activity: Activity?):Dialog(activity!!) {

    lateinit var mainBinding : DialogChangePasswordBinding
    var pref = com.codunite.myrecharge.Helper.Local_data(activity)
    var TAG="@@dialog"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.dialog_change_password_)
        mainBinding = DataBindingUtil.setContentView(activity!!,R.layout.dialog_change_password_)
        pref= com.codunite.myrecharge.Helper.Local_data(activity)
/*        val layoutParams = window!!.attributes
        window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window!!.attributes = layoutParams
//        setCancelable(false)*/

        mainBinding.imgClose!!.setOnClickListener { dismiss() }
        mainBinding.btnSubmit.setOnClickListener {

            if(mainBinding.edtOld.text!!.isEmpty())
            {
                mainBinding.edtOld.setError("Please Enter Login-ID")
            }
            else if(mainBinding.edtNewpassword.text!!.isEmpty())
            {
                mainBinding.edtNewpassword.setError("Please Enter Password")
            }
            else if(mainBinding.edtReEnterNewPassword.text!!.isEmpty())
            {
                mainBinding.edtReEnterNewPassword.setError("Please Enter Password")
            }
            else if(mainBinding.edtReEnterNewPassword!!.text!!.trim()!!.equals(mainBinding.edtNewpassword!!.text!!.trim()))
            {
                Toast.makeText(activity,"new and conform password not same!",Toast.LENGTH_LONG).show()

            }
            else
            {
                changePasssword()

            }
        }

    }

    override fun onStart() {

        super.onStart()
    }
    fun changePasssword() {

        var apiInterface: ApiInterface = RetrofitManager(activity!!).instance!!.create(
            ApiInterface::class.java)

        apiInterface.ChangePassword(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),
            mainBinding.edtOld.text.toString(),
            mainBinding.edtNewpassword.text.toString()," ").enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(TAG, "onResponse: "+response.toString())
                if (response.isSuccessful){
                    var mjsonObject: JSONObject = JSONObject(response.body().toString())
//                    var mjsonObject: JSONObject = mjsonArray.getJSONObject(0)

                    Log.d(TAG, "onResponse: url"+mjsonObject.getString("Error"))

                    if(mjsonObject.getString("Error").toLowerCase().equals("false")) {
                        Toast.makeText(
                            activity,
                            " " + mjsonObject.getString("Message"),
                            Toast.LENGTH_LONG).show()
                        showDialog(mjsonObject.getString("Message")+" ","1")

                    }
                    else{
                        showDialog(mjsonObject.getString("Message")+" ","3")
                    }
                    }else{
                    showDialog("Something wrong","3")
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(activity," "+t.message, Toast.LENGTH_LONG).show()
            }

        })
    }
    fun showDialog(title:String,type:String){
        Log.d("@@"+TAG, "showDialog: ")
        SweetAlertDialog(activity, type.toInt())
            .setTitleText(title)
//            .setContentText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
            }
            .show()

    }
}