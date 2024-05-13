package com.heyzeusv.solitaire.di

import android.content.Context
import android.net.ConnectivityManager
import android.util.DisplayMetrics
import androidx.core.content.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import androidx.lifecycle.LifecycleCoroutineScope
import com.heyzeusv.solitaire.Settings
import com.heyzeusv.solitaire.StatPreferences
import com.heyzeusv.solitaire.board.layouts.ScreenLayouts
import com.heyzeusv.solitaire.board.layouts.Width1080
import com.heyzeusv.solitaire.board.layouts.Width1440
import com.heyzeusv.solitaire.board.layouts.Width2160
import com.heyzeusv.solitaire.board.layouts.Width480
import com.heyzeusv.solitaire.board.layouts.Width720
import com.heyzeusv.solitaire.board.layouts.Width960
import com.heyzeusv.solitaire.board.piles.ShuffleSeed
import com.heyzeusv.solitaire.menu.settings.SettingsSerializer
import com.heyzeusv.solitaire.menu.stats.StatPreferencesSerializer
import com.heyzeusv.solitaire.util.MyConnectivityManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.Random
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    /**
     *  Provides [ConnectivityManager] to determine if device has access to internet.
     */
    @Provides
    fun provideConnectivityManager(@ApplicationContext context: Context): ConnectivityManager =
        context.getSystemService<ConnectivityManager>()!!

    /**
     *  Provides [MyConnectivityManager] which contains StateFlow that is used to determine if
     *  device has access to internet.
     */
    @Provides
    fun provideConnectivityManager(
        @ApplicationContext context: Context,
        scope: LifecycleCoroutineScope
    ): MyConnectivityManager =
        MyConnectivityManager(context, scope)

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
    fun provideLayoutInfo(dm: DisplayMetrics): ScreenLayouts {
        val screenWidth = dm.widthPixels
        return when {
            screenWidth >= 2160 -> Width2160((screenWidth - 2160) / 2)
            screenWidth >= 1440 -> Width1440((screenWidth - 1440) / 2)
            screenWidth >= 1080 -> Width1080((screenWidth - 1080) / 2)
            screenWidth >= 960 -> Width960((screenWidth - 960) / 2)
            screenWidth >= 720 -> Width720((screenWidth - 720) / 2)
            else -> Width480((screenWidth - 480) / 2)
        }
    }

    /**
     *  Provides [ShuffleSeed] obj containing [Random] without seed parameter ensuring that every
     *  shuffle is random.
     */
    @Provides
    fun provideShuffleSeed(): ShuffleSeed = ShuffleSeed(Random())

    /**
     *  DataStore setup was done with help from here
     *  [https://blog.stackademic.com/using-proto-datastore-in-jetpack-compose-with-hilt-19be0df088fd]
     */
    @Provides
    @Singleton
    fun provideStatPreferencesDataStore(
        @ApplicationContext context: Context,
        @IODispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        statPreferencesSerializer: StatPreferencesSerializer
    ): DataStore<StatPreferences> = DataStoreFactory.create(
        serializer = statPreferencesSerializer,
        scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
    ) {
        context.dataStoreFile("stat_preferences.pb")
    }

    /**
     *  DataStore setup was done with help from here
     *  [https://blog.stackademic.com/using-proto-datastore-in-jetpack-compose-with-hilt-19be0df088fd]
     */
    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @ApplicationContext context: Context,
        @IODispatcher ioDispatcher: CoroutineDispatcher,
        @ApplicationScope scope: CoroutineScope,
        settingsSerializer: SettingsSerializer
    ): DataStore<Settings> = DataStoreFactory.create(
        serializer = settingsSerializer,
        scope = CoroutineScope(scope.coroutineContext + ioDispatcher)
    ) {
        context.dataStoreFile("settings.pb")
    }

    @Provides
    @IODispatcher
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton
    @ApplicationScope
    fun providesCoroutineScope(@DefaultDispatcher dispatcher: CoroutineDispatcher): CoroutineScope =
        CoroutineScope(SupervisorJob() + dispatcher)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope