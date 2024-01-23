package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  In Solitaire, Foundation refers to the pile where players have to build up a specific [suit]
 *  from Ace to King.
 */
class Foundation(val suit: Suits) {

    private val _pile = mutableStateListOf<Card>()
    val pile: List<Card> get() = _pile

    /**
     *  Adds given [card] if it matches this [Foundation]'s [suit] and its value matches what is
     *  required next in the sequence. Returns true if added.
     */
    fun addCard(card: Card): Boolean {
        if (card.suit == suit && card.value == pile.size) {
            _pile.add(card)
            return true
        }
        return false
    }

    /**
     *  Removes the last card of the pile which would refer to the top card.
     */
    fun removeCard() { _pile.removeLast() }

    /**
     *  Removes all cards from the pile.
     */
    fun resetCards() = _pile.clear()
}

// TODO: Check if this is needed at all, currently replaced by SolitaireDeck Composable
/**
 *  Composable to display [Foundation] that corresponds to given [foundation].
 */
@Composable
fun SolitaireFoundation(foundation: Foundation, modifier: Modifier = Modifier) {
    if (foundation.pile.isEmpty()) {
        Image(
            modifier = modifier,
            painter = painterResource(foundation.suit.emptyIcon),
            contentDescription = "${foundation.suit} Foundation pile is empty.",
            contentScale = ContentScale.FillBounds
        )
    } else {
        SolitaireCard(
            modifier = modifier,
            card = foundation.pile.last()
        )
    }
}

@Preview
@Composable
fun SolitaireFoundationEmptyPreview() {
    SolitairePreview {
        Row {
            SolitaireFoundation(foundation = Foundation(Suits.CLUBS))
            SolitaireFoundation(foundation = Foundation(Suits.DIAMONDS))
            SolitaireFoundation(foundation = Foundation(Suits.HEARTS))
            SolitaireFoundation(foundation = Foundation(Suits.SPADES))
        }
    }
}

@Preview
@Composable
fun SolitaireFoundationPreview() {
    SolitairePreview {
        SolitaireFoundation(foundation = Foundation(Suits.CLUBS))
    }
}