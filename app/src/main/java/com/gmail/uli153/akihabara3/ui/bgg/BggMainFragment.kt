package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentBggMainBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_products.*

class BggMainFragment: AkbFragment() {

    private var _binding: FragmentBggMainBinding? = null
    private val binding: FragmentBggMainBinding get() = _binding!!

    private val adapter by lazy {
        BggPagerAdapter(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBggMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pager.isUserInputEnabled = false
        pager.adapter = BggPagerAdapter(requireActivity())
        TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }

    private inner class BggPagerAdapter(act: FragmentActivity): FragmentStateAdapter(act) {

        private val tabsTitles = listOf(
            act.getString(R.string.the_hotness),
            act.getString(R.string.search)
        )

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) BggHotnessFragment() else BggSearchFragment()
        }

        fun getTitle(position: Int): String {
            return tabsTitles[position]
        }
    }
}
