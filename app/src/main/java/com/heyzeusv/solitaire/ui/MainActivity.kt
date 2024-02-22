package com.heyzeusv.solitaire.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.ui.game.AlaskaViewModel
import com.heyzeusv.solitaire.ui.game.AustralianPatienceViewModel
import com.heyzeusv.solitaire.ui.game.CanberraViewModel
import com.heyzeusv.solitaire.ui.game.KlondikeViewModel
import com.heyzeusv.solitaire.ui.game.RussianViewModel
import com.heyzeusv.solitaire.ui.game.SolitaireBoard
import com.heyzeusv.solitaire.ui.game.YukonViewModel
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.ui.scoreboard.SolitaireScoreboard
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import com.heyzeusv.solitaire.ui.tools.MenuViewModel
import com.heyzeusv.solitaire.ui.tools.SolitaireMenu
import com.heyzeusv.solitaire.ui.tools.SolitaireTools
import com.heyzeusv.solitaire.util.Games
import dagger.hilt.android.AndroidEntryPoint

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
fun SolitaireApp(finishApp: () -> Unit) {
    val sbVM = hiltViewModel<ScoreboardViewModel>()
    val menuVM = hiltViewModel<MenuViewModel>()
    val selectedGame by menuVM.selectedGame.collectAsState()
    val gameVM = when (selectedGame) {
        Games.YUKON -> hiltViewModel<YukonViewModel>()
        Games.AUSTRALIAN_PATIENCE -> hiltViewModel<AustralianPatienceViewModel>()
        Games.CANBERRA -> hiltViewModel<CanberraViewModel>()
        Games.ALASKA -> hiltViewModel<AlaskaViewModel>()
        Games.RUSSIAN -> hiltViewModel<RussianViewModel>()
        else -> hiltViewModel<KlondikeViewModel>()
    }

    // background pattern that repeats
    val pattern = ImageBitmap.imageResource(R.drawable.pattern_noise)
    val brush = remember(pattern) {
        ShaderBrush(ImageShader(pattern, TileMode.Repeated, TileMode.Repeated))
    }

    LifecycleResumeEffect {
        if (sbVM.moves.value != 0) sbVM.startTimer()
        onPauseOrDispose { sbVM.pauseTimer() }
    }

    Scaffold(
        modifier = Modifier.background(brush),
        topBar = {
            SolitaireScoreboard(
                sbVM = sbVM,
                modifier = Modifier
            )
        },
        bottomBar = {
            SolitaireTools(
                sbVM = sbVM,
                gameVM = gameVM,
                menuVM = menuVM,
                modifier = Modifier
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
        ) {
            SolitaireBoard(
                sbVM = sbVM,
                gameVM = gameVM,
                selectedGame = selectedGame,
                modifier = Modifier
            )
        }
    }
    SolitaireMenu(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM)
    CloseGameAlertDialog(sbVM = sbVM, menuVM = menuVM, finishApp = finishApp)
    GameWonAlertDialog(sbVM = sbVM, gameVM = gameVM, menuVM = menuVM)
}