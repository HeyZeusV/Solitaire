package com.heyzeusv.solitaire.board.layouts

/**
 *  Ensures each layout type is implemented per screen width.
 */
interface LayoutPositions {
    val sevenWideLayout: SevenWideLayout
    val sevenWideFourTableauLayout: SevenWideLayout
    val tenWideLayout: TenWideLayout
    val tenWideEightTableauLayout: TenWideLayout
}