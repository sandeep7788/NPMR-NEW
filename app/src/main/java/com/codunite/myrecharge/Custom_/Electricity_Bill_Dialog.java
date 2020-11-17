package com.codunite.myrecharge.Custom_;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Adapter.CustomAlertAdapter;
import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Models.OperatorModel;
import com.codunite.myrecharge.R;

import java.util.ArrayList;

public class Electricity_Bill_Dialog extends Dialog implements AdapterView.OnItemClickListener {

    public Activity c;
    public ImageView dialog_cancel_btn;
    public ArrayList<OperatorModel> TitleName = new ArrayList<>();
    public ArrayList<OperatorModel> array_sort=new ArrayList<>();
    int textlength=0;
    public Context context;
    TextView textView;
    ListView alertdialog_Listview;
    EditText alertdialog_edittext;

    public Electricity_Bill_Dialog(Context context, final ArrayList<OperatorModel> TitleName, TextView textview, String mOperator_CODE){
        super(context,R.style.FullWidth_Dialog);
        this.context=context;
        this.TitleName=TitleName;
        this.textView=textview;
    }

    public Electricity_Bill_Dialog(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(WindowManager.LayoutParams.FILL_PARENT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setAttributes(layoutParams);
        alertdialog_Listview =  findViewById(R.id.alertdialog_Listview);
        alertdialog_edittext = findViewById(R.id.alertdialog_edittext);
        dialog_cancel_btn = findViewById(R.id.back);
//        pref.setMyappContext(context);
        onClick();
        dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void onClick() {
        CustomAlertAdapter arrayAdapter=new CustomAlertAdapter((Activity) context, TitleName);
        alertdialog_Listview.setAdapter(arrayAdapter);
        alertdialog_Listview.setOnItemClickListener(this);
        alertdialog_edittext.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s){
            }
            public void beforeTextChanged(CharSequence s,int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                alertdialog_edittext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                textlength = alertdialog_edittext.getText().length();

                array_sort.clear();

                for (int i = 0; i < TitleName.size(); i++)

                {

                    if (textlength <= TitleName.get(i).getOperatorName().length())
                    {
                        if(TitleName.get(i).getOperatorName().toLowerCase().contains(alertdialog_edittext.getText().toString().toLowerCase().trim()))

                        {

                            array_sort.add(TitleName.get(i)); } }
                }
                alertdialog_Listview.setAdapter(new CustomAlertAdapter((Activity) context,array_sort));

            }

        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Log.e(">>itemclick", String.valueOf(i));
        textView.setText(" "+TitleName.get(i).getOperatorName().toString());
        textView.setTextColor(Color.BLACK);

        Local_data pref = new Local_data(context);
        pref.writeStringPreference(Constances.PREF_operator_code,TitleName.get(i).getOperatorCode().toString());
        dismiss();
    }
}