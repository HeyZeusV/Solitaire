package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.EasthavenViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.TestCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [EasthavenViewModel] and [ScoreboardViewModel] are tied very close to each other, so I have
 *  decided to test both at the same time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class EasthavenViewModelTest {

    private val tc = TestCards
    private lateinit var ehVM: EasthavenViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        ehVM = EasthavenViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun ehSbVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = ehVM.stock.truePile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<PileHistory>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = true
        val expectedTableauSize = 3

        ehVM.resetAll(ResetOptions.RESTART)

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, ehVM.stock.truePile)
        ehVM.foundation.forEach {
            assertEquals(expectedFoundation, it.truePile)
        }
        ehVM.tableau.forEach { tableau ->
            assertEquals(expectedTableauSize, tableau.truePile.size)
        }
        assertEquals(expectedWaste, ehVM.waste.truePile)
        assertEquals(expectedHistoryList, ehVM.historyList)
        assertEquals(expectedUndoEnabled, ehVM.undoEnabled.value)
        assertEquals(expectedGameWon, ehVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, ehVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, ehVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        ehVM.resetAll(ResetOptions.NEW)
        Assert.assertNotEquals(expectedStock, ehVM.stock.truePile)
    }

    @Test
    fun ehSbVmOnStockClick() {
        val expectedTableau1 = listOf(tc.card5H, tc.card11D, tc.card4DFU, tc.card5CFU)
        val expectedTableau2 = listOf(tc.card7C, tc.card9D, tc.card7DFU, tc.card13CFU)
        val expectedTableau3 = listOf(tc.card1H, tc.card10C, tc.card7SFU, tc.card12SFU)
        val expectedTableau4 = listOf(tc.card3C, tc.card9S, tc.card7HFU, tc.card13HFU)
        val expectedTableau5 = listOf(tc.card9C, tc.card2H, tc.card13DFU, tc.card6DFU)
        val expectedTableau6 = listOf(tc.card12H, tc.card11S, tc.card8CFU, tc.card2CFU)
        val expectedTableau7 = listOf(tc.card4S, tc.card10D, tc.card10SFU, tc.card12CFU)
        val expectedTableau = listOf(
            expectedTableau1, expectedTableau2, expectedTableau3, expectedTableau4,
            expectedTableau5, expectedTableau6, expectedTableau7
        )
        val expectedMoves = 1

        sbVM.handleMoveResult(ehVM.onStockClick(1))

        ehVM.tableau.forEachIndexed { i, tableau ->
            assertEquals(expectedTableau[i], tableau.truePile.toList())
        }
        assertEquals(expectedMoves, sbVM.moves.value)
    }
}