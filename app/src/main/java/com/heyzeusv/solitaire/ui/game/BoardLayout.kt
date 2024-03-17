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
import com.heyzeusv.solitaire.util.firstCard
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

    BoardLayout(
        layInfo = gameVM.layoutInfo,
        animateInfo = animateInfo,
        updateAnimateInfo = gameVM::updateAnimateInfo,
        updateUndoEnabled = gameVM::updateUndoEnabled,
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
                updateUndoEnabled(false)
                val offsetStart = layInfo.getPilePosition(it.start, drawAmount)
                val offsetEnd = layInfo.getPilePosition(it.end, drawAmount)
                animate(
                    initialValue = offsetStart.x.toFloat(),
                    targetValue = offsetEnd.x.toFloat(),
                    animationSpec = animationSpec
                ) { value, _ ->
                    offsetX = value
                }
                it.actionAfterAnimation()
            } catch (e: Exception) {
                it.actionAfterAnimation()
            }
        }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            val offsetStart = layInfo.getPilePosition(it.start, drawAmount)
                .plus(layInfo.getCardsYOffset(it.startTableauIndex))
            val offsetEnd = layInfo.getPilePosition(it.end, drawAmount)
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
        modifier = modifier,
        content = {
            animateInfo?.let {
                when (it.flipAnimatedCards) {
                    is FlipCardInfo.FaceDown -> {
                        FlipCard(
                            card = it.animatedCards.firstCard(),
                            cardHeight = layInfo.cardHeight.toDp(),
                            flipRotation = flipRotation,
                            flipCardInfo = FlipCardInfo.FaceDown(),
                            modifier = Modifier.layoutId("Animated Pile")
                        )
                    }
                    is FlipCardInfo.FaceUp -> {
                        FlipCard(
                            card = it.animatedCards.firstCard(),
                            cardHeight = layInfo.cardHeight.toDp(),
                            flipRotation = flipRotation,
                            flipCardInfo = FlipCardInfo.FaceUp(),
                            modifier = Modifier.layoutId("Animated Pile")
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        VerticalCardPile(
                            cardHeight = layInfo.cardHeight.toDp(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Pile")
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
                    pile = foundationList[index].pile,
                    emptyIconId = suit.emptyIcon,
                    onClick = { handleMoveResult(onFoundationClick(index)) },
                    cardWidth = cardW
                )
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                pile = waste.pile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { handleMoveResult(onWasteClick()) },
                drawAmount = drawAmount,
                cardWidth = cardW
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                pile = stock.pile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick(drawAmount)) },
                cardWidth = cardW
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    pile = tableau.pile,
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

        val animatedPile = measurables.firstOrNull { it.layoutId == "Animated Pile"}
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
                animatedPile?.measure(tableauConstraints)?.place(animatedPileX, animatedPileY, 2f)
                animateInfo?.let {
                    it.tableauCardFlipInfo?.let { info ->
                        val pile =
                            if (info.flipCardInfo is FlipCardInfo.FaceDown) it.end else it.start
                        val tableauCardFlipPosition = layInfo.getPilePosition(pile, drawAmount)
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