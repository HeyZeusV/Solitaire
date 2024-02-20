package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.AustralianPatienceTableau
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Australian Patience.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
open class AustralianPatienceViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val baseRedealAmount: Int = 0
    override var redealLeft: Int = 0

    override val _tableau: MutableList<Tableau> = MutableList(7) { AustralianPatienceTableau() }

    /**
     *  Each pile starts with exactly 4 cards
     */
    override fun resetTableau() {
        _tableau.forEach { tableau ->
            val cards = MutableList(4) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    /**
     *  Autocomplete requires all Tableau piles to be a single [Suits] type and in order by value.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach { if (it.isMultiSuit() || it.notInOrder()) return false}
        return true
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}

/**
 *  Data manager for Canberra.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class CanberraViewModel @Inject constructor(
    ss: ShuffleSeed
) : AustralianPatienceViewModel(ss) {

    override val baseRedealAmount: Int = 1
    override var redealLeft: Int = 1
}