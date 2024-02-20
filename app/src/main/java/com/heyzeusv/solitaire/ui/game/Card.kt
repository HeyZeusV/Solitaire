package com.heyzeusv.solitaire.ui.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.cardsMap
import com.heyzeusv.solitaire.util.theme.SolitaireTheme
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.ui.AutoSizeText

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
        modifier = modifier.testTag("$card"),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(
            width = if (card.faceUp) 1.dp else 0.dp,
            brush = Brush.linearGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent)))
    ) {
        if (card.faceUp) {
            Column(
                modifier = Modifier.padding(4.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // displays smaller value and icon that is visible when cards are stacked on top
                Row(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth()
                        .padding(horizontal = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AutoSizeText(
                        text = cardsMap[card.value] ?: "A",
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        color = card.suit.color,
                        alignment = Alignment.CenterStart,
                        maxLines = 1
                    )
                    Image(
                        painter = painterResource(card.suit.icon),
                        contentDescription = stringResource(
                            R.string.card_cdesc_icon,
                            cardsMap[card.value] ?: "A",
                            stringResource(card.suit.suit)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        alignment = Alignment.CenterEnd
                    )
                }
                Image(
                    painter = painterResource(card.suit.icon),
                    contentDescription = stringResource(
                        R.string.card_cdesc_icon,
                        cardsMap[card.value] ?: "A",
                        stringResource(card.suit.suit)
                    ),
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxSize()
                )
            }
        } else {
            Image(
                painter = painterResource(R.drawable.card_back),
                contentDescription = stringResource(R.string.card_cdesc_back),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun SolitaireCardPreview() {
    SolitaireTheme {
        SolitaireCard(
            Card(0, Suits.CLUBS, faceUp = true),
            modifier = Modifier.width(120.dp).height(160.dp)
        )
    }
}

@Preview
@Composable
fun SolitaireCardFaceUpPreview() {
    SolitaireTheme {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            SolitaireCard(
                Card(0, Suits.CLUBS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(12, Suits.DIAMONDS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(11, Suits.HEARTS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(10, Suits.SPADES, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(9, Suits.CLUBS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(8, Suits.DIAMONDS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(7, Suits.HEARTS, faceUp = true), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
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
            SolitaireCard(
                Card(0, Suits.CLUBS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(12, Suits.DIAMONDS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(11, Suits.HEARTS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(10, Suits.SPADES), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(9, Suits.CLUBS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(8, Suits.DIAMONDS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
            SolitaireCard(
                Card(7, Suits.HEARTS), modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(0.196f)
            )
        }
    }
}