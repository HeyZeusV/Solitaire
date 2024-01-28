package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class GameViewModelTest {

    @Test
    fun gameVMReset() {
        val gameVM = GameViewModel()
        val expectedTimer = 0L
        val expectedMoves = 0
        val expectedScore = 0
        val expectedDeck = gameVM.deck.gameDeck.toList()
        val expectedFoundation = emptyList<Card>()
        val expectedWaste = emptyList<Card>()
        val expectedHistoryList = emptyList<History>()

        gameVM.reset(ResetOptions.RESTART)

        assertEquals(expectedTimer, gameVM.timer.value)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedScore, gameVM.score.value)
        assertEquals(expectedDeck, gameVM.deck.gameDeck)
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
        assertNotEquals(expectedDeck, gameVM.deck.gameDeck)
    }

    @Test
    fun gameVMOnDeckClickGameDeckNotEmpty() {
        val gameVM = GameViewModel()
        val expectedDeck = gameVM.deck.gameDeck.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(gameVM.deck.gameDeck[0].copy(faceUp = true))
        val expectedMoves = 1
        val expectedHistoryListSize = 1

        gameVM.onDeckClick()

        assertEquals(expectedDeck, gameVM.deck.gameDeck)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
    }

    @Test
    fun gameVMOnDeckClickWasteNotEmpty() {
        val gameVM = GameViewModel()
        val expectedDeck = gameVM.deck.gameDeck
        val expectedWaste = emptyList<Card>()
        val expectedMoves = 25
        val expectedHistoryListSize = 15

        // will make gameDeck empty
        for (i in 1..24) gameVM.onDeckClick()

        gameVM.onDeckClick()

        assertEquals(expectedDeck, gameVM.deck.gameDeck)
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
        val expectedDeck = gameVM.deck.gameDeck.toMutableList().apply { removeFirst() }
        val expectedWaste = listOf(gameVM.deck.gameDeck[0].copy(faceUp = true))
        val expectedMoves = 3
        val expectedHistoryListSize = 1

        gameVM.onDeckClick()
        gameVM.onDeckClick()
        gameVM.undo()

        assertEquals(expectedDeck, gameVM.deck.gameDeck)
        assertEquals(expectedWaste, gameVM.waste.pile)
        assertEquals(expectedMoves, gameVM.moves.value)
        assertEquals(expectedHistoryListSize, gameVM.historyList.size)
    }
}