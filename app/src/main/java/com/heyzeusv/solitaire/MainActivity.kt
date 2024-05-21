package com.heyzeusv.solitaire

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.solitaire.board.Board
import com.heyzeusv.solitaire.board.GameViewModel
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.menu.MenuContainer
import com.heyzeusv.solitaire.menu.MenuViewModel
import com.heyzeusv.solitaire.scoreboard.SolitaireScoreboard
import com.heyzeusv.solitaire.splash.SplashScreen
import com.heyzeusv.solitaire.toolbar.Toolbar
import com.heyzeusv.solitaire.util.MyConnectivityManager
import com.heyzeusv.solitaire.util.NavScreens
import com.heyzeusv.solitaire.util.SnackbarManager
import com.heyzeusv.solitaire.util.composables.CloseGameAlertDialog
import com.heyzeusv.solitaire.util.composables.GameWonAlertDialog
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val connectManager by lazy { MyConnectivityManager(this, lifecycleScope) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val isConnected by connectManager.connectionAsStateFlow.collectAsStateWithLifecycle()
            SolitaireTheme(darkTheme = true) {
                SolitaireApp(isConnected) { finishAndRemoveTask() }
            }
        }
    }
}

@Composable
fun SolitaireApp(
    isConnected: Boolean,
    finishApp: () -> Unit
) {
    /**
     *  DO NOT REMOVE, app was crashing when navigating to GamesMenu due to one value of
     *  [Games.orderedSubclasses] being null. Calling it here fixes it.
     */
    Games.orderedSubclasses
    Games.statsOrderedSubclasses

    val appState = rememberAppState()

    val menuVM = hiltViewModel<MenuViewModel>()
    val gameVM = hiltViewModel<GameViewModel>()

    val settings by menuVM.settingsFlow.collectAsState()
    var animationDurations by remember { mutableStateOf(AnimationDurations.Fast) }
    LaunchedEffect(key1 = settings.animationDurations) {
        animationDurations = AnimationDurations from settings.animationDurations
        gameVM.updateAutoComplete(animationDurations)
    }
    LaunchedEffect(key1 = settings.selectedGame) {
        gameVM.updateSelectedGame(Games.getGameClass(settings.selectedGame))
    }
    LifecycleResumeEffect {
        if (gameVM.sbLogic.moves.value != 0) gameVM.sbLogic.startTimer()
        onPauseOrDispose { gameVM.sbLogic.pauseTimer() }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = appState.snackbarHostState,
                modifier = Modifier.padding(all = 8.dp),
                snackbar = { snackbarData ->
                    Snackbar(snackbarData, Modifier.padding(WindowInsets.ime.asPaddingValues()))
                }
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = appState.navController,
            startDestination = NavScreens.Splash.route,
            modifier = Modifier.padding(paddingValues),
            enterTransition = { slideInHorizontally(
                animationSpec = tween(1500),
                initialOffsetX = { -it }
            ) },
            exitTransition = {
                slideOutHorizontally(
                    animationSpec = tween(1500),
                    targetOffsetX = { it }
                ) }
        ) {
            composable(route = NavScreens.Splash.route) {
                SplashScreen(
                    isConnected = isConnected,
                    navController = appState.navController,
                    menuVM = menuVM
                )
            }
            composable(route = NavScreens.Game.route) {
                GameScreen(
                    isConnected = isConnected,
                    gameVM = gameVM,
                    menuVM = menuVM,
                    animationDurations = animationDurations
                ) { finishApp() }
            }
        }
    }
}

/**
 *  Composable which displays GameScreen which includes [SolitaireScoreboard], [Board], and
 *  [Toolbar].
 */
@Composable
fun GameScreen(
    isConnected: Boolean,
    gameVM: GameViewModel,
    menuVM: MenuViewModel,
    animationDurations: AnimationDurations,
    finishApp: () -> Unit
) {
    // background pattern that repeats
    val pattern = ImageBitmap.imageResource(R.drawable.pattern_noise)
    val brush = remember(pattern) {
        ShaderBrush(ImageShader(pattern, TileMode.Repeated, TileMode.Repeated))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Column(Modifier.fillMaxSize()) {
            SolitaireScoreboard(
                gameVM = gameVM,
                modifier = Modifier
            )
            Board(
                gameVM = gameVM,
                animationDurations = animationDurations,
                modifier = Modifier.weight(1f)
            )
            Toolbar(
                gameVM = gameVM,
                menuVM = menuVM,
                modifier = Modifier
            )
        }
        MenuContainer(
            isConnected = isConnected,
            gameVM = gameVM,
            menuVM = menuVM,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
    CloseGameAlertDialog(gameVM = gameVM, menuVM = menuVM, finishApp = finishApp)
    GameWonAlertDialog(gameVM = gameVM, menuVM = menuVM)
}

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    navController: NavHostController = rememberNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = LocalContext.current.resources,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
    AppState(snackbarHostState, navController, snackbarManager, resources, coroutineScope)
}