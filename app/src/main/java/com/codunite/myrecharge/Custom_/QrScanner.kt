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
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QrScanner(var activity: Activity?):Dialog(activity!!) {

    private var title = ""
    private var text = ""
    private var dialogId = -1
    private var buttonName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scanner_dialog)

       /* val layoutParams = window!!.attributes
        window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window!!.attributes = layoutParams*/
        setCancelable(false)
        var scanner_view: CodeScannerView? =findViewById<CodeScannerView>(R.id.scanner_view)
        var img_close: ImageView? =findViewById<ImageView>(R.id.img_close)
        var txt_name: TextView? =findViewById<TextView>(R.id.txt_name)
        var pref= com.codunite.myrecharge.Helper.Local_data(activity)
        pref.setMyappContext(activity)
        var codeScanner: CodeScanner = CodeScanner(activity!!,scanner_view!!)


        img_close?.setOnClickListener { dismiss() }
        scanner_view.setOnClickListener {
            codeScanner.startPreview()
        }

        start_scaner(codeScanner)
    }

    override fun onStart() {

        super.onStart()

    }

    fun start_scaner(codeScanner : CodeScanner)
    {
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        codeScanner.decodeCallback = DecodeCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()



            }
        }
        codeScanner.errorCallback = ErrorCallback {
            activity?.runOnUiThread {
                Toast.makeText(activity, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }


    }

}