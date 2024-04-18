package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class StockTest {

    private val stock = Stock()
    private val tc = TestCards

    @Test
    fun stockAdd() {
        val expectedStock = mutableListOf(tc.card1C, tc.card12C, tc.card5S)

        stock.add(expectedStock)

        assertEquals(expectedStock, stock.truePile.toList())
    }

    @Test
    fun stockRemove() {
        // stock is filled when reset is called
        stock.reset(tc.deck)
        val expectedCard = stock.truePile[0]

        val actualCard = stock.remove()

        assertEquals(expectedCard, actualCard)
    }

    @Test
    fun stockRemoveMany() {
        stock.reset(tc.deck)
        val expectedCards = stock.truePile.subList(0, 3).toList()

        val actualCards = stock.removeMany(3)

        assertEquals(expectedCards, actualCards)
    }

    @Test
    fun stockReset() {
        val expectedStock = tc.deck
        stock.reset(listOf(tc.card1C, tc.card12C, tc.card5S))

        stock.reset(tc.deck)

        assertEquals(expectedStock, stock.truePile.toList())
    }

    @Test
    fun stockUndo() {
        stock.reset(tc.deck)
        val expectedStock = mutableListOf(tc.card1C, tc.card12C, tc.card5S)

        stock.undo()

        assertEquals(expectedStock, stock.truePile)
    }

    @Test
    fun stockUndoNoCards() {
        stock.reset(emptyList())

        stock.undo()

        assert(stock.truePile.isEmpty())
    }
}