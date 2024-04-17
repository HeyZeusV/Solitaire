package com.heyzeusv.solitaire.board.piles

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits

/**
 *  Composable that displays Tableau [pile] with [tableauIndex]. [cardDpSize] is used size the Cards
 *  and to shift each card in [pile], after the first, upwards so they overlap. [spacedByPercent]
 *  is used to determine distance between cards vertically. [onClick] triggers when any card within
 *  [pile] is clicked and is passed [tableauIndex]. Displays static image if [pile] is empty.
 */
@Composable
fun SolitaireTableau(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize = DpSize(56.dp, 79.dp),
    spacedByPercent: Float,
    tableauIndex: Int = 0,
    pile: List<Card> = emptyList(),
    onClick: (Int, Int) -> Unit = { _, _ -> }
) {
    Column(
        modifier = modifier.testTag("Tableau #$tableauIndex"),
        verticalArrangement =
            Arrangement.spacedBy(space = -(cardDpSize.height.times(spacedByPercent)))
    ) {
        if (pile.isEmpty()) {
            Image(
                modifier = Modifier.size(cardDpSize),
                painter = painterResource(R.drawable.tableau_empty),
                contentDescription = stringResource(R.string.pile_cdesc_empty),
                contentScale = ContentScale.FillBounds
            )
        } else {
            pile.forEachIndexed { cardIndex, card ->
                SolitaireCard(
                    modifier = Modifier
                        .size(cardDpSize)
                        .clip(RoundedCornerShape(4.dp)) // makes click surface have round edges
                        .clickable { onClick(tableauIndex, cardIndex) },
                    card = card
                )
            }
        }
    }
}

@Preview
@Composable
fun SolitaireTableauEmptyPreview() {
    SolitairePreview {
        SolitaireTableau(spacedByPercent = 0.75f)
    }
}

@Preview
@Composable
fun SolitaireTableauPreview() {
    SolitairePreview {
        SolitaireTableau(
            spacedByPercent = 0.75f,
            pile = listOf(
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES),
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES)
            )
        )
    }
}