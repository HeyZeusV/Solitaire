package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.History
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ShuffleSeed
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

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {


    private val tc = TestCards
    private lateinit var gameVM: GameViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        gameVM = GameViewModel(ShuffleSeed(Random(10L)))
    }

    @Test
    fun gameVmDeckCreation() {
        val expectedDeck = tc.deck
        var actualDeck = mutableListOf<Card>()

        // add all the cards into single list
        actualDeck.addAll(gameVM.stock.pile)
        actualDeck.addAll(gameVM.waste.pile)
        gameVM.foundation.forEach { actualDeck.addAll(it.pile) }
        gameVM.tableau.forEach { actualDeck.addAll(it.pile) }
        // make sure that are face down
        actualDeck = actualDeck.map { it.copy(faceUp = false) }.toMutableList()
        // sort
        actualDeck = actualDeck.sortedWith(compareBy({ it.suit }, { it.value })).toMutableList()

        assertEquals(expectedDeck, actualDeck)
    }

    @Test
    fun gameVmReset() {
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = gameVM.stock.pile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<History>()
        val expectedUndoEnabled = false
        val expectedGameWon = false
        val expectedAutoCompleteActive = false
        val expectedStockWasteEmpty = false

        gameVM.reset(ResetOptions.RESTART)

        assertEquals(expectedTimer, gameVM.timer.value)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedScore, gameVM.score.value)
        assertEquals(expectedStock, gameVM.stock.pile)
        gameVM.foundation.forEach {
            assertEquals(expectedFoundation, it.pile)
        }
        gameVM.tableau.forEachIndexed { i, tableau ->
            val expectedTableauSize = i + 1
            assertEquals(expectedTableauSize, tableau.pile.size)
        }
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedHistoryList, gameVM.historyList)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
        assertEquals(expectedGameWon, gameVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, gameVM.autoCompleteActive.value)
        assertEquals(expectedStockWasteEmpty, gameVM.stockWasteEmpty.value)

        // reset/restart options do nothing to rest of values, only to game deck order
        gameVM.reset(ResetOptions.NEW)
        assertNotEquals(expectedStock, gameVM.stock.pile)
    }

    @Test
    fun gameVmOnStockClickStockNotEmpty() {
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU, tc.card2DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }

        assertEquals(expectedStock, gameVM.stock.pile.toList())
        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, gameVM.stockWasteEmpty.value)
    }

    @Test
    fun gameVmOnStockClickStockEmpty() {
        val expectedStockBefore = emptyList<Card>()
        val expectedStockAfter = gameVM.stock.pile.toList()
        val expectedWastePileBefore = gameVM.stock.pile.map { it.copy(faceUp = true) }
        val expectedWastePileAfter = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true
        val expectedStockWasteEmpty = false

        // draw 24 Cards
        gameVM.apply { for (i in 1..24) onStockClick(1) }

        assertEquals(expectedStockBefore, gameVM.stock.pile.toList())
        assertEquals(expectedWastePileBefore, gameVM.waste.pile.toList())

        gameVM.onStockClick(1)

        assertEquals(expectedStockAfter, gameVM.stock.pile.toList())
        assertEquals(expectedWastePileAfter, gameVM.waste.pile.toList())
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
        assertEquals(expectedStockWasteEmpty, gameVM.stockWasteEmpty.value)
    }

    @Test
    fun gameVmOnWasteClick() {
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU)
        val expectedTableauPile = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedStockWasteEmpty = false

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()

        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedTableauPile, gameVM.tableau[3].pile.toList())
        assertEquals(expectedStockWasteEmpty, gameVM.stockWasteEmpty.value)
    }

    @Test
    fun gameVmOnFoundationClick() {
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU)
        val expectedTableauPile = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU, tc.card1CFU)
        val expectedFoundationPile = listOf(tc.card1CFU)
        val expectedScoreFirst = 1
        val expectedScoreSecond = 0

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()
        // draw another Card and move it to Foundation Clubs pile
        gameVM.onStockClick(1) ; gameVM.onWasteClick()

        assertEquals(expectedFoundationPile, gameVM.foundation[0].pile.toList())
        assertEquals(expectedScoreFirst, gameVM.score.value)

        // move card from Foundation Clubs pile to Tableau pile $4
        gameVM.onFoundationClick(0)

        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedTableauPile, gameVM.tableau[3].pile.toList())
        assertEquals(expectedScoreSecond, gameVM.score.value)
    }

    @Test
    fun gameVmStockWasteEmptyOnStockClick() {
        val expectedStockWasteEmptyBefore = false
        val expectedStockWasteEmptyAfter = true
        val expectedStockAfter = emptyList<Card>()
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, gameVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        gameVM.stock.reset(listOf(tc.card10C))
        gameVM.waste.undo(emptyList())
        gameVM.tableau.forEach { it.undo(emptyList()) }
        gameVM.onStockClick(1)

        assertEquals(expectedStockWasteEmptyAfter, gameVM.stockWasteEmpty.value)
        assertEquals(expectedStockAfter, gameVM.stock.pile.toList())
        assertEquals(expectedWasteAfter, gameVM.waste.pile.toList())
    }

    @Test
    fun gameVmStockWasteEmptyOnWasteClick() {
        val expectedStockWasteEmptyBefore = false
        val expectedStockWasteEmptyAfter = true
        val expectedWasteAfter = listOf(tc.card10CFU)

        assertEquals(expectedStockWasteEmptyBefore, gameVM.stockWasteEmpty.value)

        // giving each just 1 card and Tableau empty for testing
        gameVM.stock.reset(emptyList())
        gameVM.waste.undo(listOf(tc.card10CFU, tc.card13CFU))
        gameVM.tableau.forEach { it.undo(emptyList()) }
        gameVM.onWasteClick()

        assertEquals(expectedStockWasteEmptyAfter, gameVM.stockWasteEmpty.value)
        assertEquals(expectedWasteAfter, gameVM.waste.pile.toList())
    }

    @Test
    fun gameVmOnTableauClick() {
        val expectedTableauPile2Before = listOf(tc.card11D, tc.card4DFU)
        val expectedTableauPile2After = listOf(tc.card11D, tc.card4DFU, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4Before = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedTableauPile4After = listOf(tc.card1H, tc.card10C, tc.card7SFU)

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()

        assertEquals(expectedTableauPile2Before, gameVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4Before, gameVM.tableau[3].pile.toList())

        // move 2 Cards from Tableau pile 4 to Tableau pile 2
        gameVM.onTableauClick(3, 3)

        assertEquals(expectedTableauPile2After, gameVM.tableau[1].pile.toList())
        assertEquals(expectedTableauPile4After, gameVM.tableau[3].pile.toList())
    }

    @Test
    fun gameVmUndo() {
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(gameVM.stock.pile[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1
        val expectedUndoEnabled = true

        gameVM.onStockClick(1)
        gameVM.onStockClick(1)
        gameVM.undo()

        assertEquals(expectedStock, gameVM.stock.pile)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
    }

    @Test
    fun gameVmRetrieveLastGameStats() {
        val expectedLGS1 = LastGameStats(false, 0, 0, 0)
        val expectedLGS2 = LastGameStats(false, 5, 0, 1)
        val expectedLGS3 = LastGameStats(true, 5, 0, 1)

        assertEquals(expectedLGS1, gameVM.retrieveLastGameStats(false))

        // this should place A of Clubs at top of Waste pile
        gameVM.apply { for (i in 0..3) onStockClick(1) }
        // this should place A of Clubs to foundation pile and reward point
        gameVM.onWasteClick()

        assertEquals(expectedLGS2, gameVM.retrieveLastGameStats(false))
        assertEquals(expectedLGS3, gameVM.retrieveLastGameStats(true))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun gameVmAutoComplete() = runTest {
        val expectedClubs = listOf(
            tc.card1CFU, tc.card2CFU, tc.card3CFU, tc.card4CFU, tc.card5CFU, tc.card6CFU, tc.card7CFU,
            tc.card8CFU, tc.card9CFU, tc.card10CFU, tc.card11CFU, tc.card12CFU, tc.card13CFU
        )
        val expectedDiamonds = listOf(
            tc.card1DFU, tc.card2DFU, tc.card3DFU, tc.card4DFU, tc.card5DFU, tc.card6DFU, tc.card7DFU,
            tc.card8DFU, tc.card9DFU, tc.card10DFU, tc.card11DFU, tc.card12DFU, tc.card13DFU
        )
        val expectedHearts = listOf(
            tc.card1HFU, tc.card2HFU, tc.card3HFU, tc.card4HFU, tc.card5HFU, tc.card6HFU, tc.card7HFU,
            tc.card8HFU, tc.card9HFU, tc.card10HFU, tc.card11HFU, tc.card12HFU, tc.card13HFU
        )
        val expectedSpades = listOf(
            tc.card1SFU, tc.card2SFU, tc.card3SFU, tc.card4SFU, tc.card5SFU, tc.card6SFU, tc.card7SFU,
            tc.card8SFU, tc.card9SFU, tc.card10SFU, tc.card11SFU, tc.card12SFU, tc.card13SFU
        )
        val expectedMoves = 52
        val expectedScore = 52
        val expectedGameWon = true
        val expectedAutoCompleteActive = false

        // going to cheat and give lists that are ready to auto complete
        gameVM.stock.reset(emptyList())
        gameVM.waste.reset(emptyList())
        gameVM.tableau.forEach { it.undo(emptyList()) }
        gameVM.tableau[0].add(expectedClubs.reversed())
        gameVM.tableau[1].add(expectedDiamonds.reversed())
        gameVM.tableau[2].add(expectedHearts.reversed())
        gameVM.tableau[3].add(expectedSpades.reversed())

        // this should start autoComplete()
        launch { gameVM.onTableauClick(0, 12) }
        advanceUntilIdle()

        assertEquals(expectedClubs, gameVM.foundation[0].pile.toList())
        assertEquals(expectedDiamonds, gameVM.foundation[1].pile.toList())
        assertEquals(expectedHearts, gameVM.foundation[2].pile.toList())
        assertEquals(expectedSpades, gameVM.foundation[3].pile.toList())
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedScore, gameVM.score.value)
        assertEquals(expectedGameWon, gameVM.gameWon.value)
        assertEquals(expectedAutoCompleteActive, gameVM.autoCompleteActive.value)
    }
}