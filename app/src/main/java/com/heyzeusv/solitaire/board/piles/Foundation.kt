package com.heyzeusv.solitaire.board.piles

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  [Foundation] works exactly like [Pile], this is essentially just a wrapper in order to be more
 *  clear as to what the Composable is, rather than having multiple [Piles][Pile] on same screen.
 *
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param pile The [Cards][Card] to display.
 *  @param emptyIconId The image to display when [pile] is empty.
 *  @param onClick The action to take when pressed.
 */
@Composable
fun Foundation(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    @DrawableRes emptyIconId: Int,
    onClick: () -> Unit = { },
) {
    Pile(
        modifier = modifier,
        cardDpSize = cardDpSize,
        pile = pile,
        emptyIconId = emptyIconId,
        onClick = onClick,
    )
}

@Preview
@Composable
private fun FoundationPreview() {
    PreviewUtil().apply {
        Preview {
            Foundation(
                cardDpSize = cardDpSize,
                pile = pile,
                emptyIconId = R.drawable.foundation_diamond_empty,
            )
        }
    }
}

@Preview
@Composable
private fun FoundationEmptyPreview() {
    PreviewUtil().apply {
        Preview {
            Foundation(
                cardDpSize = cardDpSize,
                pile = emptyList(),
                emptyIconId = R.drawable.foundation_club_empty,
            )
        }
    }
}