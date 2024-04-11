package com.heyzeusv.solitaire.ui.board.boards.layouts

import androidx.compose.ui.unit.IntOffset

/**
 *  Interface to ensure each layout type is implemented per screen width.
 */
interface LayoutPositions {
    val sevenWideLayout: SevenWideLayout
    val tenWideLayout: TenWideLayout
}

/**
 *  Used to ensure all Width* classes have [extraWidth], which is the width beyond the class size
 *  divided by 2.
 */
abstract class ScreenLayouts(private val extraWidth: Int) : LayoutPositions

class Width480(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 480,
        cardWidth = 64,
        cardHeight = 92,
        cardSpacing = 4,
        foundationClubs = IntOffset(4 + extraWidth, 0),
        foundationDiamonds = IntOffset(72 + extraWidth, 0),
        foundationHearts = IntOffset(140 + extraWidth, 0),
        foundationSpades = IntOffset(208 + extraWidth, 0),
        wastePile = IntOffset(276 + extraWidth, 0),
        stockPile = IntOffset(412 + extraWidth, 0),
        tableauZero = IntOffset(4 + extraWidth, 94),
        tableauOne = IntOffset(72 + extraWidth, 94),
        tableauTwo = IntOffset(140 + extraWidth, 94),
        tableauThree = IntOffset(208 + extraWidth, 94),
        tableauFour = IntOffset(276 + extraWidth, 94),
        tableauFive = IntOffset(344 + extraWidth, 94),
        tableauSix = IntOffset(412 + extraWidth, 94)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 480,
        cardWidth = 46,
        cardHeight = 64,
        cardSpacing = 2,
        foundationClubsOne = IntOffset(1 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(49 + extraWidth, 0),
        foundationHeartsOne = IntOffset(97 + extraWidth, 0),
        foundationSpadesOne = IntOffset(145 + extraWidth, 0),
        foundationClubsTwo = IntOffset(193 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(241 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(289 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(337 + extraWidth, 0),
        wastePile = IntOffset(385 + extraWidth, 0),
        stockPile = IntOffset(433 + extraWidth, 0),
        tableauZero = IntOffset(1 + extraWidth, 69),
        tableauOne = IntOffset(49 + extraWidth, 69),
        tableauTwo = IntOffset(97 + extraWidth, 69),
        tableauThree = IntOffset(145 + extraWidth, 69),
        tableauFour = IntOffset(193 + extraWidth, 69),
        tableauFive = IntOffset(241 + extraWidth, 69),
        tableauSix = IntOffset(289 + extraWidth, 69),
        tableauSeven = IntOffset(337 + extraWidth, 69),
        tableauEight = IntOffset(385 + extraWidth, 69),
        tableauNine = IntOffset(433 + extraWidth, 69),
    )
}

class Width720(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 720,
        cardWidth = 98,
        cardHeight = 140,
        cardSpacing = 4,
        foundationClubs = IntOffset(5 + extraWidth, 0),
        foundationDiamonds = IntOffset(107 + extraWidth, 0),
        foundationHearts = IntOffset(209 + extraWidth, 0),
        foundationSpades = IntOffset(311 + extraWidth, 0),
        wastePile = IntOffset(413 + extraWidth, 0),
        stockPile = IntOffset(617 + extraWidth, 0),
        tableauZero = IntOffset(5 + extraWidth, 141),
        tableauOne = IntOffset(107 + extraWidth, 141),
        tableauTwo = IntOffset(209 + extraWidth, 141),
        tableauThree = IntOffset(311 + extraWidth, 141),
        tableauFour = IntOffset(413 + extraWidth, 141),
        tableauFive = IntOffset(515 + extraWidth, 141),
        tableauSix = IntOffset(617 + extraWidth, 141)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 720,
        cardWidth = 68,
        cardHeight = 96,
        cardSpacing = 4,
        foundationClubsOne = IntOffset(2 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(74 + extraWidth, 0),
        foundationHeartsOne = IntOffset(146 + extraWidth, 0),
        foundationSpadesOne = IntOffset(218 + extraWidth, 0),
        foundationClubsTwo = IntOffset(290 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(362 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(434 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(506 + extraWidth, 0),
        wastePile = IntOffset(578 + extraWidth, 0),
        stockPile = IntOffset(650 + extraWidth, 0),
        tableauZero = IntOffset(2 + extraWidth, 101),
        tableauOne = IntOffset(74 + extraWidth, 101),
        tableauTwo = IntOffset(146 + extraWidth, 101),
        tableauThree = IntOffset(218 + extraWidth, 101),
        tableauFour = IntOffset(290 + extraWidth, 101),
        tableauFive = IntOffset(362 + extraWidth, 101),
        tableauSix = IntOffset(434 + extraWidth, 101),
        tableauSeven = IntOffset(506 + extraWidth, 101),
        tableauEight = IntOffset(578 + extraWidth, 101),
        tableauNine = IntOffset(650 + extraWidth, 101),
    )
}

class Width960(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 960,
        cardWidth = 128,
        cardHeight = 180,
        cardSpacing = 8,
        foundationClubs = IntOffset(8 + extraWidth, 0),
        foundationDiamonds = IntOffset(144 + extraWidth, 0),
        foundationHearts = IntOffset(280 + extraWidth, 0),
        foundationSpades = IntOffset(416 + extraWidth, 0),
        wastePile = IntOffset(552 + extraWidth, 0),
        stockPile = IntOffset(824 + extraWidth, 0),
        tableauZero = IntOffset(8 + extraWidth, 187),
        tableauOne = IntOffset(144 + extraWidth, 187),
        tableauTwo = IntOffset(280 + extraWidth, 187),
        tableauThree = IntOffset(416 + extraWidth, 187),
        tableauFour = IntOffset(552 + extraWidth, 187),
        tableauFive = IntOffset(688 + extraWidth, 187),
        tableauSix = IntOffset(824 + extraWidth, 187)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 960,
        cardWidth = 94,
        cardHeight = 132,
        cardSpacing = 2,
        foundationClubsOne = IntOffset(1 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(97 + extraWidth, 0),
        foundationHeartsOne = IntOffset(193 + extraWidth, 0),
        foundationSpadesOne = IntOffset(289 + extraWidth, 0),
        foundationClubsTwo = IntOffset(385 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(481 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(577 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(673 + extraWidth, 0),
        wastePile = IntOffset(769 + extraWidth, 0),
        stockPile = IntOffset(865 + extraWidth, 0),
        tableauZero = IntOffset(1 + extraWidth, 137),
        tableauOne = IntOffset(97 + extraWidth, 137),
        tableauTwo = IntOffset(193 + extraWidth, 137),
        tableauThree = IntOffset(289 + extraWidth, 137),
        tableauFour = IntOffset(385 + extraWidth, 137),
        tableauFive = IntOffset(481 + extraWidth, 137),
        tableauSix = IntOffset(577 + extraWidth, 137),
        tableauSeven = IntOffset(673 + extraWidth, 137),
        tableauEight = IntOffset(769 + extraWidth, 137),
        tableauNine = IntOffset(865 + extraWidth, 137),
    )
}

class Width1080(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 1080,
        cardWidth = 148,
        cardHeight = 208,
        cardSpacing = 5,
        foundationClubs = IntOffset(7 + extraWidth, 0),
        foundationDiamonds = IntOffset(160 + extraWidth, 0),
        foundationHearts = IntOffset(313 + extraWidth, 0),
        foundationSpades = IntOffset(466 + extraWidth, 0),
        wastePile = IntOffset(619 + extraWidth, 0),
        stockPile = IntOffset(925 + extraWidth, 0),
        tableauZero = IntOffset(7 + extraWidth, 212),
        tableauOne = IntOffset(160 + extraWidth, 212),
        tableauTwo = IntOffset(313 + extraWidth, 212),
        tableauThree = IntOffset(466 + extraWidth, 212),
        tableauFour = IntOffset(619 + extraWidth, 212),
        tableauFive = IntOffset(772 + extraWidth, 212),
        tableauSix = IntOffset(925 + extraWidth, 212)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 1080,
        cardWidth = 106,
        cardHeight = 148,
        cardSpacing = 2,
        foundationClubsOne = IntOffset(1 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(109 + extraWidth, 0),
        foundationHeartsOne = IntOffset(217 + extraWidth, 0),
        foundationSpadesOne = IntOffset(325 + extraWidth, 0),
        foundationClubsTwo = IntOffset(433 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(541 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(649 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(757 + extraWidth, 0),
        wastePile = IntOffset(865 + extraWidth, 0),
        stockPile = IntOffset(973 + extraWidth, 0),
        tableauZero = IntOffset(1 + extraWidth, 153),
        tableauOne = IntOffset(109 + extraWidth, 153),
        tableauTwo = IntOffset(217 + extraWidth, 153),
        tableauThree = IntOffset(325 + extraWidth, 153),
        tableauFour = IntOffset(433 + extraWidth, 153),
        tableauFive = IntOffset(541 + extraWidth, 153),
        tableauSix = IntOffset(649 + extraWidth, 153),
        tableauSeven = IntOffset(757 + extraWidth, 153),
        tableauEight = IntOffset(865 + extraWidth, 153),
        tableauNine = IntOffset(973 + extraWidth, 153),
    )
}

class Width1440(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 1440,
        cardWidth = 196,
        cardHeight = 276,
        cardSpacing = 8,
        foundationClubs = IntOffset(10 + extraWidth, 0),
        foundationDiamonds = IntOffset(214 + extraWidth, 0),
        foundationHearts = IntOffset(418 + extraWidth, 0),
        foundationSpades = IntOffset(622 + extraWidth, 0),
        wastePile = IntOffset(826 + extraWidth, 0),
        stockPile = IntOffset(1234 + extraWidth, 0),
        tableauZero = IntOffset(10 + extraWidth, 282),
        tableauOne = IntOffset(214 + extraWidth, 282),
        tableauTwo = IntOffset(418 + extraWidth, 282),
        tableauThree = IntOffset(622 + extraWidth, 282),
        tableauFour = IntOffset(826 + extraWidth, 282),
        tableauFive = IntOffset(1030 + extraWidth, 282),
        tableauSix = IntOffset(1234 + extraWidth, 282)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 1080,
        cardWidth = 142,
        cardHeight = 200,
        cardSpacing = 2,
        foundationClubsOne = IntOffset(1 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(145 + extraWidth, 0),
        foundationHeartsOne = IntOffset(289 + extraWidth, 0),
        foundationSpadesOne = IntOffset(433 + extraWidth, 0),
        foundationClubsTwo = IntOffset(577 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(721 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(865 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(1009 + extraWidth, 0),
        wastePile = IntOffset(1153 + extraWidth, 0),
        stockPile = IntOffset(1297 + extraWidth, 0),
        tableauZero = IntOffset(1 + extraWidth, 205),
        tableauOne = IntOffset(145 + extraWidth, 205),
        tableauTwo = IntOffset(289 + extraWidth, 205),
        tableauThree = IntOffset(433 + extraWidth, 205),
        tableauFour = IntOffset(577 + extraWidth, 205),
        tableauFive = IntOffset(721 + extraWidth, 205),
        tableauSix = IntOffset(865 + extraWidth, 205),
        tableauSeven = IntOffset(1009 + extraWidth, 205),
        tableauEight = IntOffset(1153 + extraWidth, 205),
        tableauNine = IntOffset(1297 + extraWidth, 205),
    )
}

class Width2160(extraWidth: Int) : ScreenLayouts(extraWidth), LayoutPositions {
    override val sevenWideLayout: SevenWideLayout = SevenWideLayout(
        layoutWidth = 2160,
        cardWidth = 296,
        cardHeight = 416,
        cardSpacing = 10,
        foundationClubs = IntOffset(14 + extraWidth, 0),
        foundationDiamonds = IntOffset(320 + extraWidth, 0),
        foundationHearts = IntOffset(626 + extraWidth, 0),
        foundationSpades = IntOffset(932 + extraWidth, 0),
        wastePile = IntOffset(1238 + extraWidth, 0),
        stockPile = IntOffset(1850 + extraWidth, 0),
        tableauZero = IntOffset(14 + extraWidth, 424),
        tableauOne = IntOffset(320 + extraWidth, 424),
        tableauTwo = IntOffset(626 + extraWidth, 424),
        tableauThree = IntOffset(932 + extraWidth, 424),
        tableauFour = IntOffset(1238 + extraWidth, 424),
        tableauFive = IntOffset(1544 + extraWidth, 424),
        tableauSix = IntOffset(1850 + extraWidth, 424)
    )
    override val tenWideLayout: TenWideLayout = TenWideLayout(
        layoutWidth = 2160,
        cardWidth = 214,
        cardHeight = 300,
        cardSpacing = 2,
        foundationClubsOne = IntOffset(1 + extraWidth, 0),
        foundationDiamondsOne = IntOffset(217 + extraWidth, 0),
        foundationHeartsOne = IntOffset(433 + extraWidth, 0),
        foundationSpadesOne = IntOffset(649 + extraWidth, 0),
        foundationClubsTwo = IntOffset(865 + extraWidth, 0),
        foundationDiamondsTwo = IntOffset(1081 + extraWidth, 0),
        foundationHeartsTwo = IntOffset(1297 + extraWidth, 0),
        foundationSpadesTwo = IntOffset(1513 + extraWidth, 0),
        wastePile = IntOffset(1729 + extraWidth, 0),
        stockPile = IntOffset(1945 + extraWidth, 0),
        tableauZero = IntOffset(1 + extraWidth, 305),
        tableauOne = IntOffset(217 + extraWidth, 305),
        tableauTwo = IntOffset(433 + extraWidth, 305),
        tableauThree = IntOffset(649 + extraWidth, 305),
        tableauFour = IntOffset(865 + extraWidth, 305),
        tableauFive = IntOffset(1081 + extraWidth, 305),
        tableauSix = IntOffset(1297 + extraWidth, 305),
        tableauSeven = IntOffset(1513 + extraWidth, 305),
        tableauEight = IntOffset(1729 + extraWidth, 305),
        tableauNine = IntOffset(1945 + extraWidth, 305),
    )
}