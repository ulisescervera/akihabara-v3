package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.FragmentBggSearchBinding
import com.gmail.uli153.akihabara3.databinding.RowBggItemBinding
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.bottomsheets.InfoBottomSheet
import com.gmail.uli153.akihabara3.ui.bottomsheets.SearchFilterBottomSheet
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.SnackBarManager
import com.gmail.uli153.akihabara3.utils.extensions.launchMain
import com.gmail.uli153.akihabara3.utils.extensions.repeatOnStart
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import kotlinx.android.synthetic.main.row_bgg_item.view.*
import kotlinx.coroutines.flow.collectLatest

class BggSearchFragment: AkbFragment() {

    private var _binding: FragmentBggSearchBinding? = null
    private val binding: FragmentBggSearchBinding get() = _binding!!

    private val bggViewModel: BggViewModel by activityViewModels()

    private lateinit var snackBarManager: SnackBarManager

    private val adapter: Adapter by lazy {
        Adapter().apply {
            registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
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
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBggSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        snackBarManager = SnackBarManager(requireContext(), binding.root, lifecycleScope)
        binding.btnCancel.setSafeClickListener {
            binding.editSearch.setText("")
        }

        binding.editSearch.setText(bggViewModel.query)
        binding.editSearch.addTextChangedListener(afterTextChanged = {
            val query = it?.toString() ?: ""
            binding.btnCancel.isGone = query.isBlank()
            bggViewModel.search(query)
        })

        binding.btnFilters.setSafeClickListener {
            SearchFilterBottomSheet.show(parentFragmentManager)
        }

        binding.recyclerviewBgg.adapter = adapter
        binding.recyclerviewBgg.layoutManager = LinearLayoutManager(requireContext())

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
                    snackBarManager.showErrorSnackbar(getCustomErrorMessage(it.error)) {
                        bggViewModel.hideSearchError()
                    }
                }
            }
        }
    }

    private inner class SearchResultVH(val binding: RowBggItemBinding): RecyclerView.ViewHolder(binding.root) {

        private lateinit var item: BggSearchItem
        init {
            binding.root.setSafeClickListener {
                bggViewModel.fetchAndSelectBggItemById(item.id)
                navigate(BggMainFragmentDirections.actionBggDetail())
            }
        }

        fun set(item: BggSearchItem) {
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

    private inner class Adapter: ListAdapter<BggSearchItem, SearchResultVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
            return SearchResultVH(RowBggItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
        }

        override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
            val item = getItem(position) ?: return
            holder.set(item)
        }
    }

    private inner class PagedAdapter: PagingDataAdapter<BggSearchItem, SearchResultVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
            return SearchResultVH(RowBggItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
        }

        override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
            val item = getItem(position) ?: return
            holder.set(item)
        }
    }
}
