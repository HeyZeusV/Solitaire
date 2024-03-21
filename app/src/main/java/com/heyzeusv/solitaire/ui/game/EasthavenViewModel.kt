package com.heyzeusv.solitaire.ui.game

import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.EasthavenTableau
import com.heyzeusv.solitaire.util.GamePiles
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
    ss: ShuffleSeed,
    layoutInfo: LayoutInfo
) : GameViewModel(ss, layoutInfo) {

    override val baseRedealAmount: Int = 0
    override var redealLeft: Int = 0

    override val _tableau: MutableList<Tableau> = initializeTableau(EasthavenTableau::class)

    override fun onStockClick(drawAmount: Int): MoveResult {
        if (_stock.truePile.isNotEmpty()) {
            val stockCards = _stock.getCards(7)
            val aniInfo = AnimateInfo(
                start = GamePiles.Stock,
                end = GamePiles.TableauAll,
                animatedCards = stockCards,
                flipAnimatedCards = FlipCardInfo.FaceUp.MultiPile
            )
            _stock.removeMany(stockCards.size)
            _tableau.forEachIndexed { index, tableau ->
                val stockCard: List<Card> = try {
                    listOf(stockCards[index])
                } catch (e: IndexOutOfBoundsException) {
                    emptyList()
                }
                (tableau as EasthavenTableau).addFromStock(stockCard)
            }
            aniInfo.actionBeforeAnimation = { _stock.updateDisplayPile() }
            aniInfo.actionAfterAnimation = {
                _tableau.forEach { it.updateDisplayPile() }
                appendHistory(aniInfo.getUndoAnimateInfo())
            }
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