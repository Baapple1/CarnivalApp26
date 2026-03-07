package org.bridgwatercarnival.companion.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class ResultsState {
    object Loading : ResultsState()
    object Success : ResultsState()
    object Error : ResultsState()
}

class ResultsViewModel {
    private val _resultsState = MutableStateFlow<ResultsState>(ResultsState.Loading)
    val resultsState: StateFlow<ResultsState> = _resultsState.asStateFlow()

    fun loadResults() {
        // TODO: Implement results loading logic
        _resultsState.value = ResultsState.Success
    }
} 