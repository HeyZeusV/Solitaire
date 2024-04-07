package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.ClassicWestcliffViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [ClassicWestcliffViewModel] and [ScoreboardViewModel] are tied very close to each
 *  other, so I have decided to test both at the same time.
 */
class ClassicWestcliffViewModelTest {

    private lateinit var cwVM: ClassicWestcliffViewModel
    private lateinit var sbVM: ScoreboardViewModel

    private val tc = TestCards

    @Before
    fun setup() {
        cwVM = ClassicWestcliffViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun cwVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = cwVM.stock.truePile.toList()
        val expectedClubFoundation = listOf(tc.card1CFU)
        val expectedDiamondsFoundation = listOf(tc.card1DFU)
        val expectedHeartsFoundation = listOf(tc.card1HFU)
        val expectedSpadesFoundation = listOf(tc.card1SFU)
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<PileHistory>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = true
        val expectedTableauSize = 3

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, cwVM.stock.truePile)
        assertEquals(expectedClubFoundation, cwVM.foundation[0].truePile.toList())
        assertEquals(expectedDiamondsFoundation, cwVM.foundation[1].truePile.toList())
        assertEquals(expectedHeartsFoundation, cwVM.foundation[2].truePile.toList())
        assertEquals(expectedSpadesFoundation, cwVM.foundation[3].truePile.toList())
        cwVM.tableau.forEach { tableau ->
            assertEquals(expectedTableauSize, tableau.truePile.size)
        }
        assertEquals(expectedWaste, cwVM.waste.truePile)
        assertEquals(expectedHistoryList, cwVM.historyList)
        assertEquals(expectedUndoEnabled, cwVM.undoEnabled.value)
        assertEquals(expectedGameWon, cwVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, cwVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, cwVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        cwVM.resetAll(ResetOptions.NEW)
        Assert.assertNotEquals(expectedStock, cwVM.stock.truePile)
    }
}