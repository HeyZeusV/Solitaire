package com.heyzeusv.solitaire.data.tableau

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KlondikeTableauTest {

    private val tc = TestCards
    private lateinit var tableau: Tableau

    @Before
    fun setUp() {
        tableau = Tableau(GamePiles.TableauNine)
    }

    @Test
    fun klondikeTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12CFU, tc.card11HFU, tc.card10SFU)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU, tc.card5CFU, tc.card4HFU)

        // add solo King card then 3 that should follow
        val emptyKingTableau = Tableau(GamePiles.TableauNine)
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12CFU, tc.card11HFU, tc.card10SFU))
        assertEquals(expectedKingPile, emptyKingTableau.truePile.toList())

        // add cards without king
        val emptyTableau = Tableau(GamePiles.TableauNine)
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.truePile)

        // add cards to Tableau with existing cards
        val mixedTableau = Tableau(GamePiles.TableauNine)
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D))
        mixedTableau.add(listOf(tc.card5CFU, tc.card4HFU))
        assertEquals(expectedMixedPile, mixedTableau.truePile.toList())
    }

    @Test
    fun klondikeTableauRemoveCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6DFU)
        val expectedFaceDownExists = true

        tableau.undo()
        tableau.remove(3)

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }

    @Test
    fun klondikeTableauReset() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownExists = true

        tableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H))

        assertEquals(expectedPile, tableau.truePile)
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }

    @Test
    fun klondikeTableauUndoEmptyCards() {
        val expectedPile = emptyList<Card>()
        val expectedFaceDownExists = false

        tableau.undo()

        assertEquals(expectedPile, tableau.truePile)
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }

    @Test
    fun klondikeTableauUndo1Cards() {
        val expectedPile = listOf(tc.card1HFU)
        val expectedFaceDownExists = false

        tableau.undo()

        assertEquals(expectedPile, tableau.truePile)
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }

    @Test
    fun klondikeTableauUndoMoreCards() {
        val expectedPile = listOf(tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4HFU)
        val expectedFaceDownExists = true

        tableau.undo()

        assertEquals(expectedPile, tableau.truePile.toList())
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }
}