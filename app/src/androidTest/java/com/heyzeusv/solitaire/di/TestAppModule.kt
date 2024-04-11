package com.heyzeusv.solitaire.di

import android.content.Context
import android.util.DisplayMetrics
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.data.ShuffleSeed
import com.heyzeusv.solitaire.ui.board.layouts.positions.ScreenLayouts
import com.heyzeusv.solitaire.ui.board.layouts.positions.Width1080
import com.heyzeusv.solitaire.util.SettingsSerializer
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
import java.util.Random
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

    /**
     *  Provides [DisplayMetrics] to determine screen size.
     */
    @Provides
    fun provideDisplayMetrics(@ApplicationContext context: Context): DisplayMetrics =
        context.resources.displayMetrics

    /**
     *  Provides [ScreenLayouts] using screen size.
     */
    @Provides
    fun provideLayoutInfo(): ScreenLayouts = Width1080(0)

    /**
     *  Provides [ShuffleSeed] obj containing [Random] with seed parameter ensuring that every
     *  shuffle is the same.
     */
    @Provides
    fun provideShuffleSeed(): ShuffleSeed = ShuffleSeed(Random(10L))

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
    @Singleton
    fun provideTestSettingsDataStore(
        @ApplicationContext context: Context,
        @AppTestScope scope: TestScope,
        settingsSerializer: SettingsSerializer
    ): DataStore<Settings> = DataStoreFactory.create(
        serializer = settingsSerializer,
        scope = scope
    ) {
        context.dataStoreFile("test_settings.pb")
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