package com.gmail.uli153.akihabara3.ui.bottomsheets

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gmail.uli153.akihabara3.R
import com.gmail.uli153.akihabara3.databinding.BottomSheetImagesBinding
import com.gmail.uli153.akihabara3.databinding.CellImageBinding
import com.gmail.uli153.akihabara3.utils.extensions.setSafeClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ImagesBottomSheet(private val listener: ImageSelectedListener): BottomSheetDialogFragment() {

    interface ImageSelectedListener {
        fun onImageSelected(imageRes: Int?)
        fun showCamera()
        fun showGallery()
    }

    companion object {
        private const val SPAN_COUNT = 6

        fun show(manager: FragmentManager, listener: ImageSelectedListener) {
            ImagesBottomSheet(listener).show(manager, "ImagesBottomSheet")
        }
    }

    private lateinit var behavior: BottomSheetBehavior<View>

    private val adapter by lazy {
        Adapter()
    }

    private val drinksImages: List<Int> = listOf(
        R.drawable.ic_res_drink1,
        R.drawable.ic_res_drink2,
        R.drawable.ic_res_drink3,
        R.drawable.ic_res_drink4,
        R.drawable.ic_res_drink5,
        R.drawable.ic_res_drink6,
        R.drawable.ic_res_drink7,
        R.drawable.ic_res_drink8,
        R.drawable.ic_res_drink9,
        R.drawable.ic_res_drink10,
        R.drawable.ic_res_drink11,
        R.drawable.ic_res_drink12,
        R.drawable.ic_res_drink13,
        R.drawable.ic_res_drink14,
        R.drawable.ic_res_drink15,
    )
    private val foodsImages: List<Int> = listOf(
//        R.drawable.ic_res_food1,
        R.drawable.ic_res_food2,
//        R.drawable.ic_res_food3,
        R.drawable.ic_res_food4,
//        R.drawable.ic_res_food5,
        R.drawable.ic_res_food6,
        R.drawable.ic_res_food7,
        R.drawable.ic_res_food8,
        R.drawable.ic_res_food9,
//        R.drawable.ic_res_food10,
        R.drawable.ic_res_food11,
        R.drawable.ic_res_food12,
        R.drawable.ic_res_food13,
        R.drawable.ic_res_food14,
//        R.drawable.ic_res_food15,
//        R.drawable.ic_res_food16,
        R.drawable.ic_res_food17,
        R.drawable.ic_res_food18,
//        R.drawable.ic_res_food19,
        R.drawable.ic_res_food20,
//        R.drawable.ic_res_food21,
        R.drawable.ic_res_food22,
//        R.drawable.ic_res_food23,
//        R.drawable.ic_res_food24,
//        R.drawable.ic_res_food25,
//        R.drawable.ic_res_food26,
//        R.drawable.ic_res_food27,
        R.drawable.ic_res_food28
    )

    private val images: List<Int> by lazy {
        mutableListOf<Int>().apply {
            for (i in 0 until Math.max(foodsImages.size, drinksImages.size) step SPAN_COUNT/2) {
                for (f in 0 until SPAN_COUNT/2) add(foodsImages.getOrElse(i + f){ 0 })
                for (d in 0 until SPAN_COUNT/2) add(drinksImages.getOrElse(i + d){ 0 })
            }
        }
    }

    override fun getTheme(): Int {
        return R.style.AkbBottomSheetDialog
    }

    private var _binding: BottomSheetImagesBinding? = null
    private val binding: BottomSheetImagesBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        binding.recyclerviewImages.adapter = null
        _binding = null
        super.onDestroyView()
    }

    override fun onStart() {
        super.onStart()
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        behavior = BottomSheetBehavior.from(view.parent as View)
//        behavior.skipCollapsed = true
        binding.recyclerviewImages.layoutManager = GridLayoutManager(requireContext(), SPAN_COUNT)
        binding.recyclerviewImages.adapter = adapter
        binding.labelCamera.setSafeClickListener {
            listener.showCamera()
            dismiss()
        }
        binding.labelGallery.setSafeClickListener {
            listener.showGallery()
            dismiss()
        }
    }

    private inner class ImageVH(private val binding: CellImageBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setSafeClickListener {
                listener.onImageSelected(image)
                dismiss()
            }
        }
        private var image: Int = 0
        fun setImage(image: Int) {
            this.image = image
            Glide.with(binding.imageview).load(images[position]).into(binding.imageview)
        }
    }

    private inner class Adapter: RecyclerView.Adapter<ImageVH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
            return ImageVH(CellImageBinding.inflate(LayoutInflater.from(requireContext()), parent, false))
        }

        override fun onBindViewHolder(holder: ImageVH, position: Int) {
            holder.setImage(images[position])
        }

        override fun getItemCount(): Int {
            return images.size
        }
    }
}
