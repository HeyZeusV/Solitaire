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

    init {
        mPile.addAll(initialPile)
    }
}

/**
 *  Tableau piles are usually going to have slightly different rules in terms of how Cards can be
 *  stacked.
 */
abstract class TableauPile(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    // keeps track of number of face down cards in mPile
    protected var faceDownCards: Int = 0

    /**
     *  Used to determine if game could be auto completed by having all face up cards
     */
    fun allFaceUp(): Boolean = faceDownCards == 0
}