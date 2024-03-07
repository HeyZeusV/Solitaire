package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.ClassicWestcliffTableau
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Classic Westcliff.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class ClassicWestcliffViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val baseRedealAmount: Int = 0
    override var redealLeft: Int = 0

    // 48 due to Aces starting in Foundation piles
    override var baseDeck: MutableList<Card> = MutableList(48) { Card((it % 12) + 1, getSuit(it)) }

    override val _tableau: MutableList<Tableau> = initializeTableau(ClassicWestcliffTableau::class)

    /**
     *  Goes through all the card piles in the game and resets them for either the same game or a
     *  new game depending on [resetOption]. All 4 Aces start in Foundation piles.
     */
    override fun reset(resetOption: ResetOptions) {
        super.reset(resetOption)

        _foundation.forEach { it.add(listOf(Card(0, it.suit, faceUp = true))) }
    }

    /**
     *  Autocomplete requires all Tableau piles to be all face up.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach { if (it.faceDownExists()) return false }
        return true
    }

    /**
     *  Each pile in the tableau has 3 cards.
     */
    override fun resetTableau() {
        _tableau.forEach { tableau ->
            val cards = MutableList(3) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-11 -> Clubs
     *  Cards 12-23 -> Diamonds
     *  Cards 24-35 -> Hearts
     *  Cards 36-47 -> Spades
     */
    override fun getSuit(i: Int) = when (i / 12) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}