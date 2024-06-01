package com.heyzeusv.solitaire.board.piles

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  [Waste] works exactly like [Pile], this is essentially just a wrapper in order to be more
 *  clear as to what the Composable is, rather than having multiple [Piles][Pile] on same screen.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param pile The [Cards][Card] to display.
 *  @param emptyIconId The image to display when [pile] is empty.
 *  @param onClick The action to take when pressed.
 *  @param drawAmount The max number of [PlayingCard] to display horizontally.
 */
@Composable
fun Waste(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    @DrawableRes emptyIconId: Int,
    onClick: () -> Unit = { },
    drawAmount: DrawAmount = DrawAmount.One,
) {
    Pile(
        modifier = modifier,
        cardDpSize = cardDpSize,
        pile = pile,
        emptyIconId = emptyIconId,
        onClick = onClick,
        drawAmount = drawAmount,
    )
}

@Preview
@Composable
private fun WastePreview() {
    PreviewUtil().apply {
        Preview {
            Waste(
                cardDpSize = cardDpSize,
                pile = pile,
                emptyIconId = R.drawable.waste_empty,
            )
        }
    }
}

@Preview
@Composable
private fun Waste3DrawPreview() {
    PreviewUtil().apply {
        Preview {
            Waste(
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
private fun WasteEmptyPreview() {
    PreviewUtil().apply {
        Preview {
            Waste(
                cardDpSize = cardDpSize,
                pile = emptyList(),
                emptyIconId = R.drawable.waste_empty,
            )
        }
    }
}