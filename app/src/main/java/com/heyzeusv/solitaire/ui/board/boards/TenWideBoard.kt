package com.heyzeusv.solitaire.ui.board.boards

import androidx.compose.foundation.layout.size
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
import com.heyzeusv.solitaire.ui.board.SolitaireCard
import com.heyzeusv.solitaire.ui.board.SolitairePile
import com.heyzeusv.solitaire.ui.board.SolitaireStock
import com.heyzeusv.solitaire.ui.board.SolitaireTableau
import com.heyzeusv.solitaire.ui.board.TableauPileWithFlip
import com.heyzeusv.solitaire.ui.board.StaticVerticalCardPile
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
 *  [updateAnimateInfo]. [spiderAnimateInfo] is used specifically for the full Ace to King
 *  [Tableau] pile to [Foundation] pile or vice versa and can be updated with
 *  [updateSpiderAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
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
    spiderAnimateInfo: AnimateInfo?,
    updateSpiderAnimateInfo: (AnimateInfo?) -> Unit = { },
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
    spiderAnimateInfo?.let {
        // Updating AnimateInfo to null if animation is fully completed
        LaunchedEffect(key1 = it) {
            try {
                if (it.undoAnimation) updateUndoAnimation(true) else updateUndoEnabled(false)
                delay(animationDurations.fullDelay)
                updateSpiderAnimateInfo(null)
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
                        StaticVerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Static Vertical Pile")
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
            spiderAnimateInfo?.let {
                DynamicVerticalCardPile(
                    layout = layout,
                    animateInfo = it,
                    animationDurations = animationDurations,
                    modifier = Modifier.layoutId("Animated Dynamic Vertical Pile")
                )
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
            measurables.firstOrNull { it.layoutId == "Animated Static Vertical Pile" }
        val animatedMultiPile = measurables.firstOrNull { it.layoutId == "Animated Multi Pile" }
        val animatedTableauCard = measurables.firstOrNull { it.layoutId == "Animated Tableau Card" }
        val animatedDynamicPile =
            measurables.firstOrNull { it.layoutId == "Animated Dynamic Vertical Pile" }

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
            animatedDynamicPile?.measure(constraints)?.place(IntOffset.Zero, 3f)

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

/**
 *  Composable that displays up to 13 [SolitaireCard], each animated to/from a [Tableau] pile
 *  to/from a [Foundation] pile. [layout] provides animation offsets and Card sizes/constraints.
 *  [animateInfo] provides the Cards to be  displayed and their flip animation info.
 *  [animationDurations] is used to determine length of animations.
 */
@Composable
fun DynamicVerticalCardPile(
    layout: TenWideLayout,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    animateInfo.let {
        var tZeroCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tOneCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tTwoCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tThreeCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tFourCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tFiveCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tSixCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tSevenCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tEightCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tNineCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tTenCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tElevenCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tTwelveCardOffset by remember { mutableStateOf(IntOffset.Zero) }

        for (i in 0 until it.animatedCards.size) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layout.getPilePosition(it.start)
                    .plus(layout.getCardsYOffset(it.startTableauIndices[i])),
                endOffset = layout.getPilePosition(it.end)
                    .plus(layout.getCardsYOffset(it.endTableauIndices[i])),
                updateXOffset = { value ->
                    when (i) {
                        0 -> tZeroCardOffset = tZeroCardOffset.copy(x = value)
                        1 -> tOneCardOffset = tOneCardOffset.copy(x = value)
                        2 -> tTwoCardOffset = tTwoCardOffset.copy(x = value)
                        3 -> tThreeCardOffset = tThreeCardOffset.copy(x = value)
                        4 -> tFourCardOffset = tFourCardOffset.copy(x = value)
                        5 -> tFiveCardOffset = tFiveCardOffset.copy(x = value)
                        6 -> tSixCardOffset = tSixCardOffset.copy(x = value)
                        7 -> tSevenCardOffset = tSevenCardOffset.copy(x = value)
                        8 -> tEightCardOffset = tEightCardOffset.copy(x = value)
                        9 -> tNineCardOffset = tNineCardOffset.copy(x = value)
                        10 -> tTenCardOffset = tTenCardOffset.copy(x = value)
                        11 -> tElevenCardOffset = tElevenCardOffset.copy(x = value)
                        12 -> tTwelveCardOffset = tTwelveCardOffset.copy(x = value)
                    }
                },
                updateYOffset = { value ->
                    when (i) {
                        0 -> tZeroCardOffset = tZeroCardOffset.copy(y = value)
                        1 -> tOneCardOffset = tOneCardOffset.copy(y = value)
                        2 -> tTwoCardOffset = tTwoCardOffset.copy(y = value)
                        3 -> tThreeCardOffset = tThreeCardOffset.copy(y = value)
                        4 -> tFourCardOffset = tFourCardOffset.copy(y = value)
                        5 -> tFiveCardOffset = tFiveCardOffset.copy(y = value)
                        6 -> tSixCardOffset = tSixCardOffset.copy(y = value)
                        7 -> tSevenCardOffset = tSevenCardOffset.copy(y = value)
                        8 -> tEightCardOffset = tEightCardOffset.copy(y = value)
                        9 -> tNineCardOffset = tNineCardOffset.copy(y = value)
                        10 -> tTenCardOffset = tTenCardOffset.copy(y = value)
                        11 -> tElevenCardOffset = tElevenCardOffset.copy(y = value)
                        12 -> tTwelveCardOffset = tTwelveCardOffset.copy(y = value)
                    }
                }
            )
        }

        Layout(
            modifier = modifier,
            content = {
                for (i in 0 until it.animatedCards.size) {
                    SolitaireCard(
                        card = it.animatedCards[i],
                        modifier = Modifier
                            .size(layout.getCardDpSize())
                            .layoutId(layout.multiPileLayoutIds[i])
                    )
                }
            }
        ) { measurables, constraints ->
            val tZeroCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[0] }
            val tOneCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[1] }
            val tTwoCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[2] }
            val tThreeCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[3] }
            val tFourCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[4] }
            val tFiveCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[5] }
            val tSixCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[6] }
            val tSevenCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[7] }
            val tEightCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[8] }
            val tNineCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[9] }
            val tTenCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[10] }
            val tElevenCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[11] }
            val tTwelveCard =
                measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[12] }

            layout(constraints.maxWidth, constraints.maxHeight) {
                if (tZeroCardOffset != IntOffset.Zero) {
                    tZeroCard?.measure(layout.cardConstraints)?.place(tZeroCardOffset)
                }
                if (tOneCardOffset != IntOffset.Zero) {
                    tOneCard?.measure(layout.cardConstraints)?.place(tOneCardOffset, 1f)
                }
                if (tTwoCardOffset != IntOffset.Zero) {
                    tTwoCard?.measure(layout.cardConstraints)?.place(tTwoCardOffset, 2f)
                }
                if (tThreeCardOffset != IntOffset.Zero) {
                    tThreeCard?.measure(layout.cardConstraints)?.place(tThreeCardOffset, 3f)
                }
                if (tFourCardOffset != IntOffset.Zero) {
                    tFourCard?.measure(layout.cardConstraints)?.place(tFourCardOffset, 4f)
                }
                if (tFiveCardOffset != IntOffset.Zero) {
                    tFiveCard?.measure(layout.cardConstraints)?.place(tFiveCardOffset, 5f)
                }
                if (tSixCardOffset != IntOffset.Zero) {
                    tSixCard?.measure(layout.cardConstraints)?.place(tSixCardOffset, 6f)
                }
                if (tSevenCardOffset != IntOffset.Zero) {
                    tSevenCard?.measure(layout.cardConstraints)?.place(tSevenCardOffset, 7f)
                }
                if (tEightCardOffset != IntOffset.Zero) {
                    tEightCard?.measure(layout.cardConstraints)?.place(tEightCardOffset, 8f)
                }
                if (tNineCardOffset != IntOffset.Zero) {
                    tNineCard?.measure(layout.cardConstraints)?.place(tNineCardOffset, 9f)
                }
                if (tTenCardOffset != IntOffset.Zero) {
                    tTenCard?.measure(layout.cardConstraints)?.place(tTenCardOffset, 10f)
                }
                if (tElevenCardOffset != IntOffset.Zero) {
                    tElevenCard?.measure(layout.cardConstraints)?.place(tElevenCardOffset, 11f)
                }
                if (tTwelveCardOffset != IntOffset.Zero) {
                    tTwelveCard?.measure(layout.cardConstraints)?.place(tTwelveCardOffset, 12f)
                }
            }
        }
    }
}