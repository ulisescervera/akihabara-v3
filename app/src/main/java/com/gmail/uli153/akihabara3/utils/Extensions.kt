package com.gmail.uli153.akihabara3.utils

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.models.Transaction
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

fun ImageView.setProductImage(productEntity: Product) {
    productEntity.customImage?.let {
        Glide.with(this).load(it).circleCrop().error(productEntity.defaultImage).into(this)
    } ?: this.setImageResource(productEntity.defaultImage)
}

fun ImageView.setTransactionImage(transactionEntity: Transaction) {
    transactionEntity.customImage?.let {
        Glide.with(this).load(it).circleCrop().error(transactionEntity.defaultImage).into(this)
    } ?: this.setImageResource(transactionEntity.defaultImage)
}

fun List<Product>.sorted(): List<Product> {
    return this.sortedWith(compareByDescending<Product> { it.favorite }
        .thenBy { it.name.lowercase() }
        .thenBy { it.id })
}
