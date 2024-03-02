package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.EasthavenTableau
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Easthaven.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class EasthavenViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val baseRedealAmount: Int = 0
    override var redealLeft: Int = 0

    override val _tableau: MutableList<Tableau> = MutableList(7) { EasthavenTableau() }

    override fun onStockClick(drawAmount: Int): MoveResult {
        if (_stock.pile.isNotEmpty()) {
            _tableau.forEach { (it as EasthavenTableau).addFromStock(_stock.removeMany(1)) }
            appendHistory()
            return MoveResult.MOVE
        }
        return MoveResult.ILLEGAL
    }

    /**
     *  Autocomplete requires all Tableau piles to be all face up, in order, and alternating color.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach {
            if (it.faceDownExists() || it.notInOrderOrAltColor(it.pile.toList())) return false
        }
        return true
    }

    /**
     *  Each pile in the tableau has 1 more card than the previous.
     */
    override fun resetTableau() {
        _tableau.forEach { tableau ->
            val cards = MutableList(3) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}