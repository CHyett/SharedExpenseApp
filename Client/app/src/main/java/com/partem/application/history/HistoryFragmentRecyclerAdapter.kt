package com.partem.application.history

import android.animation.LayoutTransition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.partem.application.R
import com.partem.application.enums.HISTORY_TRANSACTIONS_DATA
import com.partem.application.models.Transaction
import kotlinx.android.synthetic.main.history_fragment_recycler_item.view.*
import kotlin.math.abs

internal const val EXPAND_ANIMATION = 0x0
internal const val CHANGE_DURATION = 500L

/**
 * Adapter for the history fragment RecyclerView.
 */
class HistoryFragmentRecyclerAdapter: RecyclerView.Adapter<HistoryFragmentRecyclerAdapter.HistoryRecyclerAdapterViewHolder>() {

    /**
     * List of transactions that will be displayed on the screen.
     */
    private var items = Array(HISTORY_TRANSACTIONS_DATA.size) { TransactionRecyclerItem(HISTORY_TRANSACTIONS_DATA[it]) }


    /**
     * The number of items in the items list.
     *
     * @return The number of items in the items list.
     *
     * @see items
     */
    override fun getItemCount(): Int = items.size

    override fun getItemId(position: Int): Long = position.toLong()

    /**
     * Public method to set the items list.
     *
     * @param groupList The list of Transactions to be submitted.
     */
    fun submitList(groupList: Array<Transaction>) {
        items = Array(groupList.size) { TransactionRecyclerItem(groupList[it]) }
    }

    //TODO: Complete documentation for the viewType parameter.
    /**
     * Creates and returns a HistoryRecyclerAdapterViewHolder.
     *
     * @param parent The ViewGroup that will be inflated with a RecyclerView item.
     * @param viewType ?
     *
     * @return A HistoryRecyclerAdapterViewHolder with a view from the inflated parent given.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryRecyclerAdapterViewHolder {
        return HistoryRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.history_fragment_recycler_item, parent, false))
    }

    /**
     * Binds the Transaction at the given position to the holder.
     *
     * @param position The index of the Transaction in the items array.
     * @param holder The holder that the Transaction will be bound to.
     */
    override fun onBindViewHolder(holder: HistoryRecyclerAdapterViewHolder, position: Int) = holder.bind(items[position])

    //TODO: Complete documentation for the constructor.
    /**
     * A holder class for history fragment RecyclerView Transaction items.
     *
     * @constructor
     */
    inner class HistoryRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /**
         * The name of the target for this Transaction.
         */
        private val targetName: TextView = itemView.history_fragment_recycler_target_name

        /**
         * The amount of money in this Transaction.
         */
        private val amount: TextView = itemView.history_fragment_recycler_amount

        /**
         * Button for showing optional description.
         */
        private val descriptionBtn: ImageView = itemView.history_fragment_recycler_dropdown_btn

        /**
         * Layout containing transaction description.
         */
        private val descriptionLayout: LinearLayout = itemView.history_fragment_recycler_optional_description_layout

        /**
         * The transaction description. (Optional)
         */
        private val description: TextView = itemView.history_fragment_recycler_optional_description

        /**
         *
         */
        private val rootLayout: ConstraintLayout = itemView.history_fragment_recycler_item_root

        init {
            descriptionBtn.setOnClickListener {
                items[adapterPosition].isExpanded = !items[adapterPosition].isExpanded
                notifyItemChanged(adapterPosition, EXPAND_ANIMATION)
            }
            rootLayout.layoutTransition.setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, AccelerateDecelerateInterpolator())
            rootLayout.layoutTransition.setInterpolator(LayoutTransition.DISAPPEARING, AccelerateDecelerateInterpolator())
            rootLayout.layoutTransition.setInterpolator(LayoutTransition.CHANGE_APPEARING, AccelerateDecelerateInterpolator())
            rootLayout.layoutTransition.setInterpolator(LayoutTransition.APPEARING, AccelerateDecelerateInterpolator())
            rootLayout.layoutTransition.setDuration(CHANGE_DURATION)
        }

        /**
         * Binds the Transaction data to TextViews.
         *
         * @param item The Transaction object which will be bound to this holder.
         */
        fun bind(item: TransactionRecyclerItem) {
            targetName.text = item.transaction.target
            amount.text = if (item.transaction.amount >= 0.0) "$${item.transaction.amount}" else "-$${abs(item.transaction.amount)}"
            item.transaction.description?.let {
                description.text = it
                descriptionBtn.visibility = View.VISIBLE
                descriptionLayout.visibility = if (item.isExpanded) View.VISIBLE else View.GONE
            }
        }

    }
}