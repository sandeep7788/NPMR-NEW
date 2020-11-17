package com.codunite.myrecharge.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codunite.myrecharge.Activitys.Manus.TopServicesActivity;
import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Activitys.Manus.TopServicesActivity;
import com.codunite.myrecharge.Helper.Constances;
import com.codunite.myrecharge.Helper.Local_data;
import com.codunite.myrecharge.Models.CategoryDetail;
import com.codunite.myrecharge.R;

import java.util.ArrayList;


public class RechargePlaneAdapter extends BaseAdapter{


    Context ctx=null;

    ArrayList<CategoryDetail> listarray=null;

    private LayoutInflater mInflater=null;

    public RechargePlaneAdapter(Activity activty, ArrayList<CategoryDetail> list)

    {

        this.ctx=activty;

        mInflater = activty.getLayoutInflater();

        this.listarray=list;

    }

    @Override

    public int getCount() {



        return listarray.size();

    }


    @Override

    public Object getItem(int arg0) {

        return null;

    }


    @Override

    public long getItemId(int arg0) {

        return 0;

    }

    @Override

    public View getView(final int position, View convertView, ViewGroup arg2) {

        final ViewHolder holder;

        if (convertView == null ) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.operator_plane_adapter, null);




            holder.txt_amount = (TextView) convertView.findViewById(R.id.txt_amount);
            holder.txt_title = (TextView) convertView.findViewById(R.id.txt_title);
            holder.txt_description = (TextView) convertView.findViewById(R.id.txt_description);
            holder.layout_item = convertView.findViewById(R.id.layout_item);
            convertView.setTag(holder);

        }

        else {

            holder = (ViewHolder) convertView.getTag();
        }

        Log.e(">>",listarray.get(position).getPrice().toString()+" _"+String.valueOf(position));
        String datavalue=listarray.get(position).getPrice().toString();


        holder.txt_amount.setText("₹  "+listarray.get(position).getPrice().toString());
        holder.txt_title.setText(listarray.get(position).getDescription().toString());
        holder.txt_description.setText("Validity :- "+listarray.get(position).getValidity().toString());
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, TopServicesActivity.class);
                intent.putExtra("amt",holder.txt_amount.getText().toString());
                Local_data pref = new Local_data(ctx);
                pref.writeStringPreference(Constances.PREF_amt,holder.txt_amount.getText().toString());
//                intent.putExtra("op_code",listarray.get(position).get.toString());
                ctx.startActivity(intent);
                ((Activity)ctx).finish();
            }
        });

/*        Glide.with(ctx)
                .load(listarray.get(position).getPrice())
                .thumbnail(0.5f)
                .into(holder.icon);*/

        Log.e(">>url",listarray.get(position).getPrice()+" _");

        return convertView;

    }



    private static class ViewHolder {

        TextView txt_amount;
        TextView txt_title;
        TextView txt_description;
        LinearLayout layout_item;

    }

}