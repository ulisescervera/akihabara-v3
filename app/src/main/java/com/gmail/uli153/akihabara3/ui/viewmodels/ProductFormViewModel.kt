package com.gmail.uli153.akihabara3.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.File

class ProductFormViewModel: ViewModel() {

    private val _productFormImage: MutableLiveData<Any?> = MutableLiveData(null)
    val productFormImage: LiveData<Any?> = _productFormImage

    private val _galleryImageFile: MutableLiveData<File?> = MutableLiveData(null)
    val galleryImageFile: LiveData<File?> = _galleryImageFile

    fun setProductFormImage(image: Any?) {
        _productFormImage.value = image
    }

    fun setGalleryImage(file: File?) {
        _galleryImageFile.value = file
    }
}