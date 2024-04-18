package com.heyzeusv.solitaire.data.tableau

import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class EasthavenTableauTest {

    private val tc = TestCards
    private lateinit var tableau: Tableau

    @Before
    fun setUp() {
        tableau = Tableau(GamePiles.TableauNine)
    }

    @Test
    fun easthavenAddListInOrderAndAltColor() {
        val expectedList = listOf(tc.card7CFU, tc.card6DFU, tc.card5CFU, tc.card4HFU, tc.card3SFU)

        tableau.reset(listOf(tc.card7CFU, tc.card6DFU))
        tableau.add(listOf(tc.card5CFU, tc.card4HFU, tc.card3SFU))

        assertEquals(expectedList, tableau.truePile.toList())
    }

    @Test
    fun easthavenAddListNotInOrderAndAltColor() {
        val expectedList = listOf(tc.card7CFU, tc.card6DFU)

        tableau.reset(listOf(tc.card7CFU, tc.card6DFU))
        tableau.add(listOf(tc.card4SFU, tc.card3DFU, tc.card5CFU))

        assertEquals(expectedList, tableau.truePile.toList())
    }

    @Test
    fun easthavenAddListInOrderAndNotAltColor() {
        val expectedList = listOf(tc.card7CFU, tc.card6DFU)

        tableau.reset(listOf(tc.card7CFU, tc.card6DFU))
        tableau.add(listOf(tc.card5HFU, tc.card4CFU, tc.card3DFU))

        assertEquals(expectedList, tableau.truePile.toList())
    }

    @Test
    fun easthavenAddFromStock() {
        val expectedList = listOf(tc.card7CFU, tc.card6DFU, tc.card13DFU)

        tableau.reset(listOf(tc.card7CFU, tc.card6DFU))
//        tableau.addFromStock(listOf(tc.card13DFU))

        assertEquals(expectedList, tableau.truePile.toList())
    }
}