package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
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
 *  In Solitaire, Tableau refers to the 7 piles that start with 1 face up card per [_pile] and the
 *  rest face down. Users can move cards between [Tableau] piles or move them to a [Foundation] pile
 *  in order to reveal more cards.
 */
class Tableau : Pile {

    private val _pile: SnapshotStateList<Card> = mutableStateListOf()
    override val pile: List<Card> get() = _pile

    /**
     *  Used to keep track of how many cards are face up in [_pile]. This is needed due to
     *  how [androidx.compose.runtime.snapshots.SnapshotStateList.toList] works. "The list returned
     *  is immutable and returned will not change even if the content of the list is changed in the
     *  same snapshot. It also will be the same instance until the content is changed". If
     *  [Card.faceUp] was changed, Undo feature would not return it to its previous value, so Cards
     *  would be incorrectly face up or down. I have to work on making [Card.faceUp] immutable in
     *  order for updates to occur when it changes.
     */
    private var _faceUpCards = 0
    val faceUpCards: Int get() = _faceUpCards

    /**
     *  Attempts to add given [cards] to [_pile] depending on [cards] first card value and suit and
     *  [_pile]'s last card value and suit. Returns true if added.
     */
    override fun add(cards: List<Card>): Boolean {
        if (cards.isEmpty()) return false

        val cFirst = cards.first()
        if (pile.isNotEmpty()) {
            val pLast = pile.last()
            // add cards if last card of pile is 1 more than first card of new cards
            // and if they are different colors
            if (cFirst.value == pLast.value - 1 && cFirst.suit.color != pLast.suit.color) {
                _pile.addAll(cards)
                _faceUpCards += cards.size
                return true
            }
        // add cards if pile is empty and first card of new cards is the highest value
        } else if (cFirst.value == 12) {
            _pile.addAll(cards)
            _faceUpCards += cards.size
            return true
        }
        return false
    }

    /**
     *  Removes all cards from [_pile] started from [tappedIndex] to the end of [_pile] and flips
     *  the last card if any.
     */
    override fun remove(tappedIndex: Int): Card {
        _faceUpCards -= _pile.size - tappedIndex
        _pile.subList(tappedIndex, _pile.size).clear()
        // flip the last card up
        if (_pile.isNotEmpty()) {
            _pile.last().faceUp = true
            _faceUpCards++
        }
        // return value isn't used
        return Card(0, Suits.SPADES, false)
    }

    /**
     *  Resets [_pile] by clearing existing cards, adding given [cards], and flipping last card
     *  face up.
     */
    override fun reset(cards: List<Card>) {
        _faceUpCards = 0
        _pile.apply {
            clear()
            addAll(cards)
            last().faceUp = true
        }
        _faceUpCards++
    }

    /**
     *  Used to return [_pile] to a previous state of given [cards].
     */
    override fun undo(cards: List<Card>) {
        _pile.clear()
        when (cards.size) {
            0 -> return // no cards to add
            1 -> {
                _pile.addAll(cards)
                _faceUpCards = 1
            }
            else -> {
                for (i in 0..(cards.size - 1 - _faceUpCards)) {
                    cards[i].faceUp = false
                }
                _pile.addAll(cards)
            }
        }
    }

    /**
     *  Used to return [_faceUpCards] to a previous state of given [faceUp].
     */
    fun undoFaceUpCards(faceUp: Int) {
        _faceUpCards = faceUp
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