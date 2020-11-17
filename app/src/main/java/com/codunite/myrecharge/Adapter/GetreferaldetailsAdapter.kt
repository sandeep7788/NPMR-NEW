package com.codunite.myrecharge.Adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codunite.myrecharge.Models.BankListModel
import com.codunite.myrecharge.Models.GetreferaldetailsModel
import com.codunite.myrecharge.R
import java.util.*

class GetreferaldetailsAdapter(activty: Activity, list: ArrayList<com.codunite.myrecharge.Models.GetreferaldetailsModel>?) :
    BaseAdapter() {
    var ctx: Context? = null
    var listarray: ArrayList<com.codunite.myrecharge.Models.GetreferaldetailsModel>? = null
    private var mInflater: LayoutInflater? = null
    override fun getCount(): Int {
        return listarray!!.size
    }

    override fun getItem(arg0: Int): Any {
        return null!!
    }

    override fun getItemId(arg0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, arg2: ViewGroup): View? {
        var convertView = convertView
        val holder: ViewHolder
        if (convertView == null) {
            holder = ViewHolder()
            convertView = mInflater!!.inflate(R.layout.single_item_getreferaldetails_adapter, null)
            holder.titlename = convertView.findViewById<View>(R.id.title) as TextView
            holder.txt_description = convertView.findViewById<View>(R.id.txt_description) as TextView
            holder.icon = convertView.findViewById(R.id.icon)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }

        val datavalue = listarray!![position].memberName.toString()
        holder.titlename!!.text = datavalue
        holder.txt_description!!.text = listarray!![position].mobile.toString()+"\nDate : "+listarray!![position].adddate.toString()

        return convertView
    }

    private class ViewHolder {
        var titlename: TextView? = null
        var txt_description: TextView? = null
        var icon: ImageView? = null
    }

    init {
        ctx = activty
        mInflater = activty.layoutInflater
        listarray = list
    }
}