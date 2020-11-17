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
import com.codunite.myrecharge.Models.CompanybankdetailsModel
import com.codunite.myrecharge.R
import java.lang.Exception

public class BankDitailsAdapter(private var list: List<com.codunite.myrecharge.Models.CompanybankdetailsModel>, var context:Context)
    : RecyclerView.Adapter<BankDetailsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankDetailsHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BankDetailsHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: BankDetailsHolder, position: Int) {

        try {
            holder.txt_bankname!!.setText(list.get(position).bankName.toString())
            holder.txt_Accountnumber!!.setText(list.get(position).accountnumber.toString())
            holder.txt_holdername!!.setText(list.get(position).accountholdername.toString())
            holder.txt_ifsc!!.setText(list.get(position).ifsccode.toString())
            holder.txt_account_type!!.setText(list.get(position).ifsccode.toString())
            holder.txt_bankbranch!!.setText(list.get(position).ifsccode.toString())

            Glide.with(context)
                .load(list.get(position).bankImage.toString())
                .thumbnail(0.5f)
                .into(holder?.img_bank!!)
        } catch (e:Exception) { }

    }

    override fun getItemCount(): Int = list.size
    fun filterList(filterdNames: ArrayList<com.codunite.myrecharge.Models.CompanybankdetailsModel>) {
        this.list = filterdNames
        notifyDataSetChanged()
    }
}

class BankDetailsHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.bank_details_adapter, parent, false)) {
    var img_bank: ImageView? = null
    var txt_bankname: TextView? = null
    var txt_holdername: TextView? = null
    var txt_ifsc: TextView? = null
    var txt_Accountnumber: TextView? = null
    var txt_account_type: TextView? = null
    var txt_bankbranch: TextView? = null

    init {
        img_bank = itemView.findViewById(R.id.img_bank)
        txt_bankname= itemView.findViewById(R.id.txt_bankname)
        txt_holdername= itemView.findViewById(R.id.txt_holdername)
        txt_Accountnumber= itemView.findViewById(R.id.txt_Accountnumber)
        txt_ifsc= itemView.findViewById(R.id.txt_ifsc)
        txt_account_type= itemView.findViewById(R.id.txtAccountType)
        txt_bankbranch= itemView.findViewById(R.id.txt_bankbranch)

    }
}