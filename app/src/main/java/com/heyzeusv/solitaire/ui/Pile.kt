package com.heyzeusv.solitaire.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
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
    onClick: () -> Unit
) {
    if (pile.isEmpty()) {
        Image(
            modifier = modifier.clickableSingle { onClick() },
            painter = painterResource(emptyIconId),
            contentDescription = "Pile is empty.",
            contentScale = ContentScale.FillBounds
        )
    } else {
        SolitaireCard(
            modifier = modifier.clickableSingle { onClick() },
            card = pile.last()
        )
    }
}

@Preview
@Composable
fun SolitairePileEmptyPreview() {
    SolitairePreview {
        SolitairePile(pile = emptyList(), emptyIconId = R.drawable.stock_reset) { }
    }
}

@Preview
@Composable
fun SolitairePilePreview() {
    SolitairePreview {
        SolitairePile(
            pile = listOf(Card(100, Suits.CLUBS, faceUp = true)),
            emptyIconId = R.drawable.stock_reset,
        ) { }
    }
}