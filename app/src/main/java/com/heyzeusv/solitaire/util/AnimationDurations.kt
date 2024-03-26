package com.heyzeusv.solitaire.util

enum class AnimationDurations(
    val full: Long,
    val fullInt: Int,
    val beforeAction: Long,
    val afterAction: Long,
    val tableauCardFlip: Int
) {
    TwoHundredFifty(
        full = 250,
        fullInt = 250,
        beforeAction = 15,
        afterAction = 240,
        tableauCardFlip = 200
    )
}