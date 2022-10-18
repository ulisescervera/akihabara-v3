package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.gmail.uli153.akihabara3.R
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
        bggViewModel.selectedBggItem.observe(viewLifecycleOwner) { wrapper ->
            when (wrapper) {
                is DataWrapper.Loading -> {
                    startShimmer()
                }
                is DataWrapper.Error -> {
                    stopShimmer()
                    //todo
                }
                is DataWrapper.Success -> {
                    stopShimmer()
                    val item = wrapper.data
                    Glide.with(requireContext()).load(item.image).into(binding.imageview)
                    binding.labelTitle.text = item.nameAndYear
                    binding.labelRank.text = item.ranks?.firstOrNull()?.position?.let { "$it"}
                }
            }
        }
    }

    private fun startShimmer() {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.3f)
            .build()
        binding.root.setShimmer(shimmer)
        binding.root.startShimmer()
        binding.imageview.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelTitle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelRank.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.viewRank.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
    }

    private fun stopShimmer() {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(1f)
            .build()
        binding.root.setShimmer(shimmer)
        binding.root.stopShimmer()
        binding.imageview.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelTitle.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelRank.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.viewRank.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
    }
}
