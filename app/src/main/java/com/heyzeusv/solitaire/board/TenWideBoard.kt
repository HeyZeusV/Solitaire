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
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.layouts.TenWideLayout
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.piles.Waste
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.board.layouts.Width1080
import com.heyzeusv.solitaire.board.layouts.Width1440
import com.heyzeusv.solitaire.board.layouts.Width2160
import com.heyzeusv.solitaire.board.layouts.Width480
import com.heyzeusv.solitaire.board.layouts.Width720
import com.heyzeusv.solitaire.games.Games
import com.heyzeusv.solitaire.games.Spider
import com.heyzeusv.solitaire.util.DrawAmount
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Displays [Stock], [Foundation], [Waste], and [Tableau] [Piles][Pile] needed in order to play
 *  [Games] that require 10 [Piles][Pile] side-by-side, such as [Spider] and its variants.
 *
 *  @param modifier Modifiers to be applied to the layout.
 *  @param layout Contains the positions for all [Piles][Pile].
 *  @param animationDurations The durations for each available animation.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param updateAnimateInfo Updates [animateInfo] once an animation fully completes.
 *  @param spiderAnimateInfo Contains the information needed to animate a full (A to King) pile.
 *  @param updateSpiderAnimateInfo Updates [spiderAnimateInfo] once an animation fully completes.
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
fun TenWideBoard(
    modifier: Modifier = Modifier,
    layout: TenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    spiderAnimateInfo: AnimateInfo?,
    updateSpiderAnimateInfo: (AnimateInfo?) -> Unit = { },
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
    // not used to animate per se, but instead to ensure Composable does not blink in and out
    var spiderAnimatedOffset by remember(spiderAnimateInfo) { mutableStateOf(IntOffset.Zero) }

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
    spiderAnimateInfo?.let {
        // Updating AnimateInfo to null if animation is fully completed
        LaunchedEffect(key1 = it) {
            try {
                if (it.isUndoAnimation) updateIsUndoAnimation(true) else updateIsUndoEnabled(false)
                delay(animationDurations.fullDelay)
                updateSpiderAnimateInfo(null)
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
                spiderAnimatedOffset = IntOffset(1, 1)
                delay(animationDurations.afterActionDelay)
            } finally {
                it.actionAfterAnimation()
                spiderAnimatedOffset = IntOffset.Zero
            }
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
                            modifier = Modifier.layoutId("Animated Static Vertical Pile"),
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
            spiderAnimateInfo?.let {
                DynamicVerticalCardPile(
                    modifier = Modifier.layoutId("Animated Dynamic Vertical Pile"),
                    layout = layout,
                    animationDurations = animationDurations,
                    animateInfo = it,
                )
                it.tableauCardFlipInfo?.let { _ ->
                    TableauPileWithFlip(
                        modifier = Modifier.layoutId("Animated Spider Tableau Card"),
                        cardDpSize = layout.getCardDpSize(),
                        spacedByPercent = layout.vPileSpacedByPercent,
                        animateInfo = it,
                        animationDurations = animationDurations,
                    )
                }
            }
            foundationList.forEachIndexed { index, foundation ->
                Foundation(
                    modifier = Modifier
                        .layoutId("Foundation #$index")
                        .testTag("Foundation #$index"),
                    cardDpSize = layout.getCardDpSize(),
                    pile = foundation.displayPile,
                    emptyIconId = foundation.suit.emptyIcon,
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

        val animatedHorizontalPile =
            measurables.firstOrNull { it.layoutId == "Animated Horizontal Pile" }
        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Static Vertical Pile" }
        val animatedMultiPile = measurables.firstOrNull { it.layoutId == "Animated Multi Pile" }
        val animatedTableauCard = measurables.firstOrNull { it.layoutId == "Animated Tableau Card" }
        val animatedSpiderTableauCard =
            measurables.firstOrNull { it.layoutId == "Animated Spider Tableau Card" }
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
                animatedHorizontalPile?.measure(wasteConstraints)?.place(animatedOffset, 2f)
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
            if (spiderAnimatedOffset != IntOffset.Zero) {
                spiderAnimateInfo?.let {
                    it.tableauCardFlipInfo?.let { info ->
                        val pile =
                            if (info.flipCardInfo is FlipCardInfo.FaceDown) it.end else it.start
                        val tableauCardFlipPosition = layout.getPilePosition(pile)
                        animatedSpiderTableauCard?.measure(tableauConstraints)
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
                            else -> {}
                        }
                    }
                }
            }
            animatedDynamicPile?.measure(constraints)?.place(IntOffset.Zero, 3f)
            animatedMultiPile?.measure(constraints)?.place(IntOffset.Zero, 2f)

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

@Preview(device = "id:Nexus One")
@Composable
private fun TenWideBoard480Preview() {
    PreviewUtil().apply {
        Preview {
            TenWideBoard(
                layout = Width480(0).tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                spiderAnimateInfo = null,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = List(8) { Foundation(Suits.CLUBS, GamePiles.Stock, pile) },
                tableauList = List(10) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:Nexus 4")
@Composable
private fun TenWideBoard720Preview() {
    PreviewUtil().apply {
        Preview {
            TenWideBoard(
                layout = Width720(24).tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                spiderAnimateInfo = null,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = List(8) { Foundation(Suits.CLUBS, GamePiles.Stock, pile) },
                tableauList = List(10) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview
@Composable
private fun TenWideBoard1080Preview() {
    PreviewUtil().apply {
        Preview {
            TenWideBoard(
                layout = Width1080(0).tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                spiderAnimateInfo = null,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = List(8) { Foundation(Suits.CLUBS, GamePiles.Stock, pile) },
                tableauList = List(10) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "id:pixel_xl")
@Composable
private fun TenWideBoard1440Preview() {
    PreviewUtil().apply {
        Preview {
            TenWideBoard(
                layout = Width1440(0).tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                spiderAnimateInfo = null,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = List(8) { Foundation(Suits.CLUBS, GamePiles.Stock, pile) },
                tableauList = List(10) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}

@Preview(device = "spec:width=2160px,height=3840px,dpi=640")
@Composable
private fun TenWideBoard2160Preview() {
    PreviewUtil().apply {
        Preview {
            TenWideBoard(
                layout = Width2160(0).tenWideLayout,
                animationDurations = animationDurations,
                animateInfo = animateInfo,
                spiderAnimateInfo = null,
                isUndoAnimation = false,
                drawAmount = DrawAmount.One,
                stock = Stock(pile),
                waste = Waste(),
                foundationList = List(8) { Foundation(Suits.CLUBS, GamePiles.Stock, pile) },
                tableauList = List(10) { Tableau(GamePiles.Stock, pile) },
            )
        }
    }
}