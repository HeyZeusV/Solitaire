package com.heyzeusv.solitaire.board.layouts

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.board.HorizontalCardPileWithFlip
import com.heyzeusv.solitaire.board.animation.FlipCardInfo
import com.heyzeusv.solitaire.board.piles.Card
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.toDp

/**
 *  Data class referring to layouts 10 piles wide. Contains [Card] size in pixels and pile
 *  offsets. Using predetermined positions due to [Layout] using [Int]/[IntOffset] to determine
 *  content positions on screen. This would cause issues when trying to apply padding, which would
 *  be in [Dp], as it would cause items to appear lopsided due to padding being correct on one side
 *  but not the other.
 */
data class TenWideLayout(
    val layoutWidth: Int,
    val cardWidth: Int,
    val cardHeight: Int,
    val cardSpacing: Int,
    val foundationClubsOne: IntOffset,
    val foundationDiamondsOne: IntOffset,
    val foundationHeartsOne: IntOffset,
    val foundationSpadesOne: IntOffset,
    val foundationClubsTwo: IntOffset,
    val foundationDiamondsTwo: IntOffset,
    val foundationHeartsTwo: IntOffset,
    val foundationSpadesTwo: IntOffset,
    val wastePile: IntOffset,
    val stockPile: IntOffset,
    val tableauZero: IntOffset,
    val tableauOne: IntOffset,
    val tableauTwo: IntOffset,
    val tableauThree: IntOffset,
    val tableauFour: IntOffset,
    val tableauFive: IntOffset,
    val tableauSix: IntOffset,
    val tableauSeven: IntOffset,
    val tableauEight: IntOffset,
    val tableauNine: IntOffset
) : XWideLayout {
    override val cardConstraints: Constraints =
        Constraints(cardWidth, cardWidth, cardHeight, cardHeight)
    override val wasteConstraints: Constraints =
        Constraints(cardWidth, cardWidth, cardHeight, cardHeight)

    // Waste <-> Stock offsets
    private val leftCardXOffset: Int = cardSpacing
    private val middleCardXOffset: Int = cardWidth.div(2) + cardSpacing
    private val rightCardXOffset: Int = cardSpacing

    override val vPileSpacedByPercent: Float = 0.60f

    override val horizontalPileLayoutIds: List<String> =
        listOf("Right Card", "Middle Card", "Left Card")

    override val multiPileLayoutIds: List<String> = listOf(
        "Tableau Zero Card",
        "Tableau One Card",
        "Tableau Two Card",
        "Tableau Three Card",
        "Tableau Four Card",
        "Tableau Five Card",
        "Tableau Six Card",
        "Tableau Seven Card",
        "Tableau Eight Card",
        "Tableau Nine Card",
        "Tableau Ten Card",
        "Tableau Eleven Card",
        "Tableau Twelve Card"
    )

    /**
     *  Used by movement animations to determine given [gamePile] offset. Animations between
     *  Stock and Waste requires a different offset, [stockWasteMove] determines which to use.
     *  [tAllPile] is used to recursively call this function in order to get the correct offset,
     *  when [gamePile] is [GamePiles.TableauAll].
     */
    override fun getPilePosition(
        gamePile: GamePiles,
        stockWasteMove: Boolean,
        tAllPile: GamePiles
    ): IntOffset {
        return when (gamePile) {
            GamePiles.Stock -> stockPile
            GamePiles.Waste -> wastePile
            GamePiles.FoundationClubsOne -> foundationClubsOne
            GamePiles.FoundationDiamondsOne -> foundationDiamondsOne
            GamePiles.FoundationHeartsOne -> foundationHeartsOne
            GamePiles.FoundationSpadesOne -> foundationSpadesOne
            GamePiles.FoundationClubsTwo -> foundationClubsTwo
            GamePiles.FoundationDiamondsTwo -> foundationDiamondsTwo
            GamePiles.FoundationHeartsTwo -> foundationHeartsTwo
            GamePiles.FoundationSpadesTwo -> foundationSpadesTwo
            GamePiles.TableauZero -> tableauZero
            GamePiles.TableauOne -> tableauOne
            GamePiles.TableauTwo -> tableauTwo
            GamePiles.TableauThree -> tableauThree
            GamePiles.TableauFour -> tableauFour
            GamePiles.TableauFive -> tableauFive
            GamePiles.TableauSix -> tableauSix
            GamePiles.TableauSeven -> tableauSeven
            GamePiles.TableauEight -> tableauEight
            GamePiles.TableauNine -> tableauNine
            GamePiles.TableauAll -> getPilePosition(tAllPile)
        }
    }

    /**
     *  Used by animations involving Tableau piles in order to determine additional Y offset needed
     *  since animation could only involve a sublist of Tableau, rather than entire pile.
     */
    override fun getCardsYOffset(index: Int): IntOffset {
        return IntOffset(x = 0, y = (index * (cardHeight * (1 - vPileSpacedByPercent))).toInt())
    }

    /**
     *  Used by [HorizontalCardPileWithFlip] in order to retrieve [HorizontalCardOffsets] which
     *  contains necessary offsets for animations. [flipCardInfo] determines start/end positions.
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
     *  Returns size of [Card] in dp in the form of [DpSize].
     */
    @Composable
    override fun getCardDpSize(): DpSize = DpSize(cardWidth.toDp(), cardHeight.toDp())
}
