package com.codunite.myrecharge.Activitys

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.R
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplachScreen : AppCompatActivity() {
    var version: String? = null
    var mLocal_data= com.codunite.myrecharge.Helper.Local_data(this@SplachScreen)
    var mdevice:String="0000"
    lateinit var gifview:GifImageView
    lateinit var i_re_try:ImageView
    lateinit var pDialog: ProgressBar
    val TAG="@@splachscreen"
    public var MyReceiver1: BroadcastReceiver? = null
    var pausingDialog:SweetAlertDialog?=null
    var mDialog:SweetAlertDialog?=null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splach_screen)
        mLocal_data.setMyappContext(this@SplachScreen)
        i_re_try=findViewById(R.id.i_re_try)
        pDialog=findViewById(R.id.progress_bar)
        pausingDialog =SweetAlertDialog(this@SplachScreen, SweetAlertDialog.ERROR_TYPE)
        pausingDialog!!.titleText = "Application waiting for internet connection..."
        pausingDialog!!.setCancelable(false)
        mDialog =SweetAlertDialog(this@SplachScreen, SweetAlertDialog.ERROR_TYPE)
        mDialog!!.titleText = "Something Wrong go to login panel."
        mDialog!!.cancelText = "Re-start"
        mDialog!!.confirmText = "Login panel"
        mDialog!!.setCancelable(false)
        mDialog!!.setCancelClickListener{
            mDialog!!.dismiss()
            startActivity(Intent(this@SplachScreen,SplachScreen::class.java))
            finish()
        }
        mDialog!!.setConfirmClickListener{
            mDialog!!.dismiss()
            go_to_login()
            finish()
        }
        pausingDialog!!.setConfirmClickListener{

            try {
                var MyReceiver: BroadcastReceiver?= null;
                MyReceiver = com.codunite.myrecharge.Helper.MyReceiver();
                registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                if(pausingDialog!!.isShowing) {
                    pausingDialog!!.dismiss()
                }
                val intent = Intent()
                intent.component = ComponentName(
                    "com.android.settings",
                    "com.android.settings.Settings\$DataUsageSummaryActivity"
                )
                startActivity(intent)
            }catch (e:Exception) {
                Log.e(TAG, "onCreate: "+e.message.toString())
                go_to_login()
            }
        }

        MyReceiver1 = Network_reciver()
        registerReceiver(MyReceiver1, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    fun getUrl() {
        var url_Interface: ApiInterface = RetrofitManager(this@SplachScreen).getUrl_instance!!.create(ApiInterface::class.java)
        url_Interface.getUrl("Paymyrecharge").enqueue(object : Callback<JsonArray>{
            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.d(TAG, "onFailure: "+t.message.toString()+" ")
                showDialog()
            }

            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                Log.d(TAG, "onResponse: "+response.toString())
                if (response.isSuccessful){
                    var mjsonArray:JSONArray = JSONArray(response.body().toString())
                    var mjsonObject:JSONObject = mjsonArray.getJSONObject(0)

                    Log.d(TAG, "onResponse: url"+mjsonObject.getString("AppUrl"))
                    //Toast.makeText(this@SplachScreen," "+mjsonObject.getString("UrlName").toString(),Toast.LENGTH_LONG).show()

                    mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_base_url,mjsonObject.getString("AppUrl"))
                    if ((mLocal_data.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile).length)>4){
                        login() }
                    else{
                        go_to_login()
                    }
                }else{
                    showDialog()
                }
            }
        })
    }

    fun login()
    {
        Log.d(TAG, "login: username"+mLocal_data.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile))
        Log.d(TAG, "login: password"+mLocal_data.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password))
        pDialog!!.visibility=View.VISIBLE
        var apiInterface: ApiInterface = RetrofitManager(this@SplachScreen).instance!!.create(ApiInterface::class.java)

        apiInterface.getLogin(mLocal_data.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),mLocal_data.ReadStringPreferences(
            com.codunite.myrecharge.Helper.Constances.PREF_Login_password)).
        enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@SplachScreen," "+t.message.toString(),Toast.LENGTH_LONG).show()
                pDialog!!.visibility=View.GONE
            }

            @SuppressLint("WrongConstant")
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful) {

                    Log.d(TAG, "onResponse: "+response.body().toString())
                    val jsonObject= JSONObject(response.body().toString())

                    if(jsonObject.getString("Error").toLowerCase().equals("false")){
                        //Toast.makeText(this@SplachScreen," "+jsonObject.getString("Message"),Toast.LENGTH_LONG).show()

                        val json_Array: JSONArray =jsonObject.getJSONArray("Data")

                        for (i in 0 until json_Array.length()){

                            val jsonobject1:JSONObject = json_Array.getJSONObject(i)
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Msrno,jsonobject1.getString("Msrno"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Membertype,jsonobject1.getString("Membertype"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_MemberID,jsonobject1.getString("MemberID"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_MemberName,jsonobject1.getString("MemberName"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Mobile,jsonobject1.getString("Mobile"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_TransPass,jsonobject1.getString("TransPass"))

                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_email,jsonobject1.getString("Email"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Address,jsonobject1.getString("Address"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Landmark,jsonobject1.getString("landmark"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_CountryCode,jsonobject1.getString("CountryID"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_StateId,jsonobject1.getString("stateID"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Zip,jsonobject1.getString("ZIP"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_GST_no,jsonobject1.getString("GSTno"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_F_name,jsonobject1.getString("FirstName"))
                            mLocal_data.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_L_name,jsonobject1.getString("LastName"))
                            pDialog!!.visibility=View.GONE
                            Log.d(TAG, "onResponse: 1"+jsonobject1.getString("Msrno"))
                            Log.d(TAG, "onResponse: 2"+jsonobject1.getString("Membertype"))
                            Log.e("##",jsonobject1.getString("LastName"))
                            if(mDialog?.isShowing!!) { mDialog?.dismiss()}
                            if(pausingDialog?.isShowing!!) { pausingDialog?.dismiss()}
                            startActivity(Intent(this@SplachScreen,DashboardActivity::class.java))
                            unregisterReceiver(MyReceiver1)
                            finish()
                        }
                    }
                    else{
                        showDialog()
                        startActivity(Intent(this@SplachScreen,Login_Activity::class.java))

                    }
                }else{
                    pDialog!!.visibility=View.GONE
                    Toast.makeText(this@SplachScreen,"Bad Response ! ",Toast.LENGTH_LONG).show()
                    showDialog()
                }
            }
        })
    }

    inner open class Network_reciver() : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {


            var status: String = NetworkUtil.getConnectivityStatusString(context)!!
            Log.d(TAG, "onReceive: "+status)
            if(status.equals("No internet is available"))
            {
                blockActivity(false,context)
            }else{
                Log.d("@@", "onReceive: 3")
                blockActivity(true,context)
            }
        }

        fun blockActivity(connected: Boolean,context: Context) {

            Log.d(TAG, "blockActivity: "+connected.toString())
            if (pausingDialog == null) {
                pausingDialog!!.setConfirmClickListener{
                   var MyReceiver: BroadcastReceiver?= null;
                    MyReceiver = com.codunite.myrecharge.Helper.MyReceiver();
                    context.registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
                    pausingDialog!!.dismiss()

                    val intent = Intent()
                    intent.component = ComponentName(
                        "com.android.settings",
                        "com.android.settings.Settings\$DataUsageSummaryActivity"
                    )
                    startActivity(intent)
                }
            }

            if (!connected) {
                if (!(context as Activity).isFinishing) {
                    pausingDialog!!.show()

                }
            } else {
                if(pausingDialog!!.isShowing){
                pausingDialog!!.dismiss()}
                getUrl()
                Log.d(TAG, "blockActivity: 1_"+(mLocal_data.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile)).toString().length)

            }
        }
    }

    fun showDialog(){

        if (mDialog == null) {

            mDialog!!.setConfirmClickListener{
                mDialog!!.dismiss()
                go_to_login()
            }
            mDialog!!.setCancelClickListener{
                mDialog!!.dismiss()
                go_to_login()
            }

        }
        try {
        if(!isFinishing())
        {
            if (!mDialog?.isShowing!!){
                mDialog?.show()
            }
        } } catch (e:java.lang.Exception) {
            Log.e("@@error",e.message.toString())
        }

    }

    override fun onDestroy() {
        try {
            if (pausingDialog != null)
            if (mDialog != null){ mDialog?.dismiss() }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

/*    override fun onPause() {
        try {
            if (pausingDialog != null) pausingDialog?.dismiss()
            if (mDialog != null) mDialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onPause()
    }*/
    fun go_to_login() {

    var i:Intent
    i=Intent(this@SplachScreen,Login_Activity::class.java)
    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(i)
    unregisterReceiver(MyReceiver1)
    }
}