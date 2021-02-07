package com.example.sharedexpenseapp

import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.databinding.BindingAdapter


@BindingAdapter("isEnabled")
fun setIsEnabled(button: Button, enabled: Boolean) {
    button.isEnabled = enabled
}

@BindingAdapter("android:src")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource);
}