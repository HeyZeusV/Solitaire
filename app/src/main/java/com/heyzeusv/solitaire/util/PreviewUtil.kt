package com.heyzeusv.solitaire.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.CardSize
import com.heyzeusv.solitaire.LocalCardSize
import com.heyzeusv.solitaire.ui.theme.PreviewBG
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

/**
 *  Helper function for Composable Previews. Creates LocalCardSize and passes it, so
 *  [content] displays correctly.
 */
@Composable
fun SolitairePreview(content: @Composable () -> Unit) {
    // gets device size in order to scale card.
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
    val cardHeight = cardWidth.times(1.4f)
    val cardSize = CardSize(cardWidth, cardHeight)

    CompositionLocalProvider(LocalCardSize provides cardSize) {
        SolitaireTheme {
            Box(Modifier.background(PreviewBG)) {
                content()
            }
        }
    }
}