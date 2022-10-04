package com.gmail.uli153.akihabara3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gmail.uli153.akihabara3.data.sources.BggPagingSource
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.SearchBggUseCase
import com.gmail.uli153.akihabara3.utils.DataWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
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

    private val _pagedSearch: MutableStateFlow<PagingData<BggSearchItem>> =
        MutableStateFlow(PagingData.from(emptyList()))
    val pagedSearch: StateFlow<PagingData<BggSearchItem>> = _pagedSearch

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            searchUseCase(query).cachedIn(viewModelScope).collectLatest {
                _pagedSearch.emit(it)
            }
        }
    }
}
