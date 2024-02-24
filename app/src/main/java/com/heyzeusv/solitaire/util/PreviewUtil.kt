package com.heyzeusv.solitaire.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.heyzeusv.solitaire.util.theme.PreviewBG
import com.heyzeusv.solitaire.util.theme.SolitaireTheme

/**
 *  Helper function for Composable Previews. Gives semi-transparent background to previews to make
 *  it easier to see solo components
 */
@Composable
fun SolitairePreview(content: @Composable () -> Unit) {
    SolitaireTheme {
        Box(Modifier.fillMaxWidth().background(PreviewBG)) {
            content()
        }
    }
}