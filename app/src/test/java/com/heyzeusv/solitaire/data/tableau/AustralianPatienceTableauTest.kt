package com.heyzeusv.solitaire.data.tableau

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Tableau.AustralianPatienceTableau
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
        val expectedKingMultiSuit = false
        val expectedEmptyPile = emptyList<Card>()
        val expectedEmptyMultiSuit = false
        val expectedMixedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5DFU, tc.card4DFU)
        val expectedMixedMultiSuit = true

        // add solo King card then 3 that should follow
        val emptyKingTableau = AustralianPatienceTableau()
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12DFU))
        emptyKingTableau.add(listOf(tc.card11DFU, tc.card10DFU))
        assertEquals(expectedKingPile, emptyKingTableau.truePile)
        assertEquals(expectedKingMultiSuit, emptyKingTableau.isMultiSuit())

        // add cards without king
        val emptyTableau = AustralianPatienceTableau()
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.truePile)
        assertEquals(expectedEmptyMultiSuit, emptyTableau.isMultiSuit())

        // add cards to Tableau with existing cards
        val mixedTableau = AustralianPatienceTableau(
            listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU)
        )
        mixedTableau.add(listOf(tc.card5DFU, tc.card4DFU))
        assertEquals(expectedMixedPile, mixedTableau.truePile.toList())
        assertEquals(expectedMixedMultiSuit, mixedTableau.isMultiSuit())
    }

    @Test
    fun australianPatienceTableauReset() {
        val expectedPile = listOf(tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)
        val expectedMultiSuit = true

        tableau.reset(listOf(tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun australianPatienceTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        val expectedMultiSuit = false

        tableau.undo(emptyList())

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun australianPatienceTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)
        val expectedMultiSuit = false

        tableau.undo(listOf(tc.card1HFU))

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun australianPatienceTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU)
        val expectedMultiSuit = true

        tableau.undo(listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU))

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedMultiSuit, tableau.isMultiSuit())
    }

    @Test
    fun australianPatienceTableauInOrder() {
        val inOrderTableau =
            AustralianPatienceTableau(listOf(tc.card13DFU, tc.card12DFU, tc.card11DFU, tc.card10DFU))
        val outOrderTableau =
            AustralianPatienceTableau(listOf(tc.card11DFU, tc.card13DFU, tc.card12DFU, tc.card10DFU))
        val expectedInOrder = false
        val expectedOutOrder = true

        assertEquals(expectedInOrder, inOrderTableau.notInOrder())
        assertEquals(expectedOutOrder, outOrderTableau.notInOrder())
    }
}