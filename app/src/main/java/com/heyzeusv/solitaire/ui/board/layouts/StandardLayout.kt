package com.heyzeusv.solitaire.ui.board.layouts

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
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
import com.heyzeusv.solitaire.ui.board.AnimateOffset
import com.heyzeusv.solitaire.ui.board.HorizontalCardPileWithFlip
import com.heyzeusv.solitaire.ui.board.MultiPileCardWithFlip
import com.heyzeusv.solitaire.ui.board.SolitairePile
import com.heyzeusv.solitaire.ui.board.SolitaireStock
import com.heyzeusv.solitaire.ui.board.SolitaireTableau
import com.heyzeusv.solitaire.ui.board.TableauPileWithFlip
import com.heyzeusv.solitaire.ui.board.VerticalCardPile
import com.heyzeusv.solitaire.util.AnimationDurations
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.MoveResult
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Composable that displays all [Card] piles, Stock, Waste, Foundation, and Tableau. [layInfo] is
 *  used to determine offsets of every pile. [animationDurations] determines how long each animation
 *  lasts. [animateInfo] is used to determine what needs to be animated and can be updated with
 *  [updateAnimateInfo]. [updateUndoEnabled] is used to enable/disable undo button during
 *  animations. [undoAnimation] is used to enable/disable all clicks during an undo animation and
 *  is updated using [updateUndoAnimation]. [drawAmount] determines how many cards are drawn from
 *  [Stock] and shown by [Waste].
 */
@Composable
fun StandardLayout(
    modifier: Modifier = Modifier,
    layInfo: LayoutInfo,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateUndoEnabled: (Boolean) -> Unit = { },
    undoAnimation: Boolean,
    updateUndoAnimation: (Boolean) -> Unit = { },
    drawAmount: DrawAmount,
    handleMoveResult: (MoveResult) -> Unit = { },
    /** Piles and their onClicks */
    stock: Stock,
    onStockClick: () -> MoveResult = { MoveResult.Illegal },
    waste: Waste,
    stockWasteEmpty: () -> Boolean = { true },
    onWasteClick: () -> MoveResult = { MoveResult.Illegal },
    foundationList: List<Foundation>,
    onFoundationClick: (Int) -> MoveResult = { MoveResult.Illegal },
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> MoveResult = { _, _ -> MoveResult.Illegal }
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
                startOffset = layInfo.getPilePosition(it.start, it.stockWasteMove)
                    .plus(layInfo.getCardsYOffset(it.startTableauIndices.first())),
                endOffset = layInfo.getPilePosition(it.end, it.stockWasteMove)
                    .plus(layInfo.getCardsYOffset(it.endTableauIndices.first())),
                updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
                updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) }
            )
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
                            animationDurations = animationDurations,
                            modifier = Modifier.layoutId("Animated Horizontal Pile")
                        )
                    }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
                        MultiPileCardWithFlip(
                            layInfo = layInfo,
                            animateInfo = it,
                            animationDurations = animationDurations,
                            modifier = Modifier.layoutId("Animated Multi Pile")
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        VerticalCardPile(
                            cardDpSize = layInfo.getCardDpSize(),
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile")
                        )
                    }
                }
                it.tableauCardFlipInfo?.let { _ ->
                    TableauPileWithFlip(
                        cardDpSize = layInfo.getCardDpSize(),
                        animateInfo = it,
                        animationDurations = animationDurations,
                        modifier = Modifier.layoutId("Animated Tableau Card")
                    )
                }
            }
            Suits.entries.forEachIndexed { index, suit ->
                SolitairePile(
                    modifier = Modifier
                        .layoutId("${suit.name} Foundation")
                        .testTag("Foundation #$index"),
                    cardDpSize = layInfo.getCardDpSize(),
                    pile = foundationList[index].displayPile,
                    emptyIconId = suit.emptyIcon,
                    onClick = { handleMoveResult(onFoundationClick(index)) }
                )
            }
            SolitairePile(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                cardDpSize = layInfo.getCardDpSize(),
                pile = waste.displayPile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { handleMoveResult(onWasteClick()) },
                drawAmount = drawAmount
            )
            SolitaireStock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                cardDpSize = layInfo.getCardDpSize(),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { handleMoveResult(onStockClick()) }
            )
            tableauList.forEachIndexed { index, tableau ->
                SolitaireTableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    cardDpSize = layInfo.getCardDpSize(),
                    pile = tableau.displayPile,
                    tableauIndex = index,
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

@Preview(device = "id:Nexus One")
@Composable
fun BoardLayout480Preview() {
    PreviewUtil().apply {
        Preview {
            StandardLayout(
                layInfo = LayoutInfo(LayoutPositions.Width480, 0),
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                undoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:Nexus 4")
@Composable
fun BoardLayout720Preview() {
    PreviewUtil().apply {
        Preview {
            StandardLayout(
                layInfo = LayoutInfo(LayoutPositions.Width720, 24),
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                undoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview
@Composable
fun BoardLayout1080Preview() {
    PreviewUtil().apply {
        Preview {
            StandardLayout(
                layInfo = LayoutInfo(LayoutPositions.Width1080, 0),
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                undoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:pixel_xl")
@Composable
fun BoardLayout1440Preview() {
    PreviewUtil().apply {
        Preview {
            StandardLayout(
                layInfo = LayoutInfo(LayoutPositions.Width1440, 0),
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                undoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "spec:width=2160px,height=3840px,dpi=640")
@Composable
fun BoardLayout2160Preview() {
    PreviewUtil().apply {
        Preview {
            StandardLayout(
                layInfo = LayoutInfo(LayoutPositions.Width2160, 0),
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                undoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}