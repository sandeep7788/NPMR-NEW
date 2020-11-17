package com.codunite.myrecharge.Fragment

import android.Manifest
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Activitys.Manus.*
import com.codunite.myrecharge.Custom_.QrDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.FragmentHomeBinding
import com.google.gson.JsonObject
import com.sandeep.AndroidDialog.AndroidDialog
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home_Fragment: Fragment() {
    lateinit var thiscontext: Context
    lateinit var mainBinding : FragmentHomeBinding
    lateinit var transaction: FragmentTransaction
    var pref= com.codunite.myrecharge.Helper.Local_data(context)
    var TAG="@@homefragment"
    override fun onCreateView(inflater:LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View?
    {
        pref.setMyappContext(context)
        mainBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        thiscontext=container!!.context
        getBalance()
        initView()
        monClick()


        cln()

        return mainBinding.root
    }
    fun initView(){
        mainBinding.txtBalance.text="₹ "+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Balance)
    }

    fun monClick()
    {
        var mIntent:Intent ?=null
        mIntent=Intent(context,TopServicesActivity::class.java)
        mainBinding.btnPrepaid.setOnClickListener {
            pref.writeIntPreference("opt",1)
        startActivity(mIntent)}

        mainBinding.btnPostpaid.setOnClickListener {
            pref.writeIntPreference("opt",2)
            startActivity(mIntent) }

        mainBinding.btnDth.setOnClickListener {
            pref.writeIntPreference("opt",3)
            startActivity(mIntent) }

        mainBinding.btnElectric.setOnClickListener {
            pref.writeIntPreference("opt",4)
            startActivity(mIntent) }

        mainBinding.btnMTransfer.setOnClickListener {
            startActivity(Intent(thiscontext,MT_UserAddedBankAC::class.java)) }

        mainBinding.btnBus.setOnClickListener {
            showDialog_commingSoon() }

        mainBinding.btnFlight.setOnClickListener {
            showDialog_commingSoon() }

        mainBinding.btnDatacard.setOnClickListener {
            showDialog_commingSoon() }

        mainBinding.btnGas.setOnClickListener {
            showDialog_commingSoon() }
        mainBinding.btnLadline.setOnClickListener {
            pref.writeIntPreference("opt",9)
            startActivity(mIntent) }

        mainBinding.btnWater.setOnClickListener {
            showDialog_commingSoon() }

        mainBinding.btnHotel.setOnClickListener {
            showDialog_commingSoon() }

        mainBinding.layoutBankTransfer.setOnClickListener {
            startActivity(Intent(thiscontext,MT_UserAddedBankAC::class.java))
        }
        mainBinding.btnPay.setOnClickListener { startActivity(Intent(context,Pay_Activity::class.java)) }
        mainBinding.btnHistory.setOnClickListener { startActivity(Intent(context,History_Activity::class.java)) }
         mainBinding.btnElectric.setOnClickListener { startActivity(Intent(context,Electricity_Activity::class.java)) }
        mainBinding.imgQr.setOnClickListener {

            var mQrDialog : QrDialog
              mQrDialog=
                  QrDialog(activity)
            mQrDialog.getWindow()!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mQrDialog.show()

        }
        mainBinding.layoutPaytomember.setOnClickListener {
            val requiredPermission = Manifest.permission.CAMERA
            val checkVal: Int = thiscontext.checkCallingOrSelfPermission(requiredPermission)

            if (checkVal== PackageManager.PERMISSION_GRANTED){
                startActivity(Intent(thiscontext,Scanner_Activity::class.java))
                Log.e(">>","if")
            } else {
                Log.e(">>","else")
                Toast.makeText(thiscontext,"Please Allow Camera Permission.",Toast.LENGTH_SHORT).show()

                if (ActivityCompat.checkSelfPermission(thiscontext,
                        Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED
                ) {

                    requestPermissions(arrayOf(Manifest.permission.CAMERA), 101)
                } else {
                    Log.e("DB", "PERMISSION GRANTED")
                }

            }
        }
        mainBinding.imgUser.setOnClickListener { showProfile() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(thiscontext,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT)
                    .show();
            }
            else {

                Toast.makeText(thiscontext, "Please Allow Camera Permission.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
        fun showProfile() {
            var mIntent: Intent?=null
            mIntent= Intent(thiscontext,DashboardActivity::class.java)
            mIntent.putExtra("from_HF",true)
            startActivity(mIntent)
        }

    fun dialog()
    {
        val dialog = AndroidDialog(thiscontext)
        dialog.setColor(Color.parseColor("#041e37"))
        dialog.setTitle("Your Current Balance")
        dialog.setColor(R.color.colorPrimaryDark)
        dialog.setContentTextColor(R.color.colorPrimaryDark)
        dialog.contentText = pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Balance)+" "
        dialog.setPositiveListener(
            "Ok"
        ) { dialog ->
            Toast.makeText(
                thiscontext,
                dialog.positiveText.toString(),
                Toast.LENGTH_SHORT
            ).show()
            dialog.dismiss()
        }.show()

    }

    fun showDialog_commingSoon(){
        SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Coming soon")
            .setContentText("work on progress")
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
            }
            .show()
    }


    private fun createBottomUpAnimation(
        view: View,
        listener: AnimatorListenerAdapter?, distance: Float
    ): ObjectAnimator? {
        val animator = ObjectAnimator.ofFloat(view, "translationY", -distance)
        //        animator.setDuration(???)
        animator.removeAllListeners()
        if (listener != null) {
            animator.addListener(listener)
        }
        return animator
    }
    fun createTopDownAnimation(
        view: View, listener: AnimatorListenerAdapter?,
        distance: Float
    ): ObjectAnimator? {
        view.translationY = -distance
        val animator = ObjectAnimator.ofFloat(view, "translationY", 0f)
        animator.removeAllListeners()
        if (listener != null) {
            animator.addListener(listener)
        }
        return animator
    }
    fun cln() {
        Log.d(TAG, "setview1 "+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_amt))
        Log.d(TAG, "setview2 "+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code))
        Log.d(TAG, "setview3 "+pref.ReadIntPreferences("opt"))
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_amt,"")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_operator_code,"")
        pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_temp_number,"")
        pref.writeIntPreference("opt",0)
    }

    fun getBalance()     {
        var apiInterface: ApiInterface = context?.let { RetrofitManager(it).instance }!!.create(ApiInterface::class.java)

        apiInterface.getUserBalance(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),"").
        enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context," "+t.message.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(response.isSuccessful) {

                    Log.d(TAG, "onResponse: "+response.toString())
                    Log.d(TAG, "onResponse: "+response.body().toString())

                    val jsonObject= JSONObject(response.body().toString())

                    if(jsonObject.getString("Error").toLowerCase().equals("false")){
//                        Toast.makeText(context," "+jsonObject.getString("Message"),Toast.LENGTH_LONG).show()
                        if(jsonObject.has("Data") && !jsonObject.isNull("Data") ) {
                            val json_Array: JSONArray = jsonObject.getJSONArray("Data")

                            val jsonobject1: JSONObject = json_Array.getJSONObject(0)
                            pref.writeStringPreference(com.codunite.myrecharge.Helper.Constances.PREF_Balance,jsonobject1.getString("Balance"))
                            mainBinding.txtBalance.text = "₹ "+jsonobject1.getString("Balance")
                        }else{
                            Toast.makeText(context,"Bad Response when get balance.",Toast.LENGTH_LONG).show()
                        }
                    }
                    else{
                        Toast.makeText(context,"Bad Response when get balance.",Toast.LENGTH_LONG).show()
                    }
                }else{

                    Toast.makeText(context,"Bad Response when get balance.",Toast.LENGTH_LONG).show()
                }

            }

        })
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
        getBalance()
    }
}