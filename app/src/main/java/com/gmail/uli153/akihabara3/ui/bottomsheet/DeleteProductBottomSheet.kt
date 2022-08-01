package com.gmail.uli153.akihabara3.ui.bottomsheet

import androidx.fragment.app.FragmentManager
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.ui.bottomsheet.base.DeleteBaseBottomSheet

class DeleteProductBottomSheet private constructor(
    product: Product,
    listener: DeleteListener<Product>
): DeleteBaseBottomSheet<Product>(product, listener) {

    companion object {
        fun show(manager: FragmentManager, product: Product, listener: DeleteListener<Product>) {
            DeleteProductBottomSheet(product, listener).show(manager, "DeleteProductBottomSheet")
        }
    }

}