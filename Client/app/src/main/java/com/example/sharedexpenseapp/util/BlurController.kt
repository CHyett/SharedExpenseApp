package com.example.sharedexpenseapp.util

import android.content.Context
import android.view.ViewGroup
import jp.wasabeef.blurry.Blurry

object  BlurController {

    var subjectView: ViewGroup? = null

    lateinit var context: Context

    fun blurScreen() = subjectView?.let { Blurry.with(context).radius(10).sampling(8).animate(500).onto(subjectView) }

    fun clearBlur() = subjectView?.let { Blurry.delete(subjectView) }

}