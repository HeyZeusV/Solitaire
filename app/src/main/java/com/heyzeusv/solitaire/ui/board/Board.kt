package com.heyzeusv.solitaire.ui.board

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.ui.board.layouts.StandardLayout
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau.
 */
@Composable
fun Board(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsState()
    val animateInfo by gameVM.animateInfo.collectAsState()
    val undoAnimation by gameVM.undoAnimation.collectAsState()

    when (gameVM.selectedGame) {
        else -> {
            StandardLayout(
                modifier = modifier,
                layInfo = gameVM.layoutInfo,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                drawAmount = gameVM.selectedGame.drawAmount,
                handleMoveResult = sbVM::handleMoveResult,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                onFoundationClick = gameVM::onFoundationClick,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick
            )
        }
    }
}

/**
 *  Composable that displays given [pile] in vertical orientation. [cardDpSize] is used to size each
 *  [SolitaireCard] and determine their vertical spacing.
 */
@Composable
fun VerticalCardPile(
    cardDpSize: DpSize,
    pile: List<Card>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = -(cardDpSize.height.times(0.75f)))
    ) {
        pile.forEach { card ->
            SolitaireCard(
                card = card,
                modifier = Modifier.size(cardDpSize)
            )
        }
    }
}

/**
 *  Composable that displays a Tableau pile with the bottom most card having a flip animation.
 *  [cardDpSize] is used to size each [SolitaireCard] and determine their vertical spacing.
 *  [animateInfo] contains the cards to be displayed and rotation details. [animationDurations] is
 *  used to determine length of animation.
 */
@Composable
fun TableauPileWithFlip(
    cardDpSize: DpSize,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    animateInfo.tableauCardFlipInfo?.let {
        var tableauCardFlipRotation by remember { mutableFloatStateOf(0f) }

        AnimateFlip(
            animateInfo = animateInfo,
            flipDuration = animationDurations.tableauCardFlipAniSpec,
            flipDelay = animationDurations.tableauCardFlipDelayAniSpec,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> tableauCardFlipRotation = value }
        )

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(space = -(cardDpSize.height.times(0.75f)))
        ) {
            it.remainingPile.forEach { card ->
                SolitaireCard(
                    card = card,
                    modifier = Modifier.size(cardDpSize)
                )
            }
            FlipCard(
                flipCard = it.flipCard,
                cardDpSize = cardDpSize,
                flipRotation = tableauCardFlipRotation,
                flipCardInfo = it.flipCardInfo
            )
        }
    }
}

/**
 *  Composable that displays up to 3 [FlipCard] overlapping horizontally. [layInfo] provides
 *  animation offsets and Card sizes/constraints. [animateInfo] provides the Cards to be displayed
 *  and their flip animation info. [animationDurations] is used to determine length of animations.
 */
@Composable
fun HorizontalCardPileWithFlip(
    layInfo: LayoutInfo,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    animateInfo.let {
        var rightCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var middleCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var leftCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        val offsets = layInfo.getHorizontalCardOffsets(it.flipCardInfo)
        var flipRotation by remember { mutableFloatStateOf(0f) }

        for (i in 0 until it.animatedCards.size) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = offsets.startOffsets[i],
                endOffset = offsets.endOffsets[i],
                updateXOffset = { value ->
                    when (i) {
                        0 -> rightCardOffset = rightCardOffset.copy(x = value)
                        1 -> middleCardOffset = middleCardOffset.copy(x = value)
                        2 -> leftCardOffset = leftCardOffset.copy(x = value)
                    }
                },
                updateYOffset = { }
            )
        }
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.fullAniSpec,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value }
        )

        Layout(
            modifier = modifier,
            content = {
                it.animatedCards.let { cards ->
                    cards.reversed().forEachIndexed { i, card ->
                        FlipCard(
                            flipCard = card,
                            cardDpSize = layInfo.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId(layInfo.horizontalPileLayoutIds[i])
                        )
                    }
                }
            }
        ) { measurables, constraints ->
            val rightCard = measurables.firstOrNull { meas -> meas.layoutId == "Right Card" }
            val middleCard = measurables.firstOrNull { meas -> meas.layoutId == "Middle Card" }
            val leftCard = measurables.firstOrNull { meas -> meas.layoutId == "Left Card" }

            layout(constraints.maxWidth, constraints.maxHeight) {
                rightCard?.measure(layInfo.cardConstraints)?.place(rightCardOffset, 2f)
                middleCard?.measure(layInfo.cardConstraints)?.place(middleCardOffset, 1f)
                leftCard?.measure(layInfo.cardConstraints)?.place(leftCardOffset)
            }
        }
    }
}

/**
 *  Composable that displays up to 7 [FlipCard] each animated to/from different piles. [layInfo]
 *  provides animation offsets and Card sizes/constraints. [animateInfo] provides the Cards to be
 *  displayed and their flip animation info. [animationDurations] is used to determine length of
 *  animations.
 */
@Composable
fun MultiPileCardWithFlip(
    layInfo: LayoutInfo,
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
        var flipRotation by remember { mutableFloatStateOf(0f) }

        for (i in 0 until it.animatedCards.size) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layInfo.getPilePosition(it.start, tAllPile = GamePiles.entries[i + 6])
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices[i])),
                endOffset = layInfo.getPilePosition(it.end, tAllPile = GamePiles.entries[i + 6])
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices[i])),
                updateXOffset = { value ->
                    when (i) {
                        0 -> tZeroCardOffset = tZeroCardOffset.copy(x = value)
                        1 -> tOneCardOffset = tOneCardOffset.copy(x = value)
                        2 -> tTwoCardOffset = tTwoCardOffset.copy(x = value)
                        3 -> tThreeCardOffset = tThreeCardOffset.copy(x = value)
                        4 -> tFourCardOffset = tFourCardOffset.copy(x = value)
                        5 -> tFiveCardOffset = tFiveCardOffset.copy(x = value)
                        6 -> tSixCardOffset = tSixCardOffset.copy(x = value)
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
                    }
                }
            )
        }
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.fullAniSpec,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value}
        )

        Layout(
            modifier = modifier,
            content = {
                if (it.animatedCards.size == 7) {
                    for (i in 3..6) {
                        FlipCard(
                            flipCard = it.animatedCards[i],
                            cardDpSize = layInfo.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId(layInfo.multiPileLayoutIds[i])
                        )
                    }
                }
                for (i in 0..2) {
                    FlipCard(
                        flipCard = it.animatedCards[i],
                        cardDpSize = layInfo.getCardDpSize(),
                        flipRotation = flipRotation,
                        flipCardInfo = it.flipCardInfo,
                        modifier = Modifier.layoutId(layInfo.multiPileLayoutIds[i])
                    )
                }
            }
        ) { measurables, constraints ->
            val tZeroCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[0] }
            val tOneCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[1] }
            val tTwoCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[2] }
            val tThreeCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[3] }
            val tFourCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[4] }
            val tFiveCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[5] }
            val tSixCard =
                measurables.firstOrNull { meas -> meas.layoutId == layInfo.multiPileLayoutIds[6] }

            layout(constraints.maxWidth, constraints.maxHeight) {
                if (tZeroCardOffset != IntOffset.Zero) {
                    tZeroCard?.measure(layInfo.cardConstraints)?.place(tZeroCardOffset, 6f)
                }
                if (tOneCardOffset != IntOffset.Zero) {
                    tOneCard?.measure(layInfo.cardConstraints)?.place(tOneCardOffset, 5f)
                }
                if (tTwoCardOffset != IntOffset.Zero) {
                    tTwoCard?.measure(layInfo.cardConstraints)?.place(tTwoCardOffset, 4f)
                }
                if (tThreeCardOffset != IntOffset.Zero) {
                    tThreeCard?.measure(layInfo.cardConstraints)?.place(tThreeCardOffset, 3f)
                }
                if (tFourCardOffset != IntOffset.Zero) {
                    tFourCard?.measure(layInfo.cardConstraints)?.place(tFourCardOffset, 2f)
                }
                if (tFiveCardOffset != IntOffset.Zero) {
                    tFiveCard?.measure(layInfo.cardConstraints)?.place(tFiveCardOffset, 1f)
                }
                if (tSixCardOffset != IntOffset.Zero) {
                    tSixCard?.measure(layInfo.cardConstraints)?.place(tSixCardOffset)
                }
            }
        }
    }
}

/**
 *  Composable that displays [flipCard] flipping. [cardDpSize] is used to size [SolitaireCard].
 *  [flipRotation] determines [flipCard]'s current rotation value. [flipCardInfo] is used to
 *  determine if [flipCard] should be displayed as face up or down depending on [flipRotation].
 */
@Composable
fun FlipCard(
    flipCard: Card,
    cardDpSize: DpSize,
    flipRotation: Float,
    flipCardInfo: FlipCardInfo,
    modifier: Modifier = Modifier
) {
    val animateModifier = modifier
        .size(cardDpSize)
        .graphicsLayer {
            rotationY = flipRotation
            cameraDistance = 8 * density
        }
    if (flipCardInfo.flipCondition(flipRotation)) {
        SolitaireCard(
            card = flipCard.copy(faceUp = flipCardInfo !is FlipCardInfo.FaceUp),
            modifier = animateModifier
        )
    } else {
        SolitaireCard(
            card = flipCard.copy(faceUp = flipCardInfo is FlipCardInfo.FaceUp),
            modifier = animateModifier.graphicsLayer { rotationY = flipCardInfo.endRotationY }
        )
    }
}

/**
 *  Uses two [LaunchedEffect] in order to animate XY movement. [animateInfo] is used as
 *  LaunchedEffect key to determine when to start/restart animation. [animationDurations] is used to
 *  determine length of animation. [startOffset] is start position. [endOffset] is end position.
 *  [updateXOffset] and [updateYOffset] are used to update [IntOffset] from where this function is
 *  called.
 */
@Composable
fun AnimateOffset(
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    startOffset: IntOffset,
    endOffset: IntOffset,
    updateXOffset: (Int) -> Unit,
    updateYOffset: (Int) -> Unit
) {
    val animationSpec = tween<Float>(animationDurations.fullAniSpec)

    LaunchedEffect(key1 = animateInfo) {
        animate(
            initialValue = startOffset.x.toFloat(),
            targetValue = endOffset.x.toFloat(),
            animationSpec = animationSpec
        ) { value, _ ->
            updateXOffset(value.toInt())
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animate(
            initialValue = startOffset.y.toFloat(),
            targetValue = endOffset.y.toFloat(),
            animationSpec = animationSpec
        ) { value, _ ->
            updateYOffset(value.toInt())
        }
    }
}

/**
 *  Uses two [LaunchedEffect] in order to animate XY movement. [animateInfo] is used as
 *  LaunchedEffect key to determine when to start/restart animation. [flipDuration] is the duration
 *  of flip animation, while [flipDelay] is the time before animation begins. [flipCardInfo] is used
 *  to determine start/end rotation values.
 */
@Composable
fun AnimateFlip(
    animateInfo: AnimateInfo,
    flipDuration: Int,
    flipDelay: Int,
    flipCardInfo: FlipCardInfo,
    updateRotation: (Float) -> Unit
) {
    LaunchedEffect(key1 = animateInfo) {
        animate(
            initialValue = flipCardInfo.startRotationY,
            targetValue = flipCardInfo.endRotationY,
            animationSpec = tween(durationMillis = flipDuration, delayMillis = flipDelay)
        ) { value, _ ->
            updateRotation(value)
        }
    }
}

@Preview
@Composable
fun VerticalCardPilePreview() {
    PreviewUtil().apply {
        Preview {
            VerticalCardPile(cardDpSize, pile)
        }
    }
}

@Preview
@Composable
fun TableauPileWithFlipPreview() {
    PreviewUtil().apply {
        Preview {
            TableauPileWithFlip(cardDpSize, animateInfo, animationDurations)
        }
    }
}

@Preview
@Composable
fun HorizontalCardPileWithFlipPreview() {
    PreviewUtil().apply {
        Preview {
            HorizontalCardPileWithFlip(layInfo, animateInfo, animationDurations)
        }
    }
}

@Preview
@Composable
fun FlipCardPreview() {
    PreviewUtil().apply {
        Preview {
            FlipCard(
                flipCard = animateInfo.animatedCards.first(),
                cardDpSize = cardDpSize,
                flipRotation = 0f,
                flipCardInfo = animateInfo.flipCardInfo
            )
        }
    }
}