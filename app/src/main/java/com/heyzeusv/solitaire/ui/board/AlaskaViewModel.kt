package com.heyzeusv.solitaire.ui.board

import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Alaska.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class AlaskaViewModel @Inject constructor(
    ss: ShuffleSeed,
    layoutInfo: LayoutInfo
) : GameViewModel(ss, layoutInfo) {

    override val _tableau: MutableList<Tableau> = initializeTableau(Tableau.AlaskaTableau::class)

    /**
     *  Autocomplete requires all Tableau piles to be all face up, single suit, and in order by
     *  value ascending or descending.
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