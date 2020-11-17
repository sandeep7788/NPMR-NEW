package com.codunite.myrecharge.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.fragment.app.Fragment;

import com.codunite.myrecharge.Helper.ApiInterface;
import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Helper.RetrofitManager;
import com.codunite.myrecharge.Models.ModelRequestdetails;
import com.codunite.myrecharge.Adapter.RechargePlaneAdapter;
import com.codunite.myrecharge.Helper.ApiInterface;
import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Helper.RetrofitManager;
import com.codunite.myrecharge.Models.CategoryDetail;
import com.codunite.myrecharge.Models.ModelRequestdetails;
import com.codunite.myrecharge.R;
import java.util.ArrayList;
import java.util.Arrays;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RechargePlaneFragment extends Fragment {

    private View view;
    public ArrayList<CategoryDetail> TitleName = new ArrayList<>();
    RechargePlaneAdapter arrayAdapter;
    ListView alertdialog_Listview;
    int tabid;

    public RechargePlaneFragment(int tabid) {
        this.tabid=tabid;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recharge_plane_fragment, container, false);

        arrayAdapter=new RechargePlaneAdapter(getActivity(), TitleName);
        alertdialog_Listview=view.findViewById(R.id.alertdialog_Listview);

        getBlogTab();

        return view;
    }

    private void getBlogTab() {
        Local_data pref= new Local_data(getContext());
        ApiInterface apiInterface = new RetrofitManager(getContext()).getInstance().create(ApiInterface.class);
        Call<ModelRequestdetails> call = apiInterface.GetPlandetails(pref.ReadStringPreferences(Constances.PREF_operator_code));
        call.enqueue(new Callback<ModelRequestdetails>() {
            @Override
            public void onResponse(Call<ModelRequestdetails> call, Response<ModelRequestdetails> response) {


                if (response.isSuccessful()) {
                    ModelRequestdetails childResult = response.body();


                    for (int j = 0; j < childResult.getData().get(tabid).getCategoryDetails().size(); j++) {

                        TitleName.addAll(Arrays.asList(response.body().getData().get(tabid).getCategoryDetails().get(j)));
                    }
                    alertdialog_Listview.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                    }
                }
            @Override
            public void onFailure(Call<ModelRequestdetails> call, Throwable t) {

            }
        });
    }
}