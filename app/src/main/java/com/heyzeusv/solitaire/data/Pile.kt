package com.heyzeusv.solitaire.data

import androidx.compose.runtime.mutableStateListOf

/**
 *  Originally an interface, but abstract class gave more functionality, specifically a constructor
 *  and initialized properties.
 */
abstract class Pile(initialPile: List<Card> = emptyList()) {

    protected val mPile: MutableList<Card> = mutableStateListOf()
    val pile: List<Card> get() = mPile

    abstract fun add(cards: List<Card>): Boolean
    abstract fun remove(tappedIndex: Int = 1): Card
    abstract fun reset(cards: List<Card> = emptyList())
    abstract fun undo(cards: List<Card>)

    override fun toString(): String = pile.toList().toString()

    init {
        mPile.addAll(initialPile)
    }
}

/**
 *  Tableau piles are usually going to have slightly different rules in terms of how Cards can be
 *  stacked.
 */
abstract class TableauPile(initialPile: List<Card> = emptyList()) : Pile(initialPile)