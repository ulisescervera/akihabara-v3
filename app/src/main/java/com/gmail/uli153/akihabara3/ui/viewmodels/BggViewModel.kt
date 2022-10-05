package com.gmail.uli153.akihabara3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
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

    private val _query: MutableLiveData<String> = MutableLiveData("")

    val pagedSearch: LiveData<PagingData<BggSearchItem>> = _query.distinctUntilChanged().switchMap { query ->
        if (query.isBlank()) return@switchMap MutableLiveData(PagingData.from(emptyList()))

        searchUseCase(query).cachedIn(viewModelScope).asLiveData(Dispatchers.IO)
    }

    fun search(query: String) {
        _query.value = query
    }


}
