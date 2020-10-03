package com.example.sharedexpenseapp

import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText


@BindingAdapter("app:isEnabled")
fun setIsEnabled(button: Button, enabled: Boolean) {
    button.isEnabled = enabled
}

@BindingAdapter("android:src")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource);
}








/*@BindingAdapter("android:text")
internal fun setText(view: EditText, value: Float?) {
    println("Binding adapter function fired")
    view.text = value?.let { SpannableStringBuilder(value.toString()) } ?: SpannableStringBuilder("")
}

@InverseBindingAdapter(attribute = "android:text")
internal fun getTextString(view: EditText): Float? {
    println("Inverse binding adapter function fired")
    return try {
        view.text?.toString()?.toFloat()
    } catch (e: NumberFormatException) {
        0.0f // because, um, what else can we do?
    }
    /*val editTextString: String? = view.text?.toString()
    return if(editTextString == null || editTextString == "") 0.0f else editTextString.toString().toFloat()*/
}

@BindingAdapter("android:textAttrChanged")
fun setTextChangedListener(editText: TextInputEditText, listener: InverseBindingListener) {
    editText.addTextChangedListener(object: TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            listener.onChange()
        }
    })
}*/