package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.pile.tableau.SameSuitTableau
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.TableauPile
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

    override val _tableau: MutableList<TableauPile> = MutableList(7) { SameSuitTableau() }

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
     *  Checks if Stock and Waste are empty and that all Cards in Tableau are of the same suit
     *  and in order. If so, then go through each Tableau and call onTableauClick on the last card.
     *  This is repeated until the game is completed.
     */
    override fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (_stock.pile.isEmpty() && _waste.pile.isEmpty()) {
            _tableau.forEach {
                if ((it as SameSuitTableau).isMultiSuit() && !it.inOrder()) return
            }
            viewModelScope.launch {
                _autoCompleteActive.value = true
                _autoCompleteCorrection = 0
                while (!gameWon()) {
                    _tableau.forEachIndexed { i, tableau ->
                        if (tableau.pile.isEmpty()) return@forEachIndexed
                        onTableauClick(i, tableau.pile.size - 1)
                        delay(100)
                    }
                }
            }
        }
    }

    /**
     *  Takes a [Snapshot] of current StateObject values and stores them in a [PileHistory] object. We
     *  then immediately dispose of the [Snapshot] to avoid memory leaks.
     */
    override fun recordHistory() {
        val currentSnapshot = Snapshot.takeMutableSnapshot()
        currentSnapshot.enter {
            currentStep = PileHistory(
                stock = Stock(_stock.pile),
                waste = Waste(_waste.pile),
                foundation = _foundation.map { Foundation(it.suit, it.pile) },
                tableau = _tableau.map { SameSuitTableau(it.pile) }
            )
        }
        currentSnapshot.dispose()
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