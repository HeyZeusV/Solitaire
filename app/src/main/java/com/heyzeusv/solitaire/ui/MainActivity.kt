package com.heyzeusv.solitaire.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.board.*
import com.heyzeusv.solitaire.ui.board.games.Games
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.ui.scoreboard.SolitaireScoreboard
import com.heyzeusv.solitaire.ui.toolbar.menu.MenuContainer
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import com.heyzeusv.solitaire.ui.toolbar.MenuViewModel
import com.heyzeusv.solitaire.ui.toolbar.Toolbar
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.NavScreens
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.reflect.full.createInstance

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolitaireTheme(darkTheme = true) {
                SolitaireApp(finishApp = { finishAndRemoveTask() })
            }
        }
    }
}

@Composable
fun SolitaireApp(
    finishApp: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val sbVM = hiltViewModel<ScoreboardViewModel>()
    val menuVM = hiltViewModel<MenuViewModel>()
    val gameVM = hiltViewModel<GameViewModel>()

    val settings by menuVM.settings.collectAsState()
    var animationDurations by remember { mutableStateOf(AnimationDurations.Fast) }
    LaunchedEffect(key1 = settings.animationDurations) {
        animationDurations = AnimationDurations from settings.animationDurations
        gameVM.updateAutoCompleteDelay(animationDurations.autoCompleteDelay)
    }
    LaunchedEffect(key1 = settings.selectedGame) {
        val instance = Games.getGameClass(settings.selectedGame).createInstance()
        gameVM.updateSelectedGame(instance)
    }
    LifecycleResumeEffect {
        if (sbVM.moves.value != 0) sbVM.startTimer()
        onPauseOrDispose { sbVM.pauseTimer() }
    }

    NavHost(
        navController = navController,
        startDestination = NavScreens.Splash.route,
        enterTransition = { fadeIn(animationSpec = tween(1500)) },
        exitTransition = { fadeOut(animationSpec = tween(1500)) }
    ) {
        composable(route = NavScreens.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(route = NavScreens.Game.route) {
            GameScreen(
                sbVM = sbVM,
                gameVM = gameVM,
                menuVM = menuVM,
                animationDurations = animationDurations
            ) { finishApp() }
        }
    }
}

/**
 *  Composable which displays GameScreen which includes [SolitaireScoreboard], [BoardLayout], and
 *  [Toolbar].
 */
@Composable
fun GameScreen(
    sbVM: ScoreboardViewModel,
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
                sbVM = sbVM,
                gameVM = gameVM,
                modifier = Modifier
            )
            BoardLayout(
                sbVM = sbVM,
                gameVM = gameVM,
                animationDurations = animationDurations,
                modifier = Modifier.weight(1f)
            )
            Toolbar(
                sbVM = sbVM,
                gameVM = gameVM,
                menuVM = menuVM,
                modifier = Modifier
            )
        }
        MenuContainer(
            sbVM = sbVM,
            menuVM = menuVM,
            modifier = Modifier.align(Alignment.BottomStart)
        )
    }
    CloseGameAlertDialog(sbVM = sbVM, menuVM = menuVM, finishApp = finishApp)
    GameWonAlertDialog(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM)
}

/**
 *  First screen that user sees in order to give Settings to load correct game. Uses
 *  [navController] to navigate to [GameScreen] after short duration.
 */
@Composable
fun SplashScreen(navController: NavHostController) {
    LaunchedEffect(key1 = Unit) {
        val delay = (2500L..3500L).random()
        delay(delay)
        navController.navigate(NavScreens.Game.route)
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.align(Alignment.Center)) {
            Image(
                painter = painterResource(R.mipmap.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier.shadow(
                    elevation = dimensionResource(R.dimen.aImageElevation),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.aImageRoundSize))
                )
            )
            Image(
                painter = painterResource(R.mipmap.ic_launcher_foreground),
                contentDescription = stringResource(R.string.app_name)
            )
        }
    }
}