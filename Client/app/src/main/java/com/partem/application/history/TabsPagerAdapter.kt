package com.partem.application.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.partem.application.R
import com.partem.application.enums.HISTORY_TRANSACTIONS_DATA
import com.partem.application.models.Transaction

class TabsPagerAdapter: PagerAdapter() {

    /**
     * Structure containing fragment tabs
     */
    private val tabs = arrayOf("Transactions", "Payments")

    /**
     * Gets the number of tabs that will be displayed.
     *
     * @return The number of tabs.
     */
    override fun getCount(): Int = tabs.size

    /**
     * Gets the text of the tab for the given index.
     *
     * @return The text of the tab given position in the tabs list.
     *
     * @see tabs
     */
    override fun getPageTitle(position: Int): CharSequence = tabs[position]

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object`

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any)  = container.removeView(`object` as View)

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater = LayoutInflater.from(container.context)
        val child = inflater.inflate(R.layout.history_fragment_tab_item, container, false) as ViewGroup
        val adapter = HistoryFragmentRecyclerAdapter()
        initRecyclerView(container.context, child, adapter)
        addDataSet(position, adapter)
        container.addView(child)
        return child
    }

    /**
     * Sets style for this tab's RecyclerView and adds a HistoryFragmentRecyclerAdapter to it.
     *
     * @param context Context on which this method will operate on.
     * @param layout The layout representing the current tab.
     * @param adapter The HistoryFragmentRecyclerAdapter that will be added to this tab's RecyclerView.
     */
    private fun initRecyclerView(context: Context, layout: View, adapter: HistoryFragmentRecyclerAdapter) {
        val recyclerView = layout.findViewById(R.id.history_fragment_pager_tab_item_recycler) as RecyclerView
        recyclerView.layoutManager = object: LinearLayoutManager(context) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                lp?.height = layout.height / 10
                return true
            }
        }
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        divider.setDrawable(AppCompatResources.getDrawable(context, R.drawable.recycler_divider)!!)
        recyclerView.addItemDecoration(divider)
        recyclerView.adapter = adapter
    }

    /**
     * Sets the data set for the current tab's RecyclerView's HistoryFragmentRecyclerAdapter to operate on.
     *
     * @param position The position of the current tab within the tabs list.
     * @param adapter The HistoryFragmentRecyclerAdapter that will be given a data set.
     */
    private fun addDataSet(position: Int, adapter: HistoryFragmentRecyclerAdapter) {
        val list: Array<Transaction> = if(position == 0) HISTORY_TRANSACTIONS_DATA.clone()
        else {
            val temp = ArrayList<Transaction>()
            for(transaction in HISTORY_TRANSACTIONS_DATA) {
                if(transaction.amount < 0.0) temp.add(transaction)
            }
            Array(temp.size) { temp[it] }
        }
        adapter.submitList(list)
    }

}