package com.heyzeusv.solitaire

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

/**
 *  Composable that will display all [Card] piles, Deck, Waste, Foundation, and Tableau.
 */
@Composable
fun SolitaireBoard(boardViewModel: BoardViewModel = viewModel()) {
    // background pattern that repeats
    val pattern = ImageBitmap.imageResource(R.drawable.pattern_noise)
    val brush = remember(pattern) {
        ShaderBrush(ImageShader(pattern, TileMode.Repeated, TileMode.Repeated))
    }

    // gets device size in order to scale card
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
    val cardHeight = cardWidth.times(1.4f)

    // date to be displayed
    val deck by boardViewModel.deck.gameDeck.collectAsState()
    val foundation by boardViewModel.foundation.collectAsState()
    val tableau by boardViewModel.tableau.collectAsState()
    val waste by boardViewModel.waste.pile.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .weight(0.12f)
                    .fillMaxWidth()
            ) {
                val rowModifier = Modifier.weight(1f).height(cardHeight)
                foundation.forEachIndexed { index, foundation ->
                    SolitaireDeck(pile = foundation.pile, emptyIconId = foundation.suit.emptyIcon, { boardViewModel.onFoundationTap(index) }, modifier = rowModifier)
                }
                Spacer(modifier = rowModifier)
                SolitaireDeck(pile = waste, R.drawable.waste_empty, boardViewModel::onWasteTap, modifier = rowModifier)
                SolitaireDeck(pile = deck, R.drawable.deck_empty, boardViewModel::onDeckTap, modifier = rowModifier)
            }
            Row(modifier = Modifier.weight(0.76f)) {
                tableau.forEach {
                    val rowModifier = Modifier
                        .weight(1f)
                    SolitaireTableau(pile = it.pile, cardHeight = cardHeight, rowModifier)
                }
            }
        }
    }
}

@Preview
@Composable
fun SolitaireBoardPreview() {
    SolitaireTheme {
        SolitaireBoard()
    }
}