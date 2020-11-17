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
import com.codunite.myrecharge.Models.StateModel
import com.codunite.myrecharge.R
import java.util.*

class StateListAdapter(activty: Activity, list: ArrayList<com.codunite.myrecharge.Models.StateModel>?) :
    BaseAdapter() {
    var ctx: Context? = null
    var listarray: ArrayList<com.codunite.myrecharge.Models.StateModel>? = null
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
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
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
        Log.e(">>", listarray!![position].stateName.toString() + " _" + position.toString())
        val datavalue = listarray!![position].stateName.toString()
        holder.titlename!!.text = datavalue

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