package com.heyzeusv.solitaire.data.pile

import androidx.compose.runtime.mutableStateListOf
import com.heyzeusv.solitaire.data.Card

/**
 *  Originally an interface, but abstract class gave more functionality, specifically a constructor
 *  and initialized properties.
 */
abstract class Pile(initialPile: List<Card> = emptyList()) {

    /**
     *  This is the pile that the user sees. The only time this would be different from [truePile]
     *  is during an animation. After animation is over, [animatedPiles] is used to update
     *  [displayPile].
     */
    protected val _displayPile: MutableList<Card> = mutableStateListOf()
    val displayPile: List<Card> get() = _displayPile

    /**
     *  This pile is the source of truth, it is updated almost immediately after user commits a
     *  legal move.
     */
    protected val _truePile: MutableList<Card> = mutableStateListOf()
    val truePile: List<Card> get() = _truePile

    // Cards being animated
    protected val animatedPiles: MutableList<List<Card>> = mutableListOf()
    private val historyList: MutableList<List<Card>> = mutableListOf()
    protected var currentStep: List<Card> = emptyList()

    abstract fun add(cards: List<Card>)
    abstract fun remove(tappedIndex: Int = 1): Card
    abstract fun reset(cards: List<Card> = emptyList())
    abstract fun undo()

    /**
     *  Updates [displayPile] by retrieving the first value of [animatedPiles].
     */
    fun updateDisplayPile() {
        val aniPile = animatedPiles.removeFirst()
        _displayPile.clear()
        _displayPile.addAll(aniPile)
    }

    /**
     *  Adds given [cards] to [historyList]. Limits the size of [historyList] to 15.
     */
    protected fun appendHistory(cards: List<Card>) {
        historyList.let {
            if (it.size == 15) it.removeFirst()
            it.add(currentStep)
            currentStep = cards
        }
    }

    /**
     *  Retrieves the last value of [historyList].
     */
    protected fun retrieveHistory(): MutableList<Card> {
        return try {
            historyList.removeLast().toMutableList()
        } catch (e: NoSuchElementException) {
            mutableListOf()
        }
    }

    protected fun resetHistory() = historyList.clear()

    override fun toString(): String = _truePile.toList().toString()

    init {
        _truePile.addAll(initialPile)
        _displayPile.addAll(initialPile)
    }
}