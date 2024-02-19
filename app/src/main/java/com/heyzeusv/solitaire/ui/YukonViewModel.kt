package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.TableauPile
import com.heyzeusv.solitaire.data.pile.tableau.YukonTableau
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Yukon.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class YukonViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel(ss) {

    override val _tableau: MutableList<TableauPile> = MutableList(7) { YukonTableau() }

    /**
     *  Autocomplete requires all Tableau piles to be all face up and in order by value.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach { if (it.faceDownExists() || it.notInOrder())
            return false }
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