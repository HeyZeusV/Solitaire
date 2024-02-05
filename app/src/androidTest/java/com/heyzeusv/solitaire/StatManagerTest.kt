package com.heyzeusv.solitaire

import androidx.datastore.core.DataStore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class StatManagerTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dataStore: DataStore<StatPreferences>
    @Inject
    @AppTestScope
    lateinit var testScope: TestScope

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun initialData() {

        testScope.runTest {
            val initialData = dataStore.data.first()
        }
    }
}