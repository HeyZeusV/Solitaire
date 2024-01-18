package com.heyzeusv.solitaire

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.ui.theme.SolitaireTheme

// card value to their display value
val cardsMap = mapOf(0 to "A", 1 to "2", 2 to "3" , 3 to "4", 4 to "5", 5 to "6", 6 to "7", 7 to "8", 8 to "9", 9 to "10", 10 to "J", 11 to "Q", 12 to "K")
/**
 *  Data class containing information for individual cards. [value] ranges from 0 to 13 which
 *  corresponds to the values 1 to 10, A, J, Q, K. [suit] is one of 4 possible values from the
 *  [Suits] enum class. [faceUp] determines if the user can see the card.
 */
data class Card(val value: Int, val suit: Suits, var faceUp: Boolean = false) {

    override fun toString(): String = if (faceUp) "${cardsMap[value]} of ${suit.suit}" else "???"
}

/**
 *  Composable that displays a [Card]. Depending on given [card]'s faceUp value will either display
 *  the value and suit or a static image concealing that information to the user.
 */
@Composable
fun SolitaireCard(
    card: Card,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        if (card.faceUp) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = cardsMap[card.value] ?: "A",
                    color = card.suit.color,
                    style = MaterialTheme.typography.displayMedium
                )
                Image(
                    modifier = Modifier
                        .fillMaxSize(0.9f),
                    painter = painterResource(card.suit.icon),
                    contentDescription = "Card Suit"
                )
            }
        } else {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.card_back),
                contentDescription = "Back of a card",
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun SolitaireCardFaceUpPreview() {
    SolitaireTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            SolitaireCard(Card(0, Suits.CLUBS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(12, Suits.DIAMONDS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(11, Suits.HEARTS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(10, Suits.SPADES, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(9, Suits.CLUBS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(8, Suits.DIAMONDS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(7, Suits.HEARTS, faceUp = true), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
        }
    }
}

@Preview
@Composable
fun SolitaireCardFaceDownPreview() {
    SolitaireTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            SolitaireCard(Card(0, Suits.CLUBS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(12, Suits.DIAMONDS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(11, Suits.HEARTS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(10, Suits.SPADES), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(9, Suits.CLUBS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(8, Suits.DIAMONDS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
            SolitaireCard(Card(7, Suits.HEARTS), modifier = Modifier.weight(1f).fillMaxHeight(0.196f))
        }
    }
}