package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  In Solitaire, Tableau refers to the 7 piles that start with 1 face up card per pile and the rest
 *  face down. Users can move cards between [Tableau] piles or move them to a [Foundation] pile in
 *  order to reveal more cards.
 */
class Tableau(private val _pile: MutableList<Card> = mutableListOf()) {

    val pile: List<Card> get() = _pile

    /**
     *  Attempts to add given [cards] to [pile] depending on [cards] first card value and suit and
     *  [pile]'s last card value and suit. Returns true if added.
     */
    fun addCards(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false
        val cFirst = cards.first()
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.value == pLast.value - 1 && cFirst.suit.color != pLast.suit.color) {
                _pile.addAll(cards)
                return true
            }
        // add cards if pile is empty and first card of new cards is the highest value
        } else if (cFirst.value == 12) {
            _pile.addAll(cards)
            return true
        }
        return false
    }

    /**
     *  Removes all cards from [pile] started from [tappedIndex] to the end of [pile] and flips the
     *  last card if any.
     */
    fun removeCards(tappedIndex: Int) {
        _pile.subList(tappedIndex, _pile.size).clear()
        // flip the last card up
        if (_pile.isNotEmpty()) _pile.last().faceUp = true
    }

    init {
        // flip the last card up
        if (_pile.isNotEmpty()) _pile.last().faceUp = true
    }
}

/**
 *  Composable that displays one of the Tableau [pile]. [cardHeight] is used to shift each card in
 *  [pile], after the first, upwards. Displays static image if [pile] is empty.
 */
@Composable
fun SolitaireTableau(pile: List<Card>, cardHeight: Dp, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = -(cardHeight.times(0.8f)))
    ) {
        if (pile.isEmpty()) {
            Image(
                modifier = Modifier.height(cardHeight),
                painter = painterResource(R.drawable.tableau_empty),
                contentDescription = "Pile is empty.",
                contentScale = ContentScale.FillBounds
            )
        } else {
            pile.forEach {
                SolitaireCard(
                    modifier = Modifier.height(cardHeight),
                    card = it
                )
            }
        }
    }
}

@Preview
@Composable
fun SolitaireTableauEmptyPreview() {
    SolitairePreview {
        SolitaireTableau(pile = emptyList(), cardHeight = 0.dp)
    }
}

@Preview
@Composable
fun SolitaireTableauPreview() {
    SolitairePreview {
        // gets device size in order to scale card
        val config = LocalConfiguration.current
        val sWidth = config.screenWidthDp.dp
        val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
        val cardHeight = cardWidth.times(1.4f)

        SolitaireTableau(pile = listOf(Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES), Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES)), cardHeight = cardHeight)
    }
}