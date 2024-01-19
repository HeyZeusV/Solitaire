package com.heyzeusv.solitaire

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // gets device size in order to scale card.
            val config = LocalConfiguration.current
            val sWidth = config.screenWidthDp.dp
            val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
            val cardHeight = cardWidth.times(1.4f)
            val cardSize = CardSize(cardWidth, cardHeight)

            CompositionLocalProvider(LocalCardSize provides cardSize) {
                SolitaireTheme(darkTheme = true) {
                    SolitaireApp()
                }
            }
        }
    }
}

@Composable
fun SolitaireApp() {
    SolitaireBoard()
}

data class CardSize(val width: Dp = 0.dp, val height: Dp = 0.dp)
val LocalCardSize = compositionLocalOf { CardSize() }