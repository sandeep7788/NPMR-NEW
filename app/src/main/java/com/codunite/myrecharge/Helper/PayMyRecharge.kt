package com.codunite.myrecharge.Helper

import android.app.Application
import android.content.*
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Base64
import android.util.Log
import com.codunite.myrecharge.Activitys.SplachScreen
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class PayMyRecharge : Application() {
    fun setMyappContext(mContext: Context?) {
        myappContext = mContext
    }

    override fun onCreate() {
        super.onCreate()
        setMyappContext(applicationContext)
        instance = this

/*        var MyReceiver: BroadcastReceiver?= null;
        MyReceiver = com.codunite.myrecharge.Helper.MyReceiver()
        registerReceiver(MyReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))*/

    }

    fun printHashKey() {
        try {
            val info = packageManager.getPackageInfo(
                "com.example.finance",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.e(
                    "KeyHash:",
                    Base64.encodeToString(md.digest(), Base64.DEFAULT)
                )
            }
        } catch (e: PackageManager.NameNotFoundException) {
        } catch (e: NoSuchAlgorithmException) {
        }
    } /*  public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/

    companion object {
        val TAG = Application::class.java.simpleName
        var myappContext: Context? = null
        var MY_APP_SHARED_PREFERENCES = "pmr"
        var mPreferences: SharedPreferences? = null

        @get:Synchronized
        var instance: PayMyRecharge? = null
            private set

        fun getSharedPreferences(context: Context?): SharedPreferences {
            return context!!.getSharedPreferences(
                MY_APP_SHARED_PREFERENCES,
                0
            )
        }

        fun writeIntPreference(key: String?, value: Int) {
            mPreferences =
                getSharedPreferences(myappContext)
            val mEditor =
                mPreferences!!.edit()
            mEditor.putInt(key, value)
            mEditor.commit()
        }

        fun writeStringPreference(key: String?, value: String?) {
            mPreferences =
                getSharedPreferences(myappContext)
            val mEditor =
                mPreferences!!.edit()
            mEditor.putString(key, value)
            mEditor.commit()
        }

        fun ReadStringPreferences(key: String?): String? {
            mPreferences =
                getSharedPreferences(myappContext)
            return mPreferences!!.getString(key, "")
        }

        fun ReadIntPreferences(key: String?): Int {
            mPreferences =
                getSharedPreferences(myappContext)
            return mPreferences!!.getInt(key, 0)
        }

        fun logout(confirm: Boolean) {
            if (!confirm) return
                    writeIntPreference(com.codunite.myrecharge.Helper.Constances.PREF_IsLogin, 0)
            clearPrefrences()
        }

        fun clearPrefrences() {
            mPreferences =
                getSharedPreferences(myappContext)
            val mEditor =
                mPreferences!!.edit()
            mEditor.clear()
            mEditor.commit()
            val intent = Intent(
                myappContext,
                SplachScreen::class.java
            )
            intent.flags = (Intent.FLAG_ACTIVITY_NEW_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            myappContext!!.startActivity(intent)
        }
    }
}