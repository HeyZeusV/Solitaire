package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.ShuffleSeed
import com.heyzeusv.solitaire.ui.board.CanberraViewModel
import com.heyzeusv.solitaire.ui.board.scoreboard.ScoreboardLogic
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [CanberraViewModel] and [ScoreboardLogic] are tied very close to each
 *  other, so I have decided to test both at the same time.
 */
class CanberraAndScoreboardViewModelTest {

    private lateinit var canVM: CanberraViewModel
    private lateinit var sbVM: ScoreboardLogic

    @Before
    fun setup() {
        canVM = CanberraViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardLogic()
    }

    @Test
    fun canSbVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = canVM.stock.truePile.toList()
        val expectedWastePileBefore = canVM.stock.truePile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        canVM.apply { for (i in 1..24) sbVM.handleMoveResult(onStockClick(1)) }

        assertEquals(expectedStockBefore, canVM.stock.truePile.toList())
        assertEquals(expectedWastePileBefore, canVM.waste.truePile.toList())

        sbVM.handleMoveResult(canVM.onStockClick(1))

        assertEquals(expectedStockAfter, canVM.stock.truePile.toList())
        assertEquals(expectedWastePileAfter, canVM.waste.truePile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, canVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, canVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, canVM.stockWasteEmpty.value)
    }
}