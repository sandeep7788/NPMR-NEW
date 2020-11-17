package com.codunite.myrecharge.Adapter

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.codunite.myrecharge.Models.PayReportDeatilsModel
import com.codunite.myrecharge.R
import com.google.android.material.color.MaterialColors.getColor
import java.util.*

public class Pay_Adapter(
    context: Context,
    movies: MutableList<com.codunite.myrecharge.Models.PayReportDeatilsModel>?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var movies: MutableList<com.codunite.myrecharge.Models.PayReportDeatilsModel>?
    private val context: Context
    private var isLoadingAdded = false
    var TAG = "@@PagingAdapter"
    fun getMovies(): List<com.codunite.myrecharge.Models.PayReportDeatilsModel>? {
        return movies
    }

    fun setMovies(movies: MutableList<com.codunite.myrecharge.Models.PayReportDeatilsModel>?) {
        this.movies = movies
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null
        val inflater = LayoutInflater.from(parent.context)
        Log.d(TAG, "onCreateViewHolder: $viewType")
        when (viewType) {
            ITEM -> viewHolder =
                getViewHolder(parent, inflater)
            LOADING -> {
                val v2 = inflater.inflate(R.layout.item_progress, parent, false)
                viewHolder = LoadingVH(v2)
            }
        }
        return viewHolder!!
    }

    private fun getViewHolder(
        parent: ViewGroup,
        inflater: LayoutInflater
    ): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v1 = inflater.inflate(R.layout.pay_adapter, parent, false)
        viewHolder = ItemList(v1)
        return viewHolder
    }



    override fun getItemCount(): Int {
        return if (movies == null) 0 else movies!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == movies!!.size - 1 && isLoadingAdded) LOADING else ITEM
    }

        fun add(mc: com.codunite.myrecharge.Models.PayReportDeatilsModel) {
        movies!!.add(mc)
        notifyItemInserted(movies!!.size - 1)
    }

    fun addAll(mcList: List<com.codunite.myrecharge.Models.PayReportDeatilsModel>) {
        for (mc in mcList) {
            add(mc)
        }
    }

    fun remove(city: com.codunite.myrecharge.Models.PayReportDeatilsModel?) {
        val position = movies!!.indexOf(city)
        if (position > -1) {
            movies!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        while (itemCount > 0) {
            remove(getItem(0))
        }
    }

    val isEmpty: Boolean
        get() = itemCount == 0

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(com.codunite.myrecharge.Models.PayReportDeatilsModel())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = movies!!.size - 1
        val item = getItem(position)
        if (item != null) {
            movies!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }
    fun removeLoadingFooter1() {
        isLoadingAdded = false
      /*  val position = movies!!.size - 1
        val item = getItem(position)
        if (item != null) {
            movies!!.removeAt(position)
            notifyItemRemoved(position)
        }*/
    }

    fun getItem(position: Int): com.codunite.myrecharge.Models.PayReportDeatilsModel {
        return movies!![position]
    }

    protected inner class ItemList(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val txt_id: TextView
        val txt_description: TextView
        val txt_Amount: TextView
        val txt_type: TextView
        val tableRow: CardView

        init {
            txt_id = itemView.findViewById<View>(R.id.txt_id) as TextView
            txt_description =
                itemView.findViewById<View>(R.id.txt_description) as TextView
            txt_Amount = itemView.findViewById<View>(R.id.txt_Amount) as TextView
            txt_type = itemView.findViewById<View>(R.id.txt_type) as TextView
            tableRow = itemView.findViewById<View>(R.id.tableRow) as CardView
        }
    }

    protected inner class LoadingVH(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!)

    companion object {
        private const val ITEM = 0
        private const val LOADING = 1
    }

    init {
        var movies = movies
        this.context = context
        this.movies = movies
        movies = ArrayList()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val movie = movies!![position]
        when (getItemViewType(position)) {
            ITEM -> {
                val mItemList =
                    holder as ItemList?
                try {
                    if (movie.eWalletTransactionID == null) {
                    } else {

                        mItemList?.tableRow?.visibility=View.VISIBLE
                        mItemList!!.txt_id.text = "Transaction ID : "+movie.eWalletTransactionID.toString()
                        mItemList!!.txt_description.text = movie.narration.toString()+" "+movie.adddate+" "+movie.transtime.toString()
                        mItemList!!.txt_Amount.text = movie.amount.toString()+" "

                        if(movie.amount.toInt()>0) {
                            mItemList.txt_type.setText("Cr")

                            mItemList!!.txt_type.getBackground().setColorFilter(Color.parseColor("#0b892e"),
                                PorterDuff.Mode.SRC_ATOP
                            )

                        } else {
                            mItemList.txt_type.setText("Dr")
                            mItemList!!.txt_Amount.setTextColor(Color.parseColor("#ed1b24"))
                            mItemList!!.txt_type.getBackground().setColorFilter(Color.parseColor("#ed1b24"),
                                PorterDuff.Mode.SRC_ATOP
                            )
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, " " + e.message, Toast.LENGTH_SHORT).show()
                }
            }
            LOADING -> {
            }
        }
    }
}