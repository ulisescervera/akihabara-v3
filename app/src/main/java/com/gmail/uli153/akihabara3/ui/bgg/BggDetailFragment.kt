package com.gmail.uli153.akihabara3.ui.bgg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.facebook.shimmer.Shimmer
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.databinding.FragmentBggDetailBinding
import com.gmail.uli153.akihabara3.databinding.TextviewGridlayoutBinding
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.models.BoardgameLink
import com.gmail.uli153.akihabara3.domain.models.PollType
import com.gmail.uli153.akihabara3.ui.AkbFragment
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.gmail.uli153.akihabara3.utils.extensions.bestPlayerAge
import com.gmail.uli153.akihabara3.utils.extensions.bestPlayerNumber
import com.gmail.uli153.akihabara3.utils.extensions.languageDependency
import com.gmail.uli153.akihabara3.utils.extensions.toPx

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
                    setupItem(wrapper.data)
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
        binding.labelGeekRating.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelRating.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelWeight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelPlayers.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelPlayingTime.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelAge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
        binding.labelLaguangeDependencyHeader.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.bg_shimmer))
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
        binding.labelGeekRating.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelRating.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelWeight.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelPlayers.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelPlayingTime.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelAge.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        binding.labelLaguangeDependencyHeader.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.transparent))
    }

    private fun setupItem(item: BggSearchItem) {
        with(item) {
            Glide.with(requireContext()).load(image).into(binding.imageview)
            binding.labelTitle.text = nameAndYear
            binding.labelRank.text = ranks?.firstOrNull()?.position?.let { "$it"}

            binding.labelGeekRating.isGone = geekRating == null
            if (geekRating != null) {
                binding.labelGeekRating.text = getString(R.string.geek_rating, geekRating)
            }

            binding.labelRating.isGone = rating == null
            if (rating != null) {
                binding.labelRating.text = getString(R.string.rating, rating)
            }

            binding.labelWeight.isGone = weight == null
            if (weight != null) {
                binding.labelWeight.text = getString(R.string.weight, weight)
            }

            val communityPlayers = polls.bestPlayerNumber
            binding.labelPlayers.isGone = minPlayers == null || maxPlayers == null
            binding.labelCommunityPlayers.isGone = binding.labelPlayers.isGone || communityPlayers.isNullOrBlank()
            if (minPlayers != null && maxPlayers != null) {
                binding.labelPlayers.text = getString(R.string.players, minPlayers, maxPlayers)
                if (communityPlayers.isNullOrBlank().not()) {
                    binding.labelCommunityPlayers.text = getString(R.string.community_value, communityPlayers)
                }
            }

            binding.labelPlayingTime.isGone = playingTime == null
            if (playingTime != null) {
                binding.labelPlayingTime.text = getString(R.string.playing_time, playingTime)
            }

            val communityAge = polls.bestPlayerAge
            binding.labelAge.isGone = minAge == null
            binding.labelCommunityAge.isGone = binding.labelAge.isGone || communityAge.isNullOrBlank()
            if (minAge != null) {
                binding.labelAge.text = getString(R.string.playing_age, minAge)
                if (communityAge.isNullOrBlank().not()) {
                    binding.labelCommunityAge.text = getString(R.string.community_value, communityAge)
                }
            }

            val languageDependency = polls.languageDependency
            binding.labelLaguangeDependencyHeader.isGone = languageDependency.isNullOrBlank()
            binding.labelLaguangeDependency.isGone = languageDependency.isNullOrBlank()
            binding.labelLaguangeDependency.text = languageDependency
            if (languageDependency.isNullOrBlank().not()) {
                binding.labelLaguangeDependencyHeader.text = getString(R.string.language_dependency)
            }

            val inflater = LayoutInflater.from(requireContext())

            categories.takeIf { it.size > 0 }?.let {
                val header = inflater.inflate(R.layout.textview_header_gridlayout, binding.gridlayout, false) as TextView
                header.text = getString(R.string.caterogies)
                binding.gridlayout.addView(header)
                it.forEach {
                    binding.gridlayout.addView(it.toView(inflater))
                }
            }

            mechanics.takeIf { it.size > 0 }?.let {
                val header = inflater.inflate(R.layout.textview_header_gridlayout, binding.gridlayout, false) as TextView
                header.text = getString(R.string.mechanics)
                binding.gridlayout.addView(header)
                it.forEach {
                    binding.gridlayout.addView(it.toView(inflater))
                }
            }

            val labelDescription = inflater.inflate(R.layout.textview_header_gridlayout, binding.gridlayout, false) as TextView
            labelDescription.text = description
            binding.gridlayout.addView(labelDescription)
        }
    }

    private fun BoardgameLink.toView(inflater: LayoutInflater): View {
        val binding = TextviewGridlayoutBinding.inflate(inflater, binding.gridlayout, false)
        binding.labelValue.text = name
        return binding.root
    }

}
