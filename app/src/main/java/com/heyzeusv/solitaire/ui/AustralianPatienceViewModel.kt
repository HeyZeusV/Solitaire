package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.AustralianPatienceTableau
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.TableauPile
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Australian Patience.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class AustralianPatienceViewModel @Inject constructor(
    ss: ShuffleSeed
) : GameViewModel() {

    override val _tableau: MutableList<TableauPile> = MutableList(7) { AustralianPatienceTableau() }

    /**
     *  Each pile starts with exactly 4 cards
     */
    override fun resetTableau() {
        _tableau.forEach { tableau ->
            val cards = MutableList(4) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    init {
        mSS = ss
        resetAll(ResetOptions.NEW)
    }
}