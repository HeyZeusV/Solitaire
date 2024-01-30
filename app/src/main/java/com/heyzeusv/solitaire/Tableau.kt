package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.util.SolitairePreview

/**
 *  In Solitaire, Tableau refers to the 7 piles that start with 1 face up card per [mPile] and the
 *  rest face down. Users can move cards between [Tableau] piles or move them to a [Foundation] pile
 *  in order to reveal more cards.
 */
class Tableau(initialPile: List<Card> = emptyList()) : Pile(initialPile) {

    /**
     *  Attempts to add given [cards] to [mPile] depending on [cards] first card value and suit and
     *  [mPile]'s last card value and suit. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val cFirst = cards.first()
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.value == pLast.value - 1 && cFirst.suit.color != pLast.suit.color) {
                mPile.addAll(cards)
                return true
            }
        // add cards if pile is empty and first card of new cards is the highest value
        } else if (cFirst.value == 12) {
            mPile.addAll(cards)
            return true
        }
        return false
    }

    /**
     *  Removes all cards from [mPile] started from [tappedIndex] to the end of [mPile] and flips
     *  the last card if any.
     */
    override fun remove(tappedIndex: Int): Card {
        mPile.subList(tappedIndex, mPile.size).clear()
        // flip the last card up
        if (mPile.isNotEmpty()) {
            mPile[mPile.size - 1] = mPile.last().copy(faceUp = true)
        }
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Resets [mPile] by clearing existing cards, adding given [cards], and flipping last card
     *  face up.
     */
    override fun reset(cards: List<Card>) {
        mPile.apply {
            clear()
            addAll(cards)
            this[this.size - 1] = this.last().copy(faceUp = true)
        }
    }

    /**
     *  Used to return [mPile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        mPile.clear()
        if (cards.isEmpty()) return
        mPile.addAll(cards)
    }

    override fun toString(): String = pile.toList().toString()
}

/**
 *  Composable that displays Tableau [pile] with [tableauIndex]. [cardHeight] is used to shift each
 *  card in [pile], after the first, upwards so they overlap. [onClick] triggers when any card
 *  within [pile] is clicked, Displays static image if [pile] is empty.
 */
@Composable
fun SolitaireTableau(
    modifier: Modifier = Modifier,
    cardHeight: Dp,
    tableauIndex: Int = 0,
    pile: List<Card> = emptyList(),
    onClick: (Int, Int) -> Unit = { _, _ -> }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = -(cardHeight.times(0.75f)))
    ) {
        if (pile.isEmpty()) {
            Image(
                modifier = Modifier.height(cardHeight),
                painter = painterResource(R.drawable.tableau_empty),
                contentDescription = "Pile is empty.",
                contentScale = ContentScale.FillBounds
            )
        } else {
            pile.forEachIndexed { index, card ->
                SolitaireCard(
                    modifier = Modifier
                        .height(cardHeight)
                        .clip(RoundedCornerShape(4.dp)) // makes click surface have round edges
                        .clickable { onClick(tableauIndex, index) },
                    card = card
                )
            }
        }
    }
}

@Preview
@Composable
fun SolitaireTableauEmptyPreview() {
    SolitairePreview {
        // gets device size in order to scale card
        val config = LocalConfiguration.current
        val sWidth = config.screenWidthDp.dp
        val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
        val cardHeight = cardWidth.times(1.4f)

        SolitaireTableau(cardHeight = cardHeight)
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

        SolitaireTableau(
            cardHeight = cardHeight,
            pile = listOf(
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES),
                Card(0, Suits.DIAMONDS), Card(1, Suits.SPADES)),
            )
    }
}