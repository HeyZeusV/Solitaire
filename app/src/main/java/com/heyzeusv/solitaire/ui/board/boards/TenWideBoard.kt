package com.heyzeusv.solitaire.ui.board.boards

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
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.ui.board.AnimateOffset
import com.heyzeusv.solitaire.ui.board.MultiPileCardWithFlip
import com.heyzeusv.solitaire.ui.board.SolitairePile
import com.heyzeusv.solitaire.ui.board.SolitaireStock
import com.heyzeusv.solitaire.ui.board.SolitaireTableau
import com.heyzeusv.solitaire.ui.board.TableauPileWithFlip
import com.heyzeusv.solitaire.ui.board.VerticalCardPile
import com.heyzeusv.solitaire.ui.board.boards.layouts.TenWideLayout
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau. [layout] is
 *  used to determine offsets of every pile. [animationDurations] determines how long each animation
 *  lasts. [animateInfo] is used to determine what needs to be animated and can be updated with
 *  [updateAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
 *  animations. [undoAnimation] is used to enable/disable all clicks during an undo animation and
 *  is updated using [updateUndoAnimation]. [drawAmount] determines how many cards are drawn from
 *  [Stock] and shown by [Waste].
 */
@Composable
fun TenWideBoard(
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
                if (it.undoAnimation) updateUndoAnimation(true) else updateUndoEnabled(false)
                delay(animationDurations.fullDelay)
                updateAnimateInfo(null)
            } finally {
                if (it.undoAnimation) updateUndoAnimation(false)
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
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> { }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
                        MultiPileCardWithFlip(
                            layout = layout,
                            animateInfo = it,
                            animationDurations = animationDurations,
                            modifier = Modifier.layoutId("Animated Multi Pile")
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        VerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile")
                        )
                    }
                }
                it.tableauCardFlipInfo?.let { _ ->
                    TableauPileWithFlip(
                        cardDpSize = layout.getCardDpSize(),
                        animateInfo = it,
                        animationDurations = animationDurations,
                        modifier = Modifier.layoutId("Animated Tableau Card")
                    )
                }
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

        var tableauZero = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        var tableauOne = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        var tableauTwo = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        var tableauThree = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        var tableauFour = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        var tableauFive = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        var tableauSix = measurables.firstOrNull { it.layoutId == "Tableau #6" }
        var tableauSeven = measurables.firstOrNull { it.layoutId == "Tableau #7" }
        var tableauEight = measurables.firstOrNull { it.layoutId == "Tableau #8" }
        var tableauNine = measurables.firstOrNull { it.layoutId == "Tableau #9" }

        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Vertical Pile" }
        val animatedMultiPile = measurables.firstOrNull { it.layoutId == "Animated Multi Pile" }
        val animatedTableauCard = measurables.firstOrNull { it.layoutId == "Animated Tableau Card" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layout.cardWidth
            val cardHeight = layout.cardHeight
            val cardConstraints = layout.cardConstraints
            val wasteConstraints = layout.wasteConstraints
            val tableauHeight = constraints.maxHeight - layout.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            if (animatedOffset != IntOffset.Zero) {
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
                animateInfo?.let {
                    it.tableauCardFlipInfo?.let { info ->
                        val pile =
                            if (info.flipCardInfo is FlipCardInfo.FaceDown) it.end else it.start
                        val tableauCardFlipPosition = layout.getPilePosition(pile)
                        animatedTableauCard?.measure(tableauConstraints)
                            ?.place(tableauCardFlipPosition, 1f)
                        when (pile) {
                            GamePiles.TableauZero -> tableauZero = null
                            GamePiles.TableauOne -> tableauOne = null
                            GamePiles.TableauTwo -> tableauTwo = null
                            GamePiles.TableauThree -> tableauThree = null
                            GamePiles.TableauFour -> tableauFour = null
                            GamePiles.TableauFive -> tableauFive = null
                            GamePiles.TableauSix -> tableauSix = null
                            GamePiles.TableauSeven -> tableauSeven = null
                            GamePiles.TableauEight -> tableauEight = null
                            GamePiles.TableauNine -> tableauNine = null
                            else -> { }
                        }
                    }
                }
            }
            animatedMultiPile?.measure(constraints)?.place(IntOffset.Zero, 2f)

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
            tableauEight?.measure(tableauConstraints)?.place(layout.tableauEight)
            tableauNine?.measure(tableauConstraints)?.place(layout.tableauNine)
        }
    }
}