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

class GroupRecyclerAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = RECYCLER_DATA

    override fun getItemCount(): Int = items.size

    fun submitList(groupList: Array<Group>) { items = groupList }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.home_fragment_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as GroupRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    class GroupRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val groupName: TextView = itemView.home_fragment_recycler_group_name

        private val groupDetails: TextView = itemView.home_fragment_recycler_group_details

        fun bind(group: Group) {
            groupName.text = group.name
            groupDetails.text = "$".plus(group.globalExpense.toString())
        }

    }

}