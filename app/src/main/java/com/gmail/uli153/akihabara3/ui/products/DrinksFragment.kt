package com.gmail.uli153.akihabara3.ui.products

import android.os.Bundle
import android.view.View
import com.gmail.uli153.akihabara3.data.models.Product
import com.gmail.uli153.akihabara3.utils.DataWrapper

class DrinksFragment: ProductsBaseFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.submitList(listOf())
        productsViewModel.driks.observe(viewLifecycleOwner) {
            products = if (it is DataWrapper.Success) {
                it.data
            } else {
                listOf()
            }
        }
    }
}