package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Waste
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class WasteTest {

    private val waste = Waste()
    private val tc = TestCards

    @Test
    fun wasteAdd() {
        val expectedPile = listOf(tc.card0C.copy(faceUp = true), tc.card0H.copy(faceUp = true), tc.card11C.copy(faceUp = true))

        waste.add(listOf(tc.card0C, tc.card0H, tc.card11C))

        assertEquals(expectedPile, waste.pile)
    }

    @Test
    fun wasteUndo() {
        val expectedPile = listOf(tc.card0C.copy(faceUp = true), tc.card0H.copy(faceUp = true), tc.card11C.copy(faceUp = true))
        waste.add(listOf(tc.card0C))

        waste.undo(listOf(tc.card0C, tc.card0H, tc.card11C))

        assertEquals(expectedPile, waste.pile)
    }
    @Test
    fun wasteUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        waste.add(listOf(tc.card0C))

        waste.undo(emptyList())

        assertEquals(expectedPile, waste.pile)
    }
}