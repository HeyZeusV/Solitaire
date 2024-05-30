package com.heyzeusv.solitaire.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.layouts.SevenWideLayout
import com.heyzeusv.solitaire.board.piles.SolitairePile
import com.heyzeusv.solitaire.board.piles.SolitaireStock
import com.heyzeusv.solitaire.board.piles.SolitaireTableau
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.CardLogic
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Composable that displays all [CardLogic] piles, Stock, Foundation, and Tableau. [layout] is
 *  used to determine offsets of every pile. [animationDurations] determines how long each animation
 *  lasts. [animateInfo] is used to determine what needs to be animated and can be updated with
 *  [updateAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
 *  animations. [undoAnimation] is used to enable/disable all clicks during an undo animation and
 *  is updated using [updateUndoAnimation].
 */
@Composable
fun GolfBoard(
    modifier: Modifier = Modifier,
    layout: SevenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateUndoEnabled: (Boolean) -> Unit = { },
    undoAnimation: Boolean,
    updateUndoAnimation: (Boolean) -> Unit = { },
    /** Piles and their onClicks */
    stock: Stock,
    onStockClick: () -> Unit = { },
    stockWasteEmpty: () -> Boolean = { true },
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> Unit = { },
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> Unit = { _, _ ->  }
) {
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }
    var flipRotation by remember { mutableFloatStateOf(0f) }

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
        // end pile correction
        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layout.getPilePosition(it.start)
                .plus(layout.getCardsYOffset(it.startTableauIndices.first())),
            endOffset = layout.getPilePosition(it.end)
                .plus(layout.getCardsYOffset(it.endTableauIndices.first())),
            updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
            updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) }
        )
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.duration,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value}
        )
    }

    Layout(
        modifier = modifier.gesturesDisabled(undoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipCardInfo) {
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> {
                        FlipCard(
                            flipCard = it.animatedCards.first(),
                            cardDpSize = layout.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> { }
                    FlipCardInfo.NoFlip -> {
                        StaticVerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
                            spacedByPercent = layout.vPileSpacedByPercent,
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile")
                        )
                    }
                }
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Foundation")
                    .testTag("Foundation #$0"),
                cardDpSize = layout.getCardDpSize(),
                pile = foundationList[3].displayPile,
                emptyIconId = Suits.SPADES.emptyIcon,
                onClick = { onFoundationClick(3) }
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
        val foundation = measurables.firstOrNull { it.layoutId == "Foundation" }
        val stockPile = measurables.firstOrNull { it.layoutId == "Stock" }

        val tableauPile0 = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        val tableauPile1 = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        val tableauPile2 = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        val tableauPile3 = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        val tableauPile4 = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        val tableauPile5 = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        val tableauPile6 = measurables.firstOrNull { it.layoutId == "Tableau #6" }

        val animatedHorizontalPile =
            measurables.firstOrNull { it.layoutId == "Animated Horizontal Pile" }
        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Vertical Pile" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layout.cardWidth
            val cardHeight = layout.cardHeight
            val cardConstraints = layout.cardConstraints
            val tableauHeight = constraints.maxHeight - layout.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            if (animatedOffset != IntOffset.Zero) {
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
                animatedHorizontalPile?.measure(cardConstraints)?.place(animatedOffset, 2f)
            }
            foundation?.measure(cardConstraints)?.place(layout.foundationSpades)
            stockPile?.measure(cardConstraints)?.place(layout.stockPile)

            tableauPile0?.measure(tableauConstraints)?.place(layout.tableauZero)
            tableauPile1?.measure(tableauConstraints)?.place(layout.tableauOne)
            tableauPile2?.measure(tableauConstraints)?.place(layout.tableauTwo)
            tableauPile3?.measure(tableauConstraints)?.place(layout.tableauThree)
            tableauPile4?.measure(tableauConstraints)?.place(layout.tableauFour)
            tableauPile5?.measure(tableauConstraints)?.place(layout.tableauFive)
            tableauPile6?.measure(tableauConstraints)?.place(layout.tableauSix)
        }
    }
}