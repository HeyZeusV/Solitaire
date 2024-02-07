package com.heyzeusv.solitaire.data

import androidx.datastore.core.DataStore
import com.heyzeusv.solitaire.Game
import com.heyzeusv.solitaire.GameStats
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.di.AppTestScope
import com.heyzeusv.solitaire.util.StatManager
import com.heyzeusv.solitaire.util.getStatsDefaultInstance
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.Assert.assertEquals
import javax.inject.Inject

@HiltAndroidTest
class StatManagerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var statManager: StatManager
    @Inject
    lateinit var statPreferences: DataStore<StatPreferences>
    @Inject
    @AppTestScope
    lateinit var testScope: TestScope

    @Before
    fun init() {
        hiltRule.inject()
    }

    private val turnOne: GameStats = getStatsDefaultInstance()
    private val turnThree: GameStats = getStatsDefaultInstance().toBuilder()
        .setGame(Game.GAME_KLONDIKETURNTHREE)
        .build()

    @Test
    fun statManager_initialData() {
        runTestAndCleanup {
            // should be empty when initialized
            val initialData = statManager.statData.first()
            assertEquals(true, initialData.isInitialized)
            assertEquals(0, initialData.statsCount)
            assertEquals(emptyList<GameStats>(), initialData.statsList)
        }
    }

    @Test
    fun statManager_updateData_addOneGame() {
        runTestAndCleanup {
            statManager.updateStats(turnOne)
            val data = statManager.statData.first()
            assertEquals(1, data.statsCount)
            assertEquals(listOf(turnOne), data.statsList)
        }
    }

    @Test
    fun statManager_updateData_addTwoGames() {
        runTestAndCleanup {
            statManager.updateStats(turnOne)
            statManager.updateStats(turnThree)
            val data = statManager.statData.first()
            assertEquals(2, data.statsCount)
            assertEquals(listOf(turnOne, turnThree), data.statsList)
        }
    }

    @Test
    fun statManager_updateData_noDuplicates() {
        runTestAndCleanup {
            statManager.apply {
                updateStats(turnOne)
                updateStats(turnOne)
                updateStats(turnOne)
                updateStats(turnOne)
                updateStats(turnOne)
                updateStats(turnOne)
            }
            val data = statManager.statData.first()
            assertEquals(1, data.statsCount)
            assertEquals(listOf(turnOne), data.statsList)
        }
    }

    @Test
    fun statManager_updateData_updateExisting() {
        val updatedTurnOne = turnOne.toBuilder().setBestTotalScore(10L).build()
        runTestAndCleanup {
            statManager.updateStats(turnOne)
            var data = statManager.statData.first()
            assertEquals(listOf(turnOne), data.statsList)
            statManager.updateStats(updatedTurnOne)
            data = statManager.statData.first()
            assertEquals(1, data.statsCount)
            assertEquals(listOf(updatedTurnOne), data.statsList)
        }
    }

    /**
     *  Helper function that runs [block], which is a DataStore test, and then clears test DataStore
     *  immediately afterwards.
     */
    private fun runTestAndCleanup(block: suspend () -> Unit) = testScope.runTest {
        block()
        statPreferences.updateData { it.toBuilder().clearStats().build() }
    }
}