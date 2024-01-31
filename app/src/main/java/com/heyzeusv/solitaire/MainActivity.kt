package com.heyzeusv.solitaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyzeusv.solitaire.ui.theme.BackgroundOverlay
import com.heyzeusv.solitaire.ui.theme.Purple40
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.formatTime
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SolitaireApp(
    gameVM: GameViewModel = viewModel(),
    menuVM: MenuViewModel = viewModel()
) {
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

    val displayMenu by menuVM.displayMenu.collectAsState()
    val selectedGame by menuVM.selectedGame.collectAsState()

    // start timer once user makes a move
    if (moves == 1 && gameVM.jobIsCancelled()) {
        gameVM.startTimer()
    }

    if (gameWon) {
        // pause timer once user reaches max score
        gameVM.pauseTimer()
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { gameVM.reset(ResetOptions.NEW) }) {
                    Text(text = "New Game")
                }
            },
            title = { Text(text = "You Won!") },
            text = {
                val totalScore = timer + score
                val stats = """Moves: $moves
                    |Time: ${timer.formatTime()}
                    |Score: $score
                    |Total Score (Time + Score): $totalScore""".trimMargin()
                Text(text = "Congratulations! Here are your stats...\n$stats") }
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
            modifier = Modifier.weight(0.78f),
            gameVM = gameVM
        )
        SolitaireTools(
            modifier = Modifier.weight(0.10f),
            menuOnClick = menuVM::updateDisplayMenu,
            resetOnConfirmClick = gameVM::reset,
            historyListSize = historyList.size,
            undoOnClick = gameVM::undo
        )
    }
    if (displayMenu) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clickable { menuVM.updateDisplayMenu(false) },
            color = BackgroundOverlay
        ) {}
        Card(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(all = 32.dp),
        ) {
            Column(
                modifier = Modifier.padding(all = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Games",
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Games.entries.forEach {
                        FilterChip(
                            selected = it == selectedGame,
                            onClick = { menuVM.updateSelectedGame(it) },
                            label = { Text(text = it.gameName) },
                            leadingIcon = {
                                if (it == selectedGame) {
                                    Icon(
                                        imageVector = Icons.Default.Done,
                                        contentDescription = "${it.gameName} is selected",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple40
                            )
                        )
                    }
                }
                Text(
                    text = "Stats",
                    textDecoration = TextDecoration.Underline,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(text = "Games Played: ")
                Text(text = "Games Won: ")
                Text(text = "Lowest Moves in Win: ")
                Text(text = "Average Moves: ")
                Text(text = "Total Moves: ")
                Text(text = "Fastest Win: ")
                Text(text = "Average Time: ")
                Text(text = "Total Time Played: ")
                Text(text = "Average Score: ")
                Text(text = "Best Total Score: ")
                Text(
                    text = "\u2517 Moves + Time + Score (Lower is better)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}