package com.heyzeusv.solitaire

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.heyzeusv.solitaire.di.AppModule
import com.heyzeusv.solitaire.util.StatPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 *  Hilt module that will replace AppModule when performing tests in androidTest folder.
 */
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
class TestAppModule {

    @Provides
    @Singleton
    fun provideTestStatPreferenceDataStore(
        @ApplicationContext context: Context,
        @AppTestScope scope: TestScope,
        statPreferencesSerializer: StatPreferencesSerializer
    ): DataStore<StatPreferences> = DataStoreFactory.create(
        serializer = statPreferencesSerializer,
        scope = scope
    ) {
        context.dataStoreFile("test_stat_preferences.pb")
    }

    @Provides
    @StandardTestDispatcher
    fun provideStandardTestDispatcher(): TestDispatcher = StandardTestDispatcher()

    @Provides
    @Singleton
    @AppTestScope
    fun provideTestCoroutineScope(@StandardTestDispatcher dispatcher: TestDispatcher): TestScope =
        TestScope(dispatcher + Job())
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StandardTestDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AppTestScope