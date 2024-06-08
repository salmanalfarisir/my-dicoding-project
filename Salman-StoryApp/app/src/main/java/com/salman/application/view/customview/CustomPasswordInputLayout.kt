package com.salman.application.view.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.salman.application.R

class CustomPasswordInputLayout(context: Context, attrs: AttributeSet) :
    TextInputLayout(context, attrs) {

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            s?.let {
                if (it.length < 8) {
                    error = context.getString(R.string.password_cannot_be_less_than_8_character)
                } else {
                    error = null
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    fun setEditText(editText: TextInputEditText) {
        editText.addTextChangedListener(textWatcher)
    }
}