package com.heyzeusv.solitaire.board

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
import com.heyzeusv.solitaire.board.layouts.SevenWideLayout
import com.heyzeusv.solitaire.board.layouts.Width1080
import com.heyzeusv.solitaire.board.layouts.Width1440
import com.heyzeusv.solitaire.board.layouts.Width2160
import com.heyzeusv.solitaire.board.layouts.Width480
import com.heyzeusv.solitaire.board.layouts.Width720
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Waste
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Displays [Stock], [Foundation], [Waste], and [Tableau] [Piles][Pile] needed in order to play 
 *  most Solitaire [Games].
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param layout Contains the positions for all [Piles][Pile].
 *  @param animationDurations The durations for each available animation.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param updateAnimateInfo Updates [AnimateInfo] once an animation fully completes.
 *  @param updateIsUndoEnabled Disables undo button during certain animations.
 *  @param isUndoAnimation Used to determine if currently running animation is representing an
 *  undo move.
 *  @param updateIsUndoAnimation Updates the value of [isUndoAnimation].
 *  @param drawAmount The number of [Cards][Card] that are moved to [Waste] on each [onStockClick].
 *  @param stock [Pile] where user draws more [Cards][Card] from.
 *  @param onStockClick Runs when [stock] is pressed.
 *  @param waste [Pile] where [Cards][Card] from [Stock] can be moved to.
 *  @param stockWasteEmpty Determines which empty icon should be displayed on [Stock].
 *  @param onWasteClick Runs when [waste] is pressed.
 *  @param foundationList The [Piles][Pile] where user has to move [Cards][Card] to in order to win.
 *  @param onFoundationClick Runs when any of [foundationList] is pressed.
 *  @param tableauList List of [Tableau] piles where user can move [Cards][Card] between or to
 *  [foundationList] piles.
 *  @param onTableauClick Runs when any of [tableauList] is pressed.
 */
@Composable
fun StandardBoard(
    modifier: Modifier = Modifier,
    layout: SevenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateIsUndoEnabled: (Boolean) -> Unit = { },
    isUndoAnimation: Boolean,
    updateIsUndoAnimation: (Boolean) -> Unit = { },
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
    onTableauClick: (Int, Int) -> Unit = { _, _ -> },
) {
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }

    animateInfo?.let {
        // Updating AnimateInfo to null if animation is fully completed
        LaunchedEffect(key1 = it) {
            try {
                if (it.isUndoAnimation) updateIsUndoAnimation(true) else updateIsUndoEnabled(false)
                delay(animationDurations.fullDelay)
                updateAnimateInfo(null)
            } finally {
                if (it.isUndoAnimation) updateIsUndoAnimation(false)
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
                updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) },
            )
        }
    }

    Layout(
        modifier = modifier.gesturesDisabled(isUndoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipCardInfo) {
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> {
                        HorizontalCardPileWithFlip(
                            modifier = Modifier.layoutId("Animated Horizontal Pile"),
                            layout = layout,
                            animateInfo = it,
                            animationDurations = animationDurations,
                        )
                    }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
                        MultiPileCardWithFlip(
                            modifier = Modifier.layoutId("Animated Multi Pile"),
                            layout = layout,
                            animateInfo = it,
                            animationDurations = animationDurations,
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        StaticVerticalCardPile(
                            modifier = Modifier.layoutId("Animated Vertical Pile"),
                            cardDpSize = layout.getCardDpSize(),
                            spacedByPercent = layout.vPileSpacedByPercent,
                            pile = it.animatedCards,
                        )
                    }
                }
                it.tableauCardFlipInfo?.let { _ ->
                    TableauPileWithFlip(
                        modifier = Modifier.layoutId("Animated Tableau Card"),
                        cardDpSize = layout.getCardDpSize(),
                        spacedByPercent = layout.vPileSpacedByPercent,
                        animateInfo = it,
                        animationDurations = animationDurations,
                    )
                }
            }
            Suits.entries.forEachIndexed { index, suit ->
                Foundation(
                    modifier = Modifier
                        .layoutId("${suit.name} Foundation")
                        .testTag("Foundation #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    pile = foundationList[index].displayPile,
                    emptyIconId = suit.emptyIcon,
                    onClick = { onFoundationClick(index) },
                )
            }
            Waste(
                modifier = Modifier
                    .layoutId("Waste")
                    .testTag("Waste"),
                cardDpSize = layout.getCardDpSize(),
                pile = waste.displayPile,
                emptyIconId = R.drawable.waste_empty,
                onClick = { onWasteClick() },
                drawAmount = drawAmount,
            )
            Stock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                cardDpSize = layout.getCardDpSize(),
                pile = stock.displayPile,
                stockWasteEmpty = stockWasteEmpty,
                onClick = { onStockClick() },
            )
            tableauList.forEachIndexed { index, tableau ->
                Tableau(
                    modifier = Modifier.layoutId("Tableau #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    spacedByPercent = layout.vPileSpacedByPercent,
                    pile = tableau.displayPile,
                    tableauIndex = index,
                    onClick = onTableauClick,
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
            val cardWidth = layout.cardWidth
            val cardHeight = layout.cardHeight
            val cardConstraints = layout.cardConstraints
            val wasteConstraints = layout.wasteConstraints
            val tableauHeight = constraints.maxHeight - layout.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            if (animatedOffset != IntOffset.Zero) {
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
                animatedHorizontalPile?.measure(wasteConstraints)?.place(animatedOffset, 2f)
                animateInfo?.let {
                    it.tableauCardFlipInfo?.let { info ->
                        val pile =
                            if (info.flipCardInfo is FlipCardInfo.FaceDown) it.end else it.start
                        val tableauCardFlipPosition = layout.getPilePosition(pile)
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

            clubsFoundation?.measure(cardConstraints)?.place(layout.foundationClubs)
            diamondsFoundation?.measure(cardConstraints)?.place(layout.foundationDiamonds)
            heartsFoundation?.measure(cardConstraints)?.place(layout.foundationHearts)
            spadesFoundation?.measure(cardConstraints)?.place(layout.foundationSpades)
            wastePile?.measure(wasteConstraints)?.place(layout.wastePile)
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

@Preview(device = "id:Nexus One")
@Composable
private fun StandardBoard480Preview() {
    PreviewUtil().apply {
        Preview {
            StandardBoard(
                layout = Width480(0).sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it, GamePiles.FoundationSpadesOne) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:Nexus 4")
@Composable
private fun StandardBoard720Preview() {
    PreviewUtil().apply {
        Preview {
            StandardBoard(
                layout = Width720(24).sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it, GamePiles.FoundationSpadesOne) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview
@Composable
private fun StandardBoard1080Preview() {
    PreviewUtil().apply {
        Preview {
            StandardBoard(
                layout = Width1080(0).sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it, GamePiles.FoundationSpadesOne) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:pixel_xl")
@Composable
private fun StandardBoard1440Preview() {
    PreviewUtil().apply {
        Preview {
            StandardBoard(
                layout = Width1440(0).sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it, GamePiles.FoundationSpadesOne) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "spec:width=2160px,height=3840px,dpi=640")
@Composable
private fun StandardBoard2160Preview() {
    PreviewUtil().apply {
        Preview {
            StandardBoard(
                layout = Width2160(0).sevenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = Suits.entries.map { Foundation(it, GamePiles.FoundationSpadesOne) },
                tableauList = List(7) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}