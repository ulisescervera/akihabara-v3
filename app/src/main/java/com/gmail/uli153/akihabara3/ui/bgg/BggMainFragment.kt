package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentBggMainBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.google.android.material.tabs.TabLayoutMediator

class BggMainFragment: AkbFragment<FragmentBggMainBinding>() {

    private var mediator: TabLayoutMediator? = null

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentBggMainBinding {
        return FragmentBggMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = BggPagerAdapter(this)
        binding.pager.isUserInputEnabled = false
        binding.pager.adapter = adapter
        mediator = TabLayoutMediator(binding.tabs, binding.pager) { tab, position ->
            tab.text = adapter.getTitle(position)
        }
        mediator?.attach()
    }

    override fun onDestroyView() {
        binding.pager.adapter = null
        mediator?.detach()
        mediator = null
        super.onDestroyView()
    }

    private class BggPagerAdapter(act: Fragment): FragmentStateAdapter(act) {

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
