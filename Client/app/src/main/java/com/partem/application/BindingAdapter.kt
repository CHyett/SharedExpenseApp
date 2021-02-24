package com.partem.application

import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter


@BindingAdapter("isEnabled")
fun setIsEnabled(button: Button, enabled: Boolean) {
    button.isEnabled = enabled
}

@BindingAdapter("android:src")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource);
}