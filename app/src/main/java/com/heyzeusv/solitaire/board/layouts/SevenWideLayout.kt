package com.heyzeusv.solitaire.board.layouts

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.HorizontalCardPileWithFlip
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.plusX
import com.heyzeusv.solitaire.util.toDp

/**
 *  Used by games that are 7 piles wide. Contains card dimensions and piles positions in pixels.
 *
 *  Using predetermined positions due to [Layout] using [Int]/[IntOffset] to determine content
 *  positions on screen. This would cause issues when trying to apply padding, which would be in
 *  [Dp], as it would cause items to appear lopsided due to padding being correct on one side
 *  but not the other.
 */
data class SevenWideLayout(
    val layoutWidth: Int,
    val cardWidth: Int,
    val cardHeight: Int,
    val cardSpacing: Int,
    val foundationClubs: IntOffset,
    val foundationDiamonds: IntOffset,
    val foundationHearts: IntOffset,
    val foundationSpades: IntOffset,
    val wastePile: IntOffset,
    val stockPile: IntOffset,
    val tableauZero: IntOffset,
    val tableauOne: IntOffset,
    val tableauTwo: IntOffset,
    val tableauThree: IntOffset,
    val tableauFour: IntOffset,
    val tableauFive: IntOffset,
    val tableauSix: IntOffset
) : XWideLayout {
    override val cardConstraints: Constraints =
        Constraints(cardWidth, cardWidth, cardHeight, cardHeight)
    private val wasteWidth: Int = (cardWidth * 2) + cardSpacing
    override val wasteConstraints: Constraints =
        Constraints(wasteWidth, wasteWidth, cardHeight, cardHeight)

    // Waste <-> Stock offsets
    private val leftCardXOffset: Int = cardSpacing
    private val middleCardXOffset: Int = cardWidth.div(2) + cardSpacing
    private val rightCardXOffset: Int = cardWidth + cardSpacing

    override val vPileSpacedByPercent: Float = 0.75f

    override val horizontalPileLayoutIds: List<String> =
        listOf("Right Card", "Middle Card", "Left Card")

    override val multiPileLayoutIds: List<String> = listOf(
        "Tableau Zero Card",
        "Tableau One Card",
        "Tableau Two Card",
        "Tableau Three Card",
        "Tableau Four Card",
        "Tableau Five Card",
        "Tableau Six Card"
    )

    /**
     *  Used by card animations to determine pile offset.
     *
     *  @param gamePile The pile offset to retrieve.
     *  @param stockWasteMove Animations between Stock and Waste requires a different offset.
     *  @param tAllPile Used to call this function recursively in order to get correct offset,
     *  when [gamePile] is [GamePiles.TableauAll].
     *  @return [IntOffset] corresponding to given [gamePile].
     */
    override fun getPilePosition(
        gamePile: GamePiles,
        stockWasteMove: Boolean,
        tAllPile: GamePiles
    ): IntOffset {
        return when (gamePile) {
            GamePiles.Stock -> stockPile
            GamePiles.Waste -> if (stockWasteMove) {
                wastePile
            } else {
                wastePile.plusX(cardWidth + cardSpacing)
            }
            GamePiles.FoundationClubsOne -> foundationClubs
            GamePiles.FoundationDiamondsOne -> foundationDiamonds
            GamePiles.FoundationHeartsOne -> foundationHearts
            GamePiles.FoundationSpadesOne -> foundationSpades
            GamePiles.TableauZero -> tableauZero
            GamePiles.TableauOne -> tableauOne
            GamePiles.TableauTwo -> tableauTwo
            GamePiles.TableauThree -> tableauThree
            GamePiles.TableauFour -> tableauFour
            GamePiles.TableauFive -> tableauFive
            GamePiles.TableauSix -> tableauSix
            GamePiles.TableauAll -> getPilePosition(tAllPile)
            else -> IntOffset.Zero
        }
    }

    /**
     *  Card animations involving Tableau piles require Y offset to determine where card moving
     *  beings/ends.
     *
     *  @param index The index of [Card] in Tableau pile.
     *  @return The y offset of Tableau [Card], but in [IntOffset] form.
     */
    override fun getCardsYOffset(index: Int): IntOffset {
        return IntOffset(x = 0, y = (index * (cardHeight * (1 - vPileSpacedByPercent))).toInt())
    }

    /**
     *  Used by [HorizontalCardPileWithFlip] in order to retrieve necessary offsets for animations.
     *
     *  @param flipCardInfo Determines start/end positions.
     *  @return [HorizontalCardOffsets] data class containing [IntOffsets][IntOffset]
     */
    override fun getHorizontalCardOffsets(flipCardInfo: FlipCardInfo): HorizontalCardOffsets {
        return if (flipCardInfo is FlipCardInfo.FaceDown) {
            HorizontalCardOffsets(
                rightCardStartOffset = IntOffset(rightCardXOffset, 0),
                rightCardEndOffset = IntOffset.Zero,
                middleCardStartOffset = IntOffset(middleCardXOffset, 0),
                middleCardEndOffset = IntOffset.Zero,
                leftCardStartOffset = IntOffset(leftCardXOffset, 0),
                leftCardEndOffset = IntOffset.Zero
            )
        } else {
            HorizontalCardOffsets(
                rightCardStartOffset = IntOffset.Zero,
                rightCardEndOffset = IntOffset(rightCardXOffset, 0),
                middleCardStartOffset = IntOffset.Zero,
                middleCardEndOffset = IntOffset(middleCardXOffset, 0),
                leftCardStartOffset = IntOffset.Zero,
                leftCardEndOffset = IntOffset(leftCardXOffset, 0)
            )
        }
    }

    /**
     *  @return The size of [Card] in the form of [DpSize].
     */
    @Composable
    override fun getCardDpSize(): DpSize = DpSize(cardWidth.toDp(), cardHeight.toDp())
}