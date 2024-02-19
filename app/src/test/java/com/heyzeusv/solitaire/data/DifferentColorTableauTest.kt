package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.data.pile.tableau.DifferentColorTableau
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class DifferentColorTableauTest {

    private val tc = TestCards
    private lateinit var tableau: DifferentColorTableau

    @Before
    fun setUp() {
        tableau = DifferentColorTableau()
    }

    @Test
    fun differentColorTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12CFU, tc.card11HFU, tc.card10SFU)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU, tc.card5CFU, tc.card4HFU)

        // add solo King card then 3 that should follow
        val emptyKingTableau = DifferentColorTableau()
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12CFU, tc.card11HFU, tc.card10SFU))
        assertEquals(expectedKingPile, emptyKingTableau.pile.toList())

        // add cards without king
        val emptyTableau = DifferentColorTableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = DifferentColorTableau()
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D))
        mixedTableau.add(listOf(tc.card5CFU, tc.card4HFU))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
    }

    @Test
    fun differentColorTableauRemoveCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU)
        val expectedFaceDownCards = 2

        tableau.undo(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5CFU, tc.card4HFU))
        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun differentColorTableauReset() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownCards = 4

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun differentColorTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        val expectedFaceDownCards = 0

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun differentColorTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)
        val expectedFaceDownCards = 0

        tableau.undo(listOf(tc.card1HFU))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun differentColorTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownCards = 4

        tableau.undo(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU))

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }
}