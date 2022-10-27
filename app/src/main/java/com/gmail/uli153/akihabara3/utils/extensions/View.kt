package com.gmail.uli153.akihabara3.utils.extensions

import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
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
        kotlinx.coroutines.delay(delay)
        block()
    }
}

val View.viewGroup: ViewGroup? get() {
    return this.parent as? ViewGroup
}
