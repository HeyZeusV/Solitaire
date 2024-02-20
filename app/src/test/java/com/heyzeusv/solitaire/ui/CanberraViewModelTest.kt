package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.CanberraViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [CanberraViewModel] and [ScoreboardViewModel] are tied very close to each
 *  other, so I have decided to test both at the same time.
 */
class CanberraAndScoreboardViewModelTest {

    private lateinit var canVM: CanberraViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        canVM = CanberraViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun canSbVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = canVM.stock.pile.toList()
        val expectedWastePileBefore = canVM.stock.pile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        canVM.apply { for (i in 1..24) sbVM.handleMoveResult(onStockClick(1)) }

        Assert.assertEquals(expectedStockBefore, canVM.stock.pile.toList())
        Assert.assertEquals(expectedWastePileBefore, canVM.waste.pile.toList())

        sbVM.handleMoveResult(canVM.onStockClick(1))

        Assert.assertEquals(expectedStockAfter, canVM.stock.pile.toList())
        Assert.assertEquals(expectedWastePileAfter, canVM.waste.pile.toList())
        Assert.assertEquals(expectedMoves, sbVM.moves.value)
        Assert.assertEquals(expectedHistoryListSize, canVM.historyList.size)
        Assert.assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        Assert.assertEquals(expectedUndoEnabled, canVM.undoEnabled.value)
        Assert.assertEquals(expectedStockWasteEmpty, canVM.stockWasteEmpty.value)
    }
}