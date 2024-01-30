package com.heyzeusv.solitaire

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MenuViewModel : ViewModel() {

    private val _displayMenu = MutableStateFlow(false)
    val displayMenu: StateFlow<Boolean> get() = _displayMenu

    fun menuOnClick() {
        _displayMenu.value = true
    }

    fun closeMenu() {
        _displayMenu.value = false
    }
}