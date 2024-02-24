package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Russian.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class RussianViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val _tableau: MutableList<Tableau> = MutableList(7) { Tableau.RussianTableau() }

    /**
     *  Autocomplete requires all Tableau piles to be all face up, single suit, and in order by
     *  value descending.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach {
            if (it.faceDownExists() || it.isMultiSuit() || it.notInOrder()) return false
        }
        return true
    }

    /**
     *  Entire deck starts in Tableau, 1 Card in left most, 2nd starts with 6, and the rest have one
     *  more Card than the previous.
     */
    override fun resetTableau() {
        _tableau.forEachIndexed { i, tableau ->
            if (i != 0) {
                val cards = MutableList(i + 5) { _stock.remove() }
                tableau.reset(cards)
            } else {
                tableau.reset(listOf(_stock.remove()))
            }
        }
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}