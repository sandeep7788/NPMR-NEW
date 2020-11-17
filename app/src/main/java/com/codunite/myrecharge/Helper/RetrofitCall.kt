package com.codunite.myrecharge.Helper

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RetrofitCall {

    public var TAG="@@RetrofitCall"
    lateinit var context:Context

    public constructor(context: Context)
    {
        this.context=context
    }


    fun call()
    {
        var task: MyAsyncTask = MyAsyncTask(context)
        task.execute()
    }


    public class MyAsyncTask internal constructor(context: Context) : AsyncTask<String, String, String?>()
    {
        var mResponse="0"
        override fun doInBackground(vararg p0: String?): String? {

            return mResponse
        }

        override fun onPostExecute(result: String?) {
            Log.d("@@RetrofitCall", "onPostExecute1: "+result.toString())
            super.onPostExecute(result)
        }

    }
}