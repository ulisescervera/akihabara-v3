package com.gmail.uli153.akihabara3.utils

import android.content.Context
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.data.models.Transaction
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

@ColorInt
fun Context.getColorFromAttr(
    @AttrRes attrColor: Int,
    typedValue: TypedValue = TypedValue(),
    resolveRefs: Boolean = true
): Int {
    theme.resolveAttribute(attrColor, typedValue, resolveRefs)
    return typedValue.data
}

fun ImageView.setProductImage(product: Product) {
    product.customImage?.let {
        Glide.with(this).load(it).circleCrop().error(product.defaultImage).into(this)
    } ?: this.setImageResource(product.defaultImage)
}

fun ImageView.setTransactionImage(transaction: Transaction) {
    transaction.customImage?.let {
        Glide.with(this).load(it).circleCrop().error(transaction.defaultImage).into(this)
    } ?: this.setImageResource(transaction.defaultImage)
}