package com.heyzeusv.solitaire.board

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.layouts.TenWideLayout
import com.heyzeusv.solitaire.board.layouts.XWideLayout
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.board.piles.PlayingCard
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.spacedBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *  Composables that are used to animate piles.
 */

/**
 *  Displays given [Cards][Card] in vertical orientation.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param spacedByPercent The percentage that will be taken from [Card] height which will be used.
 *  to determine distance between each [Card] vertically.
 *  @param pile The [Cards][Card] to display.
 */
@Composable
fun StaticVerticalCardPile(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    spacedByPercent: Float,
    pile: List<Card>,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement spacedBy -(cardDpSize.height.times(spacedByPercent)),
    ) {
        pile.forEach { card ->
            PlayingCard(
                modifier = Modifier.size(cardDpSize),
                card = card,
            )
        }
    }
}

/**
 *  Displays a copy of a Tableau pile with the bottom most [PlayingCard] going through a flip
 *  animation.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param cardDpSize The size to make [PlayingCard] and empty pile [Image] Composables.
 *  @param spacedByPercent The percentage that will be taken from [Card] height which will be used.
 *  to determine distance between each [Card] vertically.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param animationDurations The durations for each available animation.
 */
@Composable
fun TableauPileWithFlip(
    modifier: Modifier = Modifier,
    cardDpSize: DpSize,
    spacedByPercent: Float,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
) {
    // don't show anything if TableauCardFlipInfo in passed animateInfo is null
    animateInfo.tableauCardFlipInfo?.let {
        var tableauCardFlipRotation by remember { mutableFloatStateOf(0f) }

        AnimateFlip(
            animateInfo = animateInfo,
            flipDuration = animationDurations.tableauCardFlipDuration,
            flipDelay = animationDurations.tableauCardFlipDelay,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> tableauCardFlipRotation = value },
        )

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement spacedBy -(cardDpSize.height.times(spacedByPercent)),
        ) {
            it.remainingPile.forEach { card ->
                PlayingCard(
                    modifier = Modifier.size(cardDpSize),
                    card = card,
                )
            }
            FlipCard(
                flipCard = it.flipCard,
                cardDpSize = cardDpSize,
                flipRotation = tableauCardFlipRotation,
                flipCardInfo = it.flipCardInfo,
            )
        }
    }
}

/**
 *  Displays up to 3 [FlipCard] overlapping horizontally.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param layout Provides pile offsets and [PlayingCard] sizes/constraints.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param animationDurations The durations for each available animation.
 */
@Composable
fun HorizontalCardPileWithFlip(
    modifier: Modifier = Modifier,
    layout: XWideLayout,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
) {
    animateInfo.let {
        var rightCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var middleCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var leftCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        val offsets = layout.getHorizontalCardOffsets(it.flipCardInfo)
        var flipRotation by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = it) {
            val animationSpec = tween<Float>(animationDurations.duration)
            for (i in 0 until it.animatedCards.size) {
                animateOffset(
                    scope = scope,
                    animationSpec = animationSpec,
                    startOffset = offsets.startOffsets[i],
                    endOffset = offsets.endOffsets[i],
                    updateXOffset = { value ->
                        when (i) {
                            0 -> rightCardOffset = rightCardOffset.copy(x = value)
                            1 -> middleCardOffset = middleCardOffset.copy(x = value)
                            2 -> leftCardOffset = leftCardOffset.copy(x = value)
                        }
                    },
                    updateYOffset = { },
                )
            }
        }
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.duration,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value },
        )

        Layout(
            modifier = modifier,
            content = {
                it.animatedCards.let { cards ->
                    cards.reversed().forEachIndexed { i, card ->
                        FlipCard(
                            modifier = Modifier.layoutId(layout.horizontalPileLayoutIds[i]),
                            flipCard = card,
                            cardDpSize = layout.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                        )
                    }
                }
            }
        ) { measurables, constraints ->
            val rightCard = measurables.firstOrNull { m -> m.layoutId == "Right Card" }
            val middleCard = measurables.firstOrNull { m -> m.layoutId == "Middle Card" }
            val leftCard = measurables.firstOrNull { m -> m.layoutId == "Left Card" }

            layout(constraints.maxWidth, constraints.maxHeight) {
                rightCard?.measure(layout.cardConstraints)?.place(rightCardOffset, 2f)
                middleCard?.measure(layout.cardConstraints)?.place(middleCardOffset, 1f)
                leftCard?.measure(layout.cardConstraints)?.place(leftCardOffset)
            }
        }
    }
}

/**
 *  Displays up to 7 [FlipCard], which each start/end from the same [GamePiles], but end/start
 *  from different [GamePiles].
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param layout Provides pile offsets and [PlayingCard] sizes/constraints.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param animationDurations The durations for each available animation.
 */
@Composable
fun MultiPileCardWithFlip(
    modifier: Modifier = Modifier,
    layout: XWideLayout,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
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
        var flipRotation by remember { mutableFloatStateOf(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = it) {
            val animationSpec = tween<Float>(animationDurations.duration)
            for (i in 0 until it.animatedCards.size) {
                animateOffset(
                    scope = scope,
                    animationSpec = animationSpec,
                    startOffset = layout
                        .getPilePosition(it.start, tAllPile = GamePiles.entries[i + 10])
                        .plus(layout.getCardsYOffset(it.startTableauIndices[i])),
                    endOffset = layout
                        .getPilePosition(it.end, tAllPile = GamePiles.entries[i + 10])
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
                        }
                    },
                )
            }
        }
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.duration,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value},
        )

        Layout(
            modifier = modifier,
            content = {
                for (i in 0 until it.animatedCards.size) {
                    FlipCard(
                        modifier = Modifier.layoutId(layout.multiPileLayoutIds[i]),
                        flipCard = it.animatedCards[i],
                        cardDpSize = layout.getCardDpSize(),
                        flipRotation = flipRotation,
                        flipCardInfo = it.flipCardInfo,
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
            var tSevenCard: Measurable? = null
            var tEightCard: Measurable? = null
            var tNineCard: Measurable? = null
            if (it.animatedCards.size > 7) {
                tSevenCard =
                    measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[7] }
                tEightCard =
                    measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[8] }
                tNineCard =
                    measurables.firstOrNull { m -> m.layoutId == layout.multiPileLayoutIds[9] }
            }

            layout(constraints.maxWidth, constraints.maxHeight) {
                if (tZeroCardOffset != IntOffset.Zero) {
                    tZeroCard?.measure(layout.cardConstraints)?.place(tZeroCardOffset, 9f)
                }
                if (tOneCardOffset != IntOffset.Zero) {
                    tOneCard?.measure(layout.cardConstraints)?.place(tOneCardOffset, 8f)
                }
                if (tTwoCardOffset != IntOffset.Zero) {
                    tTwoCard?.measure(layout.cardConstraints)?.place(tTwoCardOffset, 7f)
                }
                if (tThreeCardOffset != IntOffset.Zero) {
                    tThreeCard?.measure(layout.cardConstraints)?.place(tThreeCardOffset, 6f)
                }
                if (tFourCardOffset != IntOffset.Zero) {
                    tFourCard?.measure(layout.cardConstraints)?.place(tFourCardOffset, 5f)
                }
                if (tFiveCardOffset != IntOffset.Zero) {
                    tFiveCard?.measure(layout.cardConstraints)?.place(tFiveCardOffset, 4f)
                }
                if (tSixCardOffset != IntOffset.Zero) {
                    tSixCard?.measure(layout.cardConstraints)?.place(tSixCardOffset, 3f)
                }
                if (tSevenCardOffset != IntOffset.Zero) {
                    tSevenCard?.measure(layout.cardConstraints)?.place(tSevenCardOffset, 2f)
                }
                if (tEightCardOffset != IntOffset.Zero) {
                    tEightCard?.measure(layout.cardConstraints)?.place(tEightCardOffset, 1f)
                }
                if (tNineCardOffset != IntOffset.Zero) {
                    tNineCard?.measure(layout.cardConstraints)?.place(tNineCardOffset)
                }
            }
        }
    }
}

/**
 *  Displays [PlayingCard] flipping from face up/down to face down/up.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param flipCard The [Card] to be flipped.
 *  @param cardDpSize The size to make [PlayingCard].
 *  @param flipRotation Current Y rotation value.
 *  @param flipCardInfo Contains start/end Y rotation values and when flip shows other side.
 */
@Composable
fun FlipCard(
    modifier: Modifier = Modifier,
    flipCard: Card,
    cardDpSize: DpSize,
    flipRotation: Float,
    flipCardInfo: FlipCardInfo,
) {
    val animateModifier = modifier
        .size(cardDpSize)
        .graphicsLayer {
            rotationY = flipRotation
            cameraDistance = 8 * density
        }
    if (flipCardInfo.flipCondition(flipRotation)) {
        PlayingCard(
            modifier = animateModifier,
            card = flipCard.copy(faceUp = flipCardInfo !is FlipCardInfo.FaceUp)
        )
    } else {
        PlayingCard(
            modifier = animateModifier.graphicsLayer { rotationY = flipCardInfo.endRotationY },
            card = flipCard.copy(faceUp = flipCardInfo is FlipCardInfo.FaceUp)
        )
    }
}

/**
 *  Displays up to 13 [PlayingCard], each animated from one [Pile] to another [Pile].
 *  @param layout Contains the positions for all [Piles][Pile] and [Card] sizes/constraints.
 *  @param animationDurations The durations for each available animation.
 *  @param animateInfo Contains the information needed to animate a legal move.
 */
@Composable
fun DynamicVerticalCardPile(
    modifier: Modifier = Modifier,
    layout: TenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo
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
        val scope = rememberCoroutineScope()

        LaunchedEffect(key1 = it) {
            val animationSpec = tween<Float>(animationDurations.duration)
            for (i in 0 until it.animatedCards.size) {
                animateOffset(
                    scope = scope,
                    animationSpec = animationSpec,
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
        }

        Layout(
            modifier = modifier,
            content = {
                for (i in 0 until it.animatedCards.size) {
                    PlayingCard(
                        modifier = Modifier
                            .size(layout.getCardDpSize())
                            .layoutId(layout.multiPileLayoutIds[i]),
                        card = it.animatedCards[i]
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
                    tZeroCard?.measure(layout.cardConstraints)?.place(tZeroCardOffset, 10f)
                }
                if (tOneCardOffset != IntOffset.Zero) {
                    tOneCard?.measure(layout.cardConstraints)?.place(tOneCardOffset, 11f)
                }
                if (tTwoCardOffset != IntOffset.Zero) {
                    tTwoCard?.measure(layout.cardConstraints)?.place(tTwoCardOffset, 12f)
                }
                if (tThreeCardOffset != IntOffset.Zero) {
                    tThreeCard?.measure(layout.cardConstraints)?.place(tThreeCardOffset, 13f)
                }
                if (tFourCardOffset != IntOffset.Zero) {
                    tFourCard?.measure(layout.cardConstraints)?.place(tFourCardOffset, 14f)
                }
                if (tFiveCardOffset != IntOffset.Zero) {
                    tFiveCard?.measure(layout.cardConstraints)?.place(tFiveCardOffset, 15f)
                }
                if (tSixCardOffset != IntOffset.Zero) {
                    tSixCard?.measure(layout.cardConstraints)?.place(tSixCardOffset, 16f)
                }
                if (tSevenCardOffset != IntOffset.Zero) {
                    tSevenCard?.measure(layout.cardConstraints)?.place(tSevenCardOffset, 17f)
                }
                if (tEightCardOffset != IntOffset.Zero) {
                    tEightCard?.measure(layout.cardConstraints)?.place(tEightCardOffset, 18f)
                }
                if (tNineCardOffset != IntOffset.Zero) {
                    tNineCard?.measure(layout.cardConstraints)?.place(tNineCardOffset, 19f)
                }
                if (tTenCardOffset != IntOffset.Zero) {
                    tTenCard?.measure(layout.cardConstraints)?.place(tTenCardOffset, 20f)
                }
                if (tElevenCardOffset != IntOffset.Zero) {
                    tElevenCard?.measure(layout.cardConstraints)?.place(tElevenCardOffset, 21f)
                }
                if (tTwelveCardOffset != IntOffset.Zero) {
                    tTwelveCard?.measure(layout.cardConstraints)?.place(tTwelveCardOffset, 22f)
                }
            }
        }
    }
}

/**
 *  Animates XY movement of a [PlayingCard] with the use of two [LaunchedEffect] blocks.
 *
 *  @param animateInfo Used as key to determine when to start/restart animation.
 *  @param animationDurations The durations for each available animation.
 *  @param startOffset The start position.
 *  @param endOffset The end position.
 *  @param updateXOffset Updates X position.
 *  @param updateYOffset Updates Y position.
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
    LaunchedEffect(key1 = animateInfo) {
        val animationSpec = tween<Float>(animationDurations.duration)

        animate(
            initialValue = startOffset.x.toFloat(),
            targetValue = endOffset.x.toFloat(),
            animationSpec = animationSpec
        ) { value, _ ->
            updateXOffset(value.toInt())
        }

    }
    LaunchedEffect(key1 = animateInfo) {
        val animationSpec = tween<Float>(animationDurations.duration)

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
 *  Animates XY movement of a [PlayingCard] with the use of two [LaunchedEffect] blocks. Almost
 *  copy of [AnimateOffset], but uses [CoroutineScope] rather than [LaunchedEffect]. This allows
 *  multiple animations to be controlled by a single [LaunchedEffect], rather than one per
 *  [PlayingCard], so small efficiency improvement.
 *
 *  @param scope Handles coroutines.
 *  @param animationSpec The animation configuration to be used.
 *  @param startOffset The start position.
 *  @param endOffset The end position.
 *  @param updateXOffset Updates X position.
 *  @param updateYOffset Updates Y position.
 */
suspend fun animateOffset(
    scope: CoroutineScope,
    animationSpec: TweenSpec<Float>,
    startOffset: IntOffset,
    endOffset: IntOffset,
    updateXOffset: (Int) -> Unit,
    updateYOffset: (Int) -> Unit,
) {
    scope.launch {
        animate(
            initialValue = startOffset.x.toFloat(),
            targetValue = endOffset.x.toFloat(),
            animationSpec = animationSpec
        ) { value, _ ->
            updateXOffset(value.toInt())
        }
    }
    scope.launch {
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
 *  Used [LaunchedEffect] to animate Y rotation, aka flipping a [PlayingCard] over.
 *
 *  @param animateInfo Used as key to determine when to start/restart animation.
 *  @param flipDuration The duration of animation lasts after [flipDelay] is over.
 *  @param flipDelay The duration before the animation starts.
 *  @param flipCardInfo Contains Y rotation start/end information.
 *  @param updateRotation Updates Y rotation value.
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
private fun StaticVerticalCardPreview() {
    PreviewUtil().apply {
        Preview {
            StaticVerticalCardPile(
                cardDpSize = cardDpSize,
                spacedByPercent = spacedByPercent,
                pile = pile
            )
        }
    }
}

@Preview
@Composable
private fun TableauPileWithFlipPreview() {
    PreviewUtil().apply {
        Preview {
            TableauPileWithFlip(
                cardDpSize = cardDpSize,
                spacedByPercent = spacedByPercent,
                animateInfo = animateInfoWithTableauFlip,
                animationDurations = animationDurations
            )
        }
    }
}

@Preview
@Composable
fun FlipCardFaceDownPreview() {
    PreviewUtil().apply {
        Preview {
            Row {
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 0f,
                    flipCardInfo = animateInfoWithFaceDownFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 45f,
                    flipCardInfo = animateInfoWithFaceDownFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 90f,
                    flipCardInfo = animateInfoWithFaceDownFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 135f,
                    flipCardInfo = animateInfoWithFaceDownFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 180f,
                    flipCardInfo = animateInfoWithFaceDownFlip.flipCardInfo
                )
            }
        }
    }
}

@Preview
@Composable
fun FlipCardFaceUpPreview() {
    PreviewUtil().apply {
        Preview {
            Row {
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = 0f,
                    flipCardInfo = animateInfoWithFaceUpFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = -45f,
                    flipCardInfo = animateInfoWithFaceUpFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = -90f,
                    flipCardInfo = animateInfoWithFaceUpFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = -135f,
                    flipCardInfo = animateInfoWithFaceUpFlip.flipCardInfo
                )
                FlipCard(
                    flipCard = animateInfo.animatedCards.first(),
                    cardDpSize = card7WideSize,
                    flipRotation = -180f,
                    flipCardInfo = animateInfoWithFaceUpFlip.flipCardInfo
                )
            }
        }
    }
}