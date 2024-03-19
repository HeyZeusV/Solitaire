package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.EasthavenTableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.Suits
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
    ss: ShuffleSeed,
    layoutInfo: LayoutInfo
) : GameViewModel(ss, layoutInfo) {

    override val baseRedealAmount: Int = 0
    override var redealLeft: Int = 0

    override val _tableau: MutableList<Tableau> = initializeTableau(EasthavenTableau::class)

    override fun onStockClick(drawAmount: Int): MoveResult {
        if (_stock.truePile.isNotEmpty()) {
            _tableau.forEach { (it as EasthavenTableau).addFromStock(_stock.removeMany(1)) }
            val aniInfo = AnimateInfo(GamePiles.Stock, GamePiles.TableauOne, listOf(Card(0, Suits.CLUBS, true)))
            appendHistory(aniInfo)
            _animateInfo.value = aniInfo
            return MoveResult.Move
        }
        return MoveResult.Illegal
    }

    /**
     *  Autocomplete requires all Tableau piles to be all face up, in order, and alternating color.
     */
    override fun autoCompleteTableauCheck(): Boolean {
        _tableau.forEach {
            if (it.faceDownExists() || it.notInOrderOrAltColor(it.truePile.toList())) return false
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