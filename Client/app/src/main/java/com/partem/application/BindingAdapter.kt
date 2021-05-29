package com.partem.application

import android.widget.Button
import android.widget.ImageView
import androidx.databinding.BindingAdapter


/**
 * BindingAdapter function to enable or disable methods.
 *
 * @param button The button that will be operated on.
 * @param enabled Whether the button should be enabled or not.
 */
@BindingAdapter("isEnabled")
fun setIsEnabled(button: Button, enabled: Boolean) {
    button.isEnabled = enabled
}

/**
 * BindingAdapter function to set an image resource for an ImageView.
 *
 * @param view The ImageView to be operated on.
 * @param resource The resource ID to the drawable to be used in the ImageView.
 */
@BindingAdapter("android:src")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}