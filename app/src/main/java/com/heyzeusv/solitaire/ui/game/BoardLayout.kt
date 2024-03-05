package com.heyzeusv.solitaire.ui.game

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.Suits

@Composable
fun BoardLayout(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    selectedGame: Games,
    modifier: Modifier = Modifier
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsState()

    BoardLayout(
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

@Composable
fun BoardLayout(
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
    // removed layout padding and space between cards
    val cardW = (sWidth - 4.dp - 12.dp) / 7 // need to fit 7 piles wide on screen
    val cardH = cardW.times(1.45f)

    Layout(
        modifier = modifier.padding(start = 1.dp, top = 2.dp, end = 0.dp, bottom = 2.dp),
        content = {
            Suits.entries.forEachIndexed { index, suit ->
                SolitairePile(
                    modifier = Modifier
                        .layoutId("${suit.name} Foundation")
                        .testTag("Foundation #$index"),
                    pile = foundationList[index].pile,
                    emptyIconId = suit.emptyIcon,
                    onClick = { handleMoveResult(onFoundationClick(index)) },
                    cardWidth = cardW
                )
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                pile = waste.pile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { handleMoveResult(onWasteClick()) },
                drawAmount = drawAmount,
                cardWidth = cardW
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                pile = stock.pile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick(drawAmount)) },
                cardWidth = cardW
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    pile = tableau.pile,
                    tableauIndex = index,
                    cardHeight = cardH,
                    onClick = onTableauClick,
                    handleMoveResult = handleMoveResult
                )
            }
        }
    ) { measurables, constraints ->

        val clubsFoundation = measurables.firstOrNull { it.layoutId == "CLUBS Foundation" }
        val diamondsFoundation = measurables.firstOrNull { it.layoutId == "DIAMONDS Foundation" }
        val heartsFoundation = measurables.firstOrNull { it.layoutId == "HEARTS Foundation" }
        val spadesFoundation = measurables.firstOrNull { it.layoutId == "SPADES Foundation" }
        val wastePile = measurables.firstOrNull { it.layoutId == "Waste" }
        val stockPile = measurables.firstOrNull { it.layoutId == "Stock" }

        val tableauPile0 = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        val tableauPile1 = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        val tableauPile2 = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        val tableauPile3 = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        val tableauPile4 = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        val tableauPile5 = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        val tableauPile6 = measurables.firstOrNull { it.layoutId == "Tableau #6" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // space between cards
            val cardSpacingPx = 2.dp.toPx()
            val cardSpacing = cardSpacingPx.toInt()
            // 7 cards wide
            val cardWidth = ((constraints.maxWidth - (cardSpacingPx * 6)) / 7).toInt()
            val cardHeight = (cardWidth * 1.45).toInt()
            val cardConstraints = Constraints(cardWidth, cardWidth, cardHeight, cardHeight)
            // waste is 2 card widths wide
            val wasteWidth = cardWidth * 2 + cardSpacing
            val wasteConstraints = Constraints(wasteWidth, wasteWidth, cardHeight, cardHeight)

            val tableauY = cardHeight + (cardSpacing * 5)
            val tableauHeight = constraints.maxHeight - tableauY
            val tableauConstraints = Constraints(cardWidth, cardWidth, tableauHeight, tableauHeight)

            clubsFoundation?.measure(cardConstraints)?.place(0, 0)
            diamondsFoundation?.measure(cardConstraints)?.place(cardWidth + cardSpacing, 0)
            heartsFoundation?.measure(cardConstraints)?.place((cardWidth + cardSpacing) * 2, 0)
            spadesFoundation?.measure(cardConstraints)?.place((cardWidth + cardSpacing) * 3, 0)
            wastePile?.measure(wasteConstraints)?.place((cardWidth + cardSpacing) * 4, 0)
            stockPile?.measure(cardConstraints)?.place((cardWidth + cardSpacing) * 6, 0)

            tableauPile0?.measure(tableauConstraints)?.place(0, tableauY)
            tableauPile1?.measure(tableauConstraints)?.place(cardWidth + cardSpacing, tableauY)
            tableauPile2?.measure(tableauConstraints)?.place((cardWidth + cardSpacing) * 2, tableauY)
            tableauPile3?.measure(tableauConstraints)?.place((cardWidth + cardSpacing) * 3, tableauY)
            tableauPile4?.measure(tableauConstraints)?.place((cardWidth + cardSpacing) * 4, tableauY)
            tableauPile5?.measure(tableauConstraints)?.place((cardWidth + cardSpacing) * 5, tableauY)
            tableauPile6?.measure(tableauConstraints)?.place((cardWidth + cardSpacing) * 6, tableauY)
        }
    }
}