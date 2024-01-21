package com.heyzeusv.solitaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
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
fun SolitaireApp(boardViewModel: BoardViewModel) {
    SolitaireBoard(boardViewModel)
}