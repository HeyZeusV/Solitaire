package com.heyzeusv.solitaire.ui.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
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
    // gets device size in order to scale card
    val config = LocalConfiguration.current
    val sWidth = config.screenWidthDp.dp
    // removed layout padding and space between cards
    val cardW = (sWidth - 4.dp - 12.dp) / 7 // need to fit 7 piles wide on screen

    var offsetX by remember(animateInfo) { mutableFloatStateOf(0f) }
    var offsetY by remember(animateInfo) { mutableFloatStateOf(0f) }
    var flipRotation by remember(animateInfo) { mutableFloatStateOf(0f) }
    var tableauFlipRotation by remember(animateInfo) { mutableFloatStateOf(0f) }
    val animationSpec = tween<Float>(250, easing = FastOutSlowInEasing)

    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            try {
                delay(15)
                it.actionBeforeAnimation()
            } catch (e: Exception) {
                it.actionBeforeAnimation()
            }
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            try {
                if (it.undoAnimation) updateUndoAnimation(true) else updateUndoEnabled(false)
                val offsetStart = layInfo.getPilePosition(it.start, it.stockWasteMove)
                val offsetEnd = layInfo.getPilePosition(it.end, it.stockWasteMove)
                animate(
                    initialValue = offsetStart.x.toFloat(),
                    targetValue = offsetEnd.x.toFloat(),
                    animationSpec = animationSpec
                ) { value, _ ->
                    offsetX = value
                }
                it.actionAfterAnimation()
                if (it.undoAnimation) updateUndoAnimation(false)
            } catch (e: Exception) {
                it.actionAfterAnimation()
                if (it.undoAnimation) updateUndoAnimation(false)
            }
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            val offsetStart = layInfo.getPilePosition(it.start)
                .plus(layInfo.getCardsYOffset(it.startTableauIndex))
            val offsetEnd = layInfo.getPilePosition(it.end)
                .plus(layInfo.getCardsYOffset(it.endTableauIndex))
            animate(
                initialValue = offsetStart.y.toFloat(),
                targetValue = offsetEnd.y.toFloat(),
                animationSpec = animationSpec
            ) { value, _ ->
                offsetY = value
            }
            updateAnimateInfo(null)
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            when (it.flipAnimatedCards) {
                FlipCardInfo.NoFlip -> { }
                else -> {
                    animate(
                        initialValue = it.flipAnimatedCards.startRotationY,
                        targetValue = it.flipAnimatedCards.endRotationY,
                        animationSpec = getFlipAnimationSpec(250)
                    ) { value, _ ->
                        flipRotation = value
                    }
                }
            }
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.tableauCardFlipInfo?.let {
            delay(50)
            animate(
                initialValue = it.flipCardInfo.startRotationY,
                targetValue = it.flipCardInfo.endRotationY,
                animationSpec = getFlipAnimationSpec(200)
            ) { value, _ ->
                tableauFlipRotation = value
            }
        }
    }

    Layout(
        modifier = modifier.gesturesDisabled(undoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipAnimatedCards) {
                    is FlipCardInfo.FaceDown -> {
                        HorizontalCardFlipPile(
                            layInfo = layInfo,
                            pile = it.animatedCards,
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipAnimatedCards,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    is FlipCardInfo.FaceUp -> {
                        HorizontalCardFlipPile(
                            layInfo = layInfo,
                            pile = it.animatedCards,
                            flipRotation = flipRotation,
                            flipCardInfo = it.flipAnimatedCards,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        VerticalCardPile(
                            cardHeight = layInfo.cardHeight.toDp(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile")
                        )
                    }
                }
                it.tableauCardFlipInfo?.let { info ->
                    VerticalCardPile(
                        cardHeight = layInfo.cardHeight.toDp(),
                        pile = info.remainingPile,
                        modifier = Modifier.layoutId("Animated Tableau Card")
                    ) {
                        FlipCard(
                            card = info.card,
                            cardHeight = layInfo.cardHeight.toDp(),
                            flipRotation = tableauFlipRotation,
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
                    cardWidth = cardW
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
                cardWidth = cardW
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick(drawAmount)) },
                cardWidth = cardW
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    pile = tableau.displayPile,
                    tableauIndex = index,
                    cardHeight = layInfo.cardHeight.toDp(),
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

        val animatedTableauCard = measurables.firstOrNull { it.layoutId == "Animated Tableau Card" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layInfo.cardWidth
            val cardHeight = layInfo.cardHeight
            val cardConstraints = layInfo.cardConstraints
            val wasteConstraints = layInfo.wasteConstraints
            val tableauHeight = constraints.maxHeight - layInfo.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            val animatedPileX = 0 + offsetX.toInt()
            val animatedPileY = 0 + offsetY.toInt()

            if (animatedPileX != 0 || animatedPileY != 0) {
                animatedVerticalPile?.measure(tableauConstraints)
                    ?.place(animatedPileX, animatedPileY, 2f)
                animatedHorizontalPile?.measure(wasteConstraints)
                    ?.place(animatedPileX, animatedPileY, 2f)
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
fun VerticalCardPile(
    cardHeight: Dp,
    modifier: Modifier,
    pile: List<Card> = emptyList(),
    flipCard: @Composable () -> Unit = { }
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = -(cardHeight.times(0.75f)))
    ) {
        pile.forEach { card ->
            SolitaireCard(
                card = card,
                modifier = Modifier.height(cardHeight)
            )
        }
        flipCard()
    }
}

@Composable
fun HorizontalCardFlipPile(
    layInfo: LayoutInfo,
    pile: List<Card>,
    flipRotation: Float,
    flipCardInfo: FlipCardInfo,
    modifier: Modifier
) {
    var leftCardXOffset by remember { mutableFloatStateOf(0f) }
    var middleCardXOffset by remember { mutableFloatStateOf(0f) }
    var rightCardXOffset by remember { mutableFloatStateOf(0f) }
    val animationSpec = tween<Float>(250, easing = FastOutSlowInEasing)

    if (pile.size >= 3) {
        LaunchedEffect(key1 = pile) {
            val initialValue: Float
            val targetValue: Float
            if (flipCardInfo is FlipCardInfo.FaceDown) {
                initialValue = layInfo.leftCardXOffset
                targetValue = 0f
            } else {
                initialValue = 0f
                targetValue = layInfo.leftCardXOffset
            }
            animate(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = animationSpec
            ) { value, _ ->
                leftCardXOffset = value
            }
        }
    }
    if (pile.size >= 2) {
        val initialValue: Float
        val targetValue: Float
        if (flipCardInfo is FlipCardInfo.FaceDown) {
            initialValue = layInfo.middleCardXOffset
            targetValue = 0f
        } else {
            initialValue = 0f
            targetValue = layInfo.middleCardXOffset
        }
        LaunchedEffect(key1 = pile) {
            animate(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = animationSpec
            ) { value, _ ->
                middleCardXOffset = value
            }
        }
    }
    if (pile.isNotEmpty()) {
        LaunchedEffect(key1 = pile) {
            val initialValue: Float
            val targetValue: Float
            if (flipCardInfo is FlipCardInfo.FaceDown) {
                initialValue = layInfo.rightCardXOffset
                targetValue = 0f
            } else {
                initialValue = 0f
                targetValue = layInfo.rightCardXOffset
            }
            animate(
                initialValue = initialValue,
                targetValue = targetValue,
                animationSpec = animationSpec
            ) { value, _ ->
                rightCardXOffset = value
            }
        }
    }

    Layout(
        modifier = modifier,
        content = {
            if (pile.size >= 3) {
                FlipCard(
                    card = pile[pile.size - 3],
                    cardHeight = layInfo.cardHeight.toDp(),
                    flipRotation = flipRotation,
                    flipCardInfo = flipCardInfo,
                    modifier = Modifier.layoutId("Left Card")
                )
            }
            if (pile.size >= 2) {
                FlipCard(
                    card = pile[pile.size - 2],
                    cardHeight = layInfo.cardHeight.toDp(),
                    flipRotation = flipRotation,
                    flipCardInfo = flipCardInfo,
                    modifier = Modifier.layoutId("Middle Card")
                )
            }
            FlipCard(
                card = pile.last(),
                cardHeight = layInfo.cardHeight.toDp(),
                flipRotation = flipRotation,
                flipCardInfo = flipCardInfo,
                modifier = Modifier.layoutId("Right Card")
            )
        }
    ) { measurables, constraints ->

        val leftCard = measurables.firstOrNull { it.layoutId == "Left Card" }
        val middleCard = measurables.firstOrNull { it.layoutId == "Middle Card" }
        val rightCard = measurables.firstOrNull { it.layoutId == "Right Card" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            leftCard?.measure(layInfo.cardConstraints)?.place(leftCardXOffset.toInt(), 0)
            middleCard?.measure(layInfo.cardConstraints)?.place(middleCardXOffset.toInt(), 0)
            rightCard?.measure(layInfo.cardConstraints)?.place(rightCardXOffset.toInt(), 0)
        }
    }
}

@Composable
fun FlipCard(
    card: Card,
    cardHeight: Dp,
    flipRotation: Float,
    flipCardInfo: FlipCardInfo,
    modifier: Modifier = Modifier
) {
    val animateModifier = modifier
        .height(cardHeight)
        .graphicsLayer {
            rotationY = flipRotation
            cameraDistance = 8 * density
        }
    if (flipCardInfo.flipCondition(flipRotation)) {
        SolitaireCard(
            card = card.copy(
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
            card = card.copy(
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

private fun getFlipAnimationSpec(duration: Int): TweenSpec<Float> =
    tween(duration, easing = LinearEasing)

@Preview
@Composable
fun BoardLayoutPreview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width1080, 0),
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