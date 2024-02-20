package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.KlondikeViewModel
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
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [KlondikeViewModel] and [ScoreboardViewModel] are tied very close to each other, so I have
 *  decided to test both at the same time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class KlondikeAndScoreboardViewModelTest {

    private val tc = TestCards
    private lateinit var kdVM: KlondikeViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        kdVM = KlondikeViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun kdSbVmDeckCreation() {
        val expectedDeck = tc.deck
        var actualDeck = mutableListOf<Card>()

        // add all the cards into single list
        actualDeck.addAll(kdVM.stock.pile)
        actualDeck.addAll(kdVM.waste.pile)
        kdVM.foundation.forEach { actualDeck.addAll(it.pile) }
        kdVM.tableau.forEach { actualDeck.addAll(it.pile) }
        // make sure that are face down
        actualDeck = actualDeck.map { it.copy(faceUp = false) }.toMutableList()
        // sort
        actualDeck = actualDeck.sortedWith(compareBy({ it.suit }, { it.value })).toMutableList()

        assertEquals(expectedDeck, actualDeck)
    }

    @Test
    fun kdSbVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = kdVM.stock.pile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<PileHistory>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = true

        kdVM.resetAll(ResetOptions.RESTART)

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, kdVM.stock.pile)
        kdVM.foundation.forEach {
            assertEquals(expectedFoundation, it.pile)
        }
        kdVM.tableau.forEachIndexed { i, tableau ->
            val expectedTableauSize = i + 1
            assertEquals(expectedTableauSize, tableau.pile.size)
        }
        assertEquals(expectedWaste, kdVM.waste.pile)
        assertEquals(expectedHistoryList, kdVM.historyList)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
        assertEquals(expectedGameWon, kdVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, kdVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        kdVM.resetAll(ResetOptions.NEW)
        assertNotEquals(expectedStock, kdVM.stock.pile)
    }

    @Test
    fun kdSbVmOnStockClickStockNotEmpty() {
        val expectedStock = kdVM.stock.pile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU, tc.card2DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 3 Cards
        sbVM.apply {
            handleMoveResult(kdVM.onStockClick(1))
            handleMoveResult(kdVM.onStockClick(1))
            handleMoveResult(kdVM.onStockClick(1))
        }

        assertEquals(expectedStock, kdVM.stock.pile.toList())
        assertEquals(expectedWastePile, kdVM.waste.pile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, kdVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)
    }

    @Test
    fun kdSbVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = kdVM.stock.pile.toList()
        val expectedWastePileBefore = kdVM.stock.pile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        kdVM.apply { for (i in 1..24) sbVM.handleMoveResult(onStockClick(1)) }

        assertEquals(expectedStockBefore, kdVM.stock.pile.toList())
        assertEquals(expectedWastePileBefore, kdVM.waste.pile.toList())

        sbVM.handleMoveResult(kdVM.onStockClick(1))

        assertEquals(expectedStockAfter, kdVM.stock.pile.toList())
        assertEquals(expectedWastePileAfter, kdVM.waste.pile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, kdVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)
    }

    @Test
    fun kdSbVmOnWasteClick() {
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU)
        val expectedTableauPile = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedStockWasteEmpty = false

        // fill Waste with 3 Cards
        kdVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        kdVM.onWasteClick()

        assertEquals(expectedWastePile, kdVM.waste.pile.toList())
        assertEquals(expectedTableauPile, kdVM.tableau[3].pile.toList())
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)
    }

    @Test
    fun kdSbVmOnFoundationClick() {
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU)
        val expectedTableauPile = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU, tc.card1CFU)
        val expectedFoundationPile = listOf(tc.card1CFU)
        val expectedScoreBefore = 1
        val expectedScoreAfter = 0
        val expectedMoves = 7

        // fill Waste with 3 Cards
        sbVM.apply {
            handleMoveResult(kdVM.onStockClick(1))
            handleMoveResult(kdVM.onStockClick(1))
            handleMoveResult(kdVM.onStockClick(1))
        }
        // this should remove top card from Waste and move it to Tableau pile #4
        sbVM.handleMoveResult(kdVM.onWasteClick())
        // draw another Card and move it to Foundation Clubs pile
        sbVM.handleMoveResult(kdVM.onStockClick(1))
        sbVM.handleMoveResult(kdVM.onWasteClick())

        assertEquals(expectedFoundationPile, kdVM.foundation[0].pile.toList())
        assertEquals(expectedScoreBefore, sbVM.score.value)

        // move card from Foundation Clubs pile to Tableau pile $4
        sbVM.handleMoveResult(kdVM.onFoundationClick(0))

        assertEquals(expectedWastePile, kdVM.waste.pile.toList())
        assertEquals(expectedTableauPile, kdVM.tableau[3].pile.toList())
        assertEquals(expectedScoreAfter, sbVM.score.value)
        assertEquals(expectedMoves, sbVM.moves.value)
    }

    @Test
    fun kdSbVmStockWasteEmptyOnStockClick() {
        val expectedStockWasteEmptyBefore = true
        val expectedStockWasteEmptyAfter = true
        val expectedStockAfter = emptyList<Card>()
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, kdVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        kdVM.stock.reset(listOf(tc.card10C))
        kdVM.waste.undo(emptyList())
        kdVM.tableau.forEach { it.undo(emptyList()) }
        kdVM.onStockClick(1)

        assertEquals(expectedStockWasteEmptyAfter, kdVM.stockWasteEmpty.value)
        assertEquals(expectedStockAfter, kdVM.stock.pile.toList())
        assertEquals(expectedWasteAfter, kdVM.waste.pile.toList())
    }

    @Test
    fun kdSbVmStockWasteEmptyOnWasteClick() {
        val expectedStockWasteEmptyBefore = true
        val expectedStockWasteEmptyAfter = true
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, kdVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        kdVM.stock.reset(emptyList())
        kdVM.waste.undo(listOf(tc.card10CFU, tc.card13CFU))
        kdVM.tableau.forEach { it.undo(emptyList()) }
        kdVM.onWasteClick()

        assertEquals(expectedStockWasteEmptyAfter, kdVM.stockWasteEmpty.value)
        assertEquals(expectedWasteAfter, kdVM.waste.pile.toList())
    }

    @Test
    fun kdSbVmOnTableauClick() {
        val expectedTableauPile2Before = listOf(tc.card11D, tc.card4DFU)
        val expectedTableauPile2After = listOf(tc.card11D, tc.card4DFU, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4Before = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4After = listOf(tc.card1H, tc.card10C, tc.card7SFU)

        // fill Waste with 3 Cards
        kdVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        kdVM.onWasteClick()

        assertEquals(expectedTableauPile2Before, kdVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4Before, kdVM.tableau[3].pile.toList())

        // move 2 Cards from Tableau pile 4 to Tableau pile 2
        kdVM.onTableauClick(3, 3)

        assertEquals(expectedTableauPile2After, kdVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4After, kdVM.tableau[3].pile.toList())
    }

    @Test
    fun kdSbVmUndo() {
        val expectedStock = kdVM.stock.pile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(kdVM.stock.pile[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1
        val expectedUndoEnabled = true

        sbVM.handleMoveResult(kdVM.onStockClick(1))
        sbVM.handleMoveResult(kdVM.onStockClick(1))
        kdVM.undo()
        sbVM.undo()

        assertEquals(expectedStock, kdVM.stock.pile)
        assertEquals(expectedWaste, kdVM.waste.pile)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, kdVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
    }

    @Test
    fun kdSbVmRetrieveLastGameStats() {
        val expectedLGS1 = LastGameStats(false, 0, 0, 0)
        val expectedLGS2 = LastGameStats(false, 5, 0, 1)
        val expectedLGS3 = LastGameStats(true, 5, 0, 1)

        assertEquals(expectedLGS1, sbVM.retrieveLastGameStats(false))

        // this should place A of Clubs at top of Waste pile
        kdVM.apply { for (i in 0..3) sbVM.handleMoveResult(onStockClick(1)) }
        // this should place A of Clubs to foundation pile and reward point
        sbVM.handleMoveResult(kdVM.onWasteClick())

        assertEquals(expectedLGS2, sbVM.retrieveLastGameStats(false))
        assertEquals(expectedLGS3, sbVM.retrieveLastGameStats(true))
    }

    @Test
    fun kdSbVmAutoComplete() = runTest {
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
        kdVM.stock.reset(emptyList())
        kdVM.waste.reset(emptyList())
        kdVM.tableau.forEach { it.undo(emptyList()) }
        kdVM.tableau[0].add(expectedClubs.reversed())
        kdVM.tableau[1].add(expectedDiamonds.reversed())
        kdVM.tableau[2].add(expectedHearts.reversed())
        kdVM.tableau[3].add(expectedSpades.reversed())

        // this should start autoComplete()
        launch { sbVM.handleMoveResult(kdVM.onTableauClick(0, 12)) }
        advanceUntilIdle()

        val lgs = sbVM.retrieveLastGameStats(true, kdVM.autoCompleteCorrection)

        assertEquals(expectedClubs, kdVM.foundation[0].pile.toList())
        assertEquals(expectedDiamonds, kdVM.foundation[1].pile.toList())
        assertEquals(expectedHearts, kdVM.foundation[2].pile.toList())
        assertEquals(expectedSpades, kdVM.foundation[3].pile.toList())
        assertEquals(expectedSbvmMoves, sbVM.moves.value)
        assertEquals(expectedSbvmScore, sbVM.score.value)
        assertEquals(expectedFinalMoves, lgs.moves)
        assertEquals(expectedFinalScore, lgs.score)
        assertEquals(expectedGameWon, kdVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, kdVM.autoCompleteActive.value)
    }
}