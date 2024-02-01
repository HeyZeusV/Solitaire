package com.heyzeusv.solitaire.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.res.imageResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.LastGameStats
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.ResetOptions
import com.heyzeusv.solitaire.util.formatTimeDisplay
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val gameVM: GameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SolitaireTheme(darkTheme = true) {
                SolitaireApp()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (gameVM.moves.value != 0) gameVM.startTimer()
    }

    override fun onPause() {
        super.onPause()

        gameVM.pauseTimer()
    }
}

@Composable
fun SolitaireApp(gameVM: GameViewModel = viewModel()) {

    // background pattern that repeats
    val pattern = ImageBitmap.imageResource(R.drawable.pattern_noise)
    val brush = remember(pattern) {
        ShaderBrush(ImageShader(pattern, TileMode.Repeated, TileMode.Repeated))
    }

    // stats
    val moves by gameVM.moves.collectAsState()
    val timer by gameVM.timer.collectAsState()
    val score by gameVM.score.collectAsState()

    val historyList by remember { mutableStateOf(gameVM.historyList) }

    val gameWon by gameVM.gameWon.collectAsState()

    val menuVM = hiltViewModel<MenuViewModel>()

    // start timer once user makes a move
    if (moves == 1 && gameVM.jobIsCancelled()) {
        gameVM.startTimer()
    }

    if (gameWon) {
        // pause timer once user reaches max score
        gameVM.pauseTimer()
        val lgs = LastGameStats(true, moves, timer, score)
        menuVM.updateStats(lgs)
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { gameVM.reset(ResetOptions.NEW) }) {
                    Text(text = "New Game")
                }
            },
            title = { Text(text = "You Won!") },
            text = {
                val completedStats = """Moves: $moves
                    |Time: ${timer.formatTimeDisplay()}
                    |Score: $score
                    |Total Score: ${lgs.totalScore}
                    |┗ Moves + Time + Score (Lower is better)""".trimMargin()
                Text(text = "Congratulations! Here are your stats...\n$completedStats")
            }
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        SolitaireScoreboard(
            modifier = Modifier.weight(0.12f),
            moves = moves,
            timer = timer,
            score = score
        )
        SolitaireBoard(
            gameVM = gameVM,
            modifier = Modifier.weight(0.78f)
        )
        SolitaireTools(
            modifier = Modifier.weight(0.10f),
            menuOnClick = menuVM::updateDisplayMenu,
            resetOnConfirmClick = gameVM::reset,
            updateStats = { menuVM.updateStats(LastGameStats(false, moves, timer, score)) },
            historyListSize = historyList.size,
            undoOnClick = gameVM::undo
        )
    }
    SolitaireMenu(menuVM = menuVM)
}