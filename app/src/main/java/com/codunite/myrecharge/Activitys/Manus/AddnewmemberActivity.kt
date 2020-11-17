package com.codunite.myrecharge.Activitys.Manus

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.codunite.myrecharge.Activitys.DashboardActivity
import com.codunite.myrecharge.Custom_.StateListDialog
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.StateModel
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityAddnewmemberBinding
import com.codunite.myrecharge.databinding.FragmentProfileBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddnewmemberActivity : AppCompatActivity() {
    lateinit var thiscontext: Context
    lateinit var mainBinding : ActivityAddnewmemberBinding
    var pref= com.codunite.myrecharge.Helper.Local_data(this)
    var editTextStatus=true
    var pDialog: SweetAlertDialog?=null
    var TAG="@@Profile_Fragment"
    var stateList:ArrayList<com.codunite.myrecharge.Models.StateModel> = java.util.ArrayList()
    var stateID=" "
    lateinit var locationManager : LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        thiscontext=this@AddnewmemberActivity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_addnewmember)
        pref.setMyappContext(thiscontext)
        mainBinding.back.visibility=View.GONE
        pDialog=SweetAlertDialog(thiscontext, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#132752")
        pDialog!!.titleText = "Loading ..."
        pDialog!!.setCancelable(false)
        locationManager = thiscontext.getSystemService(LOCATION_SERVICE) as LocationManager

        Onclick()
        enableAll()

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
        mainBinding.txtState.setOnClickListener { getstateList() }

        mainBinding.btnSubmit.setOnClickListener {
            if(mainBinding.edtName.text!!.isEmpty()) {
                mainBinding.edtName.setError("Please Enter Name")
            } else if(mainBinding.edtLastName.text!!.isEmpty()) {
                mainBinding.edtLastName.setError("Please Enter Last Name")

            } else if(mainBinding.edtNumber.text!!.isEmpty()) {
                mainBinding.edtNumber.setError("Please Enter Number")

            } else if(mainBinding.edtAddress.text!!.isEmpty()) {
                mainBinding.edtAddress.setError("Please Enter Address")

            } else if(mainBinding.edtLandmark.text!!.isEmpty()) {
                mainBinding.edtLandmark.setError("Please Enter Landmark")

            }  else if(mainBinding.edtName.text!!.isEmpty()) {
                mainBinding.edtName.setError("Please Enter Name")
                ///////////////////////

            } else if(mainBinding.txtState.text!!.isEmpty()) {
                mainBinding.txtState.setError("Please Select State")
                ///////////////////////

            } else {
                Addnewmember()
            }
        }

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

    fun showDialog(title: String, type: String){
        SweetAlertDialog(thiscontext, type.toInt())
            .setTitleText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                startActivity(Intent(thiscontext,DashboardActivity::class.java))
                finish()
            }
            .show()
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

    fun Addnewmember() {
        pDialog?.show()

        var email=   mainBinding.edtEmail.text.toString()
        var gstNumber=   mainBinding.edtGstno.text.toString()
        if(email.isEmpty()) {
            email=" "
        }
        if(gstNumber.isEmpty()) {
            gstNumber=" "
        }

        var apiInterface: ApiInterface = RetrofitManager(thiscontext).instance!!.create(
            ApiInterface::class.java)


        apiInterface.Addnewmember(
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Msrno),
            mainBinding.edtName.text.toString(),
            mainBinding.edtLastName.text.toString(),
            mainBinding.edtNumber.text.toString(),
            email,
            mainBinding.edtAddress.text.toString(),
            mainBinding.edtLandmark.text.toString(),
            "1",
            mainBinding.edtZipCode.text.toString(),
            gstNumber,
            stateID
        ).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d(TAG, "onResponse: " + response.body().toString())
                Log.d(TAG, "onResponse: " + response.toString())
                pDialog!!.dismiss()
                disableAll()
                if (response.isSuccessful) {
                    try {
                    val jsonObject = JSONObject(response.body().toString())
                    if (jsonObject.getString("Error").toLowerCase().equals("false")) {
                        Toast.makeText(thiscontext, " " + jsonObject.getString("Message"), Toast.LENGTH_LONG).show()
                        disableAll()
                        showDialog(jsonObject.getString("Message") + " ", "2")

                        if(jsonObject.has("Data") && !jsonObject.isNull("Data") ) {
                            val json_Array: JSONArray = jsonObject.getJSONArray("Data")
                            for (i in 0 until json_Array.length()) {

                            }

                        }else {
                            showDialog(jsonObject.getString("Message"), "3")
                        }

                    } else {
                        showDialog("Something wrong", "3")
                    }
                }
                catch (e:Exception) {
                    showDialog("Something wrong", "3")
                }} else {
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
}