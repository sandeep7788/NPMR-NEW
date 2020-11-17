package com.codunite.myrecharge.Fragment

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Custom_.StateListDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.StateModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile_Fragment : Fragment() {
    lateinit var thiscontext: Context
    lateinit var mainBinding : FragmentProfileBinding
    var pref= com.codunite.myrecharge.Helper.Local_data(context)
    var editTextStatus=true
    var pDialog: SweetAlertDialog?=null
    var TAG="@@Profile_Fragment"
    var stateList:ArrayList<com.codunite.myrecharge.Models.StateModel> = java.util.ArrayList()
    var stateID=" "
    lateinit var locationManager :LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? { mainBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        thiscontext=container!!.context
        pref.setMyappContext(context)
        mainBinding.toolbarLayout.back.visibility=View.GONE
        pDialog=SweetAlertDialog(thiscontext, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#132752")
        pDialog!!.titleText = "Loading ..."
        pDialog!!.setCancelable(false)
        locationManager = thiscontext.getSystemService(LOCATION_SERVICE) as LocationManager
        var myFocusListener =
            OnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    var l:BottomNavigationView= activity?.findViewById<BottomNavigationView>(R.id.navigation)!!
                    activity?.findViewById<BottomNavigationView>(R.id.navigation)?.visibility=View.GONE
                } else {
                    activity?.findViewById<BottomNavigationView>(R.id.navigation)?.visibility=View.VISIBLE
                }
            }

        mainBinding.toolbarLayout.btnUser.visibility=View.GONE
        mainBinding.edtName.onFocusChangeListener = myFocusListener
        mainBinding.edtAddress.onFocusChangeListener = myFocusListener
        mainBinding.edtEmail.onFocusChangeListener = myFocusListener
        mainBinding.edtGstno.onFocusChangeListener = myFocusListener
        mainBinding.edtLandmark.onFocusChangeListener = myFocusListener
        mainBinding.edtLastName.onFocusChangeListener = myFocusListener
        mainBinding.edtNumber.onFocusChangeListener = myFocusListener
        mainBinding.edtZipCode.onFocusChangeListener = myFocusListener
        Onclick()
        disableAll()
        setData()



        return mainBinding.root


    }

    fun Onclick() {
        mainBinding.edtIcon.setOnClickListener {
            if (editTextStatus) {
                enableAll()
                editTextStatus = false
                mainBinding.edtName.setInputType(InputType.TYPE_CLASS_TEXT)
                mainBinding.edtAddress.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                mainBinding.edtEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                mainBinding.edtGstno.setInputType(InputType.TYPE_CLASS_NUMBER)
                mainBinding.edtLandmark.setInputType(InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
                mainBinding.edtLastName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME)
                mainBinding.edtNumber.setInputType(InputType.TYPE_CLASS_NUMBER)
            } else {
                disableAll()
                editTextStatus = true
            }
        }

        mainBinding.btnSubmit.setOnClickListener {
            if(mainBinding.edtName.text!!.isEmpty()) {
                mainBinding.edtName.setError("Please Enter Name")
            } else if(mainBinding.edtLastName.text!!.isEmpty()) {
                mainBinding.edtLastName.setError("Please Enter Last Name")

            } else if(mainBinding.edtNumber.text!!.isEmpty()) {
                mainBinding.edtNumber.setError("Please Enter Number")

            } else if(mainBinding.edtEmail.text!!.isEmpty()) {
                mainBinding.edtEmail.setError("Please Enter E-mail")

            } else if(mainBinding.edtAddress.text!!.isEmpty()) {
                mainBinding.edtAddress.setError("Please Enter Address")

            } else if(mainBinding.edtLandmark.text!!.isEmpty()) {
                mainBinding.edtLandmark.setError("Please Enter Landmark")

            } else if(mainBinding.edtGstno.text!!.isEmpty()) {
                mainBinding.edtGstno.setError("Please Enter GST number")

            } else if(mainBinding.edtName.text!!.isEmpty()) {
                mainBinding.edtName.setError("Please Enter Name")
                ///////////////////////

            } else if(mainBinding.txtState.text!!.isEmpty()) {
                mainBinding.txtState.setError("Please Select State")
                ///////////////////////

            } else {
                updateProcess()
            }
        }
        mainBinding.txtState.setOnClickListener { getstateList() }
    }

    fun enableAll() {
        enableEditText(mainBinding.edtName)
        enableEditText(mainBinding.edtAddress)
        enableEditText(mainBinding.edtEmail)
        enableEditText(mainBinding.edtGstno)
        enableEditText(mainBinding.edtLandmark)
        enableEditText(mainBinding.edtLastName)
        enableEditText(mainBinding.edtNumber)
        enableEditText(mainBinding.edtZipCode)
        mainBinding.btnSubmit.visibility=View.VISIBLE
        mainBinding.txtState.isClickable=true
        mainBinding.view1.setBackgroundColor(getResources().getColor(R.color.c_white))
        mainBinding.view2.setBackgroundColor(getResources().getColor(R.color.c_white))
        mainBinding.edtIcon.setColorFilter(Color.parseColor("#0b892e"))
    }
    fun disableAll() {
        disableEditText(mainBinding.edtName)
        disableEditText(mainBinding.edtAddress)
        disableEditText(mainBinding.edtEmail)
        disableEditText(mainBinding.edtGstno)
        disableEditText(mainBinding.edtLandmark)
        disableEditText(mainBinding.edtLastName)
        disableEditText(mainBinding.edtNumber)
        disableEditText(mainBinding.edtZipCode)
        mainBinding.btnSubmit.visibility=View.INVISIBLE
        mainBinding.txtState.isClickable=false
        mainBinding.view1.setBackgroundColor(getResources().getColor(R.color.c_gray1))
        mainBinding.view2.setBackgroundColor(getResources().getColor(R.color.c_gray1))
        mainBinding.edtIcon.setColorFilter(Color.parseColor("#ed1b24"))
    }

    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
        editText.getBackground().setColorFilter(
            getResources().getColor(R.color.c_gray1),
            PorterDuff.Mode.SRC_ATOP
        )
    }

    private fun enableEditText(editText: EditText) {
        editText.isFocusable = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.setFocusableInTouchMode(true)
        editText.setKeyListener(AppCompatEditText(thiscontext).getKeyListener())
        editText.getBackground().setColorFilter(
            getResources().getColor(R.color.c_white),
            PorterDuff.Mode.SRC_ATOP
        )
    }
    fun updateProcess() {
        pDialog?.show()
        var apiInterface: ApiInterface = RetrofitManager(thiscontext).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.UpdateMemberProfile(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            mainBinding.edtName.text.toString(),
            mainBinding.edtLastName.text.toString(),
            mainBinding.edtNumber.text.toString(),
            mainBinding.edtEmail.text.toString(),
            mainBinding.edtAddress.text.toString(),
            mainBinding.edtLandmark.text.toString(),
            "1",
            mainBinding.edtZipCode.text.toString(),
            mainBinding.edtGstno.text.toString(),
            stateID
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(TAG, "onResponse: " + response.body().toString())
                Log.d(TAG, "onResponse: " + response.toString())
                pDialog!!.dismiss()
                disableAll()
                if (response.isSuccessful) {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.getString("Error").toLowerCase().equals("false")) {
                        Toast.makeText(thiscontext, " " + jsonObject.getString("Message"), Toast.LENGTH_LONG).show()
                        disableAll()
                        showDialog(jsonObject.getString("Message") + " ", "2")

                        if(jsonObject.has("Data") && !jsonObject.isNull("Data") ) {
                            val json_Array: JSONArray = jsonObject.getJSONArray("Data")
                            for (i in 0 until json_Array.length()) {

                                val jsonobject1: JSONObject = json_Array.getJSONObject(i)
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Msrno,
                                    jsonobject1.getString("Msrno")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Membertype,
                                    jsonobject1.getString("Membertype")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_MemberID,
                                    jsonobject1.getString("MemberID")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_MemberName,
                                    jsonobject1.getString("MemberName")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Mobile,
                                    jsonobject1.getString("Mobile")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_TransPass,
                                    jsonobject1.getString("TransPass")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_email,
                                    jsonobject1.getString("Email")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Address,
                                    jsonobject1.getString("Address")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Landmark,
                                    jsonobject1.getString("landmark")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_CountryCode,
                                    jsonobject1.getString("CountryID")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_StateId,
                                    jsonobject1.getString("stateID")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_Zip,
                                    jsonobject1.getString("ZIP")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_GST_no,
                                    jsonobject1.getString("GSTno")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_F_name,
                                    jsonobject1.getString("FirstName")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_L_name,
                                    jsonobject1.getString("LastName")
                                )
                                pref.writeStringPreference(
                                    com.codunite.myrecharge.Helper.Constances.PREF_GST_no,
                                    jsonobject1.getString("GSTno")
                                )

                                setData()

                            }
                        }else {
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
                Log.d(TAG, "onFailure: " + t.message.toString() + " ")
                Toast.makeText(thiscontext, " " + t.message, Toast.LENGTH_LONG).show()

                showDialog("Something wrong", "3")
            }

        })
    }
    fun showDialog(title: String, type: String){
        Log.d("@@" + TAG, "showDialog: ")
        SweetAlertDialog(thiscontext, type.toInt())
            .setTitleText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
            }
            .show()
    }

    fun setData() {
        Log.e("##", pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_L_name))
        mainBinding.txtMemberName.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_MemberName))
        mainBinding.edtName.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_F_name))
        mainBinding.edtLastName.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_L_name))
        mainBinding.edtEmail.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_email))
        mainBinding.edtAddress.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Address))
        mainBinding.edtLandmark.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Landmark))
        mainBinding.txtCountry.setText("India")
//        mainBinding.txtState.setText(pref.ReadStringPreferences(Constances.PREF_StateId))
        setStateName()
        mainBinding.edtGstno.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_GST_no))
        mainBinding.edtZipCode.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Zip))
        mainBinding.edtNumber.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile))

    }
    fun setStateName() {

        var apiInterface: ApiInterface = RetrofitManager(thiscontext).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.getAllState().
        enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(thiscontext, t.message.toString() + " ", Toast.LENGTH_LONG).show()

            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                stateList.clear()
                Log.d(TAG, "onResponse: " + response.toString())
                pDialog?.dismiss()
                if (response.isSuccessful) {

                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
//                    Toast.makeText(this@Prepaid_Activity,mJsonObject.getString("Message")+" ",Toast.LENGTH_LONG).show()

                    if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {


                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mStateModel: com.codunite.myrecharge.Models.StateModel =
                                    com.codunite.myrecharge.Models.StateModel()
                                mStateModel.stateID =
                                    JsonObjectData.getString("StateID").toInt()
                                mStateModel.stateName =
                                    JsonObjectData.getString("StateName")

                                stateList.add(mStateModel)
                                Log.d(
                                    "!!" + TAG,
                                    "data " + i.toString() + " " + mStateModel.stateName
                                )

                                if (JsonObjectData.getString("StateID")
                                        .toInt() == pref.ReadStringPreferences(
                                        com.codunite.myrecharge.Helper.Constances.PREF_StateId
                                    ).trim().toInt()
                                ) {
                                    mainBinding.txtState.setText(
                                        JsonObjectData.getString("StateName").toString()
                                    )
                                    stateID = JsonObjectData.getString("StateID").toString()
                                    mainBinding.txtState.setTextColor(Color.WHITE)
                                    mainBinding.txtCountry.setTextColor(Color.WHITE)
                                } else {

                                }

                            }


                        } else {

                        }
                    } else {

                    }
                } else {

                }
            }
        })

    }

    fun getstateList()
    {
        pDialog?.show()

        var apiInterface: ApiInterface = RetrofitManager(thiscontext).instance!!.create(
            ApiInterface::class.java
        )

        apiInterface.getAllState().
        enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(thiscontext, t.message.toString() + " ", Toast.LENGTH_LONG).show()
                pDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                stateList.clear()
                Log.d(TAG, "onResponse: " + response.toString())
                pDialog?.dismiss()
                if (response.isSuccessful) {

                    Log.d(TAG, "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
//                    Toast.makeText(this@Prepaid_Activity,mJsonObject.getString("Message")+" ",Toast.LENGTH_LONG).show()

                    if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {


                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mStateModel: com.codunite.myrecharge.Models.StateModel =
                                    com.codunite.myrecharge.Models.StateModel()
                                mStateModel.stateID =
                                    JsonObjectData.getString("StateID").toInt()
                                mStateModel.stateName =
                                    JsonObjectData.getString("StateName")

                                stateList.add(mStateModel)
                                Log.d(
                                    "!!" + TAG,
                                    "data " + i.toString() + " " + mStateModel.stateName
                                )

                            }
                            var operatorDialog: com.codunite.myrecharge.Custom_.StateListDialog =
                                com.codunite.myrecharge.Custom_.StateListDialog(
                                    thiscontext,
                                    stateList,
                                    mainBinding.txtState
                                )
                            operatorDialog.getWindow()!!.setFlags(
                                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN
                            );
                            operatorDialog.setDialogResult {
                                Log.e("@@data1", it.toString())
                                stateID = it.toString()
                            }
                            operatorDialog.show()

                        } else {

                        }
                    } else {

                    }
                } else {

                }
            }
        })
    }

}