package com.codunite.myrecharge.Activitys.Manus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.codunite.myrecharge.Fragment.RechargePlaneFragment
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.ModelRequestdetails
import com.codunite.myrecharge.R
import com.codunite.myrecharge.TabAdapter
import com.google.android.material.tabs.TabLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class SelectRecharge_Palne_Activity : AppCompatActivity() {
    private val mFragmentList: List<Fragment> =
        ArrayList()
    private val mFragmentTitleList: MutableList<String?> =
        ArrayList()
    var tabLayout: TabLayout? = null
    var viewPager: ViewPager? = null
    var tabname: String? = null
    var tabid: String? = null
    private var adapter: com.codunite.myrecharge.TabAdapter? = null
    private var back: ImageView? = null
    private var btn_user: ImageView? = null
    private var layout_empty_img: LinearLayout? = null
    private var progressbar: ProgressBar? = null

    var TAG="@@SelectRecharge_Palne_Activity"
    var pref =
        com.codunite.myrecharge.Helper.Local_data(this@SelectRecharge_Palne_Activity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selectrecharge_palne)
        viewPager = findViewById(R.id.pager)
        back=findViewById(R.id.back)
        btn_user=findViewById(R.id.btn_user)
        layout_empty_img=findViewById(R.id.layout_empty_img)
        progressbar=findViewById(R.id.progressbar)
        viewPager?.measure(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        tabLayout = findViewById(R.id.tabs)

        adapter = com.codunite.myrecharge.TabAdapter(supportFragmentManager)
        blogTab
        //
        back!!.setOnClickListener { super.onBackPressed() }
        btn_user!!.setVisibility(View.GONE)
        layout_empty_img!!.setVisibility(View.GONE)

    }
    
    private val blogTab: Unit
        private get() {
            val apiInterface =
                RetrofitManager(this@SelectRecharge_Palne_Activity).instance!!.create(
                    ApiInterface::class.java
                )
            Log.d(TAG, ": PREF_operator_code"+pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code))
            val call = apiInterface.GetPlandetails(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_operator_code))
            call.enqueue(object : Callback<com.codunite.myrecharge.Models.ModelRequestdetails?> {
                override fun onResponse(
                    call: Call<com.codunite.myrecharge.Models.ModelRequestdetails?>,
                    response: Response<com.codunite.myrecharge.Models.ModelRequestdetails?>
                ) {


                    progressbar!!.setVisibility(View.GONE)
                    if (response.isSuccessful) {
//                        if (mJsonObject.has("Data") && !mJsonObject.isNull("Data")) {
                        if (response.body()?.data!=null) {
                            val childResult = response.body()
                            for (j in childResult!!.data.indices) {
                                tabname = childResult.data[j].category
                                tabid = childResult.data[j].category
                                mFragmentTitleList.add(tabname)
                                adapter!!.addFragment(
                                    com.codunite.myrecharge.Fragment.RechargePlaneFragment(
                                        j
                                    ), tabname
                                )
                            }
                            viewPager!!.adapter = adapter
                            viewPager!!.offscreenPageLimit = childResult.data.size + 1
                            tabLayout!!.setupWithViewPager(viewPager)
                            for (i in mFragmentList.indices) {
                                mFragmentTitleList.add("no title")
                            }
                            layout_empty_img!!.setVisibility(View.GONE)
                        }
                        else {
                            layout_empty_img!!.setVisibility(View.VISIBLE)
                        }
                    } else {
                        layout_empty_img!!.setVisibility(View.VISIBLE)
                    }
                }

                override fun onFailure(
                    call: Call<com.codunite.myrecharge.Models.ModelRequestdetails?>,
                    t: Throwable
                ) {
                    progressbar!!.setVisibility(View.GONE)
                    layout_empty_img!!.setVisibility(View.VISIBLE)
                }
            })
        }
}