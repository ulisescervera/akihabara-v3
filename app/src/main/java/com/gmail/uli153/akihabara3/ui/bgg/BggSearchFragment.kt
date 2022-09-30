package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.FragmentBggSearchBinding
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.setSafeClickListener
import kotlinx.android.synthetic.main.row_bgg_item.view.*

class BggSearchFragment: AkbFragment() {

    private lateinit var binding: FragmentBggSearchBinding

    private val bggViewModel: BggViewModel by activityViewModels()

    private val result: MutableList<BggSearchItem> = mutableListOf()

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
        bggViewModel.searchResult.observe(viewLifecycleOwner) {
            result.clear()
            result.addAll(it)
            adapter.notifyDataSetChanged()
        }
    }

    private inner class SearchResultVH(view: View): RecyclerView.ViewHolder(view) {
        fun set(item: BggSearchItem) {
            Glide.with(itemView.image).load(item.image).into(itemView.image)
            itemView.label_name.text = item.names.firstOrNull()?.value ?: ""
        }
    }

    private inner class Adapter: RecyclerView.Adapter<SearchResultVH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultVH {
            return SearchResultVH(LayoutInflater.from(requireContext()).inflate(R.layout.row_bgg_item, parent, false))
        }

        override fun onBindViewHolder(holder: SearchResultVH, position: Int) {
            holder.set(result[position])
        }

        override fun getItemCount(): Int {
            return result.size
        }
    }
}
