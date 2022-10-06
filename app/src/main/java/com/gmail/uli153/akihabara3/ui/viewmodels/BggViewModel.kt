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
import com.gmail.uli153.akihabara3.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BggViewModel @Inject constructor(
    private val searchUseCase: SearchBggUseCase,
    private val preferenceUtils: PreferenceUtils
): ViewModel() {

    private val FILTER_BOARDGAMES = "FILTER_BOARDGAMES"
    private val FILTER_BOARDGAME_ACCESSORY = "FILTER_BOARDGAME_ACCESSORY"
    private val FILTER_BOARDGAME_EXPANSION = "FILTER_BOARDGAME_EXPANSION"
    private val FILTER_VIDEOGAME = "FILTER_VIDEOGAME"

    private val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("AKB", exception.stackTraceToString())
            throw exception
        }
    }

    private val _query: MutableLiveData<String> = MutableLiveData("")

    private val _filterBoardgame: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAMES))
    val filterBoardgame: LiveData<Boolean> = _filterBoardgame

    fun setFilterBoardgame(active: Boolean) {
        _filterBoardgame.value = active
    }

    private val _filterBoardgameAccessory: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAME_ACCESSORY))
    val filterBoardgameAccessory: LiveData<Boolean> = _filterBoardgameAccessory

    fun setFilterBoardgameAccessory(active: Boolean) {
        _filterBoardgameAccessory.value = active
    }

    private val _filterBoardgameExpansion: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAME_EXPANSION))
    val filterBoardgameExpansion: LiveData<Boolean> = _filterBoardgameExpansion

    fun setFilterBoardgameExpansion(active: Boolean) {
        _filterBoardgameExpansion.value = active
    }

    private val _filterVideogame: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_VIDEOGAME))
    val filterVideogame: LiveData<Boolean> = _filterVideogame

    fun setFilterVideogame(active: Boolean) {
        _filterVideogame.value = active
    }

    val pagedSearch: LiveData<PagingData<BggSearchItem>> = _query.distinctUntilChanged().switchMap { query ->
        if (query.isBlank()) return@switchMap MutableLiveData(PagingData.from(emptyList()))

        searchUseCase(query).cachedIn(viewModelScope).asLiveData(Dispatchers.IO)
    }

    fun search(query: String) {
        _query.value = query
    }

}
