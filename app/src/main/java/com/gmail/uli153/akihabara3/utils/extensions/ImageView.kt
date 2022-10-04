package com.gmail.uli153.akihabara3.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.domain.models.Product
import com.gmail.uli153.akihabara3.domain.models.Transaction

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