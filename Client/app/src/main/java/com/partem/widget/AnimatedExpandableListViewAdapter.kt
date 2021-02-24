package com.partem.widget

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.AbsListView
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView


private const val STATE_IDLE = 0
private const val STATE_EXPANDING = 1
private const val STATE_COLLAPSING = 2

/**
 * A specialized adapter for use with the AnimatedExpandableListView. All
 * adapters used with AnimatedExpandableListView MUST extend this class.
 */
abstract class AnimatedExpandableListAdapter : BaseExpandableListAdapter() {
    private val groupInfo: SparseArray<GroupInfo?> = SparseArray<GroupInfo?>()
    private var parent: AnimatedExpandableListView? = null
    fun setParent(parent: AnimatedExpandableListView?) {
        this.parent = parent
    }

    fun getRealChildType(groupPosition: Int, childPosition: Int): Int = 0

    val realChildTypeCount: Int
        get() = 1

    abstract fun getRealChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View

    abstract fun getRealChildrenCount(groupPosition: Int): Int

    private fun getGroupInfo(groupPosition: Int): GroupInfo {
        var info: GroupInfo? = groupInfo[groupPosition]
        if (info == null) {
            info = GroupInfo()
            groupInfo.put(groupPosition, info)
        }
        return info
    }

    fun notifyGroupExpanded(groupPosition: Int) {
        val info: GroupInfo = getGroupInfo(groupPosition)
        info.dummyHeight = -1
    }

    fun startExpandAnimation(groupPosition: Int, firstChildPosition: Int) {
        val info: GroupInfo = getGroupInfo(groupPosition)
        info.animating = true
        info.firstChildPosition = firstChildPosition
        info.expanding = true
    }

    fun startCollapseAnimation(groupPosition: Int, firstChildPosition: Int) {
        val info: GroupInfo = getGroupInfo(groupPosition)
        info.animating = true
        info.firstChildPosition = firstChildPosition
        info.expanding = false
    }

    private fun stopAnimation(groupPosition: Int) {
        val info: GroupInfo = getGroupInfo(groupPosition)
        info.animating = false
    }

    /**
     * Override [.getRealChildType] instead.
     */
    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        val info: GroupInfo = getGroupInfo(groupPosition)
        return if (info.animating) {
            // If we are animating this group, then all of it's children
            // are going to be dummy views which we will say is type 0.
            0
        } else {
            // If we are not animating this group, then we will add 1 to
            // the type it has so that no type id conflicts will occur
            // unless getRealChildType() returns MAX_INT
            getRealChildType(groupPosition, childPosition) + 1
        }
    }

    /**
     * Override [.getRealChildTypeCount] instead.
     */
    override fun getChildTypeCount(): Int {
        // Return 1 more than the childTypeCount to account for DummyView
        return realChildTypeCount + 1
    }

    protected fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return AbsListView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT, 0
        )
    }

    /**
     * Override [.getChildView] instead.
     */
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
        val info: GroupInfo = getGroupInfo(groupPosition)
        return if (info.animating) {
            handleIfAnimating(info, groupPosition, convertView, childPosition)
        } else {
            getRealChildView(groupPosition, childPosition, isLastChild, convertView, parent)
        }
    }

    private fun handleIfAnimating(info: GroupInfo, groupPosition: Int, view: View?, childPosition: Int): View {
        // If this group is animating, return the a DummyView...
        var tempView = view
        if (tempView !is DummyView) {
            tempView = DummyView(parent!!.context)
            tempView.layoutParams =
                AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 0)
        }
        if (childPosition < info.firstChildPosition) {
            // The reason why we do this is to support the collapse
            // this group when the group view is not visible but the
            // children of this group are. When notifyDataSetChanged
            // is called, the ExpandableListView tries to keep the
            // list position the same by saving the first visible item
            // and jumping back to that item after the views have been
            // refreshed. Now the problem is, if a group has 2 items
            // and the first visible item is the 2nd child of the group
            // and this group is collapsed, then the dummy view will be
            // used for the group. But now the group only has 1 item
            // which is the dummy view, thus when the ListView is trying
            // to restore the scroll position, it will try to jump to
            // the second item of the group. But this group no longer
            // has a second item, so it is forced to jump to the next
            // group. This will cause a very ugly visual glitch. So
            // the way that we counteract this is by creating as many
            // dummy views as we need to maintain the scroll position
            // of the ListView after notifyDataSetChanged has been
            // called.
            tempView.layoutParams.height = 0
            return tempView
        }
        val listView: ExpandableListView? = parent
        val dummyView: DummyView = tempView

        // Clear the views that the dummy view draws.
        dummyView.clearViews()

        // Set the style of the divider
        dummyView.setDivider(listView!!.divider, parent!!.measuredWidth, listView.dividerHeight)

        // Make measure specs to measure child views
        val measureSpecW =
            View.MeasureSpec.makeMeasureSpec(parent!!.width, View.MeasureSpec.EXACTLY)
        val measureSpecH = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        var totalHeight = 0
        val clipHeight = parent!!.height
        val len = getRealChildrenCount(groupPosition)
        for (i in info.firstChildPosition until len) {
            val childView = getRealChildView(groupPosition, i, i == len - 1, null, parent)
            var p = childView.layoutParams as? AbsListView.LayoutParams
            if (p == null) {
                p = generateDefaultLayoutParams() as AbsListView.LayoutParams
                childView.layoutParams = p
            }
            val lpHeight = p.height
            val childHeightSpec = if (lpHeight > 0) {
                View.MeasureSpec.makeMeasureSpec(lpHeight, View.MeasureSpec.EXACTLY)
            } else {
                measureSpecH
            }
            childView.measure(measureSpecW, childHeightSpec)
            totalHeight += childView.measuredHeight
            if (totalHeight < clipHeight) {
                // we only need to draw enough views to fool the user...
                dummyView.addFakeView(childView)
            } else {
                dummyView.addFakeView(childView)

                // if this group has too many views, we don't want to
                // calculate the height of everything... just do a light
                // approximation and break
                val averageHeight: Int = totalHeight / (i + 1)
                totalHeight += (len - i - 1) * averageHeight
                break
            }
        }
        var o: Any?
        val state = if (dummyView.tag.also { o = it } == null) STATE_IDLE else (o as Int?)!!
        handleExpandingOrCollapsing(info, state, dummyView, totalHeight, groupPosition, listView)
        return tempView
    }

    private fun handleExpandingOrCollapsing(info: GroupInfo, state: Int, dummyView: DummyView, totalHeight: Int, groupPosition: Int, listView: ExpandableListView?) {
        if (info.expanding && state != STATE_EXPANDING) {
            handleExpanding(dummyView, totalHeight, info, groupPosition)
        } else if (!info.expanding && state != STATE_COLLAPSING) {
            handleCollapsing(dummyView, totalHeight, info, groupPosition, listView)
        }
    }

    private fun handleCollapsing(dummyView: DummyView, totalHeight: Int, info: GroupInfo, groupPosition: Int, listView: ExpandableListView?) {
        if (info.dummyHeight == -1) {
            info.dummyHeight = totalHeight
        }
        val ani = ExpandAnimation(dummyView, info.dummyHeight, 0, info)
        ani.duration = ANIMATION_DURATION
        ani.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                onAnimationEnded(groupPosition, listView, info, dummyView)
            }

            override fun onAnimationRepeat(animation: Animation) {
                //
            }

            override fun onAnimationStart(animation: Animation) {
                //
            }
        })
        dummyView.startAnimation(ani)
        dummyView.tag = STATE_COLLAPSING
    }

    private fun onAnimationEnded(groupPosition: Int, listView: ExpandableListView?, info: GroupInfo, dummyView: DummyView) {
        stopAnimation(groupPosition)
        listView!!.collapseGroup(groupPosition)
        notifyDataSetChanged()
        info.dummyHeight = -1
        dummyView.tag = STATE_IDLE
    }

    private fun handleExpanding(dummyView: DummyView, totalHeight: Int, info: GroupInfo, groupPosition: Int) {
        val ani = ExpandAnimation(dummyView, 0, totalHeight, info)
        ani.duration = ANIMATION_DURATION
        ani.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationEnd(animation: Animation) {
                stopAnimation(groupPosition)
                notifyDataSetChanged()
                dummyView.tag = STATE_IDLE
            }

            override fun onAnimationRepeat(animation: Animation) {
                //
            }

            override fun onAnimationStart(animation: Animation) {
                //
            }
        })
        dummyView.startAnimation(ani)
        dummyView.tag = STATE_EXPANDING
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val info: GroupInfo = getGroupInfo(groupPosition)
        return if (info.animating)
            info.firstChildPosition + 1
        else
            getRealChildrenCount(groupPosition)
    }

}