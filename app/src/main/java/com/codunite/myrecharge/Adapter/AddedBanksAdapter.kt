package com.codunite.myrecharge.Adapter

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.codunite.myrecharge.Activitys.AddBankACForm_Activity
import com.codunite.myrecharge.Activitys.Manus.History_Activity
import com.codunite.myrecharge.Activitys.Manus.MoneyTransferActivity
import com.codunite.myrecharge.Activitys.Manus.MoneyTransferReportActivity
import com.codunite.myrecharge.Helper.ApiInterface
import com.codunite.myrecharge.Helper.Constances
import com.codunite.myrecharge.Helper.Local_data
import com.codunite.myrecharge.Helper.RetrofitManager
import com.codunite.myrecharge.Models.GetMemberBankAccountModel
import com.codunite.myrecharge.R
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Field
import java.lang.reflect.Method


public class BankListAdapter(private var list: ArrayList<com.codunite.myrecharge.Models.GetMemberBankAccountModel>, var context:Context)
    : RecyclerView.Adapter<MovieViewHolder1>() {
    lateinit var holder: MovieViewHolder1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder1 {
        val inflater = LayoutInflater.from(parent.context)
        return MovieViewHolder1(inflater, parent)
    }

    override fun onBindViewHolder(holder: MovieViewHolder1, position: Int) {
        this.holder =holder
        try {
            holder.txt_name!!.setText(list.get(position).customerName.toString())
            holder.txt_code!!.setText(list.get(position).ifsccode.toString())

            Glide.with(context)
                .load(list.get(position).bankImage)
                .thumbnail(0.5f)
                .into(holder.img_bank!!)
        }
        catch (e:Exception) {

        }

        holder.layout_item?.setOnClickListener {
            var i= Intent(context, MoneyTransferActivity::class.java)
            i.putExtra("id",list.get(position).id!!.toString())
            i.putExtra("bankID",list.get(position).bankID!!.toString())
            i.putExtra("bankName",list.get(position).bankName!!.toString())
            i.putExtra("customernumber",list.get(position).customernumber!!.toString())
            i.putExtra("accountnumber",list.get(position).accountnumber!!.toString())
            i.putExtra("customerName",list.get(position).customerName!!.toString())
            i.putExtra("ifsccode",list.get(position).ifsccode!!.toString())
            i.putExtra("bankImage",list.get(position).bankImage!!.toString())
            context.startActivity(i)
        }

        holder?.img_menu!!.setOnClickListener {
            val popup = PopupMenu(context, holder.img_menu)
            try {
                popup.inflate(R.menu.bank_edit_menu)
                val fields: Array<Field> = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.getName()) {
                        field.setAccessible(true)
                        val menuPopupHelper: Any = field.get(popup)
                        val classPopupHelper =
                            Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons: Method = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
                popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener{
                    override fun onMenuItemClick(p0: MenuItem?): Boolean {

                        if(p0.toString().equals("View History")) {
                            Log.e(">>",p0.toString())
                            var mIntent=Intent(context, MoneyTransferReportActivity::class.java)
                            mIntent.setFlags(FLAG_ACTIVITY_CLEAR_TASK)
                            context.startActivity(mIntent)
                        } else if(p0.toString().equals("Edit")) {
                            var i= Intent(context, com.codunite.myrecharge.Activitys.AddBankACForm_Activity::class.java)
                            i.putExtra("id",list.get(position).id!!.toString())
                            i.putExtra("bankID",list.get(position).bankID!!.toString())
                            i.putExtra("bankName",list.get(position).bankName!!.toString())
                            i.putExtra("customernumber",list.get(position).customernumber!!.toString())
                            i.putExtra("accountnumber",list.get(position).accountnumber!!.toString())
                            i.putExtra("customerName",list.get(position).customerName!!.toString())
                            i.putExtra("ifsccode",list.get(position).ifsccode!!.toString())
                            i.putExtra("bankImage",list.get(position).bankImage!!.toString())
                            context.startActivity(i)

                        } else if(p0.toString().equals("Remove")) {
                            removeMemberBankAccount(list.get(position).id!!.toString(),position)


                        } else {

                        }
                        return true
                    }

                })
                popup.show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun filterList(filterdNames: ArrayList<com.codunite.myrecharge.Models.GetMemberBankAccountModel>) {
        this.list = filterdNames
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size
    fun removeMemberBankAccount(AccountID:String,p:Int) {
        Log.e("@@",AccountID+" _ "+p.toString())
        holder.progress_bar?.visibility=View.VISIBLE
        var pref = com.codunite.myrecharge.Helper.Local_data(context)
        var apiInterface: ApiInterface = RetrofitManager(context).instance!!.create(
            ApiInterface::class.java)

        apiInterface.RemoveMemberBankAccount(pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Mobile),
            pref.ReadStringPreferences(com.codunite.myrecharge.Helper.Constances.PREF_Login_password),AccountID).
        enqueue(object : Callback<JsonObject> {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(context,"Something wrong.",Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.e("@@",response.body().toString())
                if(response.isSuccessful) {
                    var mJsonObject= JSONObject(response.body().toString())
                    if (mJsonObject.getString("Error").toLowerCase().equals("false")) {
                        removeAt(p)
                    } else {
                        Toast.makeText(context,mJsonObject.getString("Message")+" ",Toast.LENGTH_LONG).show()
                    }

                }
            }

        })
    }
    fun removeAt(position: Int) {
        holder.progress_bar?.visibility=View.GONE
        list.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, list.size)
    }
}

class MovieViewHolder1(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.bank_list_adapter, parent, false)) {
    var img_bank: ImageView? = null
    var txt_name: TextView? = null
    var img_menu: ImageView? = null
    var txt_code: TextView? = null
    var progress_bar: ProgressBar? = null
    var layout_item: LinearLayout? = null

    init {
        img_bank = itemView.findViewById(R.id.img_bank)
        txt_name = itemView.findViewById(R.id.txt_name)
        img_menu = itemView.findViewById(R.id.img_menu)
        txt_code= itemView.findViewById(R.id.txt_code)
        progress_bar= itemView.findViewById(R.id.progress_bar)
        layout_item= itemView.findViewById(R.id.layout_item)

    }
}