package com.heyzeusv.solitaire.ui.board

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits

/**
 *  Composable that displays [pile]. If given [pile] is empty, [emptyIconId] is displayed. Clicking
 *  launches [onClick]. [drawAmount] determines the max number of [Card]s that will be displayed at
 *  once. [cardDpSize] determines the size to make [SolitaireCard] Composables.
 */
@Composable
fun SolitairePile(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    @DrawableRes emptyIconId: Int,
    onClick: () -> Unit = { },
    drawAmount: Int = 1
) {
    if (pile.isEmpty()) {
        Image(
            modifier = modifier
                .size(cardDpSize)
                .clickable { onClick() },
            painter = painterResource(emptyIconId),
            contentDescription = stringResource(R.string.pile_cdesc_empty),
            contentScale = ContentScale.FillBounds
        )
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                space = -(cardDpSize.width.times(0.5f)),
                alignment = Alignment.End
            )
        ) {
            if (pile.size >= 3 && drawAmount >= 3) {
                SolitaireCard(
                    modifier = Modifier
                        .size(cardDpSize)
                        .fillMaxHeight(),
                    card = pile[pile.size - 3]
                )
            }
            if (pile.size >= 2 && drawAmount >= 2) {
                SolitaireCard(
                    modifier = Modifier
                        .size(cardDpSize)
                        .fillMaxHeight(),
                    card = pile[pile.size - 2]
                )
            }
            SolitaireCard(
                modifier = Modifier
                    .size(cardDpSize)
                    .fillMaxHeight()
                    .clickable { onClick() },
                card = pile.last()
            )
        }
    }
}

/**
 *  Composable that displays Stock [pile]. [stockWasteEmpty] determines which emptyIconId drawable
 *  should be passed to [SolitairePile]. Clicking launches [onClick]. [cardDpSize] determines the
 *  size to make [SolitaireCard] Composables.
 */
@Composable
fun SolitaireStock(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    stockWasteEmpty: () -> Boolean,
    onClick: () -> Unit = { }
) {
    SolitairePile(
        modifier = modifier,
        cardDpSize = cardDpSize,
        pile = pile,
        emptyIconId = if (stockWasteEmpty()) R.drawable.stock_empty else R.drawable.stock_reset,
        onClick = onClick
    )
}

@Preview
@Composable
fun SolitairePileEmptyPreview() {
    SolitairePreview {
        SolitairePile(
            cardDpSize = DpSize(56.dp, 79.dp),
            pile = emptyList(),
            emptyIconId = R.drawable.stock_reset
        )
    }
}

@Preview
@Composable
fun SolitairePilePreview() {
    SolitairePreview {
        SolitairePile(
            cardDpSize = DpSize(56.dp, 79.dp),
            pile = listOf(Card(100, Suits.CLUBS, faceUp = true)),
            emptyIconId = R.drawable.stock_reset
        )
    }
}

@Preview
@Composable
fun SolitairePile3DrawPreview() {
    val card = Card(2, Suits.CLUBS, faceUp = true)
    SolitairePile(
        cardDpSize = DpSize(56.dp, 79.dp),
        pile = listOf(card, card, card),
        emptyIconId = R.drawable.waste_empty,
        drawAmount = 3
    )
}