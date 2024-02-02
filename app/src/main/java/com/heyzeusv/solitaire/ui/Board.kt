package com.heyzeusv.solitaire.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.Foundation
import com.heyzeusv.solitaire.data.Stock
import com.heyzeusv.solitaire.data.Tableau
import com.heyzeusv.solitaire.data.Waste
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau.
 */
@Composable
fun SolitaireBoard(gameVM: GameViewModel, modifier: Modifier = Modifier) {

    SolitaireBoard(
        stock = gameVM.stock,
        onStockClick = gameVM::onStockClick,
        waste = gameVM.waste,
        onWasteClick = gameVM::onWasteClick,
        foundationList = gameVM.foundation,
        onFoundationClick = gameVM::onFoundationClick,
        tableauList = gameVM.tableau,
        onTableauClick = gameVM::onTableauClick,
        modifier = modifier
    )
}

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau. All the data
 *  has been hoisted into above [SolitaireBoard] thus allowing for easier testing.
 */
@Composable
fun SolitaireBoard(
    stock: Stock,
    onStockClick: () -> Unit,
    waste: Waste,
    onWasteClick: () -> Unit,
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> Unit,
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // gets device size in order to scale card
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
    val cardHeight = cardWidth.times(1.4f)

    // piles to be displayed
    val stockPile by remember { mutableStateOf(stock.pile) }
    val wastePile by remember { mutableStateOf(waste.pile) }
    val foundationPileList = foundationList.map {
        val foundationPile by remember { mutableStateOf(it.pile) }
        return@map foundationPile
    }
    val tableauPileList = tableauList.map {
        val tableauPile by remember { mutableStateOf(it.pile) }
        return@map tableauPile
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(0.15f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                val rowModifier = Modifier
                    .weight(1f)
                    .height(cardHeight)
                Suits.entries.forEachIndexed { index, suit ->
                    SolitairePile(
                        modifier = rowModifier,
                        pile = foundationPileList[index],
                        emptyIconId = suit.emptyIcon,
                        onClick = { onFoundationClick(index) }
                    )
                }
                Spacer(modifier = rowModifier)
                SolitairePile(
                    modifier = rowModifier,
                    pile = wastePile,
                    emptyIconId = R.drawable.waste_empty,
                    onClick = onWasteClick
                )
                SolitairePile(
                    modifier = rowModifier,
                    pile = stockPile,
                    emptyIconId = if (wastePile.isEmpty()) R.drawable.stock_empty else R.drawable.stock_reset,
                    onClick = onStockClick
                )
            }
            Row(
                modifier = Modifier.weight(0.85f),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tableauPileList.forEachIndexed { index, tableau ->
                    SolitaireTableau(
                        modifier = Modifier.weight(1f),
                        pile = tableau,
                        tableauIndex = index,
                        cardHeight = cardHeight,
                        onClick = onTableauClick
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun SolitaireBoardPreview() {
    SolitairePreview {
        val bCard = Card(10, Suits.SPADES, true)
        val rCard = Card(4, Suits.DIAMONDS, true)
        SolitaireBoard(
            stock = Stock(listOf(bCard, rCard, bCard)),
            onStockClick = { },
            waste = Waste(listOf(bCard, rCard, bCard)),
            onWasteClick = { },
            foundationList = listOf(
                Foundation(Suits.CLUBS, listOf(bCard)),
                Foundation(Suits.DIAMONDS, listOf(rCard)),
                Foundation(Suits.HEARTS, listOf(rCard, bCard)),
                Foundation(Suits.SPADES, emptyList())
            ),
            onFoundationClick = { _ -> },
            tableauList = listOf(
                Tableau(listOf(bCard)), Tableau(listOf(rCard)), Tableau(listOf(bCard)),
                Tableau(listOf(rCard)), Tableau(listOf(bCard)), Tableau(listOf(rCard)),
                Tableau(listOf(bCard))),
            onTableauClick = { _, _ -> }
        )
    }
}