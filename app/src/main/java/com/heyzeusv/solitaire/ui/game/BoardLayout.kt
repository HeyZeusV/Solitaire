package com.heyzeusv.solitaire.ui.game

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import com.heyzeusv.solitaire.data.FlipCard
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
import com.heyzeusv.solitaire.util.toDp

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
    val animationSpec = tween<Float>(250, easing = FastOutSlowInEasing)
    val animationSpecFlip = tween<Float>(250, easing = LinearEasing)

    LaunchedEffect(key1 = animateInfo) {
         animateInfo?.let {
             val offsetStart = layInfo.getPilePosition(it.start, drawAmount)
             val offsetEnd = layInfo.getPilePosition(it.end, drawAmount)
             animate(
                 initialValue = offsetStart.x.toFloat(),
                 targetValue = offsetEnd.x.toFloat(),
                 animationSpec = animationSpec
             ) { value, _ ->
                 offsetX = value
             }
         }
    }
    LaunchedEffect(key1 = animateInfo) {
        animateInfo?.let {
            val offsetStart = layInfo.getPilePosition(it.start, drawAmount)
                .plus(layInfo.getCardsYOffset(it.startIndex))
            val offsetEnd = layInfo.getPilePosition(it.end, drawAmount)
                .plus(layInfo.getCardsYOffset(it.endIndex))
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
            if (it.start == GamePiles.Stock) {
                val flipCardUp = FlipCard.FaceUp()
                animate(
                    initialValue = flipCardUp.startRotationY,
                    targetValue = flipCardUp.endRotationY,
                    animationSpec = animationSpecFlip
                ) { value, _ ->
                    flipRotation = value
                }
            } else if (it.start == GamePiles.Waste && it.end == GamePiles.Stock) {
                val flipCardDown = FlipCard.FaceDown()
                animate(
                    initialValue = flipCardDown.startRotationY,
                    targetValue = flipCardDown.endRotationY,
                    animationSpec = animationSpecFlip
                ) { value, _ ->
                    flipRotation = value
                }
            }
        }
    }

    Layout(
        modifier = modifier,
        content = {
            animateInfo?.let {
                if (it.start == GamePiles.Stock) {
                    FlipCard(
                        animateInfo = it,
                        cardHeight = layInfo.cardHeight.toDp(),
                        flipRotation = flipRotation,
                        flipCard = FlipCard.FaceUp(flipRotation)
                    )
                } else if (it.start == GamePiles.Waste && it.end == GamePiles.Stock) {
                    FlipCard(
                        animateInfo = it,
                        cardHeight = layInfo.cardHeight.toDp(),
                        flipRotation = flipRotation,
                        flipCard = FlipCard.FaceDown(flipRotation)
                    )
                } else {
                    VerticalCardPile(
                        cardHeight = layInfo.cardHeight.toDp(),
                        pile = it.cards
                    )
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

        val tableauPile0 = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        val tableauPile1 = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        val tableauPile2 = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        val tableauPile3 = measurables.firstOrNull { it.layoutId == "Tableau #3" }
        val tableauPile4 = measurables.firstOrNull { it.layoutId == "Tableau #4" }
        val tableauPile5 = measurables.firstOrNull { it.layoutId == "Tableau #5" }
        val tableauPile6 = measurables.firstOrNull { it.layoutId == "Tableau #6" }

        val animatedPile = measurables.firstOrNull { it.layoutId == "Animated Pile"}

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layInfo.cardWidth
            val cardConstraints = layInfo.cardConstraints
            val wasteConstraints = layInfo.wasteConstraints
            val tableauHeight = constraints.maxHeight - layInfo.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, tableauHeight, tableauHeight)

            val animatedPileX = 0 + offsetX.toInt()
            val animatedPileY = 0 + offsetY.toInt()

            if (animatedPileX != 0 || animatedPileY != 0) {
                animatedPile?.measure(tableauConstraints)?.place(animatedPileX, animatedPileY, 1f)
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
fun FlipCard(
    animateInfo: AnimateInfo,
    cardHeight: Dp,
    flipRotation: Float,
    flipCard: FlipCard
) {
    val animateModifier = Modifier.graphicsLayer {
        rotationY = flipRotation
        cameraDistance = 8 * density
    }
    if (flipCard.flipCondition) {
        VerticalCardPile(
            cardHeight = cardHeight,
            modifier = animateModifier,
            pile = animateInfo.cards
        )
    } else {
        VerticalCardPile(
            cardHeight = cardHeight,
            modifier = animateModifier.graphicsLayer { rotationY = flipCard.endRotationY },
            pile = animateInfo.cards.map { card ->
                card.copy(
                    faceUp = when (flipCard) {
                        is FlipCard.FaceUp -> true
                        is FlipCard.FaceDown -> false
                    }
                )
            }
        )
    }
}

@Composable
fun VerticalCardPile(
    cardHeight: Dp,
    modifier: Modifier = Modifier,
    pile: List<Card> = emptyList()
) {
    Column(
        modifier = modifier.layoutId("Animated Pile"),
        verticalArrangement = Arrangement.spacedBy(space = -(cardHeight.times(0.75f)))
    ) {
        pile.forEach { card ->
            SolitaireCard(
                card = card,
                modifier = Modifier.height(cardHeight)
            )
        }
    }
}

@Preview
@Composable
fun BoardLayoutPreview() {
    SolitairePreview {
        BoardLayout(
            layInfo = LayoutInfo(LayoutPositions.Width1080, 0),
            animateInfo = AnimateInfo(GamePiles.Stock, GamePiles.Stock, emptyList()),
            updateAnimateInfo = { },
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