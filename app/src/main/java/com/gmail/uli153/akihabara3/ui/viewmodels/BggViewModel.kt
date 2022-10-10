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

    private val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Log.e("AKB", exception.stackTraceToString())
            throw exception
        }
    }

    private var searchJob: Job? = null

    private val _query: MutableLiveData<String> = MutableLiveData("")

    private val _filterBoardgame: MutableLiveData<Boolean> =
        MutableLiveData(preferenceUtils.getBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgame))
    val filterBoardgame: LiveData<Boolean> = _filterBoardgame

    private val _filterBoardgameAccessory: MutableLiveData<Boolean> =
        MutableLiveData(preferenceUtils.getBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgameAccessory))
    val filterBoardgameAccessory: LiveData<Boolean> = _filterBoardgameAccessory

    private val _filterBoardgameExpansion: MutableLiveData<Boolean> =
        MutableLiveData(preferenceUtils.getBoolean(PreferenceUtils.PreferenceKeys.FilterBoardGameExpansion))
    val filterBoardgameExpansion: LiveData<Boolean> = _filterBoardgameExpansion

    private val _filterVideogame: MutableLiveData<Boolean> =
        MutableLiveData(preferenceUtils.getBoolean(PreferenceUtils.PreferenceKeys.FilterVideogame))
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
        }.toSortedSet { p0, p1 -> (p0?.ordinal ?: 0).compareTo(p1?.ordinal ?: 0) }
    }

    val searchResult: MediatorLiveData<DataWrapper<List<BggSearchItem>>> = MediatorLiveData<DataWrapper<List<BggSearchItem>>>().apply {
        val search: () -> Unit = {
            searchJob?.cancel()
            val query: String = _query.value ?: ""
            searchJob = viewModelScope.launch(Dispatchers.IO) {
                if (isActive.not()) return@launch
                val result = searchUseCase(query, types)
                if (isActive) withContext(Dispatchers.Main) {
                    value = result
                }
            }
        }

        val observer = Observer<Boolean> {
            search()
        }

        addSource(filterBoardgame, observer)
        addSource(filterBoardgameExpansion, observer)
        addSource(filterBoardgameAccessory, observer)
        addSource(filterVideogame, observer)
        addSource(_query) {
            search()
        }
    }

    fun setFilterBoardgame(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgame.value = active
        preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgame, active)
    }

    fun setFilterBoardgameExpansion(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgameExpansion.value = active
        preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardGameExpansion, active)
    }

    fun setFilterBoardgameAccessory(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterBoardgameAccessory.value = active
        preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterBoardgameAccessory, active)
    }

    fun setFilterVideogame(active: Boolean) {
        if (types.isEmpty() && !active) return

        _filterVideogame.value = active
        preferenceUtils.putBoolean(PreferenceUtils.PreferenceKeys.FilterVideogame, active)
    }

    fun search(query: String) {
        _query.value = query
    }

}
