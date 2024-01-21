package com.heyzeusv.solitaire

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  Class that handles 52 [Card] deck creation.
 */
class Deck {

    private val baseDeck = MutableList(52) { Card(it % 13, getSuit(it)) }
    private var _gameDeck: MutableList<Card> = mutableListOf()
    val gameDeck: List<Card> get() = _gameDeck

    // removes the first card from gameDeck and returns it
    fun drawCard(): Card = _gameDeck.removeFirst()

    // replace gameDeck with given list
    fun replace(list: MutableList<Card>) {
        _gameDeck = list
    }

    // reset gameDeck and shuffle the cards
    fun reset() {
        replace(baseDeck)
        _gameDeck.shuffle()
    }

    /**
     *  Used during creation of deck to assign suit to each card.
     *  Cards  0-12 -> Clubs
     *  Cards 13-25 -> Diamonds
     *  Cards 26-38 -> Hearts
     *  Cards 39-51 -> Spades
     */
    private fun getSuit(i: Int) = when (i / 13) {
        0 -> Suits.CLUBS
        1 -> Suits.DIAMONDS
        2 -> Suits.HEARTS
        else -> Suits.SPADES
    }

    init {
        _gameDeck = baseDeck
    }
}

/**
 *  Composable that displays [pile]. If given [pile] is empty, [emptyIconId] is displayed.
 */
@Composable
fun SolitaireDeck(pile: List<Card>, @DrawableRes emptyIconId: Int, modifier: Modifier = Modifier) {
    if (pile.isEmpty()) {
        Image(
            modifier = modifier,
            painter = painterResource(emptyIconId),
            contentDescription = "Pile is empty.",
            contentScale = ContentScale.FillBounds
        )
    } else {
        SolitaireCard(
            modifier = modifier,
            card = pile.last()
        )
    }
}

@Preview
@Composable
fun SolitaireDeckEmptyPreview() {
    SolitairePreview {
        SolitaireDeck(pile = emptyList(), emptyIconId = R.drawable.deck_empty)
    }
}

@Preview
@Composable
fun SolitaireDeckPreview() {
    SolitairePreview {
        SolitaireDeck(
            pile = listOf(Card(100, Suits.CLUBS, faceUp = true)),
            emptyIconId = R.drawable.deck_empty
        )
    }
}