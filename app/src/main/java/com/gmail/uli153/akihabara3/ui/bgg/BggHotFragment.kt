package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.FragmentBggHotBinding
import com.gmail.uli153.akihabara3.databinding.RowBggHotBinding
import com.gmail.uli153.akihabara3.domain.models.BggHotItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel

class BggHotFragment: AkbFragment() {

    private var _binding: FragmentBggHotBinding? = null
    private val binding: FragmentBggHotBinding get() = _binding!!

    private val bggViewModel: BggViewModel by activityViewModels()

    private val adapter by lazy {
        Adapter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBggHotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerviewHot.adapter = adapter
        binding.recyclerviewHot.layoutManager = LinearLayoutManager(requireContext())
        bggViewModel.hotest.observe(viewLifecycleOwner) {
            when(it) {
                is DataWrapper.Success -> adapter.submitList(it.data)
                is DataWrapper.Loading -> {
                    adapter.submitList(listOf())
                } //todo
                is DataWrapper.Error -> {
                    adapter.submitList(listOf())
                } //todo
            }
        }
    }

    private class DiffCallback: DiffUtil.ItemCallback<BggHotItem>() {
        override fun areItemsTheSame(oldItem: BggHotItem, newItem: BggHotItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BggHotItem, newItem: BggHotItem): Boolean {
            return oldItem == newItem
        }
    }

    private inner class BggHotVH(val binding: RowBggHotBinding): RecyclerView.ViewHolder(binding.root) {
        fun setup(item: BggHotItem) {
            Glide.with(binding.image).load(item.thumbnail).into(binding.image)
            binding.labelRank.text = getString(R.string.rank, item.rank)
            binding.labelName.text = item.name
            binding.labelYear.text = item.yearPublished?.let { "$it" }
        }
    }

    private inner class Adapter: ListAdapter<BggHotItem, BggHotVH>(DiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BggHotVH {
            return BggHotVH(RowBggHotBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
        }

        override fun onBindViewHolder(holder: BggHotVH, position: Int) {
            holder.setup(getItem(position))
        }
    }
}
