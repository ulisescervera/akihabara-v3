package com.gmail.uli153.akihabara3.ui.viewmodels

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.gmail.uli153.akihabara3.data.repositories.SearchTypes
import com.gmail.uli153.akihabara3.domain.models.BggSearchItem
import com.gmail.uli153.akihabara3.domain.use_cases.bgg.SearchBggUseCase
import com.gmail.uli153.akihabara3.data.DataWrapper
import com.gmail.uli153.akihabara3.utils.PreferenceUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
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

    private var searchJob: Job? = null

    private val _query: MutableLiveData<String> = MutableLiveData("")

    private val _filterBoardgame: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAMES))
    val filterBoardgame: LiveData<Boolean> = _filterBoardgame

    private val _filterBoardgameAccessory: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAME_ACCESSORY))
    val filterBoardgameAccessory: LiveData<Boolean> = _filterBoardgameAccessory

    private val _filterBoardgameExpansion: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_BOARDGAME_EXPANSION))
    val filterBoardgameExpansion: LiveData<Boolean> = _filterBoardgameExpansion

    private val _filterVideogame: MutableLiveData<Boolean> = MutableLiveData(preferenceUtils.getBoolean(FILTER_VIDEOGAME))
    val filterVideogame: LiveData<Boolean> = _filterVideogame

    // Not paged search yet
    val pagedSearch: LiveData<PagingData<BggSearchItem>> = _query.distinctUntilChanged().switchMap { query ->
        if (query.isBlank()) return@switchMap MutableLiveData(PagingData.from(emptyList()))

        searchUseCase.searchPaged(query).cachedIn(viewModelScope).asLiveData(Dispatchers.IO)
    }

    val types: Set<SearchTypes> get() {
        return HashSet<SearchTypes>().apply {
            if (filterBoardgame.value == true)          add(SearchTypes.Boardgame)
            if (filterBoardgameExpansion.value == true) add(SearchTypes.BoardgameExpansion)
            if (filterBoardgameAccessory.value == true) add(SearchTypes.BoardgameAccessory)
            if (filterVideogame.value == true)          add(SearchTypes.Videgame)
        }
    }

    val searchResult: LiveData<DataWrapper<List<BggSearchItem>>> = MediatorLiveData<DataWrapper<List<BggSearchItem>>>().let { mediator ->
        val mutable = MutableLiveData<DataWrapper<List<BggSearchItem>>>(DataWrapper.Success(emptyList()))

        val search: () -> Unit = {
            searchJob?.cancel()
            val query: String = _query.value ?: ""
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                delay(400)
                val result = searchUseCase(query, types)
                if (isActive) mutable.value = result
            }
        }

        val observer = Observer<Boolean> {
            search()
        }

        mediator.addSource(filterBoardgame, observer)
        mediator.addSource(filterBoardgameExpansion, observer)
        mediator.addSource(filterBoardgameAccessory, observer)
        mediator.addSource(filterVideogame, observer)
        mediator.addSource(_query) {
            search()
        }

        mutable
    }

    fun setFilterBoardgame(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgame.value = active
        preferenceUtils.putBoolean(FILTER_BOARDGAMES, active)
    }

    fun setFilterBoardgameExpansion(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgameExpansion.value = active
        preferenceUtils.putBoolean(FILTER_BOARDGAME_EXPANSION, active)
    }

    fun setFilterBoardgameAccessory(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgameAccessory.value = active
        preferenceUtils.putBoolean(FILTER_BOARDGAME_ACCESSORY, active)
    }

    fun setFilterVideogame(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterVideogame.value = active
        preferenceUtils.putBoolean(FILTER_VIDEOGAME, active)
    }

    fun search(query: String) {
        _query.value = query
    }

}
