package com.heyzeusv.solitaire

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
        val gameVM = GameViewModel()
        val expectedStock = gameVM.stock.pile.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(gameVM.stock.pile[0].copy(faceUp = true))
        val expectedMoves = 1
        val expectedHistoryListSize = 1

        gameVM.onStockClick()

        assertEquals(expectedStock, gameVM.stock.pile)
        assertEquals(expectedWaste, gameVM.waste.pile)
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

    /**
     * TODO: Implement way to manually insert certain shuffle in order to test other onClicks since
     * TODO: they rely on shuffle to work and testing will be unreliable due to randomness.
     */

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