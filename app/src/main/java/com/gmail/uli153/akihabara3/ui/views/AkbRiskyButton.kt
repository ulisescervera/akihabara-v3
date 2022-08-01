package com.gmail.uli153.akihabara3.ui.views

import android.content.Context
import android.util.AttributeSet

class AkbRiskyButton: AkbBaseButton {

    constructor(context: Context) : super(context) {
        style = AkbButtonStyle.RISKY
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        style = AkbButtonStyle.RISKY
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        style = AkbButtonStyle.RISKY
    }
}