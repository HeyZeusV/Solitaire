package com.heyzeusv.solitaire.board.piles

import androidx.compose.runtime.mutableStateListOf

/**
 *  Originally an interface, but abstract class gave more functionality, specifically a constructor
 *  and initialized properties.
 */
abstract class Pile(initialPile: List<CardLogic> = emptyList()) {

    /**
     *  This is the pile that the user sees. The only time this would be different from [truePile]
     *  is during an animation. After animation is over, [animatedPiles] is used to update
     *  [displayPile].
     */
    protected val _displayPile: MutableList<CardLogic> = mutableStateListOf()
    val displayPile: List<CardLogic> get() = _displayPile

    /**
     *  This pile is the source of truth, it is updated almost immediately after user commits a
     *  legal move.
     */
    protected val _truePile: MutableList<CardLogic> = mutableStateListOf()
    val truePile: List<CardLogic> get() = _truePile

    // Cards being animated
    protected val animatedPiles: MutableList<List<CardLogic>> = mutableListOf()
    private val historyList: MutableList<List<CardLogic>> = mutableListOf()
    protected var currentStep: List<CardLogic> = emptyList()

    abstract fun add(cards: List<CardLogic>)
    abstract fun remove(tappedIndex: Int = 1): CardLogic
    abstract fun reset(cards: List<CardLogic> = emptyList())
    abstract fun undo()

    /**
     *  Updates [displayPile] by retrieving the first value of [animatedPiles].
     */
    fun updateDisplayPile() {
        val aniPile = try {
            animatedPiles.removeFirst()
        } catch (e: NoSuchElementException) {
            _truePile.toList()
        }
        _displayPile.clear()
        _displayPile.addAll(aniPile)
    }

    /**
     *  Adds given [cards] to [historyList]. Limits the size of [historyList] to 15.
     */
    protected fun appendHistory(cards: List<CardLogic>) {
        historyList.let {
            if (it.size == 15) it.removeFirst()
            it.add(currentStep)
            currentStep = cards
        }
    }

    /**
     *  Retrieves the last value of [historyList].
     */
    protected fun retrieveHistory(): MutableList<CardLogic> {
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