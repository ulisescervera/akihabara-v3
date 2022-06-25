package com.gmail.uli153.akihabara3.ui.products.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentProductBaseFormBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel

abstract class ProductFormBaseFragment: AkbFragment() {

    protected lateinit var binding: FragmentProductBaseFormBinding
    protected val productsViewModel: ProductsViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProductBaseFormBinding.inflate(inflater, container, false)
        return binding.root
    }

}