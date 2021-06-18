package com.partem.application.history

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView


class _CustomItemAnimator: DefaultItemAnimator() {

    private lateinit var expandingViewHolder: RecyclerView.ViewHolder

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean = true

    override fun recordPreLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder, changeFlags: Int, payloads: MutableList<Any>): ItemHolderInfo {
        if(payloads.isNotEmpty() && payloads[0] == EXPAND_ANIMATION) expandingViewHolder = viewHolder
        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads)
    }

    override fun recordPostLayoutInformation(state: RecyclerView.State, viewHolder: RecyclerView.ViewHolder): ItemHolderInfo {
        return super.recordPostLayoutInformation(state, viewHolder)
    }

    override fun animateMove(holder: RecyclerView.ViewHolder, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean {
        return super.animateMove(holder, fromX, fromY, toX, toY)
    }

}