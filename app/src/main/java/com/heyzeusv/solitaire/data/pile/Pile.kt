package com.heyzeusv.solitaire.data.pile

import androidx.compose.runtime.mutableStateListOf
import com.heyzeusv.solitaire.data.Card

/**
 *  Originally an interface, but abstract class gave more functionality, specifically a constructor
 *  and initialized properties.
 */
abstract class Pile(initialPile: List<Card> = emptyList()) {

    protected val _displayPile: MutableList<Card> = mutableStateListOf()
    val displayPile: List<Card> get() = _displayPile

    protected val _truePile: MutableList<Card> = mutableStateListOf()
    val truePile: List<Card> get() = _truePile

    protected val animatedPiles: MutableList<List<Card>> = mutableListOf()
    private val historyList: MutableList<List<Card>> = mutableListOf()
    protected var currentStep: List<Card> = emptyList()

    abstract fun add(cards: List<Card>)
    abstract fun remove(tappedIndex: Int = 1): Card
    abstract fun reset(cards: List<Card> = emptyList())
    abstract fun undo()

    fun updateDisplayPile() {
        _displayPile.clear()
        val aniPile = animatedPiles.last()
        _displayPile.addAll(aniPile)
    }

    protected fun updateAnimatedPiles(cards: List<Card>) {
        if (animatedPiles.size == 15) animatedPiles.removeFirst()
        animatedPiles.add(cards)
    }

    protected fun appendHistory(cards: List<Card>) {
        historyList.let {
            if (it.size == 15) it.removeFirst()
            it.add(currentStep)
            currentStep = cards
        }
    }

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
    }
}