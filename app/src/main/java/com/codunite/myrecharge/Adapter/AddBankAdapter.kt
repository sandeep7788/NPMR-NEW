package com.codunite.myrecharge.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codunite.myrecharge.Activitys.AddBankACForm_Activity
import com.codunite.myrecharge.Models.BankListModel
import com.codunite.myrecharge.R

public class AddBankAdapter(private var list: List<com.codunite.myrecharge.Models.BankListModel>, var context:Context)
    : RecyclerView.Adapter<BankHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BankHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BankHolder, position: Int) {
        holder.t_title!!.setText(list.get(position).bankerMasterName.toString())

        Glide.with(context)
            .load(list.get(position).bankImage.toString())
            .thumbnail(0.5f)
            .into(holder?.i_image!!)

        holder.i_image?.setOnClickListener {
            var i=Intent(context,
                com.codunite.myrecharge.Activitys.AddBankACForm_Activity::class.java)
            i.putExtra("bank_name",list.get(position).bankerMasterName!!.toString())
            i.putExtra("bank_id",list.get(position).bankerMasterID!!.toString())
            i.putExtra("bank_img",list.get(position).bankImage!!.toString())
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int = list.size
    fun filterList(filterdNames: ArrayList<com.codunite.myrecharge.Models.BankListModel>) {
        this.list = filterdNames
        notifyDataSetChanged()
    }
}

class BankHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.add_bank_adapter, parent, false)) {
    var i_image: ImageView? = null
    var t_title: TextView? = null

    init {
        i_image = itemView.findViewById(R.id.i_image)
        t_title= itemView.findViewById(R.id.t_title)

    }
}