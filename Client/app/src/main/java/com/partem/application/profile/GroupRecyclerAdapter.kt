package com.partem.application.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.partem.application.R
import com.partem.application.enums.RECYCLER_DATA
import com.partem.application.models.Group
import kotlinx.android.synthetic.main.profile_fragment_recycler_item.view.profile_fragment_recycler_group_name

class GroupRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * The list of Groups that the RecyclerView will be displaying.
     */
    private var items = RECYCLER_DATA

    /**
     * Gets the number of items that will be displayed in the RecyclerView.
     *
     * @return The number of items that the RecyclerView will be displaying.
     *
     * @see items
     */
    override fun getItemCount(): Int = items.size

    //TODO: Complete documentation on the viewType parameter.
    /**
     * Creates and returns a GroupRecyclerAdapterViewHolder from the given parent ViewGroup.
     *
     * @param parent The ViewGroup that will hold the RecyclerView item.
     * @param viewType ?
     *
     * @see GroupRecyclerAdapterViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.profile_fragment_recycler_item, parent, false))
    }

    /**
     * Binds the Group at the given position to the holder.
     *
     * @param position The index of the Group in the items array.
     * @param holder The holder that the Group will be bound to.
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as GroupRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    /**
     * Sets the list of Groups that this adapter will be operating on.
     *
     * @param groupList The list of Groups that this adapter will be operating on.
     */
    fun submitList(groupList: Array<Group>) {
        items = groupList
    }

    //TODO: Complete documentation for the constructor.
    /**
     * A holder class for profile fragment RecyclerView Group items.
     *
     * @constructor
     */
    class GroupRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        /**
         * The name of the Group.
         */
        private val groupName: TextView = itemView.profile_fragment_recycler_group_name

        /**
         * Binds the Group data to a TextView.
         *
         * @param group The Group object which will be bound to this holder.
         */
        fun bind(group: Group) {
            groupName.text = group.name
        }

    }

}