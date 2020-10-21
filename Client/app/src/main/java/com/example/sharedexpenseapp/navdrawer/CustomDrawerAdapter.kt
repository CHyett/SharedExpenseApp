package com.example.sharedexpenseapp.navdrawer

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import com.example.sharedexpenseapp.R
import org.w3c.dom.Text

class CustomDrawerAdapter(con: Context, private val layoutResourceID: Int, private val listItems: List<DrawerItem>): ArrayAdapter<DrawerItem>(con, layoutResourceID, listItems) {

    private object DrawerItemHolder {

        lateinit var itemName: TextView

    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val drawerHolder: DrawerItemHolder

        if (view == null) {
            val inflater = (context as Activity).layoutInflater
            drawerHolder = DrawerItemHolder

            view = inflater.inflate(layoutResourceID, parent, false)
            drawerHolder.itemName = (view as TextView).findViewById(R.id.drawer_itemName)

            view.setTag(drawerHolder)

        } else {
            drawerHolder = view.tag as DrawerItemHolder
        }
        val dItem = this.listItems[position]
        drawerHolder.itemName.text = dItem.itemName

        return view;
    }

    fun res(view: View): Resources = view.resources

}