package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.PileHistory
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.TestCards
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [AustralianPatienceViewModel] and [ScoreboardViewModel] are tied very close to each other,
 *  so I have decided to test both at the same time.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AustralianPatienceAndScoreboardViewModelTest {

    private val tc = TestCards
    private lateinit var apVM: AustralianPatienceViewModel
    private lateinit var sbVM: ScoreboardViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        apVM = AustralianPatienceViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun apSbVmVmDeckCreation() {
        val expectedDeck = tc.deck
        var actualDeck = mutableListOf<Card>()

        // add all the cards into single list
        actualDeck.addAll(apVM.stock.pile)
        actualDeck.addAll(apVM.waste.pile)
        apVM.foundation.forEach { actualDeck.addAll(it.pile) }
        apVM.tableau.forEach { actualDeck.addAll(it.pile) }
        // make sure that are face down
        actualDeck = actualDeck.map { it.copy(faceUp = false) }.toMutableList()
        // sort
        actualDeck = actualDeck.sortedWith(compareBy({ it.suit }, { it.value })).toMutableList()

        assertEquals(expectedDeck, actualDeck)
    }

    @Test
    fun apSbVmVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = apVM.stock.pile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<PileHistory>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = false

        apVM.resetAll(ResetOptions.RESTART)

        assertEquals(expectedTimer, sbVM.time.value)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedScore, sbVM.score.value)
        assertEquals(expectedStock, apVM.stock.pile)
        apVM.foundation.forEach {
            assertEquals(expectedFoundation, it.pile)
        }
        apVM.tableau.forEach{ tableau ->
            val expectedTableauSize = 4
            assertEquals(expectedTableauSize, tableau.pile.size)
        }
        assertEquals(expectedWaste, apVM.waste.pile)
        assertEquals(expectedHistoryList, apVM.historyList)
        assertEquals(expectedUndoEnabled, apVM.undoEnabled.value)
        assertEquals(expectedGameWon, apVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, apVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, apVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        apVM.resetAll(ResetOptions.NEW)
        assertNotEquals(expectedStock, apVM.stock.pile)
    }

    @Test
    fun apSbVmVmOnStockClickStockNotEmpty() {
        val expectedStock = apVM.stock.pile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU, tc.card2DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 3 Cards
        sbVM.apply {
            handleMoveResult(apVM.onStockClick(1))
            handleMoveResult(apVM.onStockClick(1))
            handleMoveResult(apVM.onStockClick(1))
        }

        assertEquals(expectedStock, apVM.stock.pile.toList())
        assertEquals(expectedWastePile, apVM.waste.pile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, apVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, apVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, apVM.stockWasteEmpty.value)
    }

    @Test
    fun apSbVmVmOnStockClickStockEmpty() {
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

        assertEquals(expectedStockBefore, apVM.stock.pile.toList())
        assertEquals(expectedWastePileBefore, apVM.waste.pile.toList())

        sbVM.handleMoveResult(apVM.onStockClick(1))

        assertEquals(expectedStockAfter, apVM.stock.pile.toList())
        assertEquals(expectedWastePileAfter, apVM.waste.pile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, apVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, apVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, apVM.stockWasteEmpty.value)
    }

    @Test
    fun apSbVmVmOnWasteClick() {
        val expectedWastePile = listOf(
            tc.card2SFU, tc.card3DFU, tc.card2DFU, tc.card1CFU, tc.card13SFU,
            tc.card5DFU, tc.card3HFU, tc.card11CFU, tc.card9HFU, tc.card4CFU
        )
        val expectedTableauPile =
            listOf(tc.card9CFU, tc.card2HFU, tc.card13DFU, tc.card12HFU, tc.card11HFU)
        val expectedStockWasteEmpty = false
        val expectedMoves = 12

        // fill Waste with 11 Cards
        apVM.apply { for (i in 1..11) sbVM.handleMoveResult(onStockClick(1)) }
        // this should remove top card from Waste and move it to Tableau pile #4
        sbVM.handleMoveResult(apVM.onWasteClick())

        assertEquals(expectedWastePile, apVM.waste.pile.toList())
        assertEquals(expectedTableauPile, apVM.tableau[3].pile.toList())
        assertEquals(expectedStockWasteEmpty, apVM.stockWasteEmpty.value)
        assertEquals(expectedMoves, sbVM.moves.value)
    }

    @Test
    fun apSbVmVmOnFoundationClick() {
        val expectedWastePile = listOf(
            tc.card2SFU, tc.card3DFU, tc.card2DFU, tc.card1CFU,
            tc.card13SFU, tc.card5DFU, tc.card3HFU
        )
        val expectedTableauPile = listOf(
            tc.card13HFU, tc.card6DFU, tc.card2CFU, tc.card12CFU, tc.card11CFU,
            tc.card10CFU, tc.card9CFU, tc.card2HFU, tc.card1HFU
        )
        val expectedFoundationPile = listOf(tc.card1HFU)
        val expectedScoreBefore = 1
        val expectedScoreAfter = 0
        val expectedMoves = 14

        // fill Waste with 8 Cards
        sbVM.apply { for (i in 1..8) handleMoveResult(apVM.onStockClick(1)) }
        // this should remove top card from Waste and move it to Tableau pile #7
        sbVM.handleMoveResult(apVM.onWasteClick())
        // moves entire pile to Tableau pile #2
        sbVM.handleMoveResult(apVM.onTableauClick(3, 0))
        // moves 2 cards to Tableau pile #4
        sbVM.handleMoveResult(apVM.onTableauClick(1, 6))
        // moves 3 cards to Tableau pile #7
        sbVM.handleMoveResult(apVM.onTableauClick(1, 3))
        // moves A of Hearts to Foundation
        sbVM.handleMoveResult(apVM.onTableauClick(1, 2))

        assertEquals(expectedFoundationPile, apVM.foundation[2].pile.toList())
        assertEquals(expectedScoreBefore, sbVM.score.value)

        // move card from Foundation Hearts pile to Tableau pile $7
        sbVM.handleMoveResult(apVM.onFoundationClick(2))

        assertEquals(expectedWastePile, apVM.waste.pile.toList())
        assertEquals(expectedTableauPile, apVM.tableau[6].pile.toList())
        assertEquals(expectedScoreAfter, sbVM.score.value)
        assertEquals(expectedMoves, sbVM.moves.value)
    }

    @Test
    fun apSbVmVmStockWasteEmptyOnStockClick() {
        val expectedStockWasteEmptyBefore = false
        val expectedStockWasteEmptyAfter = true
        val expectedStockAfter = emptyList<Card>()
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, apVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        apVM.stock.reset(listOf(tc.card10C))
        apVM.waste.undo(emptyList())
        apVM.tableau.forEach { it.undo(emptyList()) }
        apVM.onStockClick(1)

        assertEquals(expectedStockWasteEmptyAfter, apVM.stockWasteEmpty.value)
        assertEquals(expectedStockAfter, apVM.stock.pile.toList())
        assertEquals(expectedWasteAfter, apVM.waste.pile.toList())
    }

    @Test
    fun apSbVmVmStockWasteEmptyOnWasteClick() {
        val expectedStockWasteEmptyBefore = false
        val expectedStockWasteEmptyAfter = true
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, apVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        apVM.stock.reset(emptyList())
        apVM.waste.undo(listOf(tc.card10CFU, tc.card13CFU))
        apVM.tableau.forEach { it.undo(emptyList()) }
        apVM.onWasteClick()

        assertEquals(expectedStockWasteEmptyAfter, apVM.stockWasteEmpty.value)
        assertEquals(expectedWasteAfter, apVM.waste.pile.toList())
    }

    @Test
    fun apSbVmVmOnTableauClick() {
        val expectedTableauPile2Before = listOf(tc.card9DFU, tc.card7DFU, tc.card1HFU, tc.card10CFU)
        val expectedTableauPile2After = listOf(
            tc.card9DFU, tc.card7DFU, tc.card1HFU, tc.card10CFU,
            tc.card9CFU, tc.card2HFU, tc.card13DFU, tc.card12HFU
        )
        val expectedTableauPile4Before = listOf(tc.card9CFU, tc.card2HFU, tc.card13DFU, tc.card12HFU)
        val expectedTableauPile4After = emptyList<Card>()
        val expectedMoves = 1

        assertEquals(expectedTableauPile2Before, apVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4Before, apVM.tableau[3].pile.toList())

        // move 2 Cards from Tableau pile 4 to Tableau pile 2
        sbVM.handleMoveResult(apVM.onTableauClick(3, 0))

        assertEquals(expectedTableauPile2After, apVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4After, apVM.tableau[3].pile.toList())
        assertEquals(expectedMoves, sbVM.moves.value)
    }

    @Test
    fun apSbVmVmUndo() {
        val expectedStock = apVM.stock.pile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(apVM.stock.pile[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1
        val expectedUndoEnabled = true

        sbVM.handleMoveResult(apVM.onStockClick(1))
        sbVM.handleMoveResult(apVM.onStockClick(1))
        apVM.undo()
        sbVM.undo()

        assertEquals(expectedStock, apVM.stock.pile)
        assertEquals(expectedWaste, apVM.waste.pile)
        assertEquals(expectedMoves, sbVM.moves.value)
        assertEquals(expectedHistoryListSize, apVM.historyList.size)
        assertEquals(expectedHistoryListSize, sbVM.historyList.size)
        assertEquals(expectedUndoEnabled, apVM.undoEnabled.value)
    }

    @Test
    fun apSbVmVmRetrieveLastGameStats() {
        val expectedLGS1 = LastGameStats(false, 0, 0, 0)
        val expectedLGS2 = LastGameStats(false, 5, 0, 1)
        val expectedLGS3 = LastGameStats(true, 5, 0, 1)

        assertEquals(expectedLGS1, sbVM.retrieveLastGameStats(false))

        // this should place A of Clubs at top of Waste pile
        apVM.apply { for (i in 0..3) sbVM.handleMoveResult(onStockClick(1)) }
        // this should place A of Clubs to foundation pile and reward point
        sbVM.handleMoveResult(apVM.onWasteClick())

        assertEquals(expectedLGS2, sbVM.retrieveLastGameStats(false))
        assertEquals(expectedLGS3, sbVM.retrieveLastGameStats(true))
    }

//    @OptIn(ExperimentalCoroutinesApi::class)
//    @Test
//    fun apSbVmVmAutoComplete() = runTest {
//        TODO("Once it is implemented in ViewModel")
//    }
}