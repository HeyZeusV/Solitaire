package com.heyzeusv.solitaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boardVM: BoardViewModel by viewModels()
        boardVM.reset()
        setContent {
            SolitaireTheme(darkTheme = true) {
                SolitaireApp(boardVM)
            }

        }
    }
}

@Composable
fun SolitaireApp(boardVM: BoardViewModel) {
    val gameWon by boardVM.gameWon.collectAsState()

    if (gameWon) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = { boardVM.reset() }) {
                    Text(text = "New Game")
                }
            },
            title = { Text(text = "You Won!") },
            text = { Text(text = "Congratulations! Here are your stats...") } // TODO: Add stats
        )
    }
    SolitaireBoard(boardVM)
}