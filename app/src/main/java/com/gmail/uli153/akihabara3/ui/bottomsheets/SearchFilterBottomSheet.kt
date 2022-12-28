package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
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

        val updateClickableChecks = {
            binding.checkBoardgame.isClickable = !(bggViewModel.filterBoardgame.value ?: false) || bggViewModel.types.size > 1
            binding.checkBoardgameExpansion.isClickable = !(bggViewModel.filterBoardgameExpansion.value ?: false) || bggViewModel.types.size > 1
            binding.checkBoardgameAccessory.isClickable = !(bggViewModel.filterBoardgameAccessory.value ?: false) || bggViewModel.types.size > 1
            binding.checkVideogame.isClickable = !(bggViewModel.filterVideogame.value ?: false) || bggViewModel.types.size > 1
        }

        bggViewModel.filterBoardgame.observe(viewLifecycleOwner) {
            binding.checkBoardgame.isChecked = it
            updateClickableChecks()
        }

        bggViewModel.filterBoardgameExpansion.observe(viewLifecycleOwner) {
            binding.checkBoardgameExpansion.isChecked = it
            updateClickableChecks()
        }

        bggViewModel.filterBoardgameAccessory.observe(viewLifecycleOwner) {
            binding.checkBoardgameAccessory.isChecked = it
            updateClickableChecks()
        }

        bggViewModel.filterVideogame.observe(viewLifecycleOwner) {
            binding.checkVideogame.isChecked = it
            updateClickableChecks()
        }

        binding.checkBoardgame.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgame(b) }
        binding.checkBoardgameExpansion.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgameExpansion(b) }
        binding.checkBoardgameAccessory.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterBoardgameAccessory(b) }
        binding.checkVideogame.setOnCheckedChangeListener { _, b -> bggViewModel.setFilterVideogame(b) }
    }

}
