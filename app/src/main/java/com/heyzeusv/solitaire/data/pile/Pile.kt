package com.heyzeusv.solitaire.data.pile

import androidx.compose.runtime.mutableStateListOf
import com.heyzeusv.solitaire.data.Card

/**
 *  Originally an interface, but abstract class gave more functionality, specifically a constructor
 *  and initialized properties.
 */
abstract class Pile(initialPile: List<Card> = emptyList()) {

    protected val _pile: MutableList<Card> = mutableStateListOf()
    val pile: List<Card> get() = _pile

    abstract fun add(cards: List<Card>): Boolean
    abstract fun remove(tappedIndex: Int = 1): Card
    abstract fun reset(cards: List<Card> = emptyList())
    abstract fun undo(cards: List<Card>)

    override fun toString(): String = _pile.toList().toString()

    init {
        _pile.addAll(initialPile)
    }
}