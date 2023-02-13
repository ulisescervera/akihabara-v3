package com.gmail.uli153.akihabara3.ui.bgg

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.*
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.CellBggItemBinding
import com.gmail.uli153.akihabara3.databinding.FragmentBggSearchBinding
import com.gmail.uli153.akihabara3.databinding.RowBggItemBinding
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheets.SearchFilterBottomSheet
import com.gmail.uli153.akihabara3.ui.dialogs.RecordingDialog
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.SnackBarManager
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.gmail.uli153.akihabara3.utils.extensions.toPx

class BggSearchFragment: AkbFragment<FragmentBggSearchBinding>() {

    private val SPAN_COUNT = 3

    override fun inflateView(inflater: LayoutInflater, container: ViewGroup?): FragmentBggSearchBinding {
        return FragmentBggSearchBinding.inflate(inflater, container, false)
    }

    private val bggViewModel: BggViewModel by activityViewModels()

    private var snackBarManager: SnackBarManager? = null

    private val dataObserver = object: RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            binding.recyclerviewBgg.scrollToPosition(0)
        }
    }

    private val adapter: Adapter by lazy {
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
        binding.btnCancel.setSafeClickListener {
            val query = binding.editSearch.text.toString()
            if (query.isNotBlank()) {
                binding.editSearch.setText("")
            } else {
                showRecordingAlert()
            }
        }

        var isTextBlank = binding.editSearch.text.toString().isBlank()
        binding.editSearch.addTextChangedListener(afterTextChanged = {
            val query = it?.toString() ?: ""
            val isNewTextBlank = query.isBlank()

            if (isTextBlank != isNewTextBlank) {
                isTextBlank = isNewTextBlank
                val iconRes = if (isNewTextBlank) android.R.drawable.presence_audio_online else R.drawable.ic_close
                val icon = ContextCompat.getDrawable(requireContext(), iconRes)
                binding.btnCancel.setImageDrawable(icon)
            }

            bggViewModel.search(query)
        })
        binding.editSearch.setText(bggViewModel.query)

        binding.btnFilters.setSafeClickListener {
            SearchFilterBottomSheet.show(parentFragmentManager)
        }

        adapter.registerAdapterDataObserver(dataObserver)
        binding.recyclerviewBgg.adapter = adapter
        binding.recyclerviewBgg.layoutManager = GridLayoutManager(requireContext(), 1)

        bggViewModel.gridMode.observe(viewLifecycleOwner) { grid ->
            val manager = binding.recyclerviewBgg.layoutManager as GridLayoutManager
            adapter.gridMode = grid
            manager.spanCount = if (grid) SPAN_COUNT else 1
            binding.recyclerviewBgg.adapter = adapter
            if (grid) {
                if (binding.recyclerviewBgg.itemDecorationCount == 0) {
                    binding.recyclerviewBgg.addItemDecoration(cellSeparatorVertical)
                    binding.recyclerviewBgg.addItemDecoration(cellSeparatorHorizontal)
                }
            } else {
                binding.recyclerviewBgg.removeItemDecoration(cellSeparatorVertical)
                binding.recyclerviewBgg.removeItemDecoration(cellSeparatorHorizontal)
            }
        }

        bggViewModel.searchResult.observe(viewLifecycleOwner) {
            binding.progressIndicator.isGone = it !is DataWrapper.Loading
            when(it) {
                is DataWrapper.Success -> {
                    adapter.submitList(it.data)
                }
                is DataWrapper.Loading -> {
                    adapter.submitList(emptyList())
                }
                is DataWrapper.Error -> {
                    adapter.submitList(emptyList())
                    snackBarManager?.showRetrySnackbar(getCustomErrorMessage(it.error),
                        retryListener = {
                            bggViewModel.hideSearchError()
                            val query = binding.editSearch.text.toString()
                            bggViewModel.search("")
                            bggViewModel.search(query)
                    }, dismissListener = {
                        // NOOP
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        adapter.unregisterAdapterDataObserver(dataObserver)
        binding.recyclerviewBgg.adapter = null
        snackBarManager = null
        super.onDestroyView()
    }

    private fun showRecordingAlert() {
        if (isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
            RecordingDialog() {
                bggViewModel.search(it ?: "")
            }
        } else {
            requestRecordPermissions()
        }
    }

    private fun requestRecordPermissions() {
        //TODO permissions
    }

    private abstract inner class SearchResultVH(view: View): RecyclerView.ViewHolder(view) {
        abstract fun set(item: BggSearchItem)
    }

    private inner class CellSearchResultVH(val binding: CellBggItemBinding): SearchResultVH(binding.root) {
        private lateinit var item: BggSearchItem
        init {
            binding.root.setSafeClickListener {
                bggViewModel.fetchAndSelectBggItemById(item.id)
                navigate(BggMainFragmentDirections.actionBggDetail())
            }
        }

        override fun set(item: BggSearchItem) {
            this.item = item
            Glide.with(binding.imageBggHotness).load(item.thumbnail).into(binding.imageBggHotness)
        }
    }

    private inner class RowSearchResultVH(val binding: RowBggItemBinding): SearchResultVH(binding.root) {

        private lateinit var item: BggSearchItem
        init {
            binding.root.setSafeClickListener {
                bggViewModel.fetchAndSelectBggItemById(item.id)
                navigate(BggMainFragmentDirections.actionBggDetail())
            }
        }

        override fun set(item: BggSearchItem) {
            this.item = item
            val rank = item.ranks?.firstOrNull()?.position?.let { "$it" } ?: "N/A"
            val image = item.thumbnail ?: item.image
            val name = item.names.firstOrNull()?.value
            val year = item.yearPublished?.let { "$it" } ?: ""
            val rating = item.rating?.takeIf { it > 0 }?.let { getString(R.string.rating, it) } ?: ""
            val geekRating = item.geekRating?.takeIf { it > 0 }?.let { getString(R.string.geek_rating, it) } ?: ""

            binding.labelRank.text = rank
            Glide.with(binding.image).load(image).into(binding.image)
            binding.labelName.text = name?.let { "$it ($year)" } ?: ""
            binding.labelRating.text = rating
            binding.labelGeekRating.text = geekRating
        }
    }

    private inner class DiffCallback : DiffUtil.ItemCallback<BggSearchItem>() {
        override fun areItemsTheSame(oldItem: BggSearchItem, newItem: BggSearchItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BggSearchItem, newItem: BggSearchItem): Boolean {
            return oldItem == newItem
        }
    }

    private inner class Adapter(var gridMode: Boolean): ListAdapter<BggSearchItem, SearchResultVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
            return if (gridMode) {
                CellSearchResultVH(CellBggItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
            } else {
                RowSearchResultVH(RowBggItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
            }
        }

        override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
            val item = getItem(position) ?: return
            holder.set(item)
        }
    }
}
