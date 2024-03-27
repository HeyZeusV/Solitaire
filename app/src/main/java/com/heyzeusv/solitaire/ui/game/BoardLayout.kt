package com.heyzeusv.solitaire.ui.game

import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.heyzeusv.solitaire.R
import com.heyzeusv.solitaire.data.AnimateInfo
import com.heyzeusv.solitaire.data.Card
import com.heyzeusv.solitaire.data.FlipCardInfo
import com.heyzeusv.solitaire.data.LayoutInfo
import com.heyzeusv.solitaire.data.LayoutPositions
import com.heyzeusv.solitaire.data.pile.Foundation
import com.heyzeusv.solitaire.data.pile.Stock
import com.heyzeusv.solitaire.data.pile.Tableau
import com.heyzeusv.solitaire.data.pile.Waste
import com.heyzeusv.solitaire.ui.scoreboard.ScoreboardViewModel
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.Games
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.SolitairePreview
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import com.heyzeusv.solitaire.util.toDp
import kotlinx.coroutines.delay

@Composable
fun BoardLayout(
    sbVM: ScoreboardViewModel,
    gameVM: GameViewModel,
    selectedGame: Games,
    modifier: Modifier = Modifier
) {
    val stockWasteEmpty by gameVM.stockWasteEmpty.collectAsState()
    val animateInfo by gameVM.animateInfo.collectAsState()
    val undoAnimation by gameVM.undoAnimation.collectAsState()

    BoardLayout(
        layInfo = gameVM.layoutInfo,
        animationDurations = AnimationDurations.TwoHundredFifty,
        animateInfo = animateInfo,
        updateAnimateInfo = gameVM::updateAnimateInfo,
        updateUndoEnabled = gameVM::updateUndoEnabled,
        undoAnimation = undoAnimation,
        updateUndoAnimation = gameVM::updateUndoAnimation,
        drawAmount = selectedGame.drawAmount,
        handleMoveResult = sbVM::handleMoveResult,
        stock = gameVM.stock,
        onStockClick = gameVM::onStockClick,
        waste = gameVM.waste,
        stockWasteEmpty = { stockWasteEmpty },
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
    layInfo: LayoutInfo,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit,
    updateUndoEnabled: (Boolean) -> Unit,
    undoAnimation: Boolean,
    updateUndoAnimation: (Boolean) -> Unit,
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
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }
    var flipRotation by remember(animateInfo) { mutableFloatStateOf(0f) }
    var tableauCardFlipRotation by remember(animateInfo) { mutableFloatStateOf(0f) }

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
                startOffset = layInfo.getPilePosition(it.start, it.stockWasteMove)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices.first())),
                endOffset = layInfo.getPilePosition(it.end, it.stockWasteMove)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices.first())),
                updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
                updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) }
            )
        }
        // Cards Flip Animation
        LaunchedEffect(key1 = it) {
            when (it.flipCardInfo) {
                FlipCardInfo.NoFlip -> {}
                else -> {
                    animate(
                        initialValue = it.flipCardInfo.startRotationY,
                        targetValue = it.flipCardInfo.endRotationY,
                        animationSpec = tween(animationDurations.fullAniSpec, easing = LinearEasing)
                    ) { value, _ ->
                        flipRotation = value
                    }
                }
            }
        }
        it.tableauCardFlipInfo?.let { flipInfo ->
            // Tableau Card Flip Animation
            LaunchedEffect(key1 = it) {
                animate(
                    initialValue = flipInfo.flipCardInfo.startRotationY,
                    targetValue = flipInfo.flipCardInfo.endRotationY,
                    animationSpec = tween(
                        durationMillis = animationDurations.tableauCardFlipAniSpec,
                        delayMillis = animationDurations.tableauCardFlipDelayAniSpec,
                        easing = LinearEasing
                    )
                ) { value, _ ->
                    tableauCardFlipRotation = value
                }
            }
        }
    }

    Layout(
        modifier = modifier.gesturesDisabled(undoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipCardInfo) {
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> {
                        HorizontalCardPileWithFlip(
                            layInfo = layInfo,
                            animateInfo = it,
                            animateDurations = animationDurations,
                            flipRotation = flipRotation,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
                        MultiPileCardWithFlip(
                            layInfo = layInfo,
                            animateInfo = it,
                            animationDurations = animationDurations,
                            flipRotation = flipRotation,
                            modifier = Modifier.layoutId("Animated Multi Pile")
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        VerticalCardPileCanFlip(
                            cardDpSize = layInfo.getCardDpSize(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile")
                        )
                    }
                }
                it.tableauCardFlipInfo?.let { info ->
                    VerticalCardPileCanFlip(
                        cardDpSize = layInfo.getCardDpSize(),
                        pile = info.remainingPile,
                        modifier = Modifier.layoutId("Animated Tableau Card")
                    ) {
                        FlipCard(
                            flipCard = info.flipCard,
                            cardDpSize = layInfo.getCardDpSize(),
                            flipRotation = tableauCardFlipRotation,
                            flipCardInfo = info.flipCardInfo
                        )
                    }
                }
            }
            Suits.entries.forEachIndexed { index, suit ->
                SolitairePile(
                    modifier = Modifier
                        .layoutId("${suit.name} Foundation")
                        .testTag("Foundation #$index"),
                    pile = foundationList[index].displayPile,
                    emptyIconId = suit.emptyIcon,
                    onClick = { handleMoveResult(onFoundationClick(index)) },
                    cardWidth = layInfo.cardWidth.dp
                )
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                pile = waste.displayPile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { handleMoveResult(onWasteClick()) },
                drawAmount = drawAmount,
                cardWidth = layInfo.cardWidth.toDp()
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick(drawAmount)) },
                cardWidth = layInfo.cardWidth.dp
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    pile = tableau.displayPile,
                    tableauIndex = index,
                    cardHeight = layInfo.cardHeight.toDp(),
                    cardWidth = layInfo.cardWidth.dp,
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

        var tableauPile0 = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        var tableauPile1 = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        var tableauPile2 = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        var tableauPile3 = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        var tableauPile4 = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        var tableauPile5 = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        var tableauPile6 = measurables.firstOrNull { it.layoutId == "Tableau #6" }

        val animatedHorizontalPile =
            measurables.firstOrNull { it.layoutId == "Animated Horizontal Pile" }
        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Vertical Pile" }
        val animatedMultiPile = measurables.firstOrNull { it.layoutId == "Animated Multi Pile" }

        val animatedTableauCard = measurables.firstOrNull { it.layoutId == "Animated Tableau Card" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layInfo.cardWidth
            val cardHeight = layInfo.cardHeight
            val cardConstraints = layInfo.cardConstraints
            val wasteConstraints = layInfo.wasteConstraints
            val tableauHeight = constraints.maxHeight - layInfo.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            if (animatedOffset != IntOffset.Zero) {
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
                animatedHorizontalPile?.measure(wasteConstraints)?.place(animatedOffset, 2f)
                animateInfo?.let {
                    it.tableauCardFlipInfo?.let { info ->
                        val pile =
                            if (info.flipCardInfo is FlipCardInfo.FaceDown) it.end else it.start
                        val tableauCardFlipPosition = layInfo.getPilePosition(pile)
                        animatedTableauCard?.measure(tableauConstraints)
                            ?.place(tableauCardFlipPosition, 1f)
                        when (pile) {
                            GamePiles.TableauZero -> tableauPile0 = null
                            GamePiles.TableauOne -> tableauPile1 = null
                            GamePiles.TableauTwo -> tableauPile2 = null
                            GamePiles.TableauThree -> tableauPile3 = null
                            GamePiles.TableauFour -> tableauPile4 = null
                            GamePiles.TableauFive -> tableauPile5 = null
                            GamePiles.TableauSix -> tableauPile6 = null
                            else -> { }
                        }
                    }
                }
            }
            animatedMultiPile?.measure(constraints)?.place(IntOffset.Zero, 2f)

            clubsFoundation?.measure(cardConstraints)?.place(layInfo.clubsFoundation)
            diamondsFoundation?.measure(cardConstraints)?.place(layInfo.diamondsFoundation)
            heartsFoundation?.measure(cardConstraints)?.place(layInfo.heartsFoundation)
            spadesFoundation?.measure(cardConstraints)?.place(layInfo.spadesFoundation)
            wastePile?.measure(wasteConstraints)?.place(layInfo.wastePile)
            stockPile?.measure(cardConstraints)?.place(layInfo.stockPile)

            tableauPile0?.measure(tableauConstraints)?.place(layInfo.tableauZero)
            tableauPile1?.measure(tableauConstraints)?.place(layInfo.tableauOne)
            tableauPile2?.measure(tableauConstraints)?.place(layInfo.tableauTwo)
            tableauPile3?.measure(tableauConstraints)?.place(layInfo.tableauThree)
            tableauPile4?.measure(tableauConstraints)?.place(layInfo.tableauFour)
            tableauPile5?.measure(tableauConstraints)?.place(layInfo.tableauFive)
            tableauPile6?.measure(tableauConstraints)?.place(layInfo.tableauSix)
        }
    }
}

@Composable
fun VerticalCardPileCanFlip(
    cardDpSize: DpSize,
    modifier: Modifier,
    pile: List<Card> = emptyList(),
    flipCard: @Composable () -> Unit = { }
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
        flipCard()
    }
}

@Composable
fun HorizontalCardPileWithFlip(
    layInfo: LayoutInfo,
    animateInfo: AnimateInfo,
    animateDurations: AnimationDurations,
    flipRotation: Float,
    modifier: Modifier
) {
    animateInfo.let {
        var leftCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var middleCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var rightCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        val offsets = layInfo.getHorizontalCardOffsets(it.flipCardInfo)

        if (it.animatedCards.size >= 3) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animateDurations,
                startOffset = offsets.leftCardStartOffset,
                endOffset = offsets.leftCardEndOffset,
                updateXOffset = { value -> leftCardOffset = leftCardOffset.copy(x = value) },
                updateYOffset = { }
            )
        }
        if (it.animatedCards.size >= 2) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animateDurations,
                startOffset = offsets.middleCardStartOffset,
                endOffset = offsets.middleCardEndOffset,
                updateXOffset = { value -> middleCardOffset = middleCardOffset.copy(x = value) },
                updateYOffset = { }
            )
        }
        if (it.animatedCards.isNotEmpty()) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animateDurations,
                startOffset = offsets.rightCardStartOffset,
                endOffset = offsets.rightCardEndOffset,
                updateXOffset = { value -> rightCardOffset = rightCardOffset.copy(x = value) },
                updateYOffset = { }
            )
        }

        Layout(
            modifier = modifier,
            content = {
                it.animatedCards.let { cards ->
                    if (cards.size >= 3) {
                        FlipCard(
                            flipCard = cards[cards.size - 3],
                            cardDpSize = layInfo.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId("Left Card")
                        )
                    }
                    if (cards.size >= 2) {
                        FlipCard(
                            flipCard = cards[cards.size - 2],
                            cardDpSize = layInfo.getCardDpSize(),
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipCardInfo,
                            modifier = Modifier.layoutId("Middle Card")
                        )
                    }
                    FlipCard(
                        flipCard = cards.last(),
                        cardDpSize = layInfo.getCardDpSize(),
                        flipRotation = flipRotation,
                        flipCardInfo = it.flipCardInfo,
                        modifier = Modifier.layoutId("Right Card")
                    )
                }
            }
        ) { measurables, constraints ->
            val leftCard = measurables.firstOrNull { meas -> meas.layoutId == "Left Card" }
            val middleCard = measurables.firstOrNull { meas -> meas.layoutId == "Middle Card" }
            val rightCard = measurables.firstOrNull { meas -> meas.layoutId == "Right Card" }

            layout(constraints.maxWidth, constraints.maxHeight) {
                leftCard?.measure(layInfo.cardConstraints)?.place(leftCardOffset)
                middleCard?.measure(layInfo.cardConstraints)
                    ?.place(middleCardOffset, 1f)
                rightCard?.measure(layInfo.cardConstraints)?.place(rightCardOffset, 2f)
            }
        }
    }
}

@Composable
fun MultiPileCardWithFlip(
    layInfo: LayoutInfo,
    animateInfo: AnimateInfo,
    animationDurations: AnimationDurations,
    flipRotation: Float,
    modifier: Modifier
) {
    animateInfo.let {
        var tZeroCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tOneCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tTwoCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tThreeCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tFourCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tFiveCardOffset by remember { mutableStateOf(IntOffset.Zero) }
        var tSixCardOffset by remember { mutableStateOf(IntOffset.Zero) }

        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauZero)
                .plus(layInfo.getCardsYOffset(it.startTableauIndices[0])),
            endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauZero)
                .plus(layInfo.getCardsYOffset(it.endTableauIndices[0])),
            updateXOffset = { value -> tZeroCardOffset = tZeroCardOffset.copy(x = value) },
            updateYOffset = { value -> tZeroCardOffset = tZeroCardOffset.copy(y = value) }
        )
        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauOne)
                .plus(layInfo.getCardsYOffset(it.startTableauIndices[1])),
            endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauOne)
                .plus(layInfo.getCardsYOffset(it.endTableauIndices[1])),
            updateXOffset = { value -> tOneCardOffset = tOneCardOffset.copy(x = value) },
            updateYOffset = { value -> tOneCardOffset = tOneCardOffset.copy(y = value) }
        )
        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauTwo)
                .plus(layInfo.getCardsYOffset(it.startTableauIndices[2])),
            endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauTwo)
                .plus(layInfo.getCardsYOffset(it.endTableauIndices[2])),
            updateXOffset = { value -> tTwoCardOffset = tTwoCardOffset.copy(x = value) },
            updateYOffset = { value -> tTwoCardOffset = tTwoCardOffset.copy(y = value) }
        )
        if (it.animatedCards.size == 7) {
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauThree)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices[3])),
                endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauThree)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices[3])),
                updateXOffset = { value -> tThreeCardOffset = tThreeCardOffset.copy(x = value) },
                updateYOffset = { value -> tThreeCardOffset = tThreeCardOffset.copy(y = value) }
            )
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauFour)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices[4])),
                endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauFour)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices[4])),
                updateXOffset = { value -> tFourCardOffset = tFourCardOffset.copy(x = value) },
                updateYOffset = { value -> tFourCardOffset = tFourCardOffset.copy(y = value) }
            )
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauFive)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices[5])),
                endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauFive)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices[5])),
                updateXOffset = { value -> tFiveCardOffset = tFiveCardOffset.copy(x = value) },
                updateYOffset = { value -> tFiveCardOffset = tFiveCardOffset.copy(y = value) }
            )
            AnimateOffset(
                animateInfo = it,
                animationDurations = animationDurations,
                startOffset = layInfo.getPilePosition(it.start, tableauAllPile = GamePiles.TableauSix)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices[6])),
                endOffset = layInfo.getPilePosition(it.end, tableauAllPile = GamePiles.TableauSix)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices[6])),
                updateXOffset = { value -> tSixCardOffset = tSixCardOffset.copy(x = value) },
                updateYOffset = { value -> tSixCardOffset = tSixCardOffset.copy(y = value) }
            )
        }

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
                    tZeroCard?.measure(layInfo.cardConstraints)?.place(tZeroCardOffset)
                }
                if (tOneCardOffset != IntOffset.Zero) {
                    tOneCard?.measure(layInfo.cardConstraints)?.place(tOneCardOffset)
                }
                if (tTwoCardOffset != IntOffset.Zero) {
                    tTwoCard?.measure(layInfo.cardConstraints)?.place(tTwoCardOffset)
                }
                if (tThreeCardOffset != IntOffset.Zero) {
                    tThreeCard?.measure(layInfo.cardConstraints)?.place(tThreeCardOffset)
                }
                if (tFourCardOffset != IntOffset.Zero) {
                    tFourCard?.measure(layInfo.cardConstraints)?.place(tFourCardOffset)
                }
                if (tFiveCardOffset != IntOffset.Zero) {
                    tFiveCard?.measure(layInfo.cardConstraints)?.place(tFiveCardOffset)
                }
                if (tSixCardOffset != IntOffset.Zero) {
                    tSixCard?.measure(layInfo.cardConstraints)?.place(tSixCardOffset)
                }
            }
        }
    }
}

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
            card = flipCard.copy(
                faceUp = when (flipCardInfo) {
                    is FlipCardInfo.FaceUp -> false
                    is FlipCardInfo.FaceDown -> true
                    is FlipCardInfo.NoFlip -> false
                }
            ),
            modifier = animateModifier
        )
    } else {
        SolitaireCard(
            card = flipCard.copy(
                faceUp = when (flipCardInfo) {
                    is FlipCardInfo.FaceUp -> true
                    is FlipCardInfo.FaceDown -> false
                    is FlipCardInfo.NoFlip -> false
                }
            ),
            modifier = animateModifier.graphicsLayer { rotationY = flipCardInfo.endRotationY }
        )
    }
}

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

@Preview(device = "id:Nexus One")
@Composable
fun BoardLayout480Preview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width480, 0),
            animationDurations = AnimationDurations.TwoHundredFifty,
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
            updateUndoEnabled = { },
            undoAnimation = false,
            updateUndoAnimation = { },
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(Card(10, Suits.CLUBS))),
            onStockClick = { MoveResult.Move },
            waste = Waste(),
            stockWasteEmpty = { false },
            onWasteClick = { MoveResult.Move },
            foundationList = Suits.entries.map { Foundation(it) },
            onFoundationClick = { MoveResult.Move },
            tableauList = List(7) { Tableau.KlondikeTableau() },
            onTableauClick = { _, _ -> MoveResult.Move }
        )
    }
}

@Preview(device = "id:Nexus 4")
@Composable
fun BoardLayout720Preview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width720, 24),
            animationDurations = AnimationDurations.TwoHundredFifty,
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
            updateUndoEnabled = { },
            undoAnimation = false,
            updateUndoAnimation = { },
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(Card(10, Suits.CLUBS))),
            onStockClick = { MoveResult.Move },
            waste = Waste(),
            stockWasteEmpty = { false },
            onWasteClick = { MoveResult.Move },
            foundationList = Suits.entries.map { Foundation(it) },
            onFoundationClick = { MoveResult.Move },
            tableauList = List(7) { Tableau.KlondikeTableau() },
            onTableauClick = { _, _ -> MoveResult.Move }
        )
    }
}

@Preview
@Composable
fun BoardLayout1080Preview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width1080, 0),
            animationDurations = AnimationDurations.TwoHundredFifty,
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
            updateUndoEnabled = { },
            undoAnimation = false,
            updateUndoAnimation = { },
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(Card(10, Suits.CLUBS))),
            onStockClick = { MoveResult.Move },
            waste = Waste(),
            stockWasteEmpty = { false },
            onWasteClick = { MoveResult.Move },
            foundationList = Suits.entries.map { Foundation(it) },
            onFoundationClick = { MoveResult.Move },
            tableauList = List(7) { Tableau.KlondikeTableau() },
            onTableauClick = { _, _ -> MoveResult.Move }
        )
    }
}

@Preview(device = "id:pixel_xl")
@Composable
fun BoardLayout1440Preview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width1440, 0),
            animationDurations = AnimationDurations.TwoHundredFifty,
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
            updateUndoEnabled = { },
            undoAnimation = false,
            updateUndoAnimation = { },
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(Card(10, Suits.CLUBS))),
            onStockClick = { MoveResult.Move },
            waste = Waste(),
            stockWasteEmpty = { false },
            onWasteClick = { MoveResult.Move },
            foundationList = Suits.entries.map { Foundation(it) },
            onFoundationClick = { MoveResult.Move },
            tableauList = List(7) { Tableau.KlondikeTableau() },
            onTableauClick = { _, _ -> MoveResult.Move }
        )
    }
}

@Preview(device = "spec:width=2160px,height=3840px,dpi=640")
@Composable
fun BoardLayout2160Preview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width2160, 0),
            animationDurations = AnimationDurations.TwoHundredFifty,
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
            updateUndoEnabled = { },
            undoAnimation = false,
            updateUndoAnimation = { },
            drawAmount = 1,
            handleMoveResult = { },
            stock = Stock(listOf(Card(10, Suits.CLUBS))),
            onStockClick = { MoveResult.Move },
            waste = Waste(),
            stockWasteEmpty = { false },
            onWasteClick = { MoveResult.Move },
            foundationList = Suits.entries.map { Foundation(it) },
            onFoundationClick = { MoveResult.Move },
            tableauList = List(7) { Tableau.KlondikeTableau() },
            onTableauClick = { _, _ -> MoveResult.Move }
        )
    }
}