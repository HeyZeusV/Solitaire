package com.heyzeusv.solitaire.data

import com.heyzeusv.solitaire.data.pile.tableau.YukonTableau
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class YukonTableauTest {

    private val tc = TestCards
    private lateinit var tableau: YukonTableau

    @Before
    fun setUp() {
        tableau = YukonTableau()
    }

    @Test
    fun yukonTableauResetMoreThanOne() {
        val expectedPile = listOf(
            tc.card1C, tc.card8H, tc.card13D, tc.card4D, tc.card9S,
            tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU
        )
        val expectedFaceDownCards = 5

        tableau.reset(listOf(
            tc.card1C, tc.card8H, tc.card13D, tc.card4D, tc.card9S,
            tc.card1H, tc.card12C, tc.card6D, tc.card5C, tc.card4H
        ))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }

    @Test
    fun yukonTableauResetOne() {
        val expectedPile = listOf(tc.card4HFU)
        val expectedFaceDownCards = 0

        tableau.reset(listOf(tc.card4H))

        assertEquals(expectedPile, tableau.pile)
        assertEquals(expectedFaceDownCards, tableau.faceDownCards)
    }
}