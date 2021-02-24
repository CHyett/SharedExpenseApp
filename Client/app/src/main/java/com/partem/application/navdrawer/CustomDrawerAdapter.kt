package com.partem.application.navdrawer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.partem.application.R
import com.partem.widget.AnimatedExpandableListAdapter

class CustomDrawerAdapter(private val con: Context, private val listDataHeader: List<DrawerItem>, private val listDataChild: HashMap<DrawerItem, List<DrawerItem>>): AnimatedExpandableListAdapter() {

    override fun getChild(groupPosition: Int, childPosition: Int): DrawerItem? = this.listDataChild[this.listDataHeader[groupPosition]]?.get(childPosition)

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroup(groupPosition: Int): DrawerItem = this.listDataHeader[groupPosition]

    override fun getGroupCount(): Int = this.listDataHeader.size

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val childText = getChild(groupPosition, childPosition)!!.itemName
        var view = convertView
        if(view == null) {
            val inflater = this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.main_activity_custom_nav_child_item, null)
        }
        val textListChild = view!!.findViewById(R.id.nav_drawer_child_item_text) as TextView
        textListChild.text = childText
        return view
    }

    override fun getRealChildrenCount(groupPosition: Int): Int {
        return if(this.listDataChild[this.listDataHeader[groupPosition]] == null)
            0
        else
            this.listDataChild[this.listDataHeader[groupPosition]]!!.size
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if(view == null) {
            val inflater = this.con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.main_activity_custom_nav_group_item, null)
        }
        val listHeader = view!!.findViewById(R.id.nav_drawer_group_item_text) as TextView
        listHeader.text = getGroup(groupPosition).itemName
        if(!getGroup(groupPosition).hasChildren)
            (view.findViewById(R.id.nav_drawer_group_item_image) as ImageView).setImageResource(R.color.transparent)
        else {
            if(isExpanded)
                view.findViewById<ImageView>(R.id.nav_drawer_group_item_image).setImageResource(R.drawable.down_arrow)
            else
                view.findViewById<ImageView>(R.id.nav_drawer_group_item_image).setImageResource(R.drawable.forward_arrow)
        }
        return view
    }

}