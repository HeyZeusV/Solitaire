package com.heyzeusv.solitaire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

/**
 *  Composable that will display all [Card] piles, Deck, Waste, Foundation, and Tableau.
 */
@Composable
fun SolitaireBoard(modifier: Modifier = Modifier, gameVM: GameViewModel = viewModel()) {
    // gets device size in order to scale card
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
    val cardHeight = cardWidth.times(1.4f)

    // piles to be displayed
    val deck by remember { mutableStateOf(gameVM.deck.gameDeck) }
    val foundationList = gameVM.foundation.map {
        val foundationPile by remember { mutableStateOf(it.pile) }
        return@map foundationPile
    }
    val tableauList = gameVM.tableau.map {
        val tableauPile by remember { mutableStateOf(it.pile) }
        return@map tableauPile
    }
    val waste by remember { mutableStateOf(gameVM.waste.pile) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val rowModifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
                gameVM.foundation.forEachIndexed { index, foundation ->
                    SolitaireDeck(
                        modifier = rowModifier,
                        pile = foundationList[index],
                        emptyIconId = foundation.suit.emptyIcon
                    ) { gameVM.onFoundationTap(index) }
                }
                Spacer(modifier = rowModifier)
                SolitaireDeck(
                    modifier = rowModifier,
                    pile = waste,
                    emptyIconId = R.drawable.waste_empty,
                    onClick = gameVM::onWasteTap
                )
                SolitaireDeck(
                    modifier = rowModifier,
                    pile = deck,
                    emptyIconId = if (waste.isEmpty()) R.drawable.deck_empty else R.drawable.deck_reset,
                    onClick = gameVM::onDeckTap
                )
            }
            Row(
                modifier = Modifier.weight(0.85f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tableauList.forEachIndexed { index, tableau ->
                    SolitaireTableau(
                        modifier = Modifier.weight(1f),
                        pile = tableau,
                        tableauIndex = index,
                        cardHeight = cardHeight,
                        onClick = gameVM::onTableauTap
                    )
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