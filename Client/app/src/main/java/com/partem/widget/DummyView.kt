package com.partem.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import java.util.*


class DummyView(context: Context?) : View(context) {
    private val views: MutableList<View> = ArrayList()
    private var divider: Drawable? = null
    private var dividerWidth = 0
    private var dividerHeight = 0
    fun setDivider(divider: Drawable?, dividerWidth: Int, dividerHeight: Int) {
        if (divider != null) {
            this.divider = divider
            this.dividerWidth = dividerWidth
            this.dividerHeight = dividerHeight
            divider.setBounds(0, 0, dividerWidth, dividerHeight)
        }
    }

    /**
     * Add a view for the DummyView to draw.
     *
     * @param childView View to draw
     */
    fun addFakeView(childView: View) {
        childView.layout(0, 0, width, childView.measuredHeight)
        views.add(childView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val len = views.size
        for (i in 0 until len) {
            val v = views[i]
            v.layout(left, top, left + v.measuredWidth, top + v.measuredHeight)
        }
    }

    fun clearViews() {
        views.clear()
    }

    public override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        if (divider != null) {
            divider!!.setBounds(0, 0, dividerWidth, dividerHeight)
        }
        val len = views.size
        for (i in 0 until len) {
            val v = views[i]
            canvas.save()
            canvas.clipRect(0, 0, width, v.measuredHeight)
            v.draw(canvas)
            canvas.restore()
            if (divider != null) {
                divider!!.draw(canvas)
                canvas.translate(0f, dividerHeight.toFloat())
            }
            canvas.translate(0f, v.measuredHeight.toFloat())
        }
        canvas.restore()
    }
}