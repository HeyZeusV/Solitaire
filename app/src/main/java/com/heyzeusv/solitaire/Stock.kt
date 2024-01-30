package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Class that handles 52 [Card] deck creation.
 */
class Stock(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Add given [cards] to [mPile].
     */
    override fun add(cards: List<Card>): Boolean {
        return mPile.addAll(cards.map { it.copy(faceUp = false) })
    }

    /**
     *  Remove the first [Card] in [mPile] and return it.
     */
    override fun remove(tappedIndex: Int): Card = mPile.removeFirst()

    /**
     *  Reset [mPile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        mPile.clear()
        add(cards)
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        if (cards.isEmpty()) return
        // only last card is shown to user, this makes sure it is not visible
        val mutableCards = cards.toMutableList()
        mutableCards[mutableCards.size - 1] = mutableCards.last().copy(faceUp = false)
        mPile.addAll(mutableCards)
    }

    override fun toString(): String = pile.toList().toString()
}

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
            modifier = modifier.clickable { onClick() },
            painter = painterResource(emptyIconId),
            contentDescription = "Pile is empty.",
            contentScale = ContentScale.FillBounds
        )
    } else {
        SolitaireCard(
            modifier = modifier.clickable { onClick() },
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