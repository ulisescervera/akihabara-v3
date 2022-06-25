package com.gmail.uli153.akihabara3.ui.views

import android.content.Context
import android.util.AttributeSet

class AkbMainButton: AkbBaseButton {

    constructor(context: Context) : super(context) {
        style = AkbButtonStyle.MAIN
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        style = AkbButtonStyle.MAIN
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        style = AkbButtonStyle.MAIN
    }
}