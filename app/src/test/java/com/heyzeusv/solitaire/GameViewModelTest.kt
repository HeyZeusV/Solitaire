package com.heyzeusv.solitaire

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.History
import com.heyzeusv.solitaire.ui.GameViewModel
import com.heyzeusv.solitaire.util.ResetOptions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GameViewModelTest {

    private val tc = TestCards

    @Test
    fun gameVMStockCreation() {
        val gameVM = GameViewModel()
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
        val gameVM = GameViewModel()
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedStock = gameVM.stock.pile.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<History>()

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

        // reset/restart options do nothing to rest of values, only to game deck order
        gameVM.reset(ResetOptions.NEW)
        assertNotEquals(expectedStock, gameVM.stock.pile)
    }

    @Test
    fun gameVMOnStockClickStockNotEmpty() {
        val gameVM = GameViewModel(10L)
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() ; removeFirst() ; removeFirst() }
        val expectedWastePile = listOf(tc.card1SFU, tc.card2DFU, tc.card1DFU)
        val expectedMoves = 3
        val expectedHistoryListSize = 3

        // draw 3 Cards
        gameVM.apply { onStockClick() ; onStockClick() ; onStockClick() }

        assertEquals(expectedStock, gameVM.stock.pile.toList())
        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
    }

    @Test
    fun gameVMOnStockClickWasteNotEmpty() {
        val gameVM = GameViewModel()
        val expectedStock = gameVM.stock.pile
        val expectedWaste = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15

        // will make stock empty
        for (i in 1..24) gameVM.onStockClick()

        gameVM.onStockClick()

        assertEquals(expectedStock, gameVM.stock.pile)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
    }

    @Test
    fun gameVMOnWasteClick() {
        val gameVM = GameViewModel(10L)
        val expectedWastePile = listOf(tc.card1SFU, tc.card2DFU)
        val expectedTableauPile = listOf(tc.card0H, tc.card9C, tc.card6S, tc.card2CFU, tc.card1DFU)

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick() ; onStockClick() ; onStockClick() }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()

        assertEquals(expectedWastePile, gameVM.waste.pile.toList())
        assertEquals(expectedTableauPile, gameVM.tableau[3].pile.toList())
    }

    @Test
    fun gameVMOnFoundationClick() {
        val gameVM = GameViewModel(10L)
        val expectedWastePile = listOf(tc.card1SFU, tc.card2DFU)
        val expectedTableauPile = listOf(tc.card0H, tc.card9C, tc.card6S, tc.card2CFU, tc.card1DFU, tc.card0CFU)
        val expectedFoundationPile = listOf(tc.card0CFU)
        val expectedScoreFirst = 1
        val expectedScoreSecond = 0

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick() ; onStockClick() ; onStockClick() }
        // this should remove top card from Waste and move it to Tableau pile #4
        gameVM.onWasteClick()
        // draw another Card and move it to Foundation Clubs pile
        gameVM.onStockClick() ; gameVM.onWasteClick()

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
        val gameVM = GameViewModel(10L)
        val expectedTableauPile2Before = listOf(tc.card10D, tc.card3DFU)
        val expectedTableauPile2After = listOf(tc.card10D, tc.card3DFU, tc.card2CFU, tc.card1DFU)
        val expectedTableauPile4Before = listOf(tc.card0H, tc.card9C, tc.card6S, tc.card2CFU, tc.card1DFU)
        val expectedTableauPile4After = listOf(tc.card0H, tc.card9C, tc.card6SFU)

        // fill Waste with 3 Cards
        gameVM.apply { onStockClick() ; onStockClick() ; onStockClick() }
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
        val gameVM = GameViewModel()
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(gameVM.stock.pile[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1

        gameVM.onStockClick()
        gameVM.onStockClick()
        gameVM.undo()

        assertEquals(expectedStock, gameVM.stock.pile)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
    }
}