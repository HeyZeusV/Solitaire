package com.heyzeusv.solitaire.ui

import androidx.compose.runtime.snapshots.Snapshot
import com.heyzeusv.solitaire.data.AustralianPatienceTableau
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.data.Stock
import com.heyzeusv.solitaire.data.TableauPile
import com.heyzeusv.solitaire.data.Waste
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
) : GameViewModel(ss) {

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

    override fun autoComplete() {
        TODO("Not yet implemented")
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
                tableau = _tableau.map { AustralianPatienceTableau(it.pile) }
            )
        }
        currentSnapshot.dispose()
    }

    init {
        resetAll(ResetOptions.NEW)
    }
}