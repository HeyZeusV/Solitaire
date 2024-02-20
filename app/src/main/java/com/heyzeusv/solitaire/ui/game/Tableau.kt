package com.heyzeusv.solitaire.ui.game

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.ui.clickableSingle

/**
 *  Composable that displays Tableau [pile] with [tableauIndex]. [cardHeight] is used to shift each
 *  card in [pile], after the first, upwards so they overlap. [onClick] triggers when any card
 *  within [pile] is clicked, Displays static image if [pile] is empty.
 */
@Composable
fun SolitaireTableau(
    modifier: Modifier = Modifier,
    cardHeight: Dp,
    tableauIndex: Int = 0,
    pile: List<Card> = emptyList(),
    onClick: (Int, Int) -> MoveResult = { _, _ -> MoveResult.ILLEGAL},
    handleMoveResult: (MoveResult) -> Unit = { }
) {
    Column(
        modifier = modifier.testTag("Tableau #$tableauIndex"),
        verticalArrangement = Arrangement.spacedBy(space = -(cardHeight.times(0.75f)))
    ) {
        if (pile.isEmpty()) {
            Image(
                modifier = Modifier.height(cardHeight),
                painter = painterResource(R.drawable.tableau_empty),
                contentDescription = stringResource(R.string.pile_cdesc_empty),
                contentScale = ContentScale.FillBounds
            )
        } else {
            pile.forEachIndexed { cardIndex, card ->
                SolitaireCard(
                    modifier = Modifier
                        .height(cardHeight)
                        .clip(RoundedCornerShape(4.dp)) // makes click surface have round edges
                        .clickableSingle { handleMoveResult(onClick(tableauIndex, cardIndex)) },
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
        // gets device size in order to scale card
        val config = LocalConfiguration.current
        val sWidth = config.screenWidthDp.dp
        val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
        val cardHeight = cardWidth.times(1.4f)

        SolitaireTableau(cardHeight = cardHeight)
    }
}

@Preview
@Composable
fun SolitaireTableauPreview() {
    SolitairePreview {
        // gets device size in order to scale card
        val config = LocalConfiguration.current
        val sWidth = config.screenWidthDp.dp
        val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
        val cardHeight = cardWidth.times(1.4f)

        SolitaireTableau(
            cardHeight = cardHeight,
            pile = listOf(
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES),
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES)
            )
        )
    }
}