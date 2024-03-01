package com.heyzeusv.solitaire.ui

import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.game.ClassicWestcliffViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.TestCards
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Random

/**
 *  [ClassicWestcliffViewModel] and [ScoreboardViewModel] are tied very close to each
 *  other, so I have decided to test both at the same time.
 */
class ClassicWestcliffViewModelTest {

    private lateinit var cwVM: ClassicWestcliffViewModel
    private lateinit var sbVM: ScoreboardViewModel

    private val tc = TestCards

    @Before
    fun setup() {
        cwVM = ClassicWestcliffViewModel(ShuffleSeed(Random(10L)))
        sbVM = ScoreboardViewModel()
    }

    @Test
    fun cwVmReset() {
        val expectedClubFoundation = listOf(tc.card1CFU)
        val expectedDiamondsFoundation = listOf(tc.card1DFU)
        val expectedHeartsFoundation = listOf(tc.card1HFU)
        val expectedSpadesFoundation = listOf(tc.card1SFU)
        val expectedTableauSize = 3

        assertEquals(expectedClubFoundation, cwVM.foundation[0].pile.toList())
        assertEquals(expectedDiamondsFoundation, cwVM.foundation[1].pile.toList())
        assertEquals(expectedHeartsFoundation, cwVM.foundation[2].pile.toList())
        assertEquals(expectedSpadesFoundation, cwVM.foundation[3].pile.toList())
        cwVM.tableau.forEach { tableau ->
            assertEquals(expectedTableauSize, tableau.pile.size)
        }
    }
}