package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class WasteTest {

    private val waste = Waste()
    private val tc = TestCards

    @Test
    fun wasteAdd() {
        val expectedPile = listOf(tc.card1CFU, tc.card1HFU, tc.card12CFU)

        waste.add(listOf(tc.card1C, tc.card1H, tc.card12C))

        assertEquals(expectedPile, waste.pile)
    }

    @Test
    fun wasteUndo() {
        val expectedPile = listOf(tc.card1CFU, tc.card1HFU, tc.card12CFU)
        waste.add(listOf(tc.card1C))

        waste.undo(listOf(tc.card1C, tc.card1H, tc.card12C))

        assertEquals(expectedPile, waste.pile)
    }
    @Test
    fun wasteUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        waste.add(listOf(tc.card1C))

        waste.undo(emptyList())

        assertEquals(expectedPile, waste.pile)
    }
}