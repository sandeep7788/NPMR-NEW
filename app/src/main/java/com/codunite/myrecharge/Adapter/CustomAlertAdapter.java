package com.codunite.myrecharge.Adapter;

import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;


import android.app.Activity;

import android.content.Context;

import android.view.LayoutInflater;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codunite.myrecharge.Models.OperatorModel;
import com.codunite.myrecharge.R;


public class CustomAlertAdapter extends BaseAdapter{


    Context ctx=null;

    ArrayList<OperatorModel> listarray=null;

    private LayoutInflater mInflater=null;

    public CustomAlertAdapter(Activity activty, ArrayList<OperatorModel> list)

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

    public View getView(int position, View convertView, ViewGroup arg2) {

        final ViewHolder holder;

        if (convertView == null ) {

            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.single_item, null);




            holder.titlename = (TextView) convertView.findViewById(R.id.title);
            holder.icon=convertView.findViewById(R.id.icon);

            convertView.setTag(holder);

        }

        else {

            holder = (ViewHolder) convertView.getTag();

        }


        Log.e(">>",listarray.get(position).getOperatorName().toString()+" _"+String.valueOf(position));
        String datavalue=listarray.get(position).getOperatorName().toString();


        holder.titlename.setText(datavalue);

        Glide.with(ctx)
                .load(listarray.get(position).getOperaotimageurl())
                .thumbnail(0.5f)
                .into(holder.icon);

        Log.e(">>url",listarray.get(position).getOperaotimageurl()+" _");

        return convertView;

    }



    private static class ViewHolder {

        TextView titlename;
        ImageView icon;

    }

}