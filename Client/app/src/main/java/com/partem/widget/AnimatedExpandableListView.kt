package com.partem.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ExpandableListAdapter
import android.widget.ExpandableListView

internal const val ANIMATION_DURATION = 300L

/**
 * This class defines an ExpandableListView which supports animations for
 * collapsing and expanding groups.
 */
class AnimatedExpandableListView : ExpandableListView {

    private var adapter: AnimatedExpandableListAdapter? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    /**
     * @see ExpandableListView.setAdapter
     */
    override fun setAdapter(adapter: ExpandableListAdapter) {
        super.setAdapter(adapter)

        // Make sure that the adapter extends AnimatedExpandableListAdapter
        if (adapter is AnimatedExpandableListAdapter) {
            this.adapter = adapter
            this.adapter!!.setParent(this)
        } else
            throw ClassCastException("$adapter must implement AnimatedExpandableListAdapter")
    }

    /**
     * Expands the given group with an animation.
     *
     * @param groupPos The position of the group to expand
     * @return Returns true if the group was expanded. False if the group was
     * already expanded.
     */
    @SuppressLint("NewApi")
    fun expandGroupWithAnimation(groupPos: Int): Boolean {
        val groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos))
        if (groupFlatPos != -1) {
            val childIndex = groupFlatPos - firstVisiblePosition
            if (childIndex < childCount) {
                // Get the view for the group is it is on screen...
                val v = getChildAt(childIndex)
                if (v.bottom >= bottom) {
                    // If the user is not going to be able to see the animation
                    // we just expand the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen

                    // We need to notify the adapter that the group was expanded
                    // without it's knowledge
                    adapter?.notifyGroupExpanded(groupPos)
                    return expandGroup(groupPos)
                }
            }
        }

        // Let the adapter know that we are starting the animation...
        adapter?.startExpandAnimation(groupPos, 0)
        // Finally call expandGroup (note that expandGroup will call
        // notifyDataSetChanged so we don't need to)
        return expandGroup(groupPos)
    }

    /**
     * Collapses the given group with an animation.
     *
     * @param groupPos The position of the group to collapse
     * @return Returns true if the group was collapsed. False if the group was
     * already collapsed.
     */
    fun collapseGroupWithAnimation(groupPos: Int): Boolean {
        val groupFlatPos = getFlatListPosition(getPackedPositionForGroup(groupPos))
        if (groupFlatPos != -1) {
            val childIndex = groupFlatPos - firstVisiblePosition
            if (childIndex in 0 until childCount) {
                // Get the view for the group is it is on screen...
                val v = getChildAt(childIndex)
                if (v.bottom >= bottom) {
                    // If the user is not going to be able to see the animation
                    // we just collapse the group without an animation.
                    // This resolves the case where getChildView will not be
                    // called if the children of the group is not on screen
                    return collapseGroup(groupPos)
                }
            } else {
                // If the group is offscreen, we can just collapse it without an
                // animation...
                return collapseGroup(groupPos)
            }
        }

        // Get the position of the firstChild visible from the top of the screen
        val packedPos = getExpandableListPosition(firstVisiblePosition)
        var firstChildPos = getPackedPositionChild(packedPos)
        val firstGroupPos = getPackedPositionGroup(packedPos)

        // If the first visible view on the screen is a child view AND it's a
        // child of the group we are trying to collapse, then set that
        // as the first child position of the group... see
        // {@link #startCollapseAnimation(int, int)} for why this is necessary
        firstChildPos = if (firstChildPos == -1 || firstGroupPos != groupPos) 0 else firstChildPos

        // Let the adapter know that we are going to start animating the
        // collapse animation.
        adapter?.startCollapseAnimation(groupPos, firstChildPos)

        // Force the listview to refresh it's views
        adapter?.notifyDataSetChanged()
        return isGroupExpanded(groupPos)
    }

}