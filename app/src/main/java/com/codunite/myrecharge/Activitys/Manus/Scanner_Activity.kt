package com.codunite.myrecharge.Activitys.Manus

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.budiyev.android.codescanner.*
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ScannerDialogBinding

class Scanner_Activity : AppCompatActivity() {
    lateinit var mainBinding : ScannerDialogBinding
    var pref= com.codunite.myrecharge.Helper.Local_data(this@Scanner_Activity)
    private lateinit var codeScanner: CodeScanner
    var TAG=">>Scanner_Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding =
            DataBindingUtil.setContentView(this,R.layout.scanner_dialog)
        pref.setMyappContext(this@Scanner_Activity)
        codeScanner = CodeScanner(this,mainBinding.scannerView)

        checkPermission(arrayOf(
            Manifest.permission.CAMERA),101)
    }

    override fun onResume() {
        super.onResume()
                codeScanner.startPreview()
        }

    fun start_scaner()
    {
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

                Log.e(TAG, "start_scaner: 1"+it.text )
                Log.e(TAG, "start_scaner: "+it.barcodeFormat )

                var mIntent: Intent?=null
                mIntent= Intent(this@Scanner_Activity,Pay_Activity::class.java)
                    mIntent.putExtra(com.codunite.myrecharge.Helper.Constances.PREF_MemberID,it.text.toString())
                    startActivity(mIntent)
                finish()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
                Log.e(TAG, "start_scaner: "+it.message.toString() )
            }
        }

        mainBinding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }
    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
    fun checkPermission(permission: Array<out String>, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@Scanner_Activity, permission[0]) === PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@Scanner_Activity, permission,
                requestCode
            )
        } else {
            val requiredPermission = Manifest.permission.CAMERA
            val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)

            if (checkVal==PackageManager.PERMISSION_GRANTED){
                start_scaner()
                Log.e(">>","if")
            } else {
                Log.e(">>","else")
                Toast.makeText(
                    this@Scanner_Activity,
                    "Please Allow Camera Permission.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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
                Toast.makeText(this,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT)
                    .show();
            }
            else {
                Toast.makeText(this,
                    "Camera Permission Denied",
                    Toast.LENGTH_SHORT)
                    .show();
            }
        }
    }
}