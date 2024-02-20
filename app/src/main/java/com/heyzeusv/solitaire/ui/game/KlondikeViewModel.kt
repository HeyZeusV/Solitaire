package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.KlondikeTableau
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Klondike (Turn One).
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
open class KlondikeViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val _tableau: MutableList<Tableau> = MutableList(7) { KlondikeTableau() }

    /**
     *  Autocomplete requires all Tableau piles to be all face up.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach { if (it.faceDownExists()) return false }
        return true
    }

    /**
     *  Each pile in the tableau has 1 more card than the previous.
     */
    override fun resetTableau() {
        _tableau.forEachIndexed { i, tableau ->
            val cards = MutableList(i + 1) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}