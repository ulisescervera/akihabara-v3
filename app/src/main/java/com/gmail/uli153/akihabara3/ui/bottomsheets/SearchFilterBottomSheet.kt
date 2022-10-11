package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.BottomSheetSearchFilterBinding
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchFilterBottomSheet private constructor(): BottomSheetDialogFragment() {

    companion object {
        fun show(manager: FragmentManager) {
            SearchFilterBottomSheet().show(manager, "SearchFilterBottomSheet")
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    private var _binding: BottomSheetSearchFilterBinding? = null
    private val binding: BottomSheetSearchFilterBinding get() = _binding!!

    private val bggViewModel: BggViewModel by activityViewModels()

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetSearchFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)

        bggViewModel.filterBoardgame.observe(viewLifecycleOwner) {
            binding.checkBoardgame.isChecked = it
            val clickable = !it || bggViewModel.types.size > 1
            binding.checkBoardgame.isClickable = clickable
        }

        bggViewModel.filterBoardgameExpansion.observe(viewLifecycleOwner) {
            binding.checkBoardgameExpansion.isChecked = it
            val clickable = !it || bggViewModel.types.size > 1
            binding.checkBoardgameExpansion.isClickable = clickable
        }

        bggViewModel.filterBoardgameAccessory.observe(viewLifecycleOwner) {
            binding.checkBoardgameAccessory.isChecked = it
            val clickable = !it || bggViewModel.types.size > 1
            binding.checkBoardgameAccessory.isClickable = clickable
        }

        bggViewModel.filterVideogame.observe(viewLifecycleOwner) {
            binding.checkVideogame.isChecked = it
            val clickable = !it || bggViewModel.types.size > 1
            binding.checkVideogame.isClickable = clickable
        }

        binding.checkBoardgame.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgame(b) }
        binding.checkBoardgameExpansion.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgameExpansion(b) }
        binding.checkBoardgameAccessory.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgameAccessory(b) }
        binding.checkVideogame.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterVideogame(b) }
    }

}
