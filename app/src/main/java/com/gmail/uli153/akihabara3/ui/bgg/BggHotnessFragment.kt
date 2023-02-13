package com.gmail.uli153.akihabara3.ui.bgg

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.CellBggItemBinding
import com.gmail.uli153.akihabara3.databinding.FragmentBggHotBinding
import com.gmail.uli153.akihabara3.databinding.RowBggHotnessBinding
import com.gmail.uli153.akihabara3.domain.models.BggHotItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.SnackBarManager
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.extensions.toPx

class BggHotnessFragment: AkbFragment<FragmentBggHotBinding>() {

    private val SPAN_COUNT = 3

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentBggHotBinding {
        return FragmentBggHotBinding.inflate(inflater, container, false)
    }

    private val bggViewModel: BggViewModel by activityViewModels()

    private var snackBarManager: SnackBarManager? = null

    private val adapter by lazy {
        Adapter(false)
    }

    private val cellSeparatorVertical: DividerItemDecoration by lazy {
        val separator = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        val colors = intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
        val drawable = GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors)
        drawable.setSize(1, 8.toPx.toInt())
        separator.setDrawable(drawable)
        separator
    }

    private val cellSeparatorHorizontal: DividerItemDecoration by lazy {
        val separator = DividerItemDecoration(requireContext(), DividerItemDecoration.HORIZONTAL)
        val colors = intArrayOf(Color.TRANSPARENT, Color.TRANSPARENT)
        val drawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)
        drawable.setSize(8.toPx.toInt(), 1)
        separator.setDrawable(drawable)
        separator
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        snackBarManager = SnackBarManager(requireContext(), binding.root, lifecycleScope)
        binding.recyclerviewHot.adapter = adapter
        binding.recyclerviewHot.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.swipeRefresh.setOnRefreshListener {
            bggViewModel.fetchHotness()
        }

        bggViewModel.gridMode.observe(viewLifecycleOwner) { grid ->
            val manager = binding.recyclerviewHot.layoutManager as GridLayoutManager
            adapter.gridMode = grid
            if (grid) {
                if (binding.recyclerviewHot.itemDecorationCount == 0) {
                    binding.recyclerviewHot.addItemDecoration(cellSeparatorVertical)
                    binding.recyclerviewHot.addItemDecoration(cellSeparatorHorizontal)
                }
            } else {
                binding.recyclerviewHot.removeItemDecoration(cellSeparatorVertical)
                binding.recyclerviewHot.removeItemDecoration(cellSeparatorHorizontal)
            }
            manager.spanCount = if (grid) SPAN_COUNT else 1
            binding.recyclerviewHot.adapter = adapter

        }

        bggViewModel.hotness.observe(viewLifecycleOwner) {
            when(it) {
                is DataWrapper.Success -> {
                    adapter.submitList(it.data)
                    binding.swipeRefresh.isRefreshing = false
                }
                is DataWrapper.Loading -> {
                    if (binding.swipeRefresh.isRefreshing.not()) {
                        binding.swipeRefresh.isRefreshing = true
                    }
                    adapter.submitList(listOf())
                }
                is DataWrapper.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(listOf())
                    snackBarManager?.showErrorSnackbar(getCustomErrorMessage(it.error)) {
                        bggViewModel.hideHotnessError()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        snackBarManager = null
        binding.recyclerviewHot.adapter = null
        super.onDestroyView()
    }

    private class DiffCallback: DiffUtil.ItemCallback<BggHotItem>() {
        override fun areItemsTheSame(oldItem: BggHotItem, newItem: BggHotItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BggHotItem, newItem: BggHotItem): Boolean {
            return oldItem == newItem
        }
    }

    private abstract inner class BggHotVH(view: View): RecyclerView.ViewHolder(view) {
        abstract fun setup(item: BggHotItem)
    }

    private inner class BggHotnessRowVH(val binding: CellBggItemBinding): BggHotVH(binding.root) {

        private lateinit var item: BggHotItem
        init {
            binding.root.setSafeClickListener {
                bggViewModel.fetchAndSelectBggItemById(item.id)
                navigate(BggMainFragmentDirections.actionBggDetail())
            }
        }
        override fun setup(item: BggHotItem) {
            this.item = item
            Glide.with(binding.imageBggHotness).load(item.thumbnail).into(binding.imageBggHotness)
        }
    }

    private inner class BggHotnessCellVH(val binding: RowBggHotnessBinding): BggHotVH(binding.root) {

        private lateinit var item: BggHotItem
        init {
            binding.root.setSafeClickListener {
                bggViewModel.fetchAndSelectBggItemById(item.id)
                navigate(BggMainFragmentDirections.actionBggDetail())
            }
        }

        override fun setup(item: BggHotItem) {
            this.item = item
            Glide.with(binding.image).load(item.thumbnail).into(binding.image)
            binding.labelRank.text = getString(R.string.rank, item.rank)
            binding.labelName.text = item.name
            binding.labelYear.text = item.yearPublished?.let { "$it" }
        }
    }

    private inner class Adapter(var gridMode: Boolean): ListAdapter<BggHotItem, BggHotVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BggHotVH {
            return if (gridMode) {
                BggHotnessRowVH(CellBggItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            } else {
                BggHotnessCellVH(RowBggHotnessBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }

        override fun onBindViewHolder(holder: BggHotVH, position: Int) {
            currentList[position]
            holder.setup(getItem(position))
        }
    }


}
