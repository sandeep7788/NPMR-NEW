package com.codunite.myrecharge.Helper;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.codunite.myrecharge.Activitys.Login_Activity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Local_data extends Application  {
    public  final String TAG = Application.class.getSimpleName();
    public  Context MY_APP_CONTEXT = null;
    public  String MY_APP_SHARED_PREFERENCES = "finance";
    public  SharedPreferences mPreferences;
    public  Local_data mInstance;
//      MultiDex.install(this);

    public Local_data(Context MY_APP_CONTEXT){
        this.MY_APP_CONTEXT = MY_APP_CONTEXT;
    }

    public  Context getMyappContext() {
        return MY_APP_CONTEXT;
    }

    public void setMyappContext(Context MY_APP_CONTEXT) {
        this.MY_APP_CONTEXT = MY_APP_CONTEXT;
    }

    public  SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(MY_APP_SHARED_PREFERENCES, 0);
    }

    private String someVariable;

    public String getSomeVariable() {
        return someVariable;
    }

    public void setSomeVariable(String someVariable) {
        this.someVariable = someVariable;
    }

    public  void writeIntPreference(String key, int value) {
        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putInt(key, value);
        mEditor.commit();
        for (int i=0;i<10;i++)
        {

        }
    }
    public  void writeStringPreference(String key, String value) {
        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.putString(key, value);
        mEditor.commit();
    }
    public  String ReadStringPreferences(String key) {
        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        return mPreferences.getString(key, "_");

    }

    public  String getUid() {
        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        return mPreferences.getString(Constances.user_code, "0");

    }
    public  int ReadIntPreferences(String key) {

        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        return mPreferences.getInt(key, 0);

    }
    public  synchronized Local_data getInstance() {
        return mInstance;
    }
    public  void logout(boolean confirm){
        if (!confirm)
            return;
        writeIntPreference(Constances.PREF_IsLogin, 0);
        clearPrefrences();
    }
     void clearPrefrences(){
        mPreferences = getSharedPreferences(MY_APP_CONTEXT);
        SharedPreferences.Editor mEditor = mPreferences.edit();
        mEditor.clear();
        mEditor.commit();

        Intent intent = new Intent(getMyappContext(), Login_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        getMyappContext().startActivity(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setMyappContext(getApplicationContext());
        mInstance = this;
    }
    public void printHashKey() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.finance",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
   /* public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }*/
}