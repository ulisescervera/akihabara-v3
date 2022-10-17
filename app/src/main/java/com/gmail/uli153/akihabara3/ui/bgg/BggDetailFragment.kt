package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.FragmentBggDetailBinding
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel

class BggDetailFragment: AkbFragment() {

    private var _binding: FragmentBggDetailBinding? = null
    private val binding: FragmentBggDetailBinding get() = _binding!!

    private val bggViewModel: BggViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBggDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.shimmer.root.startShimmer()
        bggViewModel.selectedBggItem.observe(viewLifecycleOwner) { wrapper ->
            binding.shimmer.root.isGone = wrapper !is DataWrapper.Loading
            when (wrapper) {
                is DataWrapper.Loading -> {
                    binding.shimmer.root.startShimmer()
                }
                is DataWrapper.Error -> {
                    //todo
                }
                is DataWrapper.Success -> {
                    val item = wrapper.data
                    Glide.with(requireContext()).load(item.image).into(binding.imageview)
                    binding.labelTitle.text = item.nameAndYear
                    binding.labelRank.text = item.ranks?.firstOrNull()?.position?.let { "$it"}
                }
            }
        }
    }
}
