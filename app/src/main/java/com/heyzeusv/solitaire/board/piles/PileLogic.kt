package com.heyzeusv.solitaire.board.piles

import androidx.compose.runtime.mutableStateListOf

/**
 *  In most Solitaire games, there are 4 types of [Card] piles, [Stock], [Waste], [Foundation] and
 *  [Tableau]. They can impose the same actions on their respective [Cards][Card] as each other,
 *  but with slight variations.
 *
 *  @param initialPile Cards that [Pile] is initialized with.
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
     *  This pile is the source of truth, it is updated immediately after user commits a
     *  legal move.
     */
    protected val _truePile: MutableList<Card> = mutableStateListOf()
    val truePile: List<Card> get() = _truePile

    // Cards being animated
    protected val animatedPiles: MutableList<List<Card>> = mutableListOf()
    private val historyList: MutableList<List<Card>> = mutableListOf()
    protected var currentStep: List<Card> = emptyList()

    /**
     *  Adds a [Card] to the end [truePile].
     *  
     *  @param card The [Card] to be added.
     */
    abstract fun add(card: Card)

    /**
     *  Adds multiple [Cards][Card] to the end [truePile].
     *  
     *  @param cards The [Cards][Card] to be added.
     */
    abstract fun addAll(cards: List<Card>)

    /**
     *  Removes single or multiple [Cards][Card], starting at an index.
     *  
     *  @param tappedIndex Index of first [Card] to remove in [truePile].
     *  @return The first [Card] removed.
     */
    abstract fun remove(tappedIndex: Int = 1): Card

    /**
     *  Resets [animatedPiles] and [historyList]. Replaces [truePile] and [displayPile] with a new 
     *  list of [Cards][Card].
     *  
     *  @param cards The new list of [Cards][Card].
     */
    abstract fun reset(cards: List<Card> = emptyList())

    /**
     *  Returns [truePile] to its previous state by accessing [historyList]. 
     */
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
     *  Adds [currentStep] to [historyList] and then updates [currentStep] with latest value of
     *  [truePile]. Limits the size of [historyList] to 15.
     */
    protected fun appendHistory() {
        historyList.let {
            if (it.size == 15) it.removeFirst()
            it.add(currentStep)
            currentStep = _truePile.toList()
        }
    }

    /**
     *  @return The last value of [historyList] or an empty list of [historyList] is empty.
     */
    protected fun retrieveHistory(): MutableList<Card> {
        return try {
            historyList.removeLast().toMutableList()
        } catch (e: NoSuchElementException) {
            mutableListOf()
        }
    }

    protected fun resetHistory() = historyList.clear()

    /**
     *  Resets all lists.
     */
    protected fun resetLists() {
        _truePile.clear()
        _displayPile.clear()
        animatedPiles.clear()
        historyList.clear()
    }

    override fun toString(): String = _truePile.toList().toString()

    init {
        _truePile.addAll(initialPile)
        _displayPile.addAll(initialPile)
    }
}