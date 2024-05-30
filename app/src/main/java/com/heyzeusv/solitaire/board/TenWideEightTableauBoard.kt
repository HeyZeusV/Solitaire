package com.heyzeusv.solitaire.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.board.piles.SolitairePile
import com.heyzeusv.solitaire.board.piles.SolitaireStock
import com.heyzeusv.solitaire.board.piles.SolitaireTableau
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.CardLogic
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.piles.Waste
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.board.layouts.TenWideLayout
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Composable that displays all [CardLogic] piles, Stock, Waste, Foundation, and Tableau. [layout] is
 *  used to determine offsets of every pile. [animationDurations] determines how long each animation
 *  lasts. [animateInfo] is used to determine what needs to be animated and can be updated with
 *  [updateAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
 *  animations. [undoAnimation] is used to enable/disable all clicks during an undo animation and
 *  is updated using [updateUndoAnimation]. [drawAmount] determines how many cards are drawn from
 *  [Stock] and shown by [Waste].
 */
@Composable
fun TenWideEightTableauBoard(
    modifier: Modifier = Modifier,
    layout: TenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateUndoEnabled: (Boolean) -> Unit = { },
    undoAnimation: Boolean,
    updateUndoAnimation: (Boolean) -> Unit = { },
    drawAmount: DrawAmount,
    /** Piles and their onClicks */
    stock: Stock,
    onStockClick: () -> Unit = { },
    waste: Waste,
    stockWasteEmpty: () -> Boolean = { true },
    onWasteClick: () -> Unit = { },
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> Unit = { },
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> Unit = { _, _ -> }
) {
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }

    animateInfo?.let {
        // Updating AnimateInfo to null if animation is fully completed
        LaunchedEffect(key1 = it) {
            try {
                if (it.isUndoAnimation) updateUndoAnimation(true) else updateUndoEnabled(false)
                delay(animationDurations.fullDelay)
                updateAnimateInfo(null)
            } finally {
                if (it.isUndoAnimation) updateUndoAnimation(false)
            }
        }
        // Action Before Animation
        LaunchedEffect(key1 = it) {
            try {
                delay(animationDurations.beforeActionDelay)
            } finally {
                it.actionBeforeAnimation()
            }
        }
        // Action After Animation
        LaunchedEffect(key1 = it) {
            try {
                delay(animationDurations.afterActionDelay)
            } finally {
                it.actionAfterAnimation()
            }
        }
        if (it.isNotMultiPile()) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layout.getPilePosition(it.start, it.stockWasteMove)
                    .plus(layout.getCardsYOffset(it.startTableauIndices.first())),
                endOffset = layout.getPilePosition(it.end, it.stockWasteMove)
                    .plus(layout.getCardsYOffset(it.endTableauIndices.first())),
                updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
                updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) }
            )
        }
    }

    Layout(
        modifier = modifier.gesturesDisabled(undoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipCardInfo) {
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> {
                        HorizontalCardPileWithFlip(
                            layout = layout,
                            animateInfo = it,
                            animationDurations = animationDurations,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
//                        MultiPileCardWithFlip(
//                            layout = layout,
//                            animateInfo = it,
//                            animationDurations = animationDurations,
//                            modifier = Modifier.layoutId("Animated Multi Pile")
//                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        StaticVerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
                            spacedByPercent = layout.vPileSpacedByPercent,
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Static Vertical Pile")
                        )
                    }
                }
//                it.tableauCardFlipInfo?.let { _ ->
//                    TableauPileWithFlip(
//                        cardDpSize = layout.getCardDpSize(),
//                        spacedByPercent = layout.vPileSpacedByPercent,
//                        animateInfo = it,
//                        animationDurations = animationDurations,
//                        modifier = Modifier.layoutId("Animated Tableau Card")
//                    )
//                }
            }
            foundationList.forEachIndexed { index, foundation ->
                SolitairePile(
                    modifier = Modifier
                        .layoutId("Foundation #$index")
                        .testTag("Foundation #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    pile = foundation.displayPile,
                    emptyIconId = foundation.suit.emptyIcon,
                    onClick = { onFoundationClick(index) }
                )
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                cardDpSize = layout.getCardDpSize(),
                pile = waste.displayPile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { onWasteClick() },
                drawAmount = drawAmount
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                cardDpSize = layout.getCardDpSize(),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { onStockClick() }
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    spacedByPercent = layout.vPileSpacedByPercent,
                    pile = tableau.displayPile,
                    tableauIndex = index,
                    onClick = onTableauClick
                )
            }
        }
    ) { measurables, constraints ->
        val foundationClubsOne = measurables.firstOrNull { it.layoutId == "Foundation #0" }
        val foundationDiamondsOne = measurables.firstOrNull { it.layoutId == "Foundation #1" }
        val foundationHeartsOne = measurables.firstOrNull { it.layoutId == "Foundation #2" }
        val foundationSpadesOne = measurables.firstOrNull { it.layoutId == "Foundation #3" }
        val foundationClubsTwo = measurables.firstOrNull { it.layoutId == "Foundation #4" }
        val foundationDiamondsTwo = measurables.firstOrNull { it.layoutId == "Foundation #5" }
        val foundationHeartsTwo = measurables.firstOrNull { it.layoutId == "Foundation #6" }
        val foundationSpadesTwo = measurables.firstOrNull { it.layoutId == "Foundation #7" }

        val wastePile = measurables.firstOrNull { it.layoutId == "Waste" }
        val stockPile = measurables.firstOrNull { it.layoutId == "Stock" }

        val tableauZero = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        val tableauOne = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        val tableauTwo = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        val tableauThree = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        val tableauFour = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        val tableauFive = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        val tableauSix = measurables.firstOrNull { it.layoutId == "Tableau #6" }
        val tableauSeven = measurables.firstOrNull { it.layoutId == "Tableau #7" }

        val animatedHorizontalPile =
            measurables.firstOrNull { it.layoutId == "Animated Horizontal Pile" }
        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Static Vertical Pile" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layout.cardWidth
            val cardHeight = layout.cardHeight
            val cardConstraints = layout.cardConstraints
            val wasteConstraints = layout.wasteConstraints
            val tableauHeight = constraints.maxHeight - layout.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            if (animatedOffset != IntOffset.Zero) {
                animatedHorizontalPile?.measure(wasteConstraints)?.place(animatedOffset, 2f)
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
            }

            foundationClubsOne?.measure(cardConstraints)?.place(layout.foundationClubsOne)
            foundationDiamondsOne?.measure(cardConstraints)?.place(layout.foundationDiamondsOne)
            foundationHeartsOne?.measure(cardConstraints)?.place(layout.foundationHeartsOne)
            foundationSpadesOne?.measure(cardConstraints)?.place(layout.foundationSpadesOne)
            foundationClubsTwo?.measure(cardConstraints)?.place(layout.foundationClubsTwo)
            foundationDiamondsTwo?.measure(cardConstraints)?.place(layout.foundationDiamondsTwo)
            foundationHeartsTwo?.measure(cardConstraints)?.place(layout.foundationHeartsTwo)
            foundationSpadesTwo?.measure(cardConstraints)?.place(layout.foundationSpadesTwo)
            wastePile?.measure(wasteConstraints)?.place(layout.wastePile)
            stockPile?.measure(cardConstraints)?.place(layout.stockPile)

            tableauZero?.measure(tableauConstraints)?.place(layout.tableauZero)
            tableauOne?.measure(tableauConstraints)?.place(layout.tableauOne)
            tableauTwo?.measure(tableauConstraints)?.place(layout.tableauTwo)
            tableauThree?.measure(tableauConstraints)?.place(layout.tableauThree)
            tableauFour?.measure(tableauConstraints)?.place(layout.tableauFour)
            tableauFive?.measure(tableauConstraints)?.place(layout.tableauFive)
            tableauSix?.measure(tableauConstraints)?.place(layout.tableauSix)
            tableauSeven?.measure(tableauConstraints)?.place(layout.tableauSeven)
        }
    }
}