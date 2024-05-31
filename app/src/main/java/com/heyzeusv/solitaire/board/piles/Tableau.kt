package com.heyzeusv.solitaire.board.piles

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.GameViewModel
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.pRes
import com.heyzeusv.solitaire.util.sRes
import com.heyzeusv.solitaire.util.spacedBy

/**
 *  Displays [Tableau] [Pile] which displays [PlayingCards][PlayingCard] in vertical orientation.
 *
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param spacedByPercent The percentage that will be taken from [Card] height which will be used
 *  to determine distance between each [Card] vertically.
 *  @param tableauIndex The index for which Tableau this Composable represents in [GameViewModel].
 *  @param pile The [Cards][Card] to display.
 *  @param onClick The action to take when pressed.
 */
@Composable
fun Tableau(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    spacedByPercent: Float,
    tableauIndex: Int = 0,
    pile: List<Card> = emptyList(),
    onClick: (Int, Int) -> Unit = { _, _ -> },
) {
    Column(
        modifier = modifier.testTag("Tableau #$tableauIndex"),
        verticalArrangement = Arrangement spacedBy -(cardDpSize.height.times(spacedByPercent)),
    ) {
        if (pile.isEmpty()) {
            Image(
                modifier = Modifier.size(cardDpSize),
                painter = pRes(R.drawable.tableau_empty),
                contentDescription = sRes(R.string.pile_cdesc_empty),
                contentScale = ContentScale.FillBounds,
            )
        } else {
            pile.forEachIndexed { cardIndex, card ->
                PlayingCard(
                    modifier = Modifier
                        .size(cardDpSize)
                        .clickable { onClick(tableauIndex, cardIndex) },
                    card = card,
                )
            }
        }
    }
}

@Preview
@Composable
private fun TableauPreview() {
    PreviewUtil().apply {
        Preview {
            Tableau(
                cardDpSize = cardDpSize,
                spacedByPercent = 0.75f,
                pile = pile,
            )
        }
    }
}

@Preview
@Composable
private fun TableauEmptyPreview() {
    PreviewUtil().apply {
        Preview {
            Tableau(
                cardDpSize = cardDpSize,
                spacedByPercent = 0.75f,
            )
        }
    }
}