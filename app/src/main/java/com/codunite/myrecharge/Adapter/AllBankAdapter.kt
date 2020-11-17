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
import com.codunite.myrecharge.R
import java.util.*

class AllBankAdapter(activty: Activity, list: ArrayList<com.codunite.myrecharge.Models.BankListModel>?) :
    BaseAdapter() {
    var ctx: Context? = null
    var listarray: ArrayList<com.codunite.myrecharge.Models.BankListModel>? = null
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
            convertView = mInflater!!.inflate(R.layout.single_item, null)
            holder.titlename = convertView.findViewById<View>(R.id.title) as TextView
            holder.icon = convertView.findViewById(R.id.icon)
            convertView.tag = holder
        } else {
            holder = convertView.tag as ViewHolder
        }
        Log.e(">>", listarray!![position].bankerMasterName.toString() + " _" + position.toString())
        val datavalue = listarray!![position].bankerMasterName.toString()
        holder.titlename!!.text = datavalue
        Glide.with(ctx!!)
            .load(listarray!![position].bankImage)
            .thumbnail(0.5f)
            .into(holder.icon!!)
        Log.e(">>url", listarray!![position].bankImage + " _")
        return convertView
    }

    private class ViewHolder {
        var titlename: TextView? = null
        var icon: ImageView? = null
    }

    init {
        ctx = activty
        mInflater = activty.layoutInflater
        listarray = list
    }
}