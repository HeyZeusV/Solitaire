package com.heyzeusv.solitaire.board.piles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.composables.AutoSizeText
import com.heyzeusv.solitaire.util.dRes
import com.heyzeusv.solitaire.util.pRes
import com.heyzeusv.solitaire.util.sRes

/**
 *  Displays either a face down Card, which is the app logo with a gradiant background, or a face
 *  up Card, which shows the Card value and its suit icon.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param card Contains the info to be displayed.
 */
@Composable
fun PlayingCard(
    modifier: Modifier = Modifier,
    card: Card,
) {
    Surface(
        modifier = modifier
            // makes click surface have round edges
            .clip(RoundedCornerShape(dRes(R.dimen.c_radius)))
            .testTag("$card"),
        shape = RoundedCornerShape(dRes(R.dimen.c_radius)),
        color = Color.White,
        border = BorderStroke(
            width = dRes(if (card.faceUp) R.dimen.c_bStroke else R.dimen.zero),
            brush = Brush.linearGradient(listOf(Color.Black.copy(alpha = 0.1f), Color.Transparent)))
    ) {
        if (card.faceUp) {
            Column(
                modifier = Modifier.padding(dRes(R.dimen.c_padding_all)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // displays smaller value and icon that is visible when cards are stacked on top
                val suit = sRes(card.suit.suit)
                val iconDescription = sRes(R.string.card_cdesc_icon, card.getDisplayValue(), suit)
                Row(
                    modifier = Modifier
                        .weight(0.2f)
                        .fillMaxWidth()
                        .padding(horizontal = dRes(R.dimen.c_padding_horizontal)),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AutoSizeText(
                        text = card.getDisplayValue(),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        color = card.suit.color,
                        alignment = Alignment.CenterStart,
                        maxLines = 1
                    )
                    Image(
                        painter = pRes(card.suit.icon),
                        contentDescription = iconDescription,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        alignment = Alignment.CenterEnd
                    )
                }
                Image(
                    painter = pRes(card.suit.icon),
                    contentDescription = iconDescription,
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxSize()
                )
            }
        } else {
            Image(
                painter = pRes(R.drawable.card_back),
                contentDescription = sRes(R.string.card_cdesc_back),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}

@Preview
@Composable
fun SingleCardPreview() {
    PreviewUtil().apply {
        Preview {
            PlayingCard(
                modifier = Modifier.size(card7WideSize),
                Card(0, Suits.CLUBS, faceUp = true)
            )
        }
    }
}

@Preview
@Composable
fun SevenWideCardFaceUpPreview() {
    PreviewUtil().apply {
        val mod = Modifier.size(card7WideSize)
        Preview {
            Row {
                PlayingCard(modifier = mod, Card(0, Suits.CLUBS, faceUp = true))
                PlayingCard(modifier = mod, Card(12, Suits.DIAMONDS, faceUp = true))
                PlayingCard(modifier = mod, Card(11, Suits.HEARTS, faceUp = true))
                PlayingCard(modifier = mod, Card(10, Suits.SPADES, faceUp = true))
                PlayingCard(modifier = mod, Card(9, Suits.CLUBS, faceUp = true))
                PlayingCard(modifier = mod, Card(8, Suits.DIAMONDS, faceUp = true))
                PlayingCard(modifier = mod, Card(7, Suits.HEARTS, faceUp = true))
            }
        }
    }
}

@Preview
@Composable
fun SevenWideCardFaceDownPreview() {
    PreviewUtil().apply {
        val mod = Modifier.size(card7WideSize)
        Preview {
            Row {
                PlayingCard(modifier = mod, Card(0, Suits.CLUBS))
                PlayingCard(modifier = mod, Card(12, Suits.DIAMONDS))
                PlayingCard(modifier = mod, Card(11, Suits.HEARTS))
                PlayingCard(modifier = mod, Card(10, Suits.SPADES))
                PlayingCard(modifier = mod, Card(9, Suits.CLUBS))
                PlayingCard(modifier = mod, Card(8, Suits.DIAMONDS))
                PlayingCard(modifier = mod, Card(7, Suits.HEARTS))
            }
        }
    }
}

@Preview
@Composable
fun TenWideCardFaceUpPreview() {
    PreviewUtil().apply {
        val mod = Modifier.size(card10WideSize)
        Preview {
            Row {
                PlayingCard(modifier = mod, Card(0, Suits.CLUBS, faceUp = true))
                PlayingCard(modifier = mod, Card(12, Suits.DIAMONDS, faceUp = true))
                PlayingCard(modifier = mod, Card(11, Suits.HEARTS, faceUp = true))
                PlayingCard(modifier = mod, Card(10, Suits.SPADES, faceUp = true))
                PlayingCard(modifier = mod, Card(9, Suits.CLUBS, faceUp = true))
                PlayingCard(modifier = mod, Card(8, Suits.DIAMONDS, faceUp = true))
                PlayingCard(modifier = mod, Card(7, Suits.HEARTS, faceUp = true))
                PlayingCard(modifier = mod, Card(6, Suits.SPADES, faceUp = true))
                PlayingCard(modifier = mod, Card(5, Suits.CLUBS, faceUp = true))
                PlayingCard(modifier = mod, Card(4, Suits.DIAMONDS, faceUp = true))

            }
        }
    }
}

@Preview
@Composable
fun TenWideCardFaceDownPreview() {
    PreviewUtil().apply {
        val mod = Modifier.size(card10WideSize)
        Preview {
            Row {
                PlayingCard(modifier = mod, Card(0, Suits.CLUBS))
                PlayingCard(modifier = mod, Card(12, Suits.DIAMONDS))
                PlayingCard(modifier = mod, Card(11, Suits.HEARTS))
                PlayingCard(modifier = mod, Card(10, Suits.SPADES))
                PlayingCard(modifier = mod, Card(9, Suits.CLUBS))
                PlayingCard(modifier = mod, Card(8, Suits.DIAMONDS))
                PlayingCard(modifier = mod, Card(7, Suits.HEARTS))
                PlayingCard(modifier = mod, Card(6, Suits.SPADES))
                PlayingCard(modifier = mod, Card(5, Suits.CLUBS))
                PlayingCard(modifier = mod, Card(4, Suits.DIAMONDS))
            }
        }
    }
}