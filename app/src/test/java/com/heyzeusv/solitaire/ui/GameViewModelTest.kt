package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.History
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import java.util.Random

class GameViewModelTest {

    private val tc = TestCards

    @Test
    fun gameVMStockCreation() {
        val gameVM = GameViewModel(ShuffleSeed(Random()))
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
    fun gameVMReset() {
        val gameVM = GameViewModel(ShuffleSeed(Random()))
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = gameVM.stock.pile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<History>()
        val expectedUndoEnabled = false

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

        // reset/restart options do nothing to rest of values, only to game deck order
        gameVM.reset(ResetOptions.NEW)
        assertNotEquals(expectedStock, gameVM.stock.pile)
    }

    @Test
    fun gameVMOnStockClickStockNotEmpty() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU, tc.card2DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3
        val expectedUndoEnabled = true

        // draw 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }

        assertEquals(expectedStock, gameVM.stock.pile.toList())
        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
    }

    @Test
    fun gameVMOnStockClickWasteNotEmpty() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
        val expectedStock = gameVM.stock.pile
        val expectedWaste = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15
        val expectedUndoEnabled = true

        // will make stock empty
        for (i in 1..24) gameVM.onStockClick(1)

        gameVM.onStockClick(1)

        assertEquals(expectedStock, gameVM.stock.pile)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
        assertEquals(expectedUndoEnabled, gameVM.undoEnabled.value)
    }

    @Test
    fun gameVMOnWasteClick() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
        val expectedWastePile = listOf(tc.card2SFU, tc.card3DFU)
        val expectedTableauPile = listOf(tc.card1H, tc.card10C, tc.card7S, tc.card3CFU, tc.card2DFU)
        val expectedWasteEmpty = false

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick(1) ; onStockClick(1) ; onStockClick(1) }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()

        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedTableauPile, gameVM.tableau[3].pile.toList())
        assertEquals(expectedWasteEmpty, gameVM.stockWasteEmpty.value)
    }

    @Test
    fun gameVMOnFoundationClick() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
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
    fun gameVMOnTableauClick() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
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
    fun gameVMUndo() {
        val gameVM = GameViewModel(ShuffleSeed(Random()))
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
    fun gameVMRetrieveLastGameStats() {
        val gameVM = GameViewModel(ShuffleSeed(Random(10L)))
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
}