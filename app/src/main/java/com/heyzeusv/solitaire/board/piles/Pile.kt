package com.heyzeusv.solitaire.board.piles

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.pRes
import com.heyzeusv.solitaire.util.sRes

/**
 *  Displays up to 3 [PlayingCard] next to each other horizontally. Displays an image if no
 *  [Cards][Card] are passed.
 *
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param pile The [Cards][Card] to display.
 *  @param emptyIconId The image to display when [pile] is empty.
 *  @param onClick The action to take when pressed.
 *  @param drawAmount The max number of [PlayingCard] to display horizontally.
 */
@Composable
fun Pile(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    @DrawableRes emptyIconId: Int,
    onClick: () -> Unit = { },
    drawAmount: DrawAmount = DrawAmount.One,
) {
    if (pile.isEmpty()) {
        Image(
            modifier = modifier
                .size(cardDpSize)
                .clickable { onClick() },
            painter = pRes(emptyIconId),
            contentDescription = sRes(R.string.pile_cdesc_empty),
            contentScale = ContentScale.FillBounds,
        )
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(
                space = -(cardDpSize.width.times(0.5f)),
                alignment = Alignment.End
            ),
        ) {
            if (pile.size >= 3 && drawAmount.amount >= 3) {
                PlayingCard(
                    modifier = Modifier.size(cardDpSize),
                    card = pile[pile.size - 3],
                )
            }
            if (pile.size >= 2 && drawAmount.amount >= 2) {
                PlayingCard(
                    modifier = Modifier.size(cardDpSize),
                    card = pile[pile.size - 2],
                )
            }
            PlayingCard(
                modifier = Modifier
                    .size(cardDpSize)
                    .clickable { onClick() },
                card = pile.last(),
            )
        }
    }
}

@Preview
@Composable
private fun PilePreview() {
    PreviewUtil().apply {
        Preview {
            Pile(
                cardDpSize = cardDpSize,
                pile = pile,
                emptyIconId = R.drawable.stock_reset,
            )
        }
    }
}

@Preview
@Composable
private fun Pile3DrawPreview() {
    PreviewUtil().apply {
        Preview {
            Pile(
                cardDpSize = cardDpSize,
                pile = pile,
                emptyIconId = R.drawable.waste_empty,
                drawAmount = DrawAmount.Three,
            )
        }
    }
}

@Preview
@Composable
private fun PileEmptyPreview() {
    PreviewUtil().apply {
        Preview {
            Pile(
                cardDpSize = cardDpSize,
                pile = emptyList(),
                emptyIconId = R.drawable.stock_reset,
            )
        }
    }
}