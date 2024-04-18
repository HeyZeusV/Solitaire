package com.heyzeusv.solitaire.data.tableau

import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Test

class AlaskaTableauTest {

    private val tc = TestCards

    @Test
    fun alaskaTableauAddCards() {
        val expectedKingPile = listOf(tc.card13DFU, tc.card12DFU, tc.card11DFU, tc.card10DFU)
        val expectedEmptyPile = emptyList<Card>()
        val expectedMixedPile = listOf(tc.card1HFU, tc.card12CFU, tc.card6DFU, tc.card5DFU, tc.card4DFU)
        val expectedReversedMixedPile = listOf(tc.card3CFU, tc.card4CFU, tc.card8HFU, tc.card12SFU)

        // add solo King card then 3 that should follow
        val emptyKingTableau = Tableau(GamePiles.TableauNine)
        emptyKingTableau.add(listOf(tc.card13DFU))
        emptyKingTableau.add(listOf(tc.card12DFU, tc.card11DFU, tc.card10DFU))
        assertEquals(expectedKingPile, emptyKingTableau.truePile.toList())

        // add cards without king
        val emptyTableau = Tableau(GamePiles.TableauNine)
        emptyTableau.add(listOf(tc.card12C, tc.card11H, tc.card10S))
        assertEquals(expectedEmptyPile, emptyTableau.truePile)

        // add cards to Tableau with existing cards desc
        val mixedTableau = Tableau(GamePiles.TableauNine)
        // reset is used to fill Tableau with existing cards
        mixedTableau.reset(listOf(tc.card1H, tc.card12C, tc.card6D))
        mixedTableau.add(listOf(tc.card5DFU, tc.card4DFU))
        assertEquals(expectedMixedPile, mixedTableau.truePile.toList())

        // add cards to Tableau with existing cards asc
        val reverseMixedTableau = Tableau(GamePiles.TableauNine)
        // reset is used to fill Tableau with existing cards
        reverseMixedTableau.reset(listOf(tc.card3C))
        reverseMixedTableau.add(listOf(tc.card4CFU, tc.card8HFU, tc.card12SFU))
        assertEquals(expectedReversedMixedPile, reverseMixedTableau.truePile.toList())
    }

    /**
     *  Uses same resetFaceUpAmount as YukonTableau, so tested in [YukonTableauTest]
     */
}