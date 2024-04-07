package com.heyzeusv.solitaire.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.ui.game.HorizontalCardPileWithFlip
import com.heyzeusv.solitaire.util.GamePiles
import com.heyzeusv.solitaire.util.plusX
import com.heyzeusv.solitaire.util.toDp

/**
 *  Helper data class that contains [Card] sizes, pile offsets, and constraints. All data is taken
 *  from given [layPos]. Pile offsets are further edited by given [extraWidth], which is the
 *  extra screen width when compared to given [LayoutPositions.layoutWidth] value.
 */
data class LayoutInfo(private val layPos: LayoutPositions, private val extraWidth: Int) {
    // Card specs
    val cardWidth: Int = layPos.cardWidth
    val cardHeight: Int = layPos.cardHeight
    private val cardSpacing: Int = layPos.cardSpacing
    // pile offsets
    val clubsFoundation: IntOffset = layPos.clubsFoundation.plusX(extraWidth)
    val diamondsFoundation: IntOffset = layPos.diamondsFoundation.plusX(extraWidth)
    val heartsFoundation: IntOffset = layPos.heartsFoundation.plusX(extraWidth)
    val spadesFoundation: IntOffset = layPos.spadesFoundation.plusX(extraWidth)
    val wastePile: IntOffset = layPos.wastePile.plusX(extraWidth)
    val stockPile: IntOffset = layPos.stockPile.plusX(extraWidth)
    val tableauZero: IntOffset = layPos.tableauZero.plusX(extraWidth)
    val tableauOne: IntOffset = layPos.tableauOne.plusX(extraWidth)
    val tableauTwo: IntOffset = layPos.tableauTwo.plusX(extraWidth)
    val tableauThree: IntOffset = layPos.tableauThree.plusX(extraWidth)
    val tableauFour: IntOffset = layPos.tableauFour.plusX(extraWidth)
    val tableauFive: IntOffset = layPos.tableauFive.plusX(extraWidth)
    val tableauSix: IntOffset = layPos.tableauSix.plusX(extraWidth)

    // pile constraints
    val cardConstraints: Constraints = Constraints(cardWidth, cardWidth, cardHeight, cardHeight)
    private val wasteWidth: Int = (cardWidth * 2) + cardSpacing
    val wasteConstraints: Constraints = Constraints(wasteWidth, wasteWidth, cardHeight, cardHeight)

    // horizontal pile offsets
    private val leftCardXOffset: Int = cardSpacing
    private val middleCardXOffset: Int = cardWidth.div(2) + cardSpacing
    private val rightCardXOffset: Int = cardWidth + cardSpacing

    // layout ids 
    val horizontalPileLayoutIds: List<String> = listOf("Right Card", "Middle Card", "Left Card")
    val multiPileLayoutIds: List<String> = listOf(
        "Tableau Zero Card",
        "Tableau One Card",
        "Tableau Two Card",
        "Tableau Three Card",
        "Tableau Four Card",
        "Tableau Five Card",
        "Tableau Six Card"
    )

    /**
     *  Used by movement animations to determine given [gamePile] offset. Animations between
     *  Stock and Waste requires a different offset, [stockWasteMove] determines which to use.
     *  [tAllPile] is used to recursively call this function in order to get the correct offset,
     *  when [gamePile] is [GamePiles.TableauAll].
     */
    fun getPilePosition(
        gamePile: GamePiles,
        stockWasteMove: Boolean = false,
        tAllPile: GamePiles = GamePiles.TableauZero
    ): IntOffset {
        return when (gamePile) {
            GamePiles.Stock -> stockPile
            GamePiles.Waste -> if (stockWasteMove) {
                wastePile
            } else {
                wastePile.plusX(cardWidth + layPos.cardSpacing)
            }
            GamePiles.ClubsFoundation -> clubsFoundation
            GamePiles.DiamondsFoundation -> diamondsFoundation
            GamePiles.HeartsFoundation -> heartsFoundation
            GamePiles.SpadesFoundation -> spadesFoundation
            GamePiles.TableauZero -> tableauZero
            GamePiles.TableauOne -> tableauOne
            GamePiles.TableauTwo -> tableauTwo
            GamePiles.TableauThree -> tableauThree
            GamePiles.TableauFour -> tableauFour
            GamePiles.TableauFive -> tableauFive
            GamePiles.TableauSix -> tableauSix
            GamePiles.TableauAll -> getPilePosition(tAllPile)
        }
    }

    /**
     *  Used by animations involving Tableau piles in order to determine additional Y offset needed
     *  since animation could only involve a sublist of Tableau, rather than entire pile.
     */
    fun getCardsYOffset(index: Int): IntOffset {
        return IntOffset(x = 0, y = (index * (cardHeight * 0.25f)).toInt())
    }

    /**
     *  Used by [HorizontalCardPileWithFlip] in order to retrieve [HorizontalCardOffsets] which
     *  contains necessary offsets for animations. [flipCardInfo] determines start/end positions.
     */
    fun getHorizontalCardOffsets(flipCardInfo: FlipCardInfo): HorizontalCardOffsets {
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
    fun getCardDpSize(): DpSize = DpSize(cardWidth.toDp(), cardHeight.toDp())
}

/**
 *  Data class containing offsets needed by [HorizontalCardPileWithFlip] animations.
 */
data class HorizontalCardOffsets(
    private val rightCardStartOffset: IntOffset,
    private val rightCardEndOffset: IntOffset,
    private val middleCardStartOffset: IntOffset,
    private val middleCardEndOffset: IntOffset,
    private val leftCardStartOffset: IntOffset,
    private val leftCardEndOffset: IntOffset
) {
    val startOffsets = listOf(rightCardStartOffset, middleCardStartOffset, leftCardStartOffset)
    val endOffsets = listOf(rightCardEndOffset, middleCardEndOffset, leftCardEndOffset)
}

/**
 *  Enum class referring to various device screen widths. Contains [Card] size in pixels and pile
 *  offsets. Using predetermined positions due to [Layout] using [Int]/[IntOffset] to determine
 *  content positions on screen. This would cause issues when trying to apply padding, which would
 *  be in [Dp], as it would cause items to appear lopsided due to padding being correct on one side
 *  but not the other.
 */
enum class LayoutPositions(
    val layoutWidth: Int,
    val cardWidth: Int,
    val cardHeight: Int,
    val cardSpacing: Int,
    val clubsFoundation: IntOffset,
    val diamondsFoundation: IntOffset,
    val heartsFoundation: IntOffset,
    val spadesFoundation: IntOffset,
    val wastePile: IntOffset,
    val stockPile: IntOffset,
    val tableauZero: IntOffset,
    val tableauOne: IntOffset,
    val tableauTwo: IntOffset,
    val tableauThree: IntOffset,
    val tableauFour: IntOffset,
    val tableauFive: IntOffset,
    val tableauSix: IntOffset
) {
    Width480(
        layoutWidth = 480,
        cardWidth = 64,
        cardHeight = 92,
        cardSpacing = 4,
        clubsFoundation = IntOffset(4, 0),
        diamondsFoundation = IntOffset(72, 0),
        heartsFoundation = IntOffset(140, 0),
        spadesFoundation = IntOffset(208, 0),
        wastePile = IntOffset(276, 0),
        stockPile = IntOffset(412, 0),
        tableauZero = IntOffset(4, 94),
        tableauOne = IntOffset(72, 94),
        tableauTwo = IntOffset(140, 94),
        tableauThree = IntOffset(208, 94),
        tableauFour = IntOffset(276, 94),
        tableauFive = IntOffset(344, 94),
        tableauSix = IntOffset(412, 94)
    ),
    Width720(
        layoutWidth = 720,
        cardWidth = 98,
        cardHeight = 140,
        cardSpacing = 4,
        clubsFoundation = IntOffset(5, 0),
        diamondsFoundation = IntOffset(107, 0),
        heartsFoundation = IntOffset(209, 0),
        spadesFoundation = IntOffset(311, 0),
        wastePile = IntOffset(413, 0),
        stockPile = IntOffset(617, 0),
        tableauZero = IntOffset(5, 141),
        tableauOne = IntOffset(107, 141),
        tableauTwo = IntOffset(209, 141),
        tableauThree = IntOffset(311, 141),
        tableauFour = IntOffset(413, 141),
        tableauFive = IntOffset(515, 141),
        tableauSix = IntOffset(617, 141)
    ),
    Width960(
        layoutWidth = 960,
        cardWidth = 128,
        cardHeight = 180,
        cardSpacing = 8,
        clubsFoundation = IntOffset(8, 0),
        diamondsFoundation = IntOffset(144, 0),
        heartsFoundation = IntOffset(280, 0),
        spadesFoundation = IntOffset(416, 0),
        wastePile = IntOffset(552, 0),
        stockPile = IntOffset(824, 0),
        tableauZero = IntOffset(8, 187),
        tableauOne = IntOffset(144, 187),
        tableauTwo = IntOffset(280, 187),
        tableauThree = IntOffset(416, 187),
        tableauFour = IntOffset(552, 187),
        tableauFive = IntOffset(688, 187),
        tableauSix = IntOffset(824, 187)
    ),
    Width1080(
        layoutWidth = 1080,
        cardWidth = 148,
        cardHeight = 208,
        cardSpacing = 5,
        clubsFoundation = IntOffset(7, 0),
        diamondsFoundation = IntOffset(160, 0),
        heartsFoundation = IntOffset(313, 0),
        spadesFoundation = IntOffset(466, 0),
        wastePile = IntOffset(619, 0),
        stockPile = IntOffset(925, 0),
        tableauZero = IntOffset(7, 212),
        tableauOne = IntOffset(160, 212),
        tableauTwo = IntOffset(313, 212),
        tableauThree = IntOffset(466, 212),
        tableauFour = IntOffset(619, 212),
        tableauFive = IntOffset(772, 212),
        tableauSix = IntOffset(925, 212)
    ),
    Width1440(
        layoutWidth = 1440,
        cardWidth = 196,
        cardHeight = 276,
        cardSpacing = 8,
        clubsFoundation = IntOffset(10, 0),
        diamondsFoundation = IntOffset(214, 0),
        heartsFoundation = IntOffset(418, 0),
        spadesFoundation = IntOffset(622, 0),
        wastePile = IntOffset(826, 0),
        stockPile = IntOffset(1234, 0),
        tableauZero = IntOffset(10, 282),
        tableauOne = IntOffset(214, 282),
        tableauTwo = IntOffset(418, 282),
        tableauThree = IntOffset(622, 282),
        tableauFour = IntOffset(826, 282),
        tableauFive = IntOffset(1030, 282),
        tableauSix = IntOffset(1234, 282)
    ),
    Width2160(
        layoutWidth = 2160,
        cardWidth = 296,
        cardHeight = 416,
        cardSpacing = 10,
        clubsFoundation = IntOffset(14, 0),
        diamondsFoundation = IntOffset(320, 0),
        heartsFoundation = IntOffset(626, 0),
        spadesFoundation = IntOffset(932, 0),
        wastePile = IntOffset(1238, 0),
        stockPile = IntOffset(1850, 0),
        tableauZero = IntOffset(14, 424),
        tableauOne = IntOffset(320, 424),
        tableauTwo = IntOffset(626, 424),
        tableauThree = IntOffset(932, 424),
        tableauFour = IntOffset(1238, 424),
        tableauFive = IntOffset(1544, 424),
        tableauSix = IntOffset(1850, 424)
    )
}