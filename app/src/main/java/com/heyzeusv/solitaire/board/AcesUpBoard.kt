package com.heyzeusv.solitaire.board

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.heyzeusv.solitaire.board.piles.Pile
import com.heyzeusv.solitaire.board.piles.Stock
import com.heyzeusv.solitaire.board.piles.Tableau
import com.heyzeusv.solitaire.board.animation.AnimateInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Foundation
import com.heyzeusv.solitaire.board.animation.AnimationDurations
import com.heyzeusv.solitaire.board.layouts.Width1080
import com.heyzeusv.solitaire.games.AcesUp
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.PreviewUtil
import com.heyzeusv.solitaire.util.Suits
import com.heyzeusv.solitaire.util.gesturesDisabled
import kotlinx.coroutines.delay

/**
 *  Displays [Stock], [Foundation], and [Tableau] [Piles][Pile] needed in order to play [AcesUp]
 *  and its variants.
 *
 *  @param layout Contains the positions for all [Piles][Pile].
 *  @param animationDurations The durations for each available animation.
 *  @param animateInfo Contains the information needed to animate a legal move.
 *  @param updateAnimateInfo Updates [AnimateInfo] once an animation fully completes.
 *  @param updateIsUndoEnabled Disables undo button during certain animations.
 *  @param isUndoAnimation Used to determine if currently running animation is representing an
 *  undo move.
 *  @param updateIsUndoAnimation Updates the value of [isUndoAnimation].
 *  @param stock [Pile] where user draws more [Cards][Card] from.
 *  @param onStockClick Runs when [stock] is pressed.
 *  @param foundation [Pile] where user has to move [Cards][Card] to in order to win.
 *  @param tableauList List of [Tableau] piles where user can move [Cards][Card] between or to
 *  [foundation].
 *  @param onTableauClick Runs when any of [tableauList] is pressed.
 */
@Composable
fun AcesUpBoard(
    modifier: Modifier = Modifier,
    layout: SevenWideLayout,
    animationDurations: AnimationDurations,
    animateInfo: AnimateInfo?,
    updateAnimateInfo: (AnimateInfo?) -> Unit = { },
    updateIsUndoEnabled: (Boolean) -> Unit = { },
    isUndoAnimation: Boolean,
    updateIsUndoAnimation: (Boolean) -> Unit = { },
    /** Piles and their onClicks */
    stock: Stock,
    onStockClick: () -> Unit = { },
    foundation: Foundation,
    tableauList: List<Tableau>,
    onTableauClick: (Int, Int) -> Unit = { _, _ ->  },
) {
    var animatedOffset by remember(animateInfo) { mutableStateOf(IntOffset.Zero) }
    var flipRotation by remember { mutableFloatStateOf(0f) }

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
        AnimateOffset(
            animateInfo = it,
            animationDurations = animationDurations,
            startOffset = layout.getPilePosition(it.start)
                .plus(layout.getCardsYOffset(it.startTableauIndices.first())),
            endOffset = layout.getPilePosition(it.end)
                .plus(layout.getCardsYOffset(it.endTableauIndices.first())),
            updateXOffset = { value -> animatedOffset = animatedOffset.copy(x = value) },
            updateYOffset = { value -> animatedOffset = animatedOffset.copy(y = value) },
        )
        AnimateFlip(
            animateInfo = it,
            flipDuration = animationDurations.duration,
            flipDelay = animationDurations.noAnimation,
            flipCardInfo = it.flipCardInfo,
            updateRotation = { value -> flipRotation = value},
        )
    }

    Layout(
        modifier = modifier.gesturesDisabled(isUndoAnimation),
        content = {
            animateInfo?.let {
                when (it.flipCardInfo) {
                    FlipCardInfo.FaceDown.SinglePile, FlipCardInfo.FaceUp.SinglePile -> { }
                    FlipCardInfo.FaceDown.MultiPile, FlipCardInfo.FaceUp.MultiPile -> {
                        MultiPileCardWithFlip(
                            layout = layout,
                            animateInfo = it,
                            animationDurations = animationDurations,
                            modifier = Modifier.layoutId("Animated Multi Pile"),
                        )
                    }
                    FlipCardInfo.NoFlip -> {
                        StaticVerticalCardPile(
                            cardDpSize = layout.getCardDpSize(),
                            spacedByPercent = layout.vPileSpacedByPercent,
                            pile = it.animatedCards,
                            modifier = Modifier.layoutId("Animated Vertical Pile"),
                        )
                    }
                }
            }
            Foundation(
                modifier = Modifier
                    .layoutId("Foundation")
                    .testTag("Foundation #$0"),
                cardDpSize = layout.getCardDpSize(),
                pile = foundation.displayPile,
                emptyIconId = R.drawable.foundation_empty,
            )
            Stock(
                modifier = Modifier
                    .layoutId("Stock")
                    .testTag("Stock"),
                cardDpSize = layout.getCardDpSize(),
                pile = stock.displayPile,
                stockWasteEmpty = { true },
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
        val foundationPile = measurables.firstOrNull { it.layoutId == "Foundation" }
        val stockPile = measurables.firstOrNull { it.layoutId == "Stock" }
        val tableauPile0 = measurables.firstOrNull { it.layoutId == "Tableau #0" }
        val tableauPile1 = measurables.firstOrNull { it.layoutId == "Tableau #1" }
        val tableauPile2 = measurables.firstOrNull { it.layoutId == "Tableau #2" }
        val tableauPile3 = measurables.firstOrNull { it.layoutId == "Tableau #3" }

        val animatedMultiPile = measurables.firstOrNull { it.layoutId == "Animated Multi Pile" }
        val animatedVerticalPile =
            measurables.firstOrNull { it.layoutId == "Animated Vertical Pile" }

        layout(constraints.maxWidth, constraints.maxHeight) {
            // card constraints
            val cardWidth = layout.cardWidth
            val cardHeight = layout.cardHeight
            val cardConstraints = layout.cardConstraints
            val tableauHeight = constraints.maxHeight - layout.tableauZero.y
            val tableauConstraints = Constraints(cardWidth, cardWidth, cardHeight, tableauHeight)

            // prevents quick flash of animated Card at (0, 0)
            if (animatedOffset != IntOffset.Zero) {
                animatedVerticalPile?.measure(tableauConstraints)?.place(animatedOffset, 2f)
            }
            animatedMultiPile?.measure(constraints)?.place(IntOffset.Zero, 2f)

            foundationPile?.measure(cardConstraints)?.place(layout.foundationSpades)
            stockPile?.measure(cardConstraints)?.place(layout.stockPile)
            tableauPile0?.measure(tableauConstraints)?.place(layout.tableauZero)
            tableauPile1?.measure(tableauConstraints)?.place(layout.tableauOne)
            tableauPile2?.measure(tableauConstraints)?.place(layout.tableauTwo)
            tableauPile3?.measure(tableauConstraints)?.place(layout.tableauThree)
        }
    }
}

@Preview
@Composable
private fun AcesUpBoardPreview() {
    PreviewUtil().apply {
        Preview {
            AcesUpBoard(
                layout = Width1080(0).sevenWideFourTableauLayout,
                animationDurations = AnimationDurations.None,
                animateInfo = null,
                isUndoAnimation = true,
                stock = Stock(pile),
                foundation = Foundation(Suits.SPADES, GamePiles.FoundationSpadesOne, pile),
                tableauList = List(4) { Tableau(GamePiles.TableauZero, pile) },
            )
        }
    }
}