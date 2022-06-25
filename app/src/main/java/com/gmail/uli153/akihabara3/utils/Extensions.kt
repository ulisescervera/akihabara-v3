package com.gmail.uli153.akihabara3.utils

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun View.setSafeClickListener(delay: Long = 500, listener: View.OnClickListener) {
    this.setOnClickListener {
        this.isEnabled = false
        it.delayOnLifecycle(delay) {
            it.isEnabled = true
        }
        listener.onClick(this)
    }
}

fun View.delayOnLifecycle(delay: Long = 500, block: () -> Unit) {
    findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.Main) {
        delay(delay)
        block()
    }
}

val Number.toPx: Float get() {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )
}