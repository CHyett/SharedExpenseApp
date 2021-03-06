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
    private var items = RECYCLER_DATA

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GroupRecyclerAdapterViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.profile_fragment_recycler_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as GroupRecyclerAdapterViewHolder
        holder.bind(items[position])
    }

    fun submitList(groupList: Array<Group>) {
        items = groupList
    }

    class GroupRecyclerAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val groupName: TextView = itemView.profile_fragment_recycler_group_name

        fun bind(group: Group) {
            groupName.text = group.name
        }

    }

}