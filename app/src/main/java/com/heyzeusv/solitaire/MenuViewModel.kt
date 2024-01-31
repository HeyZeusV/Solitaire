package com.heyzeusv.solitaire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val statManager: StatManager
) : ViewModel() {

    private val _displayMenu = MutableStateFlow(false)
    val displayMenu: StateFlow<Boolean> get() = _displayMenu
    fun updateDisplayMenu(newValue: Boolean) { _displayMenu.value = newValue }

    private val _selectedGame = MutableStateFlow(Games.KLONDIKETURNONE)
    val selectedGame: StateFlow<Games> get() = _selectedGame
    fun updateSelectedGame(newValue: Games) { _selectedGame.value = newValue }

    // TODO: Create class to hold all stats together
    val stats: StateFlow<Long> = statManager.statData
        .map {
            it.ktoFastestWin
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0L
        )
    suspend fun updateStats() { statManager.updateStats() }
}