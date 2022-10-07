package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.BottomSheetSearchFilterBinding
import com.gmail.uli153.akihabara3.ui.viewmodels.BggViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SearchFilterBottomSheet private constructor(): BottomSheetDialogFragment() {

    companion object {
        fun show(manager: FragmentManager) {
            BottomSheetDialogFragment().show(manager, "SearchFilterBottomSheet")
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    private lateinit var binding: BottomSheetSearchFilterBinding

    private val bggViewModel: BggViewModel by viewModels()

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetSearchFilterBinding.inflate(layoutInflater, container, false)
        return binding.root
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
        }
        binding.checkBoardgame.setOnCheckedChangeListener { compoundButton, b -> bggViewModel.setFilterBoardgame(b) }
    }
}
