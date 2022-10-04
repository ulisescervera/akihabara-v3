package com.gmail.uli153.akihabara3.utils.extensions

import android.content.res.Resources
import android.util.TypedValue

val Number.toPx: Float get() {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}