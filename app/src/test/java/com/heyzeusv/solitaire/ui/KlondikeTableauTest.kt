package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.KlondikeTableau
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KlondikeTableauTest {

    private val tc = TestCards
    private lateinit var tableau: KlondikeTableau

    @Before
    fun setUp() {
        tableau = KlondikeTableau()
    }

    @Test
    fun klondikeTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12CFU, tc.card11HFU, tc.card10SFU)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU, tc.card5CFU, tc.card4HFU)

        // add solo King card then 3 that should follow
        val emptyKingTableau = KlondikeTableau()
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12CFU, tc.card11HFU, tc.card10SFU))
        assertEquals(expectedKingPile, emptyKingTableau.pile.toList())

        // add cards without king
        val emptyTableau = KlondikeTableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = KlondikeTableau()
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D))
        mixedTableau.add(listOf(tc.card5CFU, tc.card4HFU))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
    }

    @Test
    fun klondikeTableauRemoveCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU)
        val expectedFaceDownCards = 2

        tableau.undo(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5CFU, tc.card4HFU))
        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun klondikeTableauReset() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownCards = 4

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun klondikeTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        val expectedFaceDownCards = 0

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun klondikeTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)
        val expectedFaceDownCards = 0

        tableau.undo(listOf(tc.card1HFU))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun klondikeTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownCards = 4

        tableau.undo(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU))

        assertEquals(expectedPile, tableau.pile.toList())
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }
}