package com.heyzeusv.solitaire

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class Waste {

    private val _pile = MutableStateFlow(mutableListOf<Card>())
    val pile: StateFlow<List<Card>> get() = _pile

    /**
     *  Adds given [card] to [Waste] pile.
     */
    fun addCard(card: Card) = _pile.update { (it + card).toMutableList() }

    /**
     *  Removes the last card of the pile which would refer to the top showing card.
     */
    fun removeCard() {
        _pile.update {
            it.removeLast()
            it
        }
    }

    /**
     *  Removes all card from the pile.
     */
    fun resetCards() {
        _pile.update {
            it.clear()
            it
        }
    }
}