package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.viewModelScope
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.DifferentColorTableau
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.Stock
import com.heyzeusv.solitaire.data.TableauPile
import com.heyzeusv.solitaire.data.Waste
import com.heyzeusv.solitaire.util.ResetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
) : GameViewModel(ss) {

    override val baseRedealAmount: Int = 1000
    override var redealLeft: Int = 1000

    override val _tableau: MutableList<TableauPile> = MutableList(7) { DifferentColorTableau() }

    /**
     *  Each pile in the tableau has 1 more card than the previous.
     */
    override fun resetTableau() {
        _tableau.forEachIndexed { i, tableau ->
            val cards = MutableList(i + 1) { _stock.remove() }
            tableau.reset(cards)
        }
    }

    /**
     *  Checks if Stock and Waste are empty and that all Cards in Tableau are face up. If so, then
     *  go through each Tableau and call onTableauClick on the last card. This is repeated until
     *  the game is completed.
     */
    override fun autoComplete() {
        if (_autoCompleteActive.value) return
        if (_stock.pile.isEmpty() && _waste.pile.isEmpty()) {
            _tableau.forEach { if (!(it as DifferentColorTableau).allFaceUp()) return }
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
                tableau = _tableau.map { DifferentColorTableau(it.pile) }
            )
        }
        currentSnapshot.dispose()
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}