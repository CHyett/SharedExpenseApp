package com.partem.application.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partem.application.R
import com.partem.application.models.Group
import kotlinx.android.synthetic.main.home_fragment_recycler_item.view.*
import com.partem.application.enums.RECYCLER_DATA

/**
 * Adapter used in the home page group listings RecyclerView.
 */
class GroupRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Holds the list of groups that the home page RecyclerView will be displaying.
     */
    private var items = RECYCLER_DATA

    /**
     * Returns the number of items in the list.
     *
     * @return The number of Groups in the list.
     *
     * @see items
     * @see Group
     */
    override fun getItemCount(): Int = items.size

    /**
     * Submits the given array as the home page RecyclerView's data.
     *
     * @param groupList The list of groups to be displayed on the home screen.
     */
    fun submitList(groupList: Array<Group>) { items = groupList }

    //TODO: Add documentation for what the viewType parameter is.
    /**
     * Creates a GroupRecyclerAdapterViewHolder from the given data.
     *
     * @param parent The ViewGroup to be added to.
     * @param viewType ?
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_fragment_recycler_item, parent, false))
    }

    /**
     * Binds the Group at the given position to the given holder.
     *
     * @param holder The GroupRecyclerAdapterViewHolder to be given data.
     * @param position The position in the Group array.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as GroupRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    /**
     * A holder for Group data that will be displayed in the home page RecyclerView.
     *
     * @param itemView The layout which will contain a Group's data.
     */
    class GroupRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /**
         * The name of the Group.
         */
        private val groupName: TextView = itemView.home_fragment_recycler_group_name

        /**
         * The amount of money owed to the group.
         */
        private val groupDetails: TextView = itemView.home_fragment_recycler_group_details

        /**
         * Binds the Group's data to a view.
         *
         * @param group The group to be operated on.
         */
        fun bind(group: Group) {
            groupName.text = group.name
            groupDetails.text = "$".plus(group.globalExpense.toString())
        }

    }

}