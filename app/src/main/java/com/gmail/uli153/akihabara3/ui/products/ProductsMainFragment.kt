package com.gmail.uli153.akihabara3.ui.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentProductsBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.ProductsViewModel
import com.gmail.uli153.akihabara3.utils.AkbNumberParser
import com.gmail.uli153.akihabara3.utils.extensions.getColorFromAttr
import com.google.android.material.tabs.TabLayoutMediator
import java.math.BigDecimal

class ProductsFragment: AkbFragment<FragmentProductsBinding>() {

    private val productsViewModel: ProductsViewModel by activityViewModels()

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentProductsBinding {
        return FragmentProductsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductsPagerAdapter(requireActivity())

        binding.pager.isUserInputEnabled = false
        binding.pager.adapter = adapter
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()

        productsViewModel.balance.observe(viewLifecycleOwner) {
            val color = if (it >= BigDecimal(0))
                requireContext().getColorFromAttr(R.attr.greenText)
            else
                ContextCompat.getColor(requireContext(), R.color.red)
            binding.labelBalanceValue.text = AkbNumberParser.LocaleParser.parseToEur(it)
            binding.labelBalanceValue.setTextColor(color)
        }
    }

    private inner class ProductsPagerAdapter(act: FragmentActivity): FragmentStateAdapter(act) {

        private val tabsTitles = listOf(
            act.getString(R.string.drinks),
            act.getString(R.string.foods)
        )

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) DrinksFragment() else FoodsFragment()
        }

        fun getTitle(position: Int): String {
            return tabsTitles[position]
        }
    }
}