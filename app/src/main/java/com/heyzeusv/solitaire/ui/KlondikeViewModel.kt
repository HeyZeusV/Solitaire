package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 *  Data manager for Klondike type games.
 *
 *  Stores and manages UI-related data in a lifecycle conscious way.
 *  Data can survive configuration changes.
 */
@HiltViewModel
class KlondikeViewModel @Inject constructor(
    ss: ShuffleSeed
) : DifferentColorGameViewModel(ss) {

    override val baseRedealAmount: Int = 1000
    override var redealLeft: Int = 1000

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