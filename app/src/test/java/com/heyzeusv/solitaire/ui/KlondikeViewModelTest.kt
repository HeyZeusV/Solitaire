package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.board.GameViewModel
import com.heyzeusv.solitaire.ui.board.boards.layouts.Width1080
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
 *  [GameViewModel] and [ScoreboardViewModel] are tied very close to each other, so I have
 *  decided to test both at the same time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class KlondikeAndScoreboardViewModelTest {

    private val tc = TestCards
    private lateinit var kdVM: GameViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        kdVM = GameViewModel(
            ShuffleSeed(Random(10L)),
            Width1080(0)
        )
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun kdSbVmDeckCreation() {
        val expectedDeck = tc.deck
        var actualDeck = mutableListOf<Card>()

        // add all the cards into single list
        actualDeck.addAll(kdVM.stock.truePile)
        actualDeck.addAll(kdVM.waste.truePile)
        kdVM.foundation.forEach { actualDeck.addAll(it.truePile) }
        kdVM.tableau.forEach { actualDeck.addAll(it.truePile) }
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
        val expectedStock = kdVM.stock.truePile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<AnimateInfo>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = true

        kdVM.resetAll(ResetOptions.RESTART)

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, kdVM.stock.truePile)
        kdVM.foundation.forEach {
            assertEquals(expectedFoundation, it.truePile)
        }
        kdVM.tableau.forEachIndexed { i, tableau ->
            val expectedTableauSize = i + 1
            assertEquals(expectedTableauSize, tableau.truePile.size)
        }
        assertEquals(expectedWaste, kdVM.waste.truePile)
        assertEquals(expectedHistoryList, kdVM.historyList)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
        assertEquals(expectedGameWon, kdVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, kdVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        kdVM.resetAll(ResetOptions.NEW)
        assertNotEquals(expectedStock, kdVM.stock.truePile)
    }

    @Test
    fun kdSbVmOnStockClickStockNotEmpty() {
        val expectedStock = kdVM.stock.truePile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU, tc.card2DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 3 Cards
        sbVM.apply {
            handleMoveResult(kdVM.onStockClick())
            handleMoveResult(kdVM.onStockClick())
            handleMoveResult(kdVM.onStockClick())
        }

        assertEquals(expectedStock, kdVM.stock.truePile.toList())
        assertEquals(expectedWastePile, kdVM.waste.truePile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, kdVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, kdVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, kdVM.stockWasteEmpty.value)
    }

    @Test
    fun kdSbVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = kdVM.stock.truePile.toList()
        val expectedWastePileBefore = kdVM.stock.truePile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        kdVM.apply { for (i in 1..24) sbVM.handleMoveResult(onStockClick()) }

        assertEquals(expectedStockBefore, kdVM.stock.truePile.toList())
        assertEquals(expectedWastePileBefore, kdVM.waste.truePile.toList())

        sbVM.handleMoveResult(kdVM.onStockClick())

        assertEquals(expectedStockAfter, kdVM.stock.truePile.toList())
        assertEquals(expectedWastePileAfter, kdVM.waste.truePile.toList())
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
        kdVM.apply { onStockClick() ; onStockClick() ; onStockClick() }
        // this should remove top card from Waste and move it to Tableau pile #4
        kdVM.onWasteClick()

        assertEquals(expectedWastePile, kdVM.waste.truePile.toList())
        assertEquals(expectedTableauPile, kdVM.tableau[3].truePile.toList())
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
            handleMoveResult(kdVM.onStockClick())
            handleMoveResult(kdVM.onStockClick())
            handleMoveResult(kdVM.onStockClick())
        }
        // this should remove top card from Waste and move it to Tableau pile #4
        sbVM.handleMoveResult(kdVM.onWasteClick())
        // draw another Card and move it to Foundation Clubs pile
        sbVM.handleMoveResult(kdVM.onStockClick())
        sbVM.handleMoveResult(kdVM.onWasteClick())

        assertEquals(expectedFoundationPile, kdVM.foundation[0].truePile.toList())
        assertEquals(expectedScoreBefore, sbVM.score.value)

        // move card from Foundation Clubs pile to Tableau pile $4
        sbVM.handleMoveResult(kdVM.onFoundationClick(0))

        assertEquals(expectedWastePile, kdVM.waste.truePile.toList())
        assertEquals(expectedTableauPile, kdVM.tableau[3].truePile.toList())
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
        kdVM.waste.undo()
        kdVM.tableau.forEach { it.undo() }
        kdVM.onStockClick()

        assertEquals(expectedStockWasteEmptyAfter, kdVM.stockWasteEmpty.value)
        assertEquals(expectedStockAfter, kdVM.stock.truePile.toList())
        assertEquals(expectedWasteAfter, kdVM.waste.truePile.toList())
    }

    @Test
    fun kdSbVmStockWasteEmptyOnWasteClick() {
        val expectedStockWasteEmptyBefore = true
        val expectedStockWasteEmptyAfter = true
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, kdVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        kdVM.stock.reset(emptyList())
        kdVM.waste.undo()
        kdVM.tableau.forEach { it.undo() }
        kdVM.onWasteClick()

        assertEquals(expectedStockWasteEmptyAfter, kdVM.stockWasteEmpty.value)
        assertEquals(expectedWasteAfter, kdVM.waste.truePile.toList())
    }

    @Test
    fun kdSbVmOnTableauClick() {
        val expectedTableauPile2Before = listOf(tc.card11D, tc.card4DFU)
        val expectedTableauPile2After = listOf(tc.card11D, tc.card4DFU, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4Before = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4After = listOf(tc.card1H, tc.card10C, tc.card7SFU)

        // fill Waste with 3 Cards
        kdVM.apply { onStockClick() ; onStockClick() ; onStockClick() }
        // this should remove top card from Waste and move it to Tableau pile #4
        kdVM.onWasteClick()

        assertEquals(expectedTableauPile2Before, kdVM.tableau[1].truePile.toList())
        assertEquals(expectedTableauPile4Before, kdVM.tableau[3].truePile.toList())

        // move 2 Cards from Tableau pile 4 to Tableau pile 2
        kdVM.onTableauClick(3, 3)

        assertEquals(expectedTableauPile2After, kdVM.tableau[1].truePile.toList())
        assertEquals(expectedTableauPile4After, kdVM.tableau[3].truePile.toList())
    }

    @Test
    fun kdSbVmUndo() {
        val expectedStock = kdVM.stock.truePile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(kdVM.stock.truePile[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1
        val expectedUndoEnabled = true

        sbVM.handleMoveResult(kdVM.onStockClick())
        sbVM.handleMoveResult(kdVM.onStockClick())
        kdVM.undo()
        sbVM.undo()

        assertEquals(expectedStock, kdVM.stock.truePile)
        assertEquals(expectedWaste, kdVM.waste.truePile)
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
        kdVM.apply { for (i in 0..3) sbVM.handleMoveResult(onStockClick()) }
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
        kdVM.tableau.forEach { it.undo() }
        kdVM.tableau[0].add(expectedClubs.reversed())
        kdVM.tableau[1].add(expectedDiamonds.reversed())
        kdVM.tableau[2].add(expectedHearts.reversed())
        kdVM.tableau[3].add(expectedSpades.reversed())

        // this should start autoComplete()
        launch { sbVM.handleMoveResult(kdVM.onTableauClick(0, 12)) }
        advanceUntilIdle()

        val lgs = sbVM.retrieveLastGameStats(true, kdVM.autoCompleteCorrection)

        assertEquals(expectedClubs, kdVM.foundation[0].truePile.toList())
        assertEquals(expectedDiamonds, kdVM.foundation[1].truePile.toList())
        assertEquals(expectedHearts, kdVM.foundation[2].truePile.toList())
        assertEquals(expectedSpades, kdVM.foundation[3].truePile.toList())
        assertEquals(expectedSbvmMoves, sbVM.moves.value)
        assertEquals(expectedSbvmScore, sbVM.score.value)
        assertEquals(expectedFinalMoves, lgs.moves)
        assertEquals(expectedFinalScore, lgs.score)
        assertEquals(expectedGameWon, kdVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, kdVM.autoCompleteActive.value)
    }
}