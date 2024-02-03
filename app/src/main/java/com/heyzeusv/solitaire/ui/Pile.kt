package com.heyzeusv.solitaire.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.clickableSingle

/**
 *  Composable that displays [pile]. If given [pile] is empty, [emptyIconId] is displayed.
 */
@Composable
fun SolitairePile(
    modifier: Modifier = Modifier,
    pile: List<Card>,
    @DrawableRes emptyIconId: Int,
    onClick: () -> Unit = { },
    drawAmount: Int = 1,
    cardWidth: Dp
) {
    if (pile.isEmpty()) {
        Image(
            modifier = modifier.clickableSingle { onClick() },
            painter = painterResource(emptyIconId),
            contentDescription = stringResource(R.string.pile_cdesc_empty),
            contentScale = ContentScale.FillBounds
        )
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                space = -(cardWidth.times(0.6f)),
                alignment = Alignment.End
            )
        ) {
            if (pile.size >= 3 && drawAmount >= 3) {
                SolitaireCard(
                    modifier = Modifier
                        .width(cardWidth)
                        .fillMaxHeight(),
                    card = pile[pile.size - 3]
                )
            }
            if (pile.size >= 2 && drawAmount >= 2) {
                SolitaireCard(
                    modifier = Modifier
                        .width(cardWidth)
                        .fillMaxHeight(),
                    card = pile[pile.size - 2]
                )
            }
            SolitaireCard(
                modifier = Modifier
                    .width(cardWidth - 2.dp)
                    .fillMaxHeight()
                    .clickableSingle { onClick() },
                card = pile.last()
            )
        }
    }
}

@Preview
@Composable
fun SolitairePileEmptyPreview() {
    SolitairePreview {
        SolitairePile(
            pile = emptyList(),
            emptyIconId = R.drawable.stock_reset,
            cardWidth = 75.dp
        )
    }
}

@Preview
@Composable
fun SolitairePilePreview() {
    SolitairePreview {
        SolitairePile(
            pile = listOf(Card(100, Suits.CLUBS, faceUp = true)),
            emptyIconId = R.drawable.stock_reset,
            cardWidth = 75.dp
        )
    }
}

@Preview
@Composable
fun SolitairePile3DrawPreview() {
    val card = Card(2, Suits.CLUBS, faceUp = true)
    SolitairePile(
        pile = listOf(card, card, card),
        emptyIconId = R.drawable.waste_empty,
        drawAmount = 3,
        cardWidth = 75.dp
    )
}