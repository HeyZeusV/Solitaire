package com.heyzeusv.solitaire.data.tableau

import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class YukonTableauTest {

    private val tc = TestCards
    private lateinit var tableau: Tableau

    @Before
    fun setUp() {
        tableau = Tableau(GamePiles.TableauNine)
    }

    /**
     *  Uses same addCondition() as KlondikeTableau, so add() is tested in [KlondikeTableauTest].
     */

    @Test
    fun yukonTableauResetMoreThanOne() {
        val expectedPile = listOf(
            tc.card1C, tc.card8H, tc.card13D, tc.card4D, tc.card9S,
            tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU
        )
        val expectedFaceDownExists = true

        tableau.reset(listOf(
            tc.card1C, tc.card8H, tc.card13D, tc.card4D, tc.card9S,
            tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H
        ))

        assertEquals(expectedPile, tableau.truePile)
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }

    @Test
    fun yukonTableauResetOne() {
        val expectedPile = listOf(tc.card4HFU)
        val expectedFaceDownExists = false

        tableau.reset(listOf(tc.card4H))

        assertEquals(expectedPile, tableau.truePile)
        assertEquals(expectedFaceDownExists, tableau.faceDownExists())
    }
}