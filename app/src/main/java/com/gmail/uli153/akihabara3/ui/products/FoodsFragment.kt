package com.gmail.uli153.akihabara3.ui.products

import android.os.Bundle
import android.view.View
import com.gmail.uli153.akihabara3.data.DataWrapper

class FoodsFragment: ProductsBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productsViewModel.foods.observe(viewLifecycleOwner) {
            products = if (it is DataWrapper.Success) {
                it.data
            } else {
                listOf()
            }
        }
    }
}
