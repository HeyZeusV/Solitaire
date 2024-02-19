package com.heyzeusv.solitaire.data.pile

import androidx.compose.runtime.mutableStateListOf
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.Suits

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
 *  added.
 */
abstract class TableauPile(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    // keeps track of number of face down cards in mPile
    protected var _faceDownCards: Int = 0
    val faceDownCards: Int get() = _faceDownCards

    // keeps track of number of different Suits in mPile
    protected var _suitTypes: Int = 0

    /**
     *  Used to determine if game could be auto completed by having all face up cards
     */
    fun faceDownExists(): Boolean = _faceDownCards > 0

    /**
     *  Used to determine if pile contains more than 1 [Suits] type.
     */
    fun isMultiSuit(): Boolean = _suitTypes > 1

    /**
     *  It is possible for pile to be same suit, but out of order. This checks if pile is not in
     *  order, this way autocomplete will not be stuck in an infinite loop.
     */
    fun notInOrder(): Boolean {
        val it = mPile.iterator()
        if (!it.hasNext()) return false
        var current = it.next()
        while (true) {
            if (!it.hasNext()) return false
            val next = it.next()
            if (current.value < next.value) return true
            current = next
        }
    }
}