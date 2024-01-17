package com.heyzeusv.solitaire

import org.junit.Assert.assertEquals
import org.junit.Test

class TableauTest {

    private val tc = TestCards

    // checking that cards are correctly added
    @Test
    fun tableauAddCards() {
        val expectedKingPile = listOf(tc.card12D, tc.card11C, tc.card10H, tc.card9S)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card0H, tc.card11C, tc.card5D, tc.card4C, tc.card3H)

        // add solo King card then 3 that should follow
        val emptyKingTableau = Tableau()
        emptyKingTableau.addCards(listOf(tc.card12D))
        emptyKingTableau.addCards(listOf(tc.card11C, tc.card10H, tc.card9S))
        assertEquals(expectedKingPile, emptyKingTableau.pile)

        // add cards without king
        val emptyTableau = Tableau()
        emptyTableau.addCards(listOf(tc.card11C, tc.card10H, tc.card9S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = Tableau(mutableListOf(tc.card0H, tc.card11C, tc.card5D))
        mixedTableau.addCards(listOf(tc.card4C, tc.card3H))
        assertEquals(expectedMixedPile, mixedTableau.pile)
    }
}