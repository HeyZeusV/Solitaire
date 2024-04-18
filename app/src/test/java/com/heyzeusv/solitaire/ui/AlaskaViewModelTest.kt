package com.heyzeusv.solitaire.ui

//import com.heyzeusv.solitaire.board.piles.ShuffleSeed
//import com.heyzeusv.solitaire.ui.board.AlaskaViewModel
//import com.heyzeusv.solitaire.ui.board.YukonViewModel
//import com.heyzeusv.solitaire.ui.board.scoreboard.ScoreboardLogic
//import com.heyzeusv.solitaire.util.TestCards
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.test.StandardTestDispatcher
//import kotlinx.coroutines.test.advanceUntilIdle
//import kotlinx.coroutines.test.runTest
//import kotlinx.coroutines.test.setMain
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Test
//import java.util.Random
//
///**
// *  [AlaskaViewModel] and [ScoreboardLogic] are tied very close to each other, so I have
// *  decided to test both at the same time.
// */
//@OptIn(ExperimentalCoroutinesApi::class)
//class AlaskaViewModelTest {
//
//    private val tc = TestCards
//    private lateinit var akVM: AlaskaViewModel
//    private lateinit var sbVM: ScoreboardLogic
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(StandardTestDispatcher())
//        akVM = AlaskaViewModel(ShuffleSeed(Random(10L)))
//        sbVM = ScoreboardLogic()
//    }
//
//    /**
//     *  Uses same reset() function as [YukonViewModel] and is tested in [YukonViewModelTest].
//     */
//
//    @Test
//    fun akSbVmAutoComplete() = runTest {
//        val expectedClubs = tc.clubs
//        val expectedDiamonds = tc.diamonds
//        val expectedHearts = tc.hearts
//        val expectedSpades = tc.spades
//        val expectedFinalMoves = 52
//        val expectedFinalScore = 52
//        val expectedSbvmMoves = 1
//        val expectedSbvmScore = 1
//        val expectedGameWon = true
//        val expectedAutoCompleteActive = false
//
//        // going to cheat and give lists that are ready to auto complete
//        akVM.tableau.forEach { it.undo(emptyList()) }
//        akVM.tableau[0].add(tc.clubs.reversed())
//        akVM.tableau[1].add(tc.diamonds.reversed())
//        akVM.tableau[2].add(tc.hearts.reversed())
//        akVM.tableau[3].add(tc.spades.reversed())
//
//        // this should start autoComplete()
//        launch { sbVM.handleMoveResult(akVM.onTableauClick(0, 12)) }
//        advanceUntilIdle()
//
//        val lgs = sbVM.retrieveLastGameStats(true, akVM.autoCompleteCorrection)
//
//        assertEquals(expectedClubs, akVM.foundation[0].truePile.toList())
//        assertEquals(expectedDiamonds, akVM.foundation[1].truePile.toList())
//        assertEquals(expectedHearts, akVM.foundation[2].truePile.toList())
//        assertEquals(expectedSpades, akVM.foundation[3].truePile.toList())
//        assertEquals(expectedSbvmMoves, sbVM.moves.value)
//        assertEquals(expectedSbvmScore, sbVM.score.value)
//        assertEquals(expectedFinalMoves, lgs.moves)
//        assertEquals(expectedFinalScore, lgs.score)
//        assertEquals(expectedGameWon, akVM.gameWon.value)
//        assertEquals(expectedAutoCompleteActive, akVM.autoCompleteActive.value)
//    }
//}