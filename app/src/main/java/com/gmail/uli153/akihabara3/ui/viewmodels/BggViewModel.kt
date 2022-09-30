package com.gmail.uli153.akihabara3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.SearchBggUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BggViewModel @Inject constructor(
    private val searchUseCase: SearchBggUseCase
): ViewModel() {

    private val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("AKB", exception.stackTraceToString())
            throw exception
        }
    }

    private val _searchResult: LiveData<List<BggSearchItem>> = MutableLiveData()
    val searchResult: LiveData<List<BggSearchItem>> = _searchResult

    fun search(query: String) {
        viewModelScope.launch(exceptionHandler) {
            searchUseCase(query)
        }
    }
}
