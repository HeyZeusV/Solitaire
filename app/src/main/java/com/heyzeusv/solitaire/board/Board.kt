package com.heyzeusv.solitaire.board

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
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.layouts.XWideLayout
import com.heyzeusv.solitaire.board.piles.SolitaireCard
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.games.FortyAndEight
import com.heyzeusv.solitaire.games.FortyThieves
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau.
 */
@Composable
fun Board(
    gameVM: GameViewModel,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsState()
    val animateInfo by gameVM.animateInfo.collectAsState()
    val spiderAnimateInfo by gameVM.spiderAnimateInfo.collectAsState()
    val undoAnimation by gameVM.undoAnimation.collectAsState()
    val selectedGame by gameVM.selectedGame.collectAsState()

    when (selectedGame) {
        is Games.AcesUpVariants -> {
            AcesUpBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideFourTableauLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                stockWasteEmpty = { stockWasteEmpty },
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick
            )
        }
        is Games.GolfFamily -> {
            GolfBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                stockWasteEmpty = { stockWasteEmpty },
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick
            )
        }
        is Games.SpiderFamily, is FortyThieves -> {
            TenWideBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                spiderAnimateInfo = spiderAnimateInfo,
                updateSpiderAnimateInfo = gameVM::updateSpiderAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                drawAmount = selectedGame.drawAmount,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick
            )
        }
        is FortyAndEight -> {
            TenWideEightTableauBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.tenWideEightTableauLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                drawAmount = selectedGame.drawAmount,
                stock = gameVM.stock,
                onStockClick = gameVM::onStockClick,
                waste = gameVM.waste,
                stockWasteEmpty = { stockWasteEmpty },
                onWasteClick = gameVM::onWasteClick,
                foundationList = gameVM.foundation,
                tableauList = gameVM.tableau,
                onTableauClick = gameVM::onTableauClick
            )
        }
        else -> {
            StandardBoard(
                modifier = modifier,
                layout = gameVM.screenLayouts.sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                updateAnimateInfo = gameVM::updateAnimateInfo,
                updateUndoEnabled = gameVM::updateUndoEnabled,
                undoAnimation = undoAnimation,
                updateUndoAnimation = gameVM::updateUndoAnimation,
                drawAmount = selectedGame.drawAmount,
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
 *  [SolitaireCard] and determine their vertical spacing. [spacedByPercent] is used to determine
 *  distance between cards vertically.
 */
@Composable
fun StaticVerticalCardPile(
    cardDpSize: DpSize,
    spacedByPercent: Float,
    pile: List<Card>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement =
            Arrangement.spacedBy(space = -(cardDpSize.height.times(spacedByPercent)))
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
 *  [spacedByPercent] is used to determine distance between cards vertically. [animateInfo]
 *  contains the cards to be displayed and rotation details. [animationDurations] is used to
 *  determine length of animation.
 */
@Composable
fun TableauPileWithFlip(
    cardDpSize: DpSize,
    spacedByPercent: Float,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    animateInfo.tableauCardFlipInfo?.let {
        var tableauCardFlipRotation by remember { mutableFloatStateOf(0f) }

        AnimateFlip(
            animateInfo = animateInfo,
            flipDuration = animationDurations.tableauCardFlipDuration,
            flipDelay = animationDurations.tableauCardFlipDelay,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> tableauCardFlipRotation = value }
        )

        Column(
            modifier = modifier,
            verticalArrangement =
                Arrangement.spacedBy(space = -(cardDpSize.height.times(spacedByPercent)))
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
 *  Composable that displays up to 3 [FlipCard] overlapping horizontally. [layout] provides
 *  animation offsets and Card sizes/constraints. [animateInfo] provides the Cards to be displayed
 *  and their flip animation info. [animationDurations] is used to determine length of animations.
 */
@Composable
fun HorizontalCardPileWithFlip(
    layout: XWideLayout,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    modifier: Modifier = Modifier
) {
    animateInfo.let {
        var rightCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var middleCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var leftCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        val offsets = layout.getHorizontalCardOffsets(it.flipCardInfo)
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
            flipDuration = animationDurations.duration,
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
                            cardDpSize = layout.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId(layout.horizontalPileLayoutIds[i])
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
 *  Composable that displays up to 7 [FlipCard], each animated to/from different piles. [layout]
 *  provides animation offsets and Card sizes/constraints. [animateInfo] provides the Cards to be
 *  displayed and their flip animation info. [animationDurations] is used to determine length of
 *  animations.
 */
@Composable
fun MultiPileCardWithFlip(
    layout: XWideLayout,
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
        var flipRotation by remember { mutableFloatStateOf(0f) }

        for (i in 0 until it.animatedCards.size) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layout.getPilePosition(it.start, tAllPile = GamePiles.entries[i + 10])
                    .plus(layout.getCardsYOffset(it.startTableauIndices[i])),
                endOffset = layout.getPilePosition(it.end, tAllPile = GamePiles.entries[i + 10])
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
                }
            )
        }
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.duration,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value}
        )

        Layout(
            modifier = modifier,
            content = {
                for (i in 0 until it.animatedCards.size) {
                    FlipCard(
                        flipCard = it.animatedCards[i],
                        cardDpSize = layout.getCardDpSize(),
                        flipRotation = flipRotation,
                        flipCardInfo = it.flipCardInfo,
                        modifier = Modifier.layoutId(layout.multiPileLayoutIds[i])
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
    val animationSpec = tween<Float>(animationDurations.duration)

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
            StaticVerticalCardPile(cardDpSize, spacedByPercent,  pile)
        }
    }
}

@Preview
@Composable
fun TableauPileWithFlipPreview() {
    PreviewUtil().apply {
        Preview {
            TableauPileWithFlip(cardDpSize, spacedByPercent, animateInfo, animationDurations)
        }
    }
}

@Preview
@Composable
fun HorizontalCardPileWithFlipPreview() {
    PreviewUtil().apply {
        Preview {
            HorizontalCardPileWithFlip(screenWidth.sevenWideLayout, animateInfo, animationDurations)
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