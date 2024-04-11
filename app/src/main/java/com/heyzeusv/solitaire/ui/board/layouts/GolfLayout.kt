package com.heyzeusv.solitaire.ui.board.layouts

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
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.ui.board.AnimateFlip
import com.heyzeusv.solitaire.ui.board.AnimateOffset
import com.heyzeusv.solitaire.ui.board.FlipCard
import com.heyzeusv.solitaire.ui.board.SolitairePile
import com.heyzeusv.solitaire.ui.board.SolitaireStock
import com.heyzeusv.solitaire.ui.board.SolitaireTableau
import com.heyzeusv.solitaire.ui.board.VerticalCardPile
import com.heyzeusv.solitaire.ui.board.layouts.positions.SevenWideLayout
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Composable that displays all [Card] piles, Stock, Foundation, and Tableau. [layout] is
 *  used to determine offsets of every pile. [animationDurations] determines how long each animation
 *  lasts. [animateInfo] is used to determine what needs to be animated and can be updated with
 *  [updateAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
 *  animations. [undoAnimation] is used to enable/disable all clicks during an undo animation and
 *  is updated using [updateUndoAnimation].
 */
@Composable
fun GolfLayout(
    modifier: Modifier = Modifier,
    layout: SevenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateUndoEnabled: (Boolean) -> Unit = { },
    undoAnimation: Boolean,
    updateUndoAnimation: (Boolean) -> Unit = { },
    handleMoveResult: (MoveResult) -> Unit = { },
    /** Piles and their onClicks */
    stock: Stock,
    onStockClick: () -> MoveResult = { MoveResult.Illegal },
    stockWasteEmpty: () -> Boolean = { true },
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> MoveResult = { MoveResult.Illegal },
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> MoveResult = { _, _ -> MoveResult.Illegal }
) {
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }
    var flipRotation by remember { mutableFloatStateOf(0f) }

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
        // end pile correction
        val endPile =
            if (it.end == GamePiles.ClubsFoundation) GamePiles.SpadesFoundation else it.end
        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layout.getPilePosition(it.start)
                .plus(layout.getCardsYOffset(it.startTableauIndices.first())),
            endOffset = layout.getPilePosition(endPile)
                .plus(layout.getCardsYOffset(it.endTableauIndices.first())),
            updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
            updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) }
        )
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.fullAniSpec,
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
                        VerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
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
                pile = foundationList.first().displayPile,
                emptyIconId = Suits.SPADES.emptyIcon,
                onClick = { handleMoveResult(onFoundationClick(0)) }
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                cardDpSize = layout.getCardDpSize(),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick()) }
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    pile = tableau.displayPile,
                    tableauIndex = index,
                    onClick = onTableauClick,
                    handleMoveResult = handleMoveResult
                )
            }
        }
    ) { measurables, constraints ->
        val spadesFoundation = measurables.firstOrNull { it.layoutId == "Foundation" }
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
            spadesFoundation?.measure(cardConstraints)?.place(layout.foundationSpades)
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