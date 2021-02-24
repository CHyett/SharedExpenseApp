package com.partem.widget

import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation


class ExpandAnimation internal constructor(v: View, private val baseHeight: Int, endHeight: Int, info: GroupInfo): Animation() {

    private val delta: Int = endHeight - baseHeight

    private val view: View = v

    private val groupInfo: GroupInfo = info

    init {
        view.layoutParams.height = baseHeight
        view.requestLayout()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)
        if (interpolatedTime < 1.0f) {
            val `val` = baseHeight + (delta * interpolatedTime).toInt()
            view.layoutParams.height = `val`
            groupInfo.dummyHeight = `val`
            view.requestLayout()
        } else {
            val `val` = baseHeight + delta
            view.layoutParams.height = `val`
            groupInfo.dummyHeight = `val`
            view.requestLayout()
        }
    }

}