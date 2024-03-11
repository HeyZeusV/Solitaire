package com.heyzeusv.solitaire.ui.game

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Tableau.KlondikeTableau
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau.
 */
@Composable
fun SolitaireBoard(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    selectedGame: Games,
    modifier: Modifier = Modifier
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsState()

    SolitaireBoard(
        drawAmount = selectedGame.drawAmount,
        handleMoveResult = sbVM::handleMoveResult,
        stock = gameVM.stock,
        onStockClick = gameVM::onStockClick,
        waste = gameVM.waste,
        stockWasteEmpty = {  stockWasteEmpty },
        onWasteClick = gameVM::onWasteClick,
        foundationList = gameVM.foundation,
        onFoundationClick = gameVM::onFoundationClick,
        tableauList = gameVM.tableau,
        onTableauClick = gameVM::onTableauClick,
        modifier = modifier
    )
}

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau. [drawAmount]
 *  determines how many cards are added to the Waste pile on single [onStockClick]. All the data
 *  has been hoisted into above [SolitaireBoard] thus allowing for easier testing.
 */
@Composable
fun SolitaireBoard(
    drawAmount: Int,
    handleMoveResult: (MoveResult) -> Unit,
    stock: Stock,
    onStockClick: (Int) -> MoveResult,
    waste: Waste,
    stockWasteEmpty: () -> Boolean,
    onWasteClick: () -> MoveResult,
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> MoveResult,
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> MoveResult,
    modifier: Modifier = Modifier
) {
    // gets device size in order to scale card
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    val cardWidth = sWidth / 7 // need to fit 7 piles wide on screen
    val cardHeight = cardWidth.times(1.4f)

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
                        modifier = rowModifier.testTag("Foundation #$index"),
                        pile = foundationList[index].pile,
                        emptyIconId = suit.emptyIcon,
                        onClick = { handleMoveResult(onFoundationClick(index)) },
                        cardWidth = cardWidth
                    )
                }
                if (drawAmount == 1) Spacer(modifier = rowModifier)
                SolitairePile(
                    modifier = Modifier
                        .weight(if (drawAmount == 1) 1f else 2.04f)
                        .height(cardHeight)
                        .testTag("Waste"),
                    pile = waste.pile,
                    emptyIconId = R.drawable.waste_empty,
                    onClick = { handleMoveResult(onWasteClick()) },
                    drawAmount = drawAmount,
                    cardWidth = cardWidth
                )
                SolitaireStock(
                    modifier = rowModifier.testTag("Stock"),
                    pile = stock.pile,
                    stockWasteEmpty = stockWasteEmpty,
                    onClick = { handleMoveResult(onStockClick(drawAmount)) },
                    cardWidth = cardWidth
                )
            }
            Row(
                modifier = Modifier
                    .weight(0.85f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                tableauList.forEachIndexed { index, tableau ->
                    SolitaireTableau(
                        modifier = Modifier.weight(1f),
                        pile = tableau.pile,
                        tableauIndex = index,
                        cardHeight = cardHeight,
                        onClick = onTableauClick,
                        handleMoveResult = handleMoveResult
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
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(bCard, rCard, bCard)),
            onStockClick = { MoveResult.Illegal },
            waste = Waste(listOf(bCard, rCard, bCard)),
            stockWasteEmpty = { true },
            onWasteClick = { MoveResult.Illegal },
            foundationList = listOf(
                Foundation(Suits.CLUBS, listOf(bCard)),
                Foundation(Suits.DIAMONDS, listOf(rCard)),
                Foundation(Suits.HEARTS, listOf(rCard, bCard)),
                Foundation(Suits.SPADES, emptyList())
            ),
            onFoundationClick = { _ -> MoveResult.Illegal},
            tableauList = listOf(
                KlondikeTableau(initialPile = listOf(bCard)), KlondikeTableau(initialPile = listOf(rCard)),
                KlondikeTableau(initialPile = listOf(bCard)), KlondikeTableau(initialPile = listOf(rCard)),
                KlondikeTableau(initialPile = listOf(bCard)), KlondikeTableau(initialPile = listOf(rCard)),
                KlondikeTableau(initialPile = listOf(bCard))
            ),
            onTableauClick = { _, _ -> MoveResult.Illegal}
        )
    }
}