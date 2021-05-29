package com.partem.application.navdrawer

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.partem.application.R
import com.partem.widget.AnimatedExpandableListAdapter

//TODO: Complete documentation for this constructor.
/**
 * Custom adapter for the nav drawer ExpandableListView
 *
 * @constructor.
 */
class CustomDrawerAdapter(private val con: Context, private val listDataHeader: List<DrawerItem>, private val listDataChild: HashMap<DrawerItem, List<DrawerItem>>): AnimatedExpandableListAdapter() {

    /**
     * Gets the DrawerItem corresponding to the given group and child position indices.
     *
     * @param groupPosition The index of the group.
     * @param childPosition The index of the child within the given group at the given index.
     *
     * @return The DrawerItem at the given indices.
     */
    override fun getChild(groupPosition: Int, childPosition: Int): DrawerItem? = this.listDataChild[this.listDataHeader[groupPosition]]?.get(childPosition)

    /**
     * Gets the ID of the child corresponding to the given indices.
     *
     * @param groupPosition The index of the group.
     * @param childPosition The index of the child within the given group at the given index.
     *
     * @return The ID corresponding to the child DrawerItem at the given indices.
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    /**
     * Gets the group DrawerItem at the given index.
     *
     * @param groupPosition The index of the group.
     *
     * @return The group DrawerItem at the given index.
     */
    override fun getGroup(groupPosition: Int): DrawerItem = this.listDataHeader[groupPosition]

    /**
     * Gets the number of group drawer items.
     *
     * @return The number of group DrawerItems.
     */
    override fun getGroupCount(): Int = this.listDataHeader.size

    /**
     * Gets the ID of the group corresponding to the given index.
     *
     * @param groupPosition The index of the group.
     *
     * @return The ID corresponding to the group DrawerItem at the given index.
     */
    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    //TODO: Write some documentation on what this method does.
    /**
     * ?
     *
     * @return
     */
    override fun hasStableIds(): Boolean = false

    //TODO: Complete documentation on what this method does.
    /**
     * ?
     *
     * @param groupPosition The index of the group.
     * @param childPosition The index of the child within the given group at the given index.
     *
     * @return
     */
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    /**
     * Creates and returns a child nav drawer view that is ready to be displayed.
     *
     * @param groupPosition The index of the group.
     * @param childPosition The index of the child within the given group at the given index.
     * @param convertView The view that will contain the nav drawer items contents.
     * @param isLastChild Whether this view is the last item in a group.
     * @param parent ?
     *
     * @return An inflated child view that is ready to be displayed.
     */
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

    /**
     * Returns the number of children belonging to group item at the given index.
     *
     * @param groupPosition The index of the group.
     *
     * @return The number of children belonging to group item at the given index.
     */
    override fun getRealChildrenCount(groupPosition: Int): Int {
        return if(this.listDataChild[this.listDataHeader[groupPosition]] == null)
            0
        else
            this.listDataChild[this.listDataHeader[groupPosition]]!!.size
    }

    /**
     * Creates a group nav drawer view.
     *
     * @param groupPosition The index of the group.
     * @param isExpanded Whether this group is expanded or not.
     * @param convertView The view that will contain the nav drawer items contents.
     * @param parent ?
     *
     * @return An inflated group view that is ready to be displayed.
     */
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