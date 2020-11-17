package com.codunite.myrecharge.Activitys.Manus;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.codunite.myrecharge.Helper.ApiInterface;
import com.codunite.myrecharge.Helper.RetrofitManager;
import com.codunite.myrecharge.Models.ModelRequestdetails;
import com.codunite.myrecharge.TabAdapter;
import com.codunite.myrecharge.Fragment.RechargePlaneFragment;
import com.codunite.myrecharge.Helper.ApiInterface;
import com.codunite.myrecharge.Helper.RetrofitManager;
import com.codunite.myrecharge.Models.ModelRequestdetails;
import com.codunite.myrecharge.R;
import com.codunite.myrecharge.TabAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TabActivity extends AppCompatActivity {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    TabLayout tabLayout;
    ViewPager viewPager;
    String tabname, tabid;
    private TabAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectrecharge_palne);

        viewPager =findViewById(R.id.pager);
        viewPager.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tabLayout = findViewById(R.id.tabs);
        adapter = new TabAdapter(getSupportFragmentManager());
        getBlogTab();

    }

    private void getBlogTab() {
        ApiInterface apiInterface = new RetrofitManager(TabActivity.this).getInstance().create(ApiInterface.class);
        Call<ModelRequestdetails> call = apiInterface.GetPlandetails("AT");
        call.enqueue(new Callback<ModelRequestdetails>() {
            @Override
            public void onResponse(Call<ModelRequestdetails> call, Response<ModelRequestdetails> response) {

                if (response.isSuccessful()) {
                    ModelRequestdetails childResult = response.body();


                    for (int j = 0; j < childResult.getData().size(); j++) {
                        tabname = childResult.getData().get(j).getCategory();
                        tabid = childResult.getData().get(j).getCategory();
                        mFragmentTitleList.add(tabname);
                        adapter.addFragment(new RechargePlaneFragment(j), tabname);
                    }
                    viewPager.setAdapter(adapter);
                    viewPager.setOffscreenPageLimit(childResult.getData().size() + 1);
                    tabLayout.setupWithViewPager(viewPager);
                    for (int i = 0; i < mFragmentList.size(); i++) {
                        mFragmentTitleList.add("no title");
                    }
                }
            }
            @Override
            public void onFailure(Call<ModelRequestdetails> call, Throwable t) {

            }
        });
    }
}