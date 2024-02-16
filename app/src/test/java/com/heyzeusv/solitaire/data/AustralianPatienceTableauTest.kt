package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AustralianPatienceTableauTest {

    private val tc = TestCards
    private lateinit var tableau: AustralianPatienceTableau

    @Before
    fun setUp() {
        tableau = AustralianPatienceTableau()
    }

    @Test
    fun australianPatienceTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12DFU, tc.card11DFU, tc.card10DFU)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5DFU, tc.card4DFU)

        // add solo King card then 3 that should follow
        val emptyKingTableau = AustralianPatienceTableau()
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12DFU))
        emptyKingTableau.add(listOf(tc.card11DFU, tc.card10DFU))
        assertEquals(expectedKingPile, emptyKingTableau.pile)

        // add cards without king
        val emptyTableau = AustralianPatienceTableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.pile)

        // add cards to Tableau with existing cards
        val mixedTableau = AustralianPatienceTableau(listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU))
        mixedTableau.add(listOf(tc.card5DFU, tc.card4DFU))
        assertEquals(expectedMixedPile, mixedTableau.pile.toList())
    }

    @Test
    fun australianPatienceTableauRemoveCards() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU)

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))
        tableau.remove(3)

        assertEquals(expectedPile, tableau.pile.toList())
    }

    @Test
    fun australianPatienceTableauReset() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun australianPatienceTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun australianPatienceTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)

        tableau.undo(listOf(tc.card1HFU))

        assertEquals(expectedPile, tableau.pile)
    }

    @Test
    fun australianPatienceTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)

        tableau.undo(listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU))

        assertEquals(expectedPile, tableau.pile.toList())
    }
}