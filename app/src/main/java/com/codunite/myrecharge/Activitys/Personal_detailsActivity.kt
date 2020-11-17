package com.codunite.myrecharge.Activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityPersonalDetailsBinding

class Personal_detailsActivity : AppCompatActivity() {
    lateinit var mainBinding : ActivityPersonalDetailsBinding
    var pref=
        com.codunite.myrecharge.Helper.Local_data(this@Personal_detailsActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_personal_details)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding =
            DataBindingUtil.setContentView(
                this,
                R.layout.activity_personal_details
            )
        pref.setMyappContext(this@Personal_detailsActivity)

        set_data()
        set_editable_false()
        set_listeners()





    }
    fun set_data() {
        mainBinding.edtName.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_MemberName))
        mainBinding.edtAddress.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Address))
        mainBinding.edtMobileno.setText(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile))
    }

    fun set_editable_false()
    {
        mainBinding.edtName.isFocusable = false;
        mainBinding.edtName.isFocusableInTouchMode = false
        mainBinding.edtName.isClickable = false

        mainBinding.edtMobileno.isFocusable = false;
        mainBinding.edtMobileno.isFocusableInTouchMode = false
        mainBinding.edtMobileno.isClickable = false

        mainBinding.edtName.isFocusable = false;
        mainBinding.edtName.isFocusableInTouchMode = false
        mainBinding.edtName.isClickable = false

        mainBinding.edtName.isFocusable = false;
        mainBinding.edtName.isFocusableInTouchMode = false
        mainBinding.edtName.isClickable = false
    }
    fun set_listeners()
    {
        mainBinding.toolbar.back.setOnClickListener {
            finish()
        }
        mainBinding.btnSubmit.setOnClickListener {
            Toast.makeText(this@Personal_detailsActivity,"Please Try Later....",Toast.LENGTH_LONG).show()
        }
    }
}