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

/**
 * Adapter for the history fragment RecyclerView.
 */
class HistoryFragmentRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * List of transactions that will be displayed on the screen.
     */
    private var items = HISTORY_TRANSACTIONS_DATA

    /**
     * The number of items in the items list.
     *
     * @return The number of items in the items list.
     *
     * @see items
     */
    override fun getItemCount(): Int = items.size

    /**
     * Public method to set the items list.
     *
     * @param groupList The list of Transactions to be submitted.
     */
    fun submitList(groupList: Array<Transaction>) { items = groupList }

    //TODO: Complete documentation for the viewType parameter.
    /**
     * Creates and returns a HistoryRecyclerAdapterViewHolder.
     *
     * @param parent The ViewGroup that will be inflated with a RecyclerView item.
     * @param viewType ?
     *
     * @return A HistoryRecyclerAdapterViewHolder with a view from the inflated parent given.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return HistoryRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_fragment_recycler_item, parent, false))
    }

    /**
     * Binds the Transaction at the given position to the holder.
     *
     * @param position The index of the Transaction in the items array.
     * @param holder The holder that the Transaction will be bound to.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as HistoryRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    //TODO: Complete documentation for the constructor.
    /**
     * A holder class for history fragment RecyclerView Transaction items.
     *
     * @constructor
     */
    class HistoryRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /**
         * The name of the target for this Transaction.
         */
        private val targetName: TextView = itemView.history_fragment_recycler_target_name

        /**
         * The amount of money in this Transaction.
         */
        private val amount: TextView = itemView.history_fragment_recycler_amount

        /**
         * Binds the Transaction data to TextViews.
         *
         * @param transaction The Transaction object which will be bound to this holder.
         */
        fun bind(transaction: Transaction) {
            targetName.text = transaction.target
            amount.text = if(transaction.amount >= 0.0) "$${transaction.amount}"  else "-$${abs(transaction.amount)}"
        }

    }

}