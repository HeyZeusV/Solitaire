package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Class that handles 52 [Card] deck creation.
 */
class Stock : Pile {

    private val _pile = mutableStateListOf<Card>()
    override val pile: List<Card> get() = _pile

    /**
     *  Add given [cards] to [_pile].
     */
    override fun add(cards: List<Card>): Boolean {
        return _pile.addAll(cards.map { it.copy(faceUp = false) })
    }

    /**
     *  Remove the first [Card] in [_pile] and return it.
     */
    override fun remove(tappedIndex: Int): Card = _pile.removeFirst()

    /**
     *  Reset [_pile] using given [cards].
     */
    override fun reset(cards: List<Card>) {
        _pile.clear()
        add(cards)
    }

    /**
     *  Used to return [_pile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        _pile.clear()
        if (cards.isEmpty()) return
        // only last card is shown to user, this makes sure it is not visible
        val mutableCards = cards.toMutableList()
        mutableCards[mutableCards.size - 1] = mutableCards.last().copy(faceUp = false)
        _pile.addAll(mutableCards)
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