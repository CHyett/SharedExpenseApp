package com.partem.application.util

import android.content.Context
import android.view.ViewGroup
import jp.wasabeef.blurry.Blurry

object  BlurController {

    /**
     * ViewGroup to apply blur to.
     */
    var subjectView: ViewGroup? = null

    /**
     * Context on which to operate on.
     */
    lateinit var context: Context

    /**
     * Blurs the screen starting at the subjectView.
     *
     * @see subjectView
     */
    fun blurScreen() = subjectView?.let { Blurry.with(context).radius(10).sampling(8).animate(500).onto(subjectView) }

    /**
     * Clears up the screen.
     */
    fun clearBlur() = subjectView?.let { Blurry.delete(subjectView) }

}