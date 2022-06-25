package com.gmail.uli153.akihabara3.ui.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.gmail.uli153.akihabara3.R

enum class AkbButtonStyle {
    MAIN, GREY
}

open class AkbBaseButton: AppCompatButton {

    var style: AkbButtonStyle = AkbButtonStyle.GREY
        set(value) {
            field = value
            when(value) {
                AkbButtonStyle.MAIN -> {
                    background = ContextCompat.getDrawable(context, R.drawable.bg_main_button)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    isEnabled = true
                }
                AkbButtonStyle.GREY -> {
                    background = ContextCompat.getDrawable(context, R.drawable.bg_grey_button)
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    isEnabled = false
                }
            }
        }

    constructor(context: Context) : super(context) {
        style = AkbButtonStyle.GREY
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        style = AkbButtonStyle.GREY
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        style = AkbButtonStyle.GREY
    }
}