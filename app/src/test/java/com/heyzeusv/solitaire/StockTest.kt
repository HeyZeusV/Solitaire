package com.heyzeusv.solitaire

import com.heyzeusv.solitaire.data.Stock
import org.junit.Assert.assertEquals
import org.junit.Test

class StockTest {

    private val stock = Stock()
    private val tc = TestCards

    @Test
    fun stockAdd() {
        val expectedStock = mutableListOf(tc.card0C, tc.card11C, tc.card4S)

        stock.add(expectedStock)

        assertEquals(expectedStock, stock.pile.toList())
    }

    @Test
    fun stockRemove() {
        // stock is filled when reset is called
        stock.reset(tc.deck)
        val expectedCard = stock.pile[0]

        val actualCard = stock.remove()

        assertEquals(expectedCard, actualCard)
    }

    @Test
    fun stockReset() {
        val expectedStock = tc.deck
        stock.reset(listOf(tc.card0C, tc.card11C, tc.card4S))

        stock.reset(tc.deck)

        assertEquals(expectedStock, stock.pile.toList())
    }

    @Test
    fun stockUndo() {
        stock.reset(tc.deck)
        val expectedStock = mutableListOf(tc.card0C, tc.card11C, tc.card4S)

        stock.undo(expectedStock)

        assertEquals(expectedStock, stock.pile)
    }

    @Test
    fun stockUndoNoCards() {
        stock.reset(emptyList())

        stock.undo(emptyList())

        assert(stock.pile.isEmpty())
    }
}