package com.partem.application.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partem.application.R
import com.partem.application.enums.HISTORY_TRANSACTIONS_DATA
import com.partem.application.models.Transaction
import kotlinx.android.synthetic.main.history_fragment_recycler_item.view.*
import kotlin.math.abs

class HistoryFragmentRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = HISTORY_TRANSACTIONS_DATA

    override fun getItemCount(): Int = items.size

    fun submitList(groupList: Array<Transaction>) { items = groupList }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HistoryRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_fragment_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as HistoryRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    class HistoryRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val targetName: TextView = itemView.history_fragment_recycler_target_name

        private val amount: TextView = itemView.history_fragment_recycler_amount

        fun bind(transaction: Transaction) {
            targetName.text = transaction.target
            amount.text = if(transaction.amount >= 0.0) "$${transaction.amount}"  else "-$${abs(transaction.amount)}"
        }

    }

}