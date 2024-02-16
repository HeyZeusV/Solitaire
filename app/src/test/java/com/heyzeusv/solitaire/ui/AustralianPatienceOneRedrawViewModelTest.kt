package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.ShuffleSeed
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [AustralianPatienceOneRedrawViewModel] and [ScoreboardViewModel] are tied very close to each
 *  other, so I have decided to test both at the same time.
 */
class AustralianPatienceOneRedrawAndScoreboardViewModelTest {

    private lateinit var apVM: AustralianPatienceOneRedrawViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        apVM = AustralianPatienceOneRedrawViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun apodSbVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = apVM.stock.pile.toList()
        val expectedWastePileBefore = apVM.stock.pile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        apVM.apply { for (i in 1..24) sbVM.handleMoveResult(onStockClick(1)) }

        Assert.assertEquals(expectedStockBefore, apVM.stock.pile.toList())
        Assert.assertEquals(expectedWastePileBefore, apVM.waste.pile.toList())

        sbVM.handleMoveResult(apVM.onStockClick(1))

        Assert.assertEquals(expectedStockAfter, apVM.stock.pile.toList())
        Assert.assertEquals(expectedWastePileAfter, apVM.waste.pile.toList())
        Assert.assertEquals(expectedMoves, sbVM.moves.value)
        Assert.assertEquals(expectedHistoryListSize, apVM.historyList.size)
        Assert.assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        Assert.assertEquals(expectedUndoEnabled, apVM.undoEnabled.value)
        Assert.assertEquals(expectedStockWasteEmpty, apVM.stockWasteEmpty.value)
    }
}