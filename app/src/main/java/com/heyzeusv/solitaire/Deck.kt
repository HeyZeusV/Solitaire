package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

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
 *  Composable that displays [Deck]. Show static image if [emptyDeck] else show a face down card.
 */
@Composable
fun SolitaireDeck(emptyDeck: Boolean) {
    // TODO: move this somewhere where all composables can access
    // gets device size in order to scale card.
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7
    val cardHeight = cardWidth.times(1.4f)

    val modifier = Modifier.size(width = cardWidth, height = cardHeight)
    if (emptyDeck) {
        Image(
            modifier = modifier,
            painter = painterResource(R.drawable.deck_empty),
            contentDescription = "Deck is empty.",
            contentScale = ContentScale.None
        )
    } else {
        // won't be used in game, just for show
        SolitaireCard(
            modifier = modifier,
            card = Card(100, Suits.CLUBS)
        )
    }
}

@Preview
@Composable
fun SolitaireDeckEmptyPreview() {
    SolitaireTheme {
        SolitaireDeck(emptyDeck = true)
    }
}

@Preview
@Composable
fun SolitaireDeckPreview() {
    SolitaireTheme {
        SolitaireDeck(emptyDeck = false)
    }
}