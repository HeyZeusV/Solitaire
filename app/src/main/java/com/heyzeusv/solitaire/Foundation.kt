package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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

    private val _pile = mutableListOf<Card>()
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
    fun removeCard() {
        _pile.removeLast()
    }

    /**
     *  Removes all cards from the pile.
     */
    fun resetCards() {
        _pile.clear()
    }
}

/**
 *  Composable to display [Foundation] that corresponds to given [suit]. Shows static image if
 *  [emptyDeck].
 */
@Composable
fun SolitaireFoundation(suit: Suits, emptyDeck: Boolean) {
    val cardWidth = LocalCardSize.current.width
    val cardHeight = LocalCardSize.current.height

    val modifier = Modifier.size(width = cardWidth, height = cardHeight)
    if (emptyDeck) {
        Image(
            modifier = modifier,
            painter = painterResource(suit.emptyIcon),
            contentDescription = "${suit.suit} Foundation pile is empty.",
            contentScale = ContentScale.FillBounds
        )
    } else {
        // TODO: Use actual data
        SolitaireCard(
            modifier = modifier,
            card = Card(100, suit)
        )
    }
}

@Preview
@Composable
fun SolitaireFoundationEmptyPreview() {
    SolitairePreview {
        Row {
            SolitaireFoundation(suit = Suits.CLUBS, emptyDeck = true)
            SolitaireFoundation(suit = Suits.DIAMONDS, emptyDeck = true)
            SolitaireFoundation(suit = Suits.HEARTS, emptyDeck = true)
            SolitaireFoundation(suit = Suits.SPADES, emptyDeck = true)
        }
    }
}

@Preview
@Composable
fun SolitaireFoundationPreview() {
    SolitairePreview {
        SolitaireFoundation(suit = Suits.CLUBS, emptyDeck = false)
    }
}