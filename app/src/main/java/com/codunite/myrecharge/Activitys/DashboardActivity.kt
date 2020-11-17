package com.codunite.myrecharge.Activitys

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import androidx.fragment.app.FragmentTransaction
import com.codunite.myrecharge.Fragment.Home_Fragment
import com.codunite.myrecharge.Fragment.Profile_Fragment
import com.codunite.myrecharge.Fragment.SettingFragment
import com.codunite.myrecharge.Fragment.WalletFragment
import com.codunite.myrecharge.R
import com.codunite.myrecharge.databinding.ActivityDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import org.jsoup.Jsoup

class DashboardActivity : AppCompatActivity() {

    lateinit var mainBinding: ActivityDashboardBinding
    lateinit var transaction: FragmentTransaction
    var pref = com.codunite.myrecharge.Helper.Local_data(this@DashboardActivity)
    var CAMERA_PERMISSION_CODE = 100
    var STORAGE_PERMISSION_CODE = 101
    var FRAGMENT_OTHER = "FRAGMENT_OTHER"
    //
    private val REQ_CODE_VERSION_UPDATE: Int = 530
    private val appUpdateManager: AppUpdateManager? = null
    private val installStateUpdatedListener: InstallStateUpdatedListener? = null
    lateinit var context:Context

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        pref.setMyappContext(this@DashboardActivity)
        mainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_dashboard)

        var MyReceiver: BroadcastReceiver? = null;
        MyReceiver = com.codunite.myrecharge.Helper.MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        context=this



        if(intent!=null) {
            Log.e(">>",intent.getBooleanExtra("from_HF",false).toString())
            if(intent.getBooleanExtra("from_HF",false)) {
                setFram(Profile_Fragment())
            } else {  setFram(Home_Fragment()) }
        }
        val asyncTask = GetVersionCode()
        asyncTask.execute()

        mainBinding.navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    viewFragment(Home_Fragment(), "FRAGMENT_HOME")
                }
                R.id.navigation_Wallet -> {
                    viewFragment(WalletFragment(), FRAGMENT_OTHER)
                }
                R.id.navigation_profile -> {
                    viewFragment(Profile_Fragment(), FRAGMENT_OTHER)
                }
                R.id.setting -> {
                    viewFragment(SettingFragment(), FRAGMENT_OTHER)
                }
            }
            true
        })

        checkPermission(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_NUMBERS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), CAMERA_PERMISSION_CODE
        )
    }

    @SuppressLint("WrongConstant")
    fun setFram(fram: Fragment) {
        val fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fram)
        fragmentTransaction.commit()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("@@Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("@Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("@@Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    private fun viewFragment(
        fragment: Fragment,
        name: String
    ) {
        var fragmentManager: FragmentManager = supportFragmentManager
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame, fragment)
        // 1. Know how many fragments there are in the stack
        val count = fragmentManager.backStackEntryCount
        // 2. If the fragment is **not** "home type", save it to the stack
        if (name == FRAGMENT_OTHER) {
            fragmentTransaction.addToBackStack(name)
        }
        // Commit !
        fragmentTransaction.commit()
        // 3. After the commit, if the fragment is not an "home type" the back stack is changed, triggering the
        // OnBackStackChanged callback
        fragmentManager.addOnBackStackChangedListener(object :
            FragmentManager.OnBackStackChangedListener {
            override fun onBackStackChanged() {
                // If the stack decreases it means I clicked the back button
                if (fragmentManager.backStackEntryCount <= count) {
                    // pop all the fragment and remove the listener
                    fragmentManager.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE)
                    fragmentManager.removeOnBackStackChangedListener(this)
                    // set the home button selected
                    mainBinding.navigation.getMenu().getItem(0).setChecked(true)
                }
            }
        })
    }


    fun exit_dialog() {
        val builder = AlertDialog.Builder(this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
        //set title for alert dialog
        builder.setTitle("Exit")
        //set message for alert dialog
        builder.setMessage("Do you want to Exit.")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            Toast.makeText(applicationContext, "Exit....", Toast.LENGTH_LONG).show()
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(homeIntent)
        }

        builder.setNegativeButton("No") { dialogInterface, which ->
            setFram(Home_Fragment())
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun checkPermission(permission: Array<out String>, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@DashboardActivity,
                permission[0]
            ) === PackageManager.PERMISSION_DENIED
        ) {

            // Requesting the permission
            ActivityCompat.requestPermissions(
                this@DashboardActivity, permission,
                requestCode
            )
        } else {
            /*Toast.makeText(
                this@DashboardActivity,
                "Permission already granted",
                Toast.LENGTH_SHORT
            ).show()*/

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Camera Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show();
            } else {
                Toast.makeText(
                    this,
                    "Camera Permission Denied",
                    Toast.LENGTH_SHORT
                )
                    .show();
                val requiredPermission = Manifest.permission.CAMERA
                val checkVal: Int = checkCallingOrSelfPermission(requiredPermission)

                if (checkVal == PackageManager.PERMISSION_GRANTED) {


                    Log.e(">>", "if")

                } else {
                    Log.e(">>", "else")
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + this.packageName)
                        )
                    )
                }
            }
        } else if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.size > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show();

            } else {
                Toast.makeText(
                    this,
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }

    }

    inner class GetVersionCode :
        AsyncTask<Void?, String?, String?>() {



        override fun onPostExecute(onlineVersion: String?) {
            super.onPostExecute(onlineVersion)

            val currentVersion = context.packageManager.getPackageInfo(packageName, 0).versionName
            Log.d(
                "update",
                "Current version " + currentVersion + "playstore version " + onlineVersion+" "+packageName
            )
            if (onlineVersion != null && !onlineVersion.isEmpty()) {
                if (java.lang.Float.valueOf(currentVersion) < java.lang.Float.valueOf(onlineVersion)) {
                    //show dialog
                }
            }
        }

        override fun  doInBackground(vararg voids: Void?): String? {
            var newVersion: String? = null
            return try {
                newVersion = Jsoup.connect(
                    "https://play.google.com/store/apps/details?id=" + context.getPackageName()
                        .toString() + "&hl=it"
                )
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select(".hAyfc .htlgb")[7]
                    .ownText()
                newVersion
            } catch (e: Exception) {
                newVersion
            }
        }
    }
}