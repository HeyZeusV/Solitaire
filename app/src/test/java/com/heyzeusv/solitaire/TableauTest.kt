package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class TableauTest {

    private val tc = TestCards

    @Test
    fun tableauAddCards() {
        val expectedKingPile = listOf(tc.card12D, tc.card11C, tc.card10H, tc.card9S)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card0H, tc.card11C, tc.card5D.copy(faceUp = true), tc.card4C, tc.card3H)

        // add solo King card then 3 that should follow
        val emptyKingTableau = Tableau()
        emptyKingTableau.add(listOf(tc.card12D))
        emptyKingTableau.add(listOf(tc.card11C, tc.card10H, tc.card9S))
        assertEquals(expectedKingPile, emptyKingTableau.pile)

        // add cards without king
        val emptyTableau = Tableau()
        emptyTableau.add(listOf(tc.card11C, tc.card10H, tc.card9S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = Tableau()
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card0H, tc.card11C, tc.card5D))
        mixedTableau.add(listOf(tc.card4C, tc.card3H))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
    }

    @Test
    fun tableauRemoveCards() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card0H, tc.card11C, tc.card5D.copy(faceUp = true))
        tableau.reset(listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H))

        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
    }

    @Test
    fun tableauReset() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H.copy(faceUp = true))

        tableau.reset(listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H))

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun tableauUndoEmptyCards() {
        val tableau = Tableau()
        val expectedPile = emptyList<Card>()

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun tableauUndo1Cards() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card0H)

        tableau.undo(listOf(tc.card0H))

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun tableauUndoMoreCards() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H)

        tableau.undo(listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H))

        assertEquals(expectedPile, tableau.pile.toList())
    }
}