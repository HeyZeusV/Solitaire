package com.heyzeusv.solitaire.board.piles

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  Displays [Stock] [Pile]. Acts the same as [Pile], but additional [Boolean] that is used to
 *  determine which empty image is used.
 *
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param pile The [Cards][Card] to display.
 *  @param stockWasteEmpty Determines which empty image to use.
 *  @param onClick The action to take when pressed.
 */
@Composable
fun Stock(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    pile: List<Card>,
    stockWasteEmpty: () -> Boolean,
    onClick: () -> Unit = { }
) {
    Pile(
        modifier = modifier,
        cardDpSize = cardDpSize,
        pile = pile,
        emptyIconId = if (stockWasteEmpty()) R.drawable.stock_empty else R.drawable.stock_reset,
        onClick = onClick
    )
}

@Preview
@Composable
private fun StockPreview() {
    PreviewUtil().apply {
        Preview {
            Stock(
                cardDpSize = cardDpSize,
                pile = pile,
                stockWasteEmpty = { true }
            )
        }
    }
}

@Preview
@Composable
private fun StockEmptyTrue() {
    PreviewUtil().apply {
        Preview {
            Stock(
                cardDpSize = cardDpSize,
                pile = emptyList(),
                stockWasteEmpty = { true }
            )
        }
    }
}

@Preview
@Composable
private fun StockEmptyFalse() {
    PreviewUtil().apply {
        Preview {
            Stock(
                cardDpSize = cardDpSize,
                pile = emptyList(),
                stockWasteEmpty = { false }
            )
        }
    }
}