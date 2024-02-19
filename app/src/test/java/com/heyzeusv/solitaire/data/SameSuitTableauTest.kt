package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.data.pile.tableau.SameSuitTableau
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SameSuitTableauTest {

    private val tc = TestCards
    private lateinit var tableau: SameSuitTableau

    @Before
    fun setUp() {
        tableau = SameSuitTableau()
    }

    @Test
    fun sameSuitTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12DFU, tc.card11DFU, tc.card10DFU)
        val expectedKingMultiSuit = false
        val expectedEmptyPile = emptyList<Card>()
        val expectedEmptyMultiSuit = false
        val expectedMixedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5DFU, tc.card4DFU)
        val expectedMixedMultiSuit = true

        // add solo King card then 3 that should follow
        val emptyKingTableau = SameSuitTableau()
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12DFU))
        emptyKingTableau.add(listOf(tc.card11DFU, tc.card10DFU))
        assertEquals(expectedKingPile, emptyKingTableau.pile)
        assertEquals(expectedKingMultiSuit, emptyKingTableau.isMultiSuit())

        // add cards without king
        val emptyTableau = SameSuitTableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)
        assertEquals(expectedEmptyMultiSuit, emptyTableau.isMultiSuit())

        // add cards to Tableau with existing cards
        val mixedTableau = SameSuitTableau(listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU))
        mixedTableau.add(listOf(tc.card5DFU, tc.card4DFU))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
        assertEquals(expectedMixedMultiSuit, mixedTableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauRemoveCards() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU)
        val expectedMultiSuit = true

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))
        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauReset() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)
        val expectedMultiSuit = true

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        val expectedMultiSuit = false

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)
        val expectedMultiSuit = false

        tableau.undo(listOf(tc.card1HFU))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)
        val expectedMultiSuit = true

        tableau.undo(listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU))

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun sameSuitTableauInOrder() {
        val inOrderTableau =
            SameSuitTableau(listOf(tc.card13DFU, tc.card12DFU, tc.card11DFU, tc.card10DFU))
        val outOrderTableau =
            SameSuitTableau(listOf(tc.card11DFU, tc.card13DFU, tc.card12DFU, tc.card10DFU))
        val expectedInOrder = true
        val expectedOutOrder = false

        assertEquals(expectedInOrder, inOrderTableau.inOrder())
        assertEquals(expectedOutOrder, outOrderTableau.inOrder())
    }
}