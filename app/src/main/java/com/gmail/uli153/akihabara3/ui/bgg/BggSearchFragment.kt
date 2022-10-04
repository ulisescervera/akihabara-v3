package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentBggSearchBinding
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.DataWrapper
import com.gmail.uli153.akihabara3.utils.extensions.repeatOnStart
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import kotlinx.android.synthetic.main.row_bgg_item.view.*
import kotlinx.coroutines.flow.collectLatest

class BggSearchFragment: AkbFragment() {

    private lateinit var binding: FragmentBggSearchBinding

    private val bggViewModel: BggViewModel by activityViewModels()

    private val adapter: Adapter by lazy {
        Adapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentBggSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSearch.setSafeClickListener {
            val query = binding.editSearch.text.toString().takeIf { it.isNotBlank() } ?: return@setSafeClickListener
            bggViewModel.search(query)
        }
        binding.recyclerviewBgg.adapter = adapter
        binding.recyclerviewBgg.layoutManager = LinearLayoutManager(requireContext())
        
        repeatOnStart {
            bggViewModel.pagedSearch.collectLatest {
                adapter.submitData(it)
            }
            adapter.loadStateFlow.collectLatest {
                //todo
            }
        }

        binding.editSearch.setText("catan")
    }

    private inner class SearchResultVH(view: View): RecyclerView.ViewHolder(view) {
        fun set(item: BggSearchItem) {
            Glide.with(itemView.image).load(item.image).into(itemView.image)
            itemView.label_name.text = item.names.firstOrNull()?.value ?: ""
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

    private inner class Adapter: PagingDataAdapter<BggSearchItem, SearchResultVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
            return SearchResultVH(LayoutInflater.from(requireContext()).inflate(R.layout.row_bgg_item, parent, false))
        }

        override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
            val item = getItem(position) ?: return
            holder.set(item)
        }
    }
}
