package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Tableau
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class TableauTest {

    private val tc = TestCards

    @Test
    fun tableauAddCards() {
        val expectedKingPile = listOf(tc.card13D, tc.card12C, tc.card11H, tc.card10S)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU, tc.card5C, tc.card4H)

        // add solo King card then 3 that should follow
        val emptyKingTableau = Tableau()
        emptyKingTableau.add(listOf(tc.card13D))
        emptyKingTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedKingPile, emptyKingTableau.pile)

        // add cards without king
        val emptyTableau = Tableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = Tableau()
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D))
        mixedTableau.add(listOf(tc.card5C, tc.card4H))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
    }

    @Test
    fun tableauRemoveCards() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU)
        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
    }

    @Test
    fun tableauReset() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

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
        val expectedPile = listOf(tc.card1H)

        tableau.undo(listOf(tc.card1H))

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun tableauUndoMoreCards() {
        val tableau = Tableau()
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H)

        tableau.undo(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.pile.toList())
    }
}