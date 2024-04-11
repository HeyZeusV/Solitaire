package com.heyzeusv.solitaire.ui.board.layouts.positions

import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.heyzeusv.solitaire.data.Card

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
)
