package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.YukonViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.TestCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [YukonViewModel] and [ScoreboardViewModel] are tied very close to each other, so I have
 *  decided to test both at the same time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class YukonViewModelTest {

    private val tc = TestCards
    private lateinit var ykVM: YukonViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        ykVM = YukonViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun ykSbVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = emptyList<Card>()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<PileHistory>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = true

        ykVM.resetAll(ResetOptions.RESTART)

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, ykVM.stock.pile)
        ykVM.foundation.forEach {
            assertEquals(expectedFoundation, it.pile)
        }
        ykVM.tableau.forEachIndexed { i, tableau ->
            if (i > 0) {
                val expectedTableauSize = i + 5
                assertEquals(expectedTableauSize, tableau.pile.size)
            } else {
                assertEquals(1, tableau.pile.size)
            }
        }
        assertEquals(expectedWaste, ykVM.waste.pile)
        assertEquals(expectedHistoryList, ykVM.historyList)
        assertEquals(expectedUndoEnabled, ykVM.undoEnabled.value)
        assertEquals(expectedGameWon, ykVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, ykVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, ykVM.stockWasteEmpty.value)
    }

    @Test
    fun ykSbVmAutoComplete() = runTest {
        val expectedClubs = tc.clubs
        val expectedDiamonds = tc.diamonds
        val expectedHearts = tc.hearts
        val expectedSpades = tc.spades
        val expectedFinalMoves = 52
        val expectedFinalScore = 52
        val expectedSbvmMoves = 1
        val expectedSbvmScore = 1
        val expectedGameWon = true
        val expectedAutoCompleteActive = false

        // going to cheat and give lists that are ready to auto complete
        ykVM.tableau.forEach { it.undo(emptyList()) }
        ykVM.tableau[0].add(tc.heartSpade.reversed())
        ykVM.tableau[1].add(tc.spadeHeart.reversed())
        ykVM.tableau[2].add(tc.clubDiamond.reversed())
        ykVM.tableau[3].add(tc.diamondClub.reversed())

        // this should start autoComplete()
        launch { sbVM.handleMoveResult(ykVM.onTableauClick(0, 12)) }
        advanceUntilIdle()

        val lgs = sbVM.retrieveLastGameStats(true, ykVM.autoCompleteCorrection)

        assertEquals(expectedClubs, ykVM.foundation[0].pile.toList())
        assertEquals(expectedDiamonds, ykVM.foundation[1].pile.toList())
        assertEquals(expectedHearts, ykVM.foundation[2].pile.toList())
        assertEquals(expectedSpades, ykVM.foundation[3].pile.toList())
        assertEquals(expectedSbvmMoves, sbVM.moves.value)
        assertEquals(expectedSbvmScore, sbVM.score.value)
        assertEquals(expectedFinalMoves, lgs.moves)
        assertEquals(expectedFinalScore, lgs.score)
        assertEquals(expectedGameWon, ykVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, ykVM.autoCompleteActive.value)
    }
}